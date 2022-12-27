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
package com.wl4g.rengine.client.springboot.intercept;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

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
        TestCreateOrderDTO dto = TestCreateOrderDTO.builder().userId("u100101").goodId("G202202082139942").address("").build();
        List<Object> arguments = new ArrayList<>();
        arguments.add(dto);
        arguments.add(2);

        String paramsTemplate = "{{userId=#0.userId, goodId=#0.goodId, count=#1}}";
        Map<String, Object> args = DefaultREvaluationHandler.parseParamsTemplate(arguments, defaultMethodSignature,
                paramsTemplate);
        System.out.println(args);
        Assertions.assertEquals(args.get("goodId"), "G202202082139942");
        Assertions.assertEquals(args.get("userId"), "u100101");
        Assertions.assertEquals(args.get("count"), 2);
    }

    @Test(expected = Throwable.class)
    public void testParseParamsTemplateFail() {
        List<Object> arguments = new ArrayList<>();
        arguments.add("u100101");
        arguments.add("G202202082139942");
        arguments.add("2");
        String paramsTemplate = "{{ userId=0, goodId=1 }}";
        DefaultREvaluationHandler.parseParamsTemplate(arguments, defaultMethodSignature, paramsTemplate);

        String paramsTemplate2 = "userId=1, goodId=0";
        DefaultREvaluationHandler.parseParamsTemplate(arguments, defaultMethodSignature, paramsTemplate2);

        String paramsTemplate3 = "{{ userId=#0, goodId=#0 }}";
        DefaultREvaluationHandler.parseParamsTemplate(arguments, defaultMethodSignature, paramsTemplate3);

        String paramsTemplate4 = "{{ userId=#0, userId=#1 }}";
        DefaultREvaluationHandler.parseParamsTemplate(arguments, defaultMethodSignature, paramsTemplate4);
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    static class TestCreateOrderDTO {
        private String userId;
        private String goodId;
        private String address;
    }

    static class TestOrderServiceImpl {
        public Map<String, String> create2(TestCreateOrderDTO order, Integer count) {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    static MethodSignature defaultMethodSignature = new MethodSignature() {

        @Override
        public String toShortString() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String toLongString() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getName() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getModifiers() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public String getDeclaringTypeName() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Class getDeclaringType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Class[] getParameterTypes() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String[] getParameterNames() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Class[] getExceptionTypes() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Class getReturnType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Method getMethod() {
            return TestOrderServiceImpl.class.getMethods()[0];
        }
    };

}
