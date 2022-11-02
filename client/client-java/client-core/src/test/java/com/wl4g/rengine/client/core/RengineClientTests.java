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
package com.wl4g.rengine.client.core;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.wl4g.rengine.client.core.config.ClientConfig;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.common.model.EvaluationResult;
import com.wl4g.rengine.common.model.EvaluationResult.ResultDescription;

/**
 * {@link RengineClientTests}
 * 
 * @author James Wong
 * @version 2022-10-17
 * @since v3.0.0
 */
public class RengineClientTests {

    ClientConfig invalidClientConfig = ClientConfig.builder()
            .endpoint(URI.create("http://localhost:12345"))
            .clientId("iot-mqttcollector01")
            .clientSecret("abcdefghijklmnopqrstuvwxyz")
            .build();

    EvaluationResult defaultFailbackResult = EvaluationResult.builder()
            .errorCount(2)
            .results(singletonList(ResultDescription.builder().node("wf10010:03").build()))
            .build();

    RengineClient defaultClient;

    @Before
    public void setupDefault() {
        defaultClient = RengineClient.builder()
                .config(ClientConfig.builder()
                        .endpoint(URI.create("http://localhost:28002"))
                        .clientId("iot-mqttcollector01")
                        .clientSecret("abcdefghijklmnopqrstuvwxyz")
                        .build())
                .failback(e -> {
                    System.err.println("Failed to evaluation of reason: ");
                    e.printStackTrace();
                    return null;
                })
                .build();
    }

    @Test
    public void testNewRengineClientEvaluationWithDefault() {
        Map<String, String> args = new HashMap<>();
        final var result = defaultClient.evaluate("iot_temp_warn", args);
        System.out.println("Evaluation result: " + result);
    }

    @Test(expected = RengineException.class)
    public void testNewRengineClientEvaluationWithTimeoutFail() {
        RengineClient timeoutClient = RengineClient.builder().config(invalidClientConfig).failback(e -> {
            System.out.println("\nFailed to evaluation of reason: " + e.getMessage());
            return defaultFailbackResult;
        }).build();

        final var result = timeoutClient.evaluate("iot_temp_warn", 1000L, false, emptyMap());
        System.out.println("Evaluated result: " + result);
    }

    @Test
    public void testNewRengineClientEvaluationWithTimeoutSuccess() {
        RengineClient timeoutClient = RengineClient.builder().config(invalidClientConfig).failback(e -> {
            System.out.println("\nFailed to evaluation of reason: " + e.getMessage());
            return defaultFailbackResult;
        }).build();

        final var result = timeoutClient.evaluate("iot_temp_warn", 1000L, true, emptyMap());
        System.out.println("Evaluated result: " + result);
        Assertions.assertEquals(defaultFailbackResult, result);
    }

}
