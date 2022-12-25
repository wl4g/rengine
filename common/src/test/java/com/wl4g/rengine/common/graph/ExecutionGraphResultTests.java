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
package com.wl4g.rengine.common.graph;

import org.junit.Test;

import com.wl4g.rengine.common.graph.ExecutionGraphResult.ReturnState;

/**
 * {@link ExecutionGraphResultTests}
 * 
 * @author James Wong
 * @version 2022-12-25
 * @since v1.0.0
 */
public class ExecutionGraphResultTests {

    @Test
    public void testAddValues() {
        final ExecutionGraphResult result = new ExecutionGraphResult(ReturnState.FALSE).addValue("foo", "bar")
                .addValue("foo1", null)
                .addValue("foo2", null)
                .addValue("foo3", null);
        System.out.println(result);
    }

}
