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
package com.wl4g.rengine.executor.rest;

import static com.google.common.base.Charsets.UTF_8;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.Resources;
import com.wl4g.infra.common.lang.EnvironmentUtil;
import com.wl4g.infra.common.runtime.JvmRuntimeTool;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.executor.rest.intercept.CustomValid;

import groovy.lang.GroovyClassLoader;
import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link TestGroovyResource}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v1.0.0
 */
@Slf4j
@Path("/test/groovy")
@CustomValid
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SuppressWarnings("unchecked")
// 注1: 当编译为native运行时, 必须显示指定单例, 否则方法体中使用成员属性会空指针. (但使用JVM运行时却不会?)
@Singleton
@VisibleForTesting
public class TestGroovyResource {

    @NotNull
    GroovyClassLoader gcl;

    @PostConstruct
    void init() {
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

    @POST
    @Path("/execution")
    public RespBase<Object> execution(TestGroovyExecution model) throws Throwable {
        log.info("called: groovy execution ...");

        // Limiting test process.
        if (!JvmRuntimeTool.isJvmInDebugging && !EnvironmentUtil.getBooleanProperty("test.rest", false)) {
            return RespBase.create().withMessage("Rejected test rest");
        }

        try {
            log.info("Groovy script path: {}", model.getScriptPath());

            String codes = Resources.toString(URI.create(model.getScriptPath()).toURL(), UTF_8);
            log.info("Groovy class codes: {}", codes);

            final Class<?> cls = gcl.parseClass(codes, model.getScriptPath().substring(model.getScriptPath().lastIndexOf("/")));
            log.info("Load groovy class: {}", cls);

            final Function<List<String>, String> obj = (Function<List<String>, String>) cls.getConstructor().newInstance();
            log.info("Instantiated groovy class object: {}, is instance of java.util.function.Function: {}", obj,
                    (obj instanceof Function));

            final var result = obj.apply(model.getArgs());
            log.info("Groovy execution result: {}", result);

            return RespBase.create().withData(result);
        } catch (Throwable e) {
            log.error("Failed to excution groovy script.", e);
            throw e;
        }

    }

    @Getter
    @Setter
    @ToString
    public static class TestGroovyExecution {
        @NotBlank
        String scriptPath;
        @NotEmpty
        List<String> args;
    }

}
