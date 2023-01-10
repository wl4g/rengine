/*
 * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.executor.execution.engine;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.FastTimeClock.currentTimeMillis;
import static com.wl4g.infra.common.lang.StringUtils2.getFilename;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_EXECUTOR_MAIN_FUNCTION;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_EXECUTOR_TMP_SCRIPT_CACHE_DIR;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_time;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import com.wl4g.infra.common.graalvm.polyglot.GraalPolyglotManager;
import com.wl4g.infra.common.graalvm.polyglot.GraalPolyglotManager.ContextWrapper;
import com.wl4g.infra.common.graalvm.polyglot.JdkLoggingOutputStream;
import com.wl4g.infra.common.lang.StringUtils2;
import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;
import com.wl4g.rengine.common.entity.Rule.RuleWrapper;
import com.wl4g.rengine.common.exception.EvaluationException;
import com.wl4g.rengine.common.exception.ExecutionScriptException;
import com.wl4g.rengine.common.graph.ExecutionGraphContext;
import com.wl4g.rengine.common.graph.ExecutionGraphParameter;
import com.wl4g.rengine.executor.execution.ExecutionConfig;
import com.wl4g.rengine.executor.execution.ExecutionConfig.ScriptLogConfig;
import com.wl4g.rengine.executor.execution.sdk.ScriptContext;
import com.wl4g.rengine.executor.execution.sdk.ScriptExecutor;
import com.wl4g.rengine.executor.execution.sdk.ScriptResult;
import com.wl4g.rengine.executor.metrics.ExecutorMeterService;
import com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsTag;
import com.wl4g.rengine.executor.minio.MinioManager.ObjectResource;

import io.micrometer.core.instrument.Timer;
import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link GraalJSScriptEngine}
 * 
 * @author James Wong
 * @version 2022-09-22
 * @since v1.0.0
 */
@CustomLog
@Getter
@Singleton
public class GraalJSScriptEngine extends AbstractScriptEngine {

    @Inject
    ExecutionConfig config;

    @NotNull
    GraalPolyglotManager graalPolyglotManager;

    @PostConstruct
    void init() {
        try {
            log.info("Initialzing graal JS script engine ...");
            final ScriptLogConfig scriptLogConfig = config.log();

            this.graalPolyglotManager = GraalPolyglotManager.newDefaultGraalJS(DEFAULT_EXECUTOR_TMP_SCRIPT_CACHE_DIR,
                    metadata -> new JdkLoggingOutputStream(buildScriptLogFilePattern(scriptLogConfig.baseDir(), metadata, false),
                            Level.INFO, scriptLogConfig.fileMaxSize(), scriptLogConfig.fileMaxCount(),
                            scriptLogConfig.enableConsole(), false),
                    metadata -> new JdkLoggingOutputStream(buildScriptLogFilePattern(scriptLogConfig.baseDir(), metadata, true),
                            Level.WARNING, scriptLogConfig.fileMaxSize(), scriptLogConfig.fileMaxCount(),
                            scriptLogConfig.enableConsole(), true));
        } catch (Exception e) {
            throw new ExecutionScriptException("Failed to init graal JS Script engine.", e);
        }
    }

    @PreDestroy
    void destroy() {
        try {
            log.info("Destroy graal JSScript manager ...");
            graalPolyglotManager.close();
        } catch (Exception e) {
            log.error("Failed to destroy graal JSScript manager.", e);
        }
    }

    @Override
    public ScriptResult execute(@NotNull final ExecutionGraphContext graphContext, @NotNull final RuleWrapper rule) {
        final String traceId = graphContext.getParameter().getTraceId();
        final String clientId = graphContext.getParameter().getClientId();
        final String scenesCode = graphContext.getParameter().getScenesCode();
        final Long workflowId = graphContext.getParameter().getWorkflowId();
        hasTextOf(traceId, "traceId");
        hasTextOf(clientId, "clientId");
        hasTextOf(scenesCode, "scenesCode");
        notNullOf(workflowId, "workflowId");

        log.debug("Execution JS script for scenesCode: {} ...", scenesCode);
        // see:https://github.com/oracle/graaljs/blob/vm-ee-22.1.0/graal-js/src/com.oracle.truffle.js.test.threading/src/com/oracle/truffle/js/test/threading/AsyncTaskTests.java#L283
        try (ContextWrapper graalContext = graalPolyglotManager.getContext(singletonMap(KEY_WORKFLOW_ID, workflowId));) {
            // New construct script context.
            final ScriptContext scriptContext = newScriptContext(graphContext);

            // Load all scripts dependencies.
            final List<ObjectResource> scripts = safeList(loadScriptResources(scenesCode, rule, true));
            for (ObjectResource script : scripts) {
                isTrue(!script.isBinary(), "invalid js dependency lib type");
                log.debug("Evaling js-dependencys: {}", script.getObjectPrefix());

                final String scriptName = StringUtils2.getFilename(script.getObjectPrefix()).concat("@").concat(scenesCode);
                try {
                    // merge JS library with dependency.
                    graalContext.eval(Source.newBuilder("js", script.readToString(), scriptName).build());
                } catch (PolyglotException e) {
                    throw new EvaluationException(traceId, scenesCode,
                            format("Unable to parse JS dependency of '%s', scenesCode: %s", scriptName, scenesCode), e);
                }
            }

            final Value bindings = graalContext.getBindings("js");
            bindingMembers(scriptContext, bindings);

            log.trace("Loading js script ...");
            final Value mainFunction = bindings.getMember(DEFAULT_EXECUTOR_MAIN_FUNCTION);

            // Buried-point: execute cost-time.
            final Set<String> scriptFileNames = scripts.stream().map(s -> getFilename(s.getObjectPrefix())).collect(toSet());
            final Timer executeTimer = meterService.timer(execution_time.getName(), execution_time.getHelp(),
                    ExecutorMeterService.DEFAULT_PERCENTILES, MetricsTag.CLIENT_ID, clientId, MetricsTag.SCENESCODE, scenesCode,
                    MetricsTag.ENGINE, rule.getEngine().name(), MetricsTag.LIBRARY, scriptFileNames.toString());

            final long begin = currentTimeMillis();
            final Value result = mainFunction.execute(scriptContext);
            final long costTime = currentTimeMillis() - begin;
            executeTimer.record(costTime, MILLISECONDS);

            log.debug("Executed for scenesCode: {}, cost: {}ms, result: {}", scenesCode, costTime, result);
            return result.as(ScriptResult.class);
        } catch (Throwable e) {
            throw new EvaluationException(traceId, clientId, scenesCode, workflowId, "Failed to execution js script", e);
        }
    }

    private void bindingMembers(final @NotNull ScriptContext scriptContext, final @NotNull Value bindings) {
        // Try not to bind implicit objects, let users create new objects by
        // self or get default objects from the graal context.
        REGISTER_MEMBERS.forEach((name, member) -> bindings.putMember(name, member));
    }

    @Override
    protected ScriptExecutor createScriptExecutor(
            final @NotNull ExecutionGraphParameter parameter,
            final @NotNull SafeScheduledTaskPoolExecutor executor) {
        return new ScriptExecutor(parameter.getWorkflowId(), executor, graalPolyglotManager);
    }

}
