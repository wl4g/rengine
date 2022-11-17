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
package com.wl4g.rengine.job.cep;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * {@link CepDynamicPatternTests}
 * 
 * @author James Wong
 * @version 2022-11-21
 * @since v1.0.0
 */
public class CepDynamicPatternTests {

    @SuppressWarnings("rawtypes")
    @Test
    public void testSimpleDeserialzePattern() {
        Pattern<JsonNode, JsonNode> loginFailPattern = Pattern.begin("begin");
        loginFailPattern = loginFailPattern.where(new SimpleCondition<JsonNode>() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean filter(JsonNode value) throws Exception {
                return value.at("/eventType").asText().equalsIgnoreCase("fail");
            }
        }).followedBy("next1").where(new SimpleCondition<JsonNode>() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean filter(JsonNode value) throws Exception {
                return value.at("/eventType").asText().equalsIgnoreCase("fail");
            }
        }).within(Time.milliseconds(1001)).oneOrMore();

        String patternJson = toJSONString(loginFailPattern, true);
        System.out.println(patternJson);

        // TODO use
        // vvr-flink-cep:org.apache.flink.cep.dynamic.impl.json.util.CepJsonUtils???
        Pattern deserialzedPattern = parseJSON(patternJson, Pattern.class);
        System.out.println(deserialzedPattern);
    }

}
