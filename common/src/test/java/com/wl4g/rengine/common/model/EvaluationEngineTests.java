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
package com.wl4g.rengine.common.model;

import static com.wl4g.infra.common.reflect.ReflectionUtils2.findMethodNullable;
import static java.util.Arrays.asList;

import org.junit.Test;

import com.wl4g.rengine.common.entity.Rule.RuleEngine;

/**
 * {@link EvaluationEngine}
 * 
 * @author James Wong
 * @date 2022-10-10
 * @since v1.0.0
 */
public class EvaluationEngineTests {

    @SuppressWarnings("rawtypes")
    @Test
    public void testReflectGetEnumNameMethod() throws Exception {
        Class<?> cls = RuleEngine.class;

        System.out.println(findMethodNullable(cls, "name"));
        System.out.println(cls.getMethod("name"));

        Enum[] constants = (Enum[]) cls.getEnumConstants();
        System.out.println(asList(constants));
        System.out.println(constants[0].name());
    }

}
