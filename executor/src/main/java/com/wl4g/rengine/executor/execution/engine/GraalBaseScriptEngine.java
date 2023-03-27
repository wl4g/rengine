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

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.Exceptions.getStackTraceAsString;
import static com.wl4g.infra.common.lang.FastTimeClock.currentTimeMillis;
import static com.wl4g.infra.common.lang.StringUtils2.getFilename;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_EXECUTOR_MAIN_FUNCTION;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_time;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.SystemUtils.LINE_SEPARATOR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
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
import com.wl4g.rengine.common.graph.ExecutionGraphContext;
import com.wl4g.rengine.common.graph.ExecutionGraphParameter;
import com.wl4g.rengine.executor.execution.EngineConfig;
import com.wl4g.rengine.executor.execution.sdk.ScriptContext;
import com.wl4g.rengine.executor.execution.sdk.ScriptExecutor;
import com.wl4g.rengine.executor.execution.sdk.ScriptResult;
import com.wl4g.rengine.executor.meter.RengineExecutorMeterService;
import com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsTag;
import com.wl4g.rengine.executor.minio.MinioManager.ObjectResource;

import io.micrometer.core.instrument.Timer;
import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link GraalBaseScriptEngine}
 * 
 * @author James Wong
 * @version 2022-09-22
 * @since v1.0.0
 */
@CustomLog
@Getter
public abstract class GraalBaseScriptEngine extends AbstractScriptEngine {

    @Inject
    EngineConfig engineConfig;

    @NotNull
    GraalPolyglotManager graalPolyglotManager;

    @PostConstruct
    @Override
    protected void init() {
        super.init();
        this.graalPolyglotManager = notNullOf(createGraalPolyglotManager(), "graalPolyglotManager");
    }

    protected abstract String getPermittedLanguages();

    protected abstract GraalPolyglotManager createGraalPolyglotManager();

    @PreDestroy
    protected void destroy() {
        try {
            log.info("Destroy graal polyglot manager ...");
            graalPolyglotManager.close();
        } catch (Exception e) {
            log.error("Failed to destroy graal polyglot manager.", e);
        }
    }

