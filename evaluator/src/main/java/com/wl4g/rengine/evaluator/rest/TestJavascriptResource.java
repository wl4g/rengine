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
package com.wl4g.rengine.evaluator.rest;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import com.google.common.annotations.VisibleForTesting;
import com.wl4g.infra.common.graalvm.GraalPolyglotManager.ContextWrapper;
import com.wl4g.infra.common.lang.EnvironmentUtil;
import com.wl4g.infra.common.runtime.JvmRuntimeTool;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.evaluator.execution.engine.GraalJSScriptEngine;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext.ScriptEventLocation;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext.ScriptEventSource;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext.ScriptRengineEvent;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptHttpClient;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptResult;
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
 * @since v1.0.0
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
@VisibleForTesting
public class TestJavascriptResource {

    // 注: 同一 Context 实例不允许多线程并发调用.
    @Inject
    @NotNull
    GraalJSScriptEngine jsScriptEngine;

    // 已过时 Java11+ 计划移除.
    // graal.js, nashorn
    // @Inject
    // @NotNull
    // ScriptEngineManager scriptEngineManager;

    void onStart(@Observes StartupEvent event) {
        init();
    }

    // @PostConstruct
    void init() {
        // try {
        // log.info("Initialzing script engine manager ...");
        // scriptEngineManager = new ScriptEngineManager();
        // } catch (Exception e) {
        // log.error("Failed to init script engine manager.", e);
        // }
    }

    @POST
    @Path("/execution")
    public RespBase<Object> execution(TestJavascriptExecution model) {
        log.info("called: JSScript execution ... {}", model);

        // Limiting test process.
        if (!JvmRuntimeTool.isJvmInDebugging && !EnvironmentUtil.getBooleanProperty("test.rest", false)) {
            return RespBase.create().withMessage("Rejected test rest");
        }

        long begin = currentTimeMillis();
        try (ContextWrapper context = jsScriptEngine.getGraalPolyglotManager().getContext();) {
            log.info("JSScript execution ...");
            System.out.println(format("cost(newContext): %sms", (currentTimeMillis() - begin)));

            begin = currentTimeMillis();
            String codes = Files.readString(Paths.get(URI.create(model.getScriptPath())), Charset.forName("UTF-8"));
            System.out.println(format("cost(loadJSScript): %sms", (currentTimeMillis() - begin)));
            log.info("Loaded JSScript codes: {}", codes);

            ScriptRengineEvent event = new ScriptRengineEvent("generic_device_temp_warning",
                    ScriptEventSource.builder()
                            .time(currentTimeMillis())
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
                    .args(ProxyObject.fromMap(model.getArgs()))
                    .event(event)
                    .attributes(ProxyObject.fromMap(attributes))
                    .build();

            begin = currentTimeMillis();
            context.eval(Source.newBuilder("js", codes, "test-js2java.js").build());
            System.out.println(format("cost(eval): %sms", (currentTimeMillis() - begin)));

            begin = currentTimeMillis();
            Value bindings = context.getBindings("js");
            System.out.println(format("cost(getBindings): %sms", (currentTimeMillis() - begin)));

            begin = currentTimeMillis();
            bindings.putMember(ScriptHttpClient.class.getSimpleName(), ScriptHttpClient.class);
            bindings.putMember(ScriptResult.class.getSimpleName(), ScriptResult.class);
            System.out.println(format("cost(putMember): %sms", (currentTimeMillis() - begin)));

            begin = currentTimeMillis();
            Value processFunction = bindings.getMember("process");
            System.out.println(format("cost(getMember): %sms", (currentTimeMillis() - begin)));

            begin = currentTimeMillis();
            Value resultValue = processFunction.execute(scriptContext);
            System.out.println(format("cost(execute): %sms", (currentTimeMillis() - begin)));

            log.info("JSScript execution resultValue: {}, scriptContext.getAttributes().getMembe('key1'): {}", resultValue,
                    scriptContext.getAttributes().getMember("key1"));

            ScriptResult result = resultValue.as(ScriptResult.class);
            log.info("JSScript execution result: {}", result);

            return RespBase.create().withData(result);
        } catch (Throwable e) {
            log.error("Failed to excution JSScript.", e);
            return RespBase.create().withCode(500).withMessage(format("Failed to execution JSScript: %s", e.getMessage()));
        }
    }

    @Data
    @NoArgsConstructor
    @SuperBuilder
    public static class TestJavascriptExecution {
        @NotBlank
        String scriptPath;
        @NotEmpty
        Map<String, Object> args;
    }

}
