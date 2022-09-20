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
package com.wl4g.rengine.evaluator.rest;

import static com.wl4g.infra.common.lang.Assert2.notNull;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.graalvm.polyglot.Context;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.evaluator.rest.interceptor.CustomValid;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link TestJavascriptResource}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v3.0.0
 * @see https://github.com/graalvm/graal-js-jdk11-maven-demo
 * @see https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/prog_guide/api.html
 */
@Slf4j
@Path("/test/javascript")
@CustomValid
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestJavascriptResource {

    @NotNull
    Context context;

    @NotNull
    ScriptEngineManager engineManager;

    @PostConstruct
    void init() {
        try {
            log.info("Init graalvm context ...");
            this.context = Context.create();
            this.engineManager = new ScriptEngineManager();
        } catch (Exception e) {
            log.error("Failed to init graalvm context.", e);
        }
    }

    @PreDestroy
    void destroy() {
        try {
            log.info("Destroy graalvm context ...");
            this.context.close();
        } catch (Exception e) {
            log.error("Failed to destroy graalvm context.", e);
        }
    }

    @POST
    @Path("/execution")
    public RespBase<Object> execution(JavascriptExecution model) throws Throwable {
        log.info("called: Javascript execution ...");

        try {
            log.info("Javascript script path: {}", model.getScriptPath());

            String codes = Files.readString(Paths.get(URI.create(model.getScriptPath())), Charset.forName("UTF-8"));
            log.info("Load Javascript codes: {}", codes);

            ScriptEngine engine = engineManager.getEngineByName(model.getScriptEngine());
            notNull(engine, "Cannot found script engine by %s", model.getScriptEngine());
            log.info("Load Javascript engine: {}", engine);

            engine.eval(codes);
            final var result = ((Invocable) engine).invokeFunction(model.getScriptMain());
            log.info("Javascript execution result: {}", result);

            return RespBase.create().withData(result);
        } catch (Throwable e) {
            log.error("Failed to excution Javascript script.", e);
            throw e;
        }

    }

    @Data
    @NoArgsConstructor
    @SuperBuilder
    public static class JavascriptExecution {
        @NotBlank
        String scriptPath;
        @NotBlank
        String scriptEngine; // graal.js, nashorn
        @NotBlank
        String scriptMain;
        @NotEmpty
        List<String> args;
    }

}
