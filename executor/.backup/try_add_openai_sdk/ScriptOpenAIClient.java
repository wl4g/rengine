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
package com.wl4g.rengine.executor.execution.sdk;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_client_failure;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_client_success;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_client_time;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_client_total;

import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.infra.common.remoting.HttpEntity;
import com.wl4g.infra.common.remoting.standard.HttpHeaders;
import com.wl4g.rengine.executor.meter.MeterUtil;

import lombok.ToString;

/**
 * {@link ScriptOpenAIClient}
 * 
 * @author James Wong
 * @date 2022-09-25
 * @since v1.0.0
 */
@ToString
public class ScriptOpenAIClient extends ScriptHttpClient {
    final static String METHOD_GET_FOR_TEXT = "getForText";

    public @HostAccess.Export ScriptOpenAIClient() {
        super();
    }

    public @HostAccess.Export ScriptOpenAIClient(Integer connectTimeout, Integer readTimeout, Integer maxResponseSize) {
        super(false, connectTimeout, readTimeout, maxResponseSize);
    }

    public @HostAccess.Export ScriptOpenAIClient(@NotNull Boolean debug,
            @NotNull @Min(DEFAULT_MIN_CONNECT_TIMEOUT) @Max(DEFAULT_MAX_CONNECT_TIMEOUT) Integer connectTimeout,
            @NotNull @Min(DEFAULT_MIN_READ_TIMEOUT) @Max(DEFAULT_MAX_READ_TIMEOUT) Integer readTimeout,
            @NotNull @Min(DEFAULT_MIN_RESPONSE_SIZE) @Max(DEFAULT_MAX_RESPONSE_SIZE) Integer maxResponseSize) {
        super(debug, connectTimeout, readTimeout, maxResponseSize);
    }

    /**
     * for examples:
     * 
     * <pre>
     * curl -s https://api.openai.com/v1/completions \
     *   -H "Content-Type: application/json" \
     *   -H "Authorization: Bearer $OPENAI_API_KEY" \
     *   -d '{
     *   "model": "text-curie-001",
     *   "prompt": "Human: Quarkus 2.12 throws error for Thread[vert.x-eventloop-thread-2,5,main] has been blocked for 19170 ms, time limit is 2000 ms?",
     *   "temperature": 0.9,
     *   "max_tokens": 150,
     *   "top_p": 1,
     *   "frequency_penalty": 0,
     *   "presence_penalty": 0.6,
     *   "stop": [" Human:", " AI:"]
     * }' | jq
     * </pre>
     */
    public @HostAccess.Export String completions(final @NotBlank String brerarToken, final @NotNull Map<String, Object> request) {
        hasTextOf(brerarToken, "brerarToken");
        MeterUtil.counter(execution_sdk_client_total, ScriptOpenAIClient.class, METHOD_GET_FOR_TEXT);

        try {
            final String result = MeterUtil.timer(execution_sdk_client_time, ScriptOpenAIClient.class, METHOD_GET_FOR_TEXT,
                    () -> {
                        final HttpHeaders headers = new HttpHeaders();
                        final HttpEntity<String> entity = new HttpEntity<>(headers);
                        exchange("", brerarToken, request, request);
                        return null;
                    });

            MeterUtil.counter(execution_sdk_client_success, ScriptOpenAIClient.class, METHOD_GET_FOR_TEXT);
            return result;
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_client_failure, ScriptOpenAIClient.class, METHOD_GET_FOR_TEXT);
            throw e;
        }
    }

}
