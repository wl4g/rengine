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

package org.apache.flink.cep.dynamic.impl.json.deserializer;

import org.apache.flink.cep.dynamic.impl.json.spec.AviatorConditionSpec;
import org.apache.flink.cep.dynamic.impl.json.spec.ClassConditionSpec;
import org.apache.flink.cep.dynamic.impl.json.spec.ConditionSpec;
import org.apache.flink.cep.dynamic.impl.json.spec.RichAndConditionSpec;
import org.apache.flink.cep.dynamic.impl.json.spec.RichNotConditionSpec;
import org.apache.flink.cep.dynamic.impl.json.spec.RichOrConditionSpec;
import org.apache.flink.cep.dynamic.impl.json.spec.SubTypeConditionSpec;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParser;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** The customized StdDeserializer for ConditionSpec. */
public class ConditionSpecStdDeserializer extends StdDeserializer<ConditionSpec> {

    public static final ConditionSpecStdDeserializer INSTANCE = new ConditionSpecStdDeserializer();
    private static final long serialVersionUID = 1L;

    public ConditionSpecStdDeserializer() {
        this(null);
    }

    public ConditionSpecStdDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ConditionSpec deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        ConditionSpec.ConditionType type = ConditionSpec.ConditionType.valueOf(node.get("type").asText());
        if (type.equals(ConditionSpec.ConditionType.CLASS)) {
            String className = node.get("className").asText();
            if (node.get("nestedConditions") != null) {
                List<ConditionSpec> nestedConditions = new ArrayList<>();
                Iterator<JsonNode> embeddedElementNames = node.get("nestedConditions").elements();
                while (embeddedElementNames.hasNext()) {
                    JsonNode jsonNode = embeddedElementNames.next();
                    ConditionSpec embedNode = jsonParser.getCodec().treeToValue(jsonNode, ConditionSpec.class);
                    nestedConditions.add(embedNode);
                }
                if (className.endsWith("flink.cep.pattern.conditions.RichAndCondition")) {
                    return new RichAndConditionSpec(nestedConditions);
                } else if (className.endsWith("flink.cep.pattern.conditions.RichOrCondition")) {
                    return new RichOrConditionSpec(nestedConditions);
                } else if (className.endsWith("flink.cep.pattern.conditions.RichNotCondition")) {
                    return new RichNotConditionSpec(nestedConditions);
                }
            } else if (node.get("subClassName") != null) {
                return new SubTypeConditionSpec(className, node.get("subClassName").asText());
            }
            return new ClassConditionSpec(className);
        } else if (type.equals(ConditionSpec.ConditionType.AVIATOR)) {
            if (node.get("expression") != null) {
                return new AviatorConditionSpec(node.get("expression").asText());
            } else {
                throw new IllegalArgumentException("The expression field of Aviator Condition cannot be null!");
            }
        }
        throw new IllegalStateException("Unsupported Condition type: " + type);
    }
}
