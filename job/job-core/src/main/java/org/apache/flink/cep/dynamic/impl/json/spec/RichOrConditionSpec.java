/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.cep.dynamic.impl.json.spec;

import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.cep.pattern.conditions.RichOrCondition;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** This class is to (de)serialize {@link RichOrCondition} in json format. */
public class RichOrConditionSpec extends RichCompositeConditionSpec {
    public RichOrConditionSpec(@JsonProperty("nestedConditions") List<ConditionSpec> nestedConditions) {
        super(RichOrCondition.class.getCanonicalName(), nestedConditions);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public IterativeCondition<?> toIterativeCondition(ClassLoader classLoader) throws Exception {
        return new RichOrCondition(this.getNestedConditions().get(0).toIterativeCondition(classLoader),
                this.getNestedConditions().get(1).toIterativeCondition(classLoader));
    }
}
