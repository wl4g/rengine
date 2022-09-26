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

import static java.lang.String.format;

import java.util.List;
import java.util.function.Function;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import com.wl4g.rengine.common.bean.UploadObject.UploadType;
import com.wl4g.rengine.common.exception.ExecutionException;
import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.common.model.EvaluationResult;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext;

import groovy.lang.GroovyClassLoader;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link GroovyScriptEngine}
 * 
 * @author James Wong
 * @version 2022-09-22
 * @since v3.0.0
 */
@Slf4j
@Singleton
public class GroovyScriptEngine extends AbstractScriptEngine {

    @NotNull
    GroovyClassLoader gcl;

    void onStart(@Observes StartupEvent ev) {
        try {
            log.info("Init groovy classloader ...");
            this.gcl = new GroovyClassLoader();
        } catch (Exception e) {
            log.error("Failed to init groovy classloader.", e);
        }
    }

    @PreDestroy
    void destroy() {
        try {
            log.info("Destroy groovy classloader ...");
            this.gcl.close();
        } catch (Exception e) {
            log.error("Failed to destroy groovy classloader.", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public EvaluationResult apply(Evaluation model) {
        final String scriptMain = model.getScripting().getMainFun();
        try {
            final List<String> scripts = loadScripts(UploadType.USER_LIBRARY_WITH_GROOVY, model);

            // TODO merge(import?) scripts all.
            final Class<?> cls = gcl.parseClass(scripts.get(0), scriptMain);
            log.info("Load groovy script class: {}", cls);

            log.info("Instantiating groovy script class with {} ...", scriptMain);
            final Function<ScriptContext, String> function = (Function<ScriptContext, String>) cls.getConstructor().newInstance();
            log.info("Instantiated script class object: {}, is instance of java.util.function.Function: {}", function,
                    (function instanceof Function));

            log.info("Execution groovy script with {} ...", scriptMain);
            final var result = function.apply(newScriptContext(model));
            log.info("Execution groovy script: {}, result: {}", scriptMain, result.toString());

            // TODO re-definition result model structure
            return EvaluationResult.GenericEvaluationResult.builder().result(result).build();
        } catch (Exception e) {
            throw new ExecutionException(format("Failed to execution '%s' with Groovy engine.", scriptMain), e);
        }
    }

}
