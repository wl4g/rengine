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

package org.apache.flink.cep.dynamic.impl.json.util;

import org.apache.flink.cep.dynamic.impl.json.deserializer.ConditionSpecStdDeserializer;
import org.apache.flink.cep.dynamic.impl.json.deserializer.NodeSpecStdDeserializer;
import org.apache.flink.cep.dynamic.impl.json.deserializer.TimeStdDeserializer;
import org.apache.flink.cep.dynamic.impl.json.spec.ConditionSpec;
import org.apache.flink.cep.dynamic.impl.json.spec.GraphSpec;
import org.apache.flink.cep.dynamic.impl.json.spec.NodeSpec;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.flink.streaming.api.windowing.time.Time;

import lombok.CustomLog;

/**
 * {@link CepJsonUtils}
 * 
 * @author James Wong
 * @version 2023-02-22
 * @since v1.0.0
 */
@CustomLog
public class CepJsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new SimpleModule().addDeserializer(ConditionSpec.class, ConditionSpecStdDeserializer.INSTANCE)
                    .addDeserializer(Time.class, TimeStdDeserializer.INSTANCE)
                    .addDeserializer(NodeSpec.class, NodeSpecStdDeserializer.INSTANCE));

    public static Pattern<?, ?> toPattern(String jsonString) {
        return toPattern(jsonString, Thread.currentThread().getContextClassLoader());
    }

    public static Pattern<?, ?> toPattern(String jsonString, ClassLoader userCodeClassLoader) {
        try {
            if (userCodeClassLoader == null) {
                log.warn("The given userCodeClassLoader is null. Will try to use ContextClassLoader of current thread.");
                return toPattern(jsonString);
            }
            GraphSpec deserializedGraphSpec = objectMapper.readValue(jsonString, GraphSpec.class);
            return deserializedGraphSpec.toPattern(userCodeClassLoader);
        } catch (Throwable ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static GraphSpec toGraphSpec(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, GraphSpec.class);
        } catch (Throwable ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static String toJson(GraphSpec graphSpec) {
        try {
            return objectMapper.writeValueAsString(graphSpec);
        } catch (Throwable ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static String toJson(Pattern<?, ?> pattern) {
        try {
            GraphSpec graphSpec = GraphSpec.fromPattern(pattern);
            return objectMapper.writeValueAsString(graphSpec);
        } catch (Throwable ex) {
            throw new IllegalArgumentException(ex);
        }
    }

}
