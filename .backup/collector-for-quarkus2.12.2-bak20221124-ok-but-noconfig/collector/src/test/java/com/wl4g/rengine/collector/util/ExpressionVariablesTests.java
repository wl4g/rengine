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
package com.wl4g.rengine.collector.util;

import static java.util.Collections.singletonMap;

import org.junit.Test;

import com.wl4g.infra.common.lang.DateUtils2;

import io.quarkus.qute.Engine;
import io.quarkus.qute.ReflectionValueResolver;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateExtension;

/**
 * {@link ExpressionVariablesTests}
 * 
 * @author James Wong
 * @date 2022-11-25
 * @since v1.0.0
 */
public class ExpressionVariablesTests {

    @Test
    public void testQuteRender() {
        Engine engine = Engine.builder().addDefaults().build();
        Template helloTemplate = engine.parse("Hello, {user.name}");
        final String result = helloTemplate.data("user", singletonMap("name", "Jim")).render();
        System.out.println(result);
    }

    @Test
    public void testQuteRenderWithDynamicMethod() {
        Engine engine = Engine.builder().addDefaults().addValueResolver(new ReflectionValueResolver()).build();
        try {
            Template helloTemplate = engine.parse("The now date is: {tool.getDate}");
            final String result = helloTemplate.data("tool", new MyTool()).render();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TemplateExtension(namespace = "date")
    // @TemplateGlobal
    static class MyTemplateExtensions {
        @TemplateExtension
        static String getDate() {
            return DateUtils2.getDate("yyyyMMdd");
        }

        @TemplateExtension
        static String getDate(MyTool tool) {
            return DateUtils2.getDate("yyyyMMdd");
        }

        static String getDate(String pattern) {
            return DateUtils2.getDate(pattern);
        }
    }

    static class MyTool {
    }

}
