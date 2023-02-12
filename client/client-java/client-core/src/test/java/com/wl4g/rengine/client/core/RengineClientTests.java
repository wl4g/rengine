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
package com.wl4g.rengine.client.core;

import static java.lang.System.out;
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
import com.wl4g.rengine.common.model.WorkflowExecuteResult;
import com.wl4g.rengine.common.model.WorkflowExecuteResult.ResultDescription;
import com.wl4g.rengine.common.util.IdGenUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link RengineClientTests}
 * 
 * @author James Wong
 * @version 2022-10-17
 * @since v3.0.0
 */
@Slf4j
public class RengineClientTests {

    ClientConfig invalidClientConfig = ClientConfig.builder()
            .endpoint(URI.create("http://localhost:12345"))
            .clientId("iot-mqttcollector01")
            .clientSecret("abcdefghijklmnopqrstuvwxyz")
            .build();

    WorkflowExecuteResult defaultFailbackResult = WorkflowExecuteResult.builder()
            .results(singletonList(ResultDescription.builder().scenesCode("s1001001").build()))
            .build();

    RengineClient defaultClient;

    @Before
    public void setupDefault() {
        this.defaultClient = RengineClient.builder()
                .config(ClientConfig.builder()
                        .endpoint(URI.create("http://localhost:28002"))
                        .clientId("iot-mqttcollector01")
                        .clientSecret("abcdefghijklmnopqrstuvwxyz")
                        .build())
                .defaultFailback(f -> {
                    log.error("Failed to evaluation of reason: ", f.getError().getMessage());
                    return null;
                })
                .build();
    }

    @Test
    public void testNewRengineClientExecutionWithDefault() {
        Map<String, Object> args = new HashMap<>();
        final WorkflowExecuteResult result = defaultClient.execute(singletonList("ecommerce_trade_gift"), args);
        out.println("Executed result: " + result);
    }

    @Test(expected = RengineException.class)
    public void testNewRengineClientExecutionWithTimeoutFail() {
        RengineClient timeoutClient = RengineClient.builder().config(invalidClientConfig).defaultFailback(f -> {
            out.println("\nFailed to evaluation of reason: " + f.getError().getMessage());
            return defaultFailbackResult;
        }).build();

        final WorkflowExecuteResult result = timeoutClient.execute(IdGenUtils.next(), singletonList("ecommerce_trade_gift"), 1000L, false,
                emptyMap());
        out.println("Executed result: " + result);
    }

    @Test
    public void testNewRengineClientExecutionWithTimeoutSuccess() {
        RengineClient timeoutClient = RengineClient.builder().config(invalidClientConfig).defaultFailback(f -> {
            out.println("\nFailed to evaluation of reason: " + f.getError().getMessage());
            return defaultFailbackResult;
        }).build();

        final var result = timeoutClient.execute(IdGenUtils.next(), singletonList("ecommerce_trade_gift"), 1000L, true,
                emptyMap());
        out.println("Executed result: " + result);
        Assertions.assertEquals(defaultFailbackResult, result);
    }

}
