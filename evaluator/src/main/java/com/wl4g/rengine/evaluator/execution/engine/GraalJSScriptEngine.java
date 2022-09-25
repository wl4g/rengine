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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.evaluator.execution.engine;

import static com.wl4g.infra.common.lang.EnvironmentUtil.getBooleanProperty;
import static java.lang.String.format;

import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import com.wl4g.infra.common.graalvm.GraalJsScriptManager;
import com.wl4g.infra.common.graalvm.GraalJsScriptManager.ContextWrapper;
import com.wl4g.infra.common.lang.EnvironmentUtil;
import com.wl4g.rengine.common.bean.UploadObject.UploadType;
import com.wl4g.rengine.common.exception.ExecutionException;
import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.common.model.EvaluationResult;

import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link GraalJSScriptEngine}
 * 
 * @author James Wong
 * @version 2022-09-22
 * @since v3.0.0
 */
@Slf4j
@Singleton
public class GraalJSScriptEngine extends AbstractScriptEngine {

    @NotNull
    GraalJsScriptManager graalScriptManager;

    void onStart(@Observes StartupEvent event) {
        try {
            log.info("Initialzing graal scriptEngine ...");

            // Extraction graal.js from environment.
            Map<String, String> graaljsOptions = EnvironmentUtil.getConfigProperties("GRAALJS_OPTIONS_");

            graalScriptManager = new GraalJsScriptManager(1, 10,
                    () -> Context.newBuilder("js")
                            .allowAllAccess(getBooleanProperty("graaljs.allowAllAccess", true))
                            .allowExperimentalOptions(getBooleanProperty("graaljs.allowExperimentalOptions", true))
                            .allowIO(getBooleanProperty("graaljs.allowIO", true))
                            .allowCreateProcess(getBooleanProperty("graaljs.allowCreateProcess", true))
                            .allowCreateThread(getBooleanProperty("graaljs.allowCreateThread", true))
                            .allowNativeAccess(getBooleanProperty("graaljs.allowNativeAccess", true))
                            .allowHostClassLoading(getBooleanProperty("graaljs.allowHostClassLoading", true))
                            .allowValueSharing(getBooleanProperty("graaljs.allowValueSharing", true))
                            .allowPolyglotAccess(PolyglotAccess.ALL)
                            .useSystemExit(getBooleanProperty("graaljs.useSystemExit", true))
                            .options(graaljsOptions)
                            .build());
        } catch (Exception e) {
            log.error("Failed to init graal scriptEngine.", e);
        }
    }

    @PreDestroy
    void destroy() {
        try {
            log.info("Destroy graal scriptManager ...");
            graalScriptManager.close();
        } catch (Exception e) {
            log.error("Failed to destroy graal scriptManager.", e);
        }
    }

    @Override
    public EvaluationResult apply(Evaluation model) {
        final String scriptMain = model.getScripting().getMainFun();

        try (ContextWrapper ctx = graalScriptManager.getContext();) {
            final List<String> scripts = loadScripts(UploadType.USER_LIBRARY_WITH_JS, model);

            // TODO merge(import?) scripts all.
            ctx.getContext().eval(Source.newBuilder("js", scripts.get(0), scriptMain).build());

            log.info("Binding JS script with {} ...", scriptMain);
            Value mainFunction = ctx.getContext().getBindings("js").getMember(scriptMain);

            log.info("Execution JS script with {} ...", scriptMain);
            Value result = mainFunction.execute();
            log.info("Execution JS script: {}, result: {}", scriptMain, result.toString());

            // TODO re-definition result model structure
            return EvaluationResult.GenericEvaluationResult.builder().result(result.toString()).build();
        } catch (Exception e) {
            throw new ExecutionException(format("Failed to execution '%s' with JS engine.", scriptMain), e);
        }
    }

}
