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

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import com.wl4g.infra.common.lang.EnvironmentUtil;
import com.wl4g.infra.common.runtime.JvmRuntimeTool;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext.ScriptEventLocation;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext.ScriptEventSource;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext.ScriptRengineEvent;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptHttpClient;
import com.wl4g.rengine.evaluator.rest.interceptor.CustomValid;

import io.quarkus.runtime.StartupEvent;
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
// 注1: 当编译为native运行时, 必须显示指定单例, 否则方法体中使用成员属性会空指针. (但使用JVM运行时却不会?)
@Singleton
public class TestJavascriptResource {

    @NotNull
    Context singletonContext; // 同一 Context 实例不允许多线程并发调用.

    // graal.js, nashorn
    // @NotNull
    // ScriptEngineManager engineManager;

    void onStart(@Observes StartupEvent event) {
        init();
    }

    // @PostConstruct
    void init() {
        try {
            log.info("Initialzing graalvm polyglot singleton context ...");
            singletonContext = Context.create();
        } catch (Exception e) {
            log.error("Failed to init graalvm polyglot singleton context.", e);
        }
        // try {
        // log.info("Initialzing script engine manager ...");
        // engineManager = new ScriptEngineManager();
        // } catch (Exception e) {
        // log.error("Failed to init script engine manager.", e);
        // }
    }

    @PreDestroy
    void destroy() {
        try {
            log.info("Destroy graalvm polyglot singleton context ...");
            singletonContext.close();
        } catch (Exception e) {
            log.error("Failed to destroy graalvm polyglot context.", e);
        }
    }

    @POST
    @Path("/execution")
    public RespBase<Object> execution(JavascriptExecution model) throws Throwable {
        log.info("called: Javascript execution ... {}", model);

        // Limiting test process.
        if (!JvmRuntimeTool.isJvmInDebugging && !EnvironmentUtil.getBooleanProperty("test.rest", false)) {
            return RespBase.create().withMessage("Limited test process");
        }

        try (Context newContext = Context.create();) {
            log.info("Javascript script ...");
            String codes = Files.readString(Paths.get(URI.create(model.getScriptPath())), Charset.forName("UTF-8"));
            log.info("Loaded Javascript codes: {}", codes);

            Context c = Context.newBuilder("js").allowAllAccess(true).build();
            Value bindings = c.getBindings("js");

            ScriptRengineEvent event = new ScriptRengineEvent("generic_device_temp_warning",
                    ScriptEventSource.builder()
                            .sourceTime(currentTimeMillis())
                            .principals(singletonList("admin"))
                            .location(ScriptEventLocation.builder().zipcode("20500").build())
                            .build(),
                    "A serious alarm occurs when the device temperature is greater than 52℃", singletonMap("objId", "1010012"));

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("objId", "1010012");
            attributes.put("remark", "The test js call to java ...");

            ScriptContext scriptContext = ScriptContext.builder()
                    .id("100101")
                    .type("iot_warning")
                    .args(model.getArgs())
                    .event(event)
                    .attributes(ProxyObject.fromMap(attributes))
                    .build();

            bindings.putMember("httpClient", new ScriptHttpClient());
            c.eval(Source.newBuilder("js", codes, "test-js2java.js").build());

            Value processFunction = c.getBindings("js").getMember("process");
            Value result = processFunction.execute(scriptContext);
            log.info("Javascript execution result: {}", result);

            return RespBase.create().withData(result.toString());
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
        @NotEmpty
        List<String> args;
    }

}