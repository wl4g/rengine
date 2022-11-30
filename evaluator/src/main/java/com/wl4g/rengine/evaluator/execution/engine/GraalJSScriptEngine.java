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
package com.wl4g.rengine.evaluator.execution.engine;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.EnvironmentUtil.getBooleanProperty;
import static com.wl4g.infra.common.lang.EnvironmentUtil.getIntProperty;
import static com.wl4g.infra.common.lang.FastTimeClock.currentTimeMillis;
import static com.wl4g.infra.common.lang.StringUtils2.getFilename;
import static com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService.MetricsName.evaluation_execute_time;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import com.wl4g.infra.common.graalvm.GraalPolyglotManager;
import com.wl4g.infra.common.graalvm.GraalPolyglotManager.ContextWrapper;
import com.wl4g.infra.common.lang.EnvironmentUtil;
import com.wl4g.infra.common.lang.StringUtils2;
import com.wl4g.rengine.common.entity.Scenes;
import com.wl4g.rengine.common.entity.UploadObject.UploadType;
import com.wl4g.rengine.common.exception.ExecutionException;
import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptHttpClient;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptResult;
import com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService.MetricsTag;
import com.wl4g.rengine.evaluator.minio.MinioManager.ObjectResource;

import io.micrometer.core.instrument.Timer;
import io.quarkus.runtime.StartupEvent;
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

    @NotNull
    GraalPolyglotManager graalPolyglotManager;

    void onStart(@Observes StartupEvent event) {
        try {
            log.info("Initialzing graal scriptEngine ...");

            // Extraction graal.js from environment.
            Map<String, String> graaljsOptions = EnvironmentUtil.getConfigProperties("GRAALJS_OPTIONS_");

            graalPolyglotManager = new GraalPolyglotManager(getIntProperty("graaljs.context.pool.min", 1),
                    getIntProperty("graaljs.context.pool.max", 10), () -> Context.newBuilder("js") // Only-allowed-JS-language
                            .allowAllAccess(getBooleanProperty("graaljs.allowAllAccess", true))
                            .allowExperimentalOptions(getBooleanProperty("graaljs.allowExperimentalOptions", true))
                            .allowIO(getBooleanProperty("graaljs.allowIO", true))
                            .allowCreateProcess(getBooleanProperty("graaljs.allowCreateProcess", true))
                            .allowCreateThread(getBooleanProperty("graaljs.allowCreateThread", true))
                            .allowNativeAccess(getBooleanProperty("graaljs.allowNativeAccess", true))
                            .allowHostClassLoading(getBooleanProperty("graaljs.allowHostClassLoading", true))
                            .allowValueSharing(getBooleanProperty("graaljs.allowValueSharing", true))
                            .allowPolyglotAccess(PolyglotAccess.ALL)
                            .useSystemExit(getBooleanProperty("graaljs.useSystemExit", false))
                            .options(graaljsOptions)
                            .build());
        } catch (Exception e) {
            log.error("Failed to init graal JSScript Engine.", e);
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
    public ScriptResult execute(final Evaluation evaluation, final Scenes scenes) {
        log.debug("Execution JS script for scenesCode: {} ...", scenes.getScenesCode());

        try (ContextWrapper context = graalPolyglotManager.getContext();) {
            // Load all scripts dependencies.
            List<ObjectResource> scripts = safeList(loadScriptResources(UploadType.USER_LIBRARY_WITH_JS, scenes, true));
            for (ObjectResource script : scripts) {
                isTrue(!script.isBinary(), "Invalid JS dependency library binary type");
                log.debug("Evaling js-dependencys: {}", script.getObjectPrefix());

                String scriptName = StringUtils2.getFilename(script.getObjectPrefix()).concat("@").concat(scenes.getScenesCode());
                try {
                    // merge JS library with dependency.
                    context.eval(Source.newBuilder("js", script.readToString(), scriptName).build());
                } catch (PolyglotException e) {
                    throw new ExecutionException(evaluation.getRequestId(), scenes.getScenesCode(),
                            format("Unable to parse JS dependency of '%s', scenesCode: %s", scriptName, scenes.getScenesCode()),
                            e);
                }
            }

            Value bindings = context.getBindings("js");
            // Try not to bind implicit objects, let users create new objects by
            // self or get default objects from the context.
            bindings.putMember(ScriptHttpClient.class.getSimpleName(), ScriptHttpClient.class);
            bindings.putMember(ScriptResult.class.getSimpleName(), ScriptResult.class);

            log.debug("Loading js-script ...");
            Value mainFunction = bindings.getMember(DEFAULT_MAIN_FUNCTION);

            // Buried-point: execute cost-time.
            Set<String> scriptFileNames = scripts.stream().map(s -> getFilename(s.getObjectPrefix())).collect(toSet());
            Timer executeTimer = meterService.timer(evaluation_execute_time.getName(), evaluation_execute_time.getHelp(),
                    new double[] { 0.5, 0.9, 0.95 }, MetricsTag.CLIENT_ID, evaluation.getClientId(), MetricsTag.SCENESCODE,
                    evaluation.getScenesCode(), MetricsTag.ENGINE, scenes.getWorkflow().getRuleEngine().name(),
                    MetricsTag.LIBRARY, scriptFileNames.toString());

            final long begin = currentTimeMillis();
            Value result = mainFunction.execute(newScriptContext(evaluation));
            final long costTime = currentTimeMillis() - begin;
            executeTimer.record(costTime, MILLISECONDS);

            log.info("Executed for scenesCode: {}, cost: {}ms, result: {}", scenes.getScenesCode(), costTime, result);
            return result.as(ScriptResult.class);
        } catch (Exception e) {
            throw new ExecutionException(evaluation.getRequestId(), scenes.getScenesCode(),
                    format("Failed to execution for scenesCode: %s", scenes.getScenesCode()), e);
        }
    }

}
