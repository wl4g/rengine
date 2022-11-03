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
package com.wl4g.rengine.client.springboot.intercept;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

/**
 * {@link DefaultREvaluationHandlerTests}
 * 
 * @author James Wong
 * @version 2022-11-03
 * @since v3.0.0
 */
public class DefaultREvaluationHandlerTests {

    @Test
    public void testParseParamsTemplate() {
        List<Object> arguments = new ArrayList<>();
        arguments.add("u100101");
        arguments.add("G202202082139942");
        arguments.add("2");

        String paramsTemplate = "{{ userId=#0, goodId=#1 }}";
        Map<String, String> args = DefaultREvaluationHandler.parseParamsTemplate(arguments, paramsTemplate);
        System.out.println(args);
        Assertions.assertEquals(args.get("goodId"), "G202202082139942");
        Assertions.assertEquals(args.get("userId"), "u100101");
        Assertions.assertEquals(args.get("count"), null);
    }

    @Test(expected = Throwable.class)
    public void testParseParamsTemplateFail() {
        List<Object> arguments = new ArrayList<>();
        String paramsTemplate = "{{ userId=0, goodId=1 }}";
        DefaultREvaluationHandler.parseParamsTemplate(arguments, paramsTemplate);

        String paramsTemplate2 = "userId=1, goodId=0";
        DefaultREvaluationHandler.parseParamsTemplate(arguments, paramsTemplate2);

        String paramsTemplate3 = "{{ userId=#0, goodId=#0 }}";
        DefaultREvaluationHandler.parseParamsTemplate(arguments, paramsTemplate3);

        String paramsTemplate4 = "{{ userId=#0, userId=#1 }}";
        DefaultREvaluationHandler.parseParamsTemplate(arguments, paramsTemplate4);
    }

}
