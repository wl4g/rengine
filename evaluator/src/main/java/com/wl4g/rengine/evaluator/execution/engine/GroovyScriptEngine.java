///*
// * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.rengine.evaluator.execution.engine;
//
//import static java.lang.String.format;
//
//import java.util.function.Function;
//
//import javax.annotation.PreDestroy;
//import javax.enterprise.event.Observes;
//import javax.inject.Singleton;
//import javax.validation.constraints.NotNull;
//
//import com.wl4g.infra.common.lang.StringUtils2;
//import com.wl4g.rengine.common.entity.Scenes;
//import com.wl4g.rengine.common.entity.UploadObject.UploadType;
//import com.wl4g.rengine.common.exception.ExecutionException;
//import com.wl4g.rengine.common.model.Evaluation;
//import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext;
//import com.wl4g.rengine.evaluator.execution.sdk.ScriptResult;
//import com.wl4g.rengine.evaluator.minio.MinioManager.ObjectResource;
//
//import groovy.lang.GroovyClassLoader;
//import io.quarkus.runtime.StartupEvent;
//import lombok.CustomLog;
//import lombok.Getter;
//
///**
// * {@link GroovyScriptEngine}
// * 
// * @author James Wong
// * @version 2022-09-22
// * @since v1.0.0
// */
//@CustomLog
//@Getter
//@Singleton
//public class GroovyScriptEngine extends AbstractScriptEngine {
//
//    @NotNull
//    GroovyClassLoader gcl;
//
//    void onStart(@Observes StartupEvent ev) {
//        try {
//            log.info("Init groovy classloader ...");
//            this.gcl = new GroovyClassLoader();
//        } catch (Exception e) {
//            log.error("Failed to init groovy classloader.", e);
//        }
//    }
//
//    @PreDestroy
//    void destroy() {
//        try {
//            log.info("Destroy groovy classloader ...");
//            this.gcl.close();
//        } catch (Exception e) {
//            log.error("Failed to destroy groovy classloader.", e);
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public ScriptResult execute(final Evaluation evaluation, final Scenes scenes) {
//        log.debug("Execution JS script for scenesCode: {} ...", scenes.getScenesCode());
//
//        try {
//            // Load all scripts dependencies.
//            Function<ScriptContext, String> mainFunction = null;
//            for (ObjectResource script : loadScriptResources(UploadType.USER_LIBRARY_WITH_JS, scenes, true)) {
//                if (log.isDebugEnabled()) {
//                    log.debug("Parsing groovy dependency: {}", script.getObjectPrefix());
//                }
//                // TODO handcode for 'process' ???
//                String scriptName = StringUtils2.getFilename(script.getObjectPrefix()).concat("@").concat(DEFAULT_MAIN_FUNCTION);
//                try {
//                    // TODO merge depends library
//                    if (script.isBinary()) {
//                        // TODO load jar file
//                        gcl.addClasspath(script.getLocalFile().getAbsolutePath());
//                    } else {
//                        Class<?> cls = gcl.parseClass(script.readToString(), DEFAULT_MAIN_FUNCTION);
//
//                        if (log.isDebugEnabled()) {
//                            log.debug("Instantiating Groovy library main: {} ...", DEFAULT_MAIN_FUNCTION);
//                        }
//
//                        // TODO determine main function
//                        mainFunction = (Function<ScriptContext, String>) cls.getConstructor().newInstance();
//                        if (log.isDebugEnabled()) {
//                            log.debug("Execution Groovy libary main: {} with object: {} ...", DEFAULT_MAIN_FUNCTION,
//                                    mainFunction);
//                        }
//                    }
//                } catch (Throwable e) {
//                    throw new ExecutionException(evaluation.getRequestId(), scenes.getScenesCode(),
//                            format("Failed to load groovy dependency '%s'", scriptName), e);
//                }
//            }
//
//            // TODO
//            final String result = mainFunction.apply(newScriptContext(evaluation));
//            log.debug("Executed Groovy script main: {}, result: {}", DEFAULT_MAIN_FUNCTION, result.toString());
//            // TODO re-wrap result object
//            return new ScriptResult(null);
//        } catch (Exception e) {
//            throw new ExecutionException(evaluation.getRequestId(), scenes.getScenesCode(),
//                    format("Failed to execution Groovy script for scenesCode: %s", scenes.getScenesCode()), e);
//        }
//    }
//
//}