    @Override
    public ScriptResult execute(
            final @NotNull ExecutionGraphContext graphContext,
            final @NotNull RuleWrapper rule,
            final boolean usingCache) {
        final String traceId = hasTextOf(graphContext.getParameter().getTraceId(), "traceId");
        final String clientId = hasTextOf(graphContext.getParameter().getClientId(), "clientId");
        final Long workflowId = notNullOf(graphContext.getParameter().getWorkflowId(), "workflowId");
        final String permittedLanguages = getPermittedLanguages();
        log.debug("Executing {} script for workflowId: {} ...", permittedLanguages, workflowId);

        // see:https://github.com/oracle/graaljs/blob/vm-ee-22.1.0/graal-js/src/com.oracle.truffle.js.test.threading/src/com/oracle/truffle/js/test/threading/AsyncTaskTests.java#L283
        final ContextWrapper graalContext = graalPolyglotManager.getContext(singletonMap(KEY_WORKFLOW_ID, workflowId));
        try {
            // Build script context.
            final ScriptContext scriptContext = newScriptContext(graphContext);

            // Load all scripts dependencies.
            final List<ObjectResource> scripts = safeList(loadScriptResources(workflowId, rule, usingCache));
            for (ObjectResource script : scripts) {
                isTrue(!script.isBinary(), "invalid %s script dependency lib type", permittedLanguages);
                log.debug("Evaling {} script dependency: {}", permittedLanguages, script.getObjectPrefix());

                final String scriptName = StringUtils2.getFilename(script.getObjectPrefix()).concat("@").concat(workflowId + "");
                try {
                    // merge script library with dependency.
                    graalContext.eval(Source.newBuilder(permittedLanguages, script.readToString(), scriptName).build());
                } catch (PolyglotException e) {
                    throw new EvaluationException(traceId, clientId, workflowId,
                            format("Unable to parse %s script dependency of '%s', workflowId: %s", permittedLanguages, scriptName,
                                    workflowId),
                            e);
                }
            }

            final Value bindings = graalContext.getBindings(permittedLanguages);
            log.trace("Binding {} script members ...", permittedLanguages);
            bindingMembers(scriptContext, bindings);

            final Value mainFunction = bindings.getMember(DEFAULT_EXECUTOR_MAIN_FUNCTION);

            // Buried-point: execute cost-time.
            final Set<String> scriptFileNames = scripts.stream().map(s -> getFilename(s.getObjectPrefix())).collect(toSet());
            final Timer executeTimer = meterService.timer(execution_time.getName(), execution_time.getHelp(),
                    RengineExecutorMeterService.DEFAULT_PERCENTILES, MetricsTag.CLIENT_ID, clientId, MetricsTag.ENGINE,
                    rule.getEngine().name(), MetricsTag.LIBRARY, scriptFileNames.toString());

            final long begin = currentTimeMillis();
            final Value result = mainFunction.execute(scriptContext);
            final long costTime = currentTimeMillis() - begin;
            executeTimer.record(costTime, MILLISECONDS);

            log.debug("Executed for workflowId: {}, cost: {}ms, result: {}", workflowId, costTime, result);
            return result.as(ScriptResult.class);
        } catch (Throwable ex) {
            // Gets current graal context stderr.
            logScriptErrorMessage(ex, (JdkLoggingOutputStream) graalContext.getStderr());
            throw new EvaluationException(traceId, clientId, workflowId,
                    format("Failed to execute %s script", permittedLanguages), ex);
        } finally {
            try {
                graalContext.close();
            } catch (Throwable ex) {
                throw new IllegalStateException(format("Failed to closing context wrapper for %s", workflowId), ex);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    protected void bindingMembers(final @NotNull ScriptContext scriptContext, final @NotNull Value bindings) {
        // Try not to bind implicit objects, let users create new objects by
        // self or get default objects from the graal context.
        REGISTER_MEMBERS.forEach((name, member) -> {
            // Allowed for obtain lazy member supplier.
            if (member instanceof Supplier) {
                bindings.putMember(name, ((Supplier) member).get());
            } else {
                bindings.putMember(name, member);
            }
        });
    }

    protected void logScriptErrorMessage(Throwable ex, JdkLoggingOutputStream graalCtxStderr) {
        if (nonNull(graalCtxStderr)) {
            try {
                String scriptErrmsg = getStackTraceAsString(ex);
                if (engineConfig.log().extractStackCausesAsLog()) {
                    scriptErrmsg = extractStackCausesAsLog(scriptErrmsg);
                }
                graalCtxStderr.write(trimToEmpty(scriptErrmsg).getBytes(UTF_8));
                graalCtxStderr.flush();
            } catch (IOException ex2) {
                log.error("Unable to write script execute error log.", ex2);
            }
        }
    }

    public static String extractStackCausesAsLog(String scriptStacktrace) {
        final List<String> errStacks = new ArrayList<>(4);
        final Matcher matcher = Pattern.compile(DEFAULT_SCRIPT_ERRMSG_REGEX).matcher(scriptStacktrace);
        while (matcher.find()) {
            errStacks.add(matcher.group());
        }
        return trimToEmpty(join(errStacks.toArray(), LINE_SEPARATOR));
    }

    @Override
    protected ScriptExecutor createScriptExecutor(
            final @NotNull ExecutionGraphParameter parameter,
            final @NotNull SafeScheduledTaskPoolExecutor executor) {
        return new ScriptExecutor(parameter.getWorkflowId(), executor, graalPolyglotManager);
    }

    /**
     * for example:
     * 
     * <pre>
     *   2023-24-45 19:45:12 WARN co.wl.re.ex.ex.DefaultWorkflowExecution (ReactiveEngineExecutionServiceImpl-1) Failed to execution workflow graph for workflowId: 6150868953448440[39m[38;5;203m: com.wl4g.rengine.common.exception.ExecutionGraphException: com.wl4g.rengine.common.exception.EvaluationException: Failed to execute script
     *   at com.wl4g.rengine.common.graph.ExecutionGraph$BaseOperator.doExecute(ExecutionGraph.java:300)
     *   ... 10 more
     *   Caused by: com.wl4g.rengine.common.exception.EvaluationException: Failed to execute script
     *   at com.wl4g.rengine.executor.execution.engine.GraalJSScriptEngine.execute(GraalJSScriptEngine.java:207)
     *   at com.wl4g.rengine.executor.execution.engine.GraalJSScriptEngine_Subclass.execute$$superforward1(Unknown Source)
     *   at com.wl4g.rengine.executor.execution.engine.GraalJSScriptEngine_Subclass$$function$$6.apply(Unknown Source)
     *   at io.quarkus.arc.impl.AroundInvokeInvocationContext.proceed(AroundInvokeInvocationContext.java:53)
     *   ... 28 more
     *   Caused by: org.graalvm.polyglot.PolyglotException: [AF] - vmHost is required
     *   at com.wl4g.infra.common.lang.Assert2.hasText(Assert2.java:608)
     *   at com.wl4g.infra.common.lang.Assert2.hasTextOf(Assert2.java:772)
     *   at com.wl4g.rengine.executor.execution.sdk.tools.Assert.hasTextOf(Assert.java:80)
     *   at &lt;js&gt;.process(vm-health-detecter-1.0.0.js@6150868953448440:4)
     *   at &lt;js&gt; process(vm-health-detecter-1.0.0.js@6150868953448440:4)
     *   at org.graalvm.polyglot.Value.execute(Value.java:841)
     *   at com.wl4g.rengine.executor.execution.engine.GraalJSScriptEngine.execute(GraalJSScriptEngine.java:198)
     *   at com.wl4g.rengine.executor.execution.engine.GraalJSScriptEngine_Subclass.execute$$superforward1(Unknown Source)
     *   ... 5 more
     * </pre>
     */
    public static final String DEFAULT_SCRIPT_ERRMSG_REGEX = "Caused by:(.+)|at <(js|python|py|r|ruby)>(\\s|\\.)([a-zA-Z0-9]+)\\((.+)\\)";

}
