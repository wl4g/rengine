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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_client_failure;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_client_success;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_client_time;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_client_total;
import static java.lang.String.format;

import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.HostAccess;

import com.fasterxml.jackson.databind.JsonNode;
import com.wl4g.infra.common.remoting.HttpEntity;
import com.wl4g.infra.common.remoting.HttpResponseEntity;
import com.wl4g.infra.common.remoting.RestClient;
import com.wl4g.infra.common.remoting.standard.HttpHeaders;
import com.wl4g.rengine.executor.metrics.MeterUtil;

import io.netty.handler.codec.http.HttpMethod;
import lombok.ToString;

/**
 * {@link ScriptHttpClient}
 * 
 * @author James Wong
 * @version 2022-09-25
 * @since v1.0.0
 */
@ToString
public class ScriptHttpClient {
    final static String METHOD_GET_FOR_TEXT = "getForText";
    final static String METHOD_POST_FOR_TEXT = "getForText";
    final static String METHOD_GET_FOR_JSON = "getForJson";
    final static String METHOD_POST_FOR_JSON = "postForJson";
    final static String METHOD_EXCHANGE = "exchange";

    final RestClient restClient;

    public @HostAccess.Export ScriptHttpClient() {
        // Default: connectTimeout=6sec, readTimeout=60sec, maxResponseSize=2M
        this(false, 6 * 1000, 60 * 1000, 2 * 1024 * 1024);
    }

    public @HostAccess.Export ScriptHttpClient(Integer connectTimeout, Integer readTimeout, Integer maxResponseSize) {
        this(false, connectTimeout, readTimeout, maxResponseSize);
    }

    public @HostAccess.Export ScriptHttpClient(@NotNull Boolean debug,
            @NotNull @Min(DEFAULT_MIN_CONNECT_TIMEOUT) @Max(DEFAULT_MAX_CONNECT_TIMEOUT) Integer connectTimeout,
            @NotNull @Min(DEFAULT_MIN_READ_TIMEOUT) @Max(DEFAULT_MAX_READ_TIMEOUT) Integer readTimeout,
            @NotNull @Min(DEFAULT_MIN_RESPONSE_SIZE) @Max(DEFAULT_MAX_RESPONSE_SIZE) Integer maxResponseSize) {
        notNullOf(debug, "debug");
        notNullOf(connectTimeout, "connectTimeout");
        notNullOf(readTimeout, "readTimeout");
        notNullOf(maxResponseSize, "maxResponseSize");
        isTrueOf(connectTimeout >= DEFAULT_MIN_CONNECT_TIMEOUT && connectTimeout <= DEFAULT_MAX_CONNECT_TIMEOUT,
                format("connectTimeout>=%s and connectTimeout<=%s", DEFAULT_MIN_CONNECT_TIMEOUT, DEFAULT_MAX_CONNECT_TIMEOUT));
        isTrueOf(readTimeout >= DEFAULT_MIN_READ_TIMEOUT && readTimeout <= DEFAULT_MAX_READ_TIMEOUT,
                format("readTimeout>=%s and readTimeout<=%s", DEFAULT_MIN_READ_TIMEOUT, DEFAULT_MAX_READ_TIMEOUT));
        isTrueOf(maxResponseSize >= DEFAULT_MIN_RESPONSE_SIZE && maxResponseSize <= DEFAULT_MAX_RESPONSE_SIZE,
                format("maxResponseSize>=%s and maxResponseSize<=%s", DEFAULT_MIN_RESPONSE_SIZE, DEFAULT_MAX_RESPONSE_SIZE));
        this.restClient = new RestClient(debug, connectTimeout, readTimeout, maxResponseSize);
    }

    public @HostAccess.Export String getForText(final @NotBlank String url) {
        hasTextOf(url, "url");
        MeterUtil.counter(execution_sdk_client_total, ScriptHttpClient.class, METHOD_GET_FOR_TEXT);

        try {
            final String result = MeterUtil.timer(execution_sdk_client_time, ScriptHttpClient.class, METHOD_GET_FOR_TEXT,
                    () -> restClient.getForObject(url, String.class));

            MeterUtil.counter(execution_sdk_client_success, ScriptHttpClient.class, METHOD_GET_FOR_TEXT);
            return result;
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_client_failure, ScriptHttpClient.class, METHOD_GET_FOR_TEXT);
            throw e;
        }
    }

    public @HostAccess.Export String postForText(final @NotBlank String url, final @NotNull Object request) {
        hasTextOf(url, "url");
        notNullOf(request, "request");
        MeterUtil.counter(execution_sdk_client_total, ScriptHttpClient.class, METHOD_POST_FOR_TEXT);

        try {
            final String result = MeterUtil.timer(execution_sdk_client_time, ScriptHttpClient.class, METHOD_POST_FOR_TEXT,
                    () -> restClient.postForObject(url, request, String.class));

            MeterUtil.counter(execution_sdk_client_success, ScriptHttpClient.class, METHOD_POST_FOR_TEXT);
            return result;
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_client_failure, ScriptHttpClient.class, METHOD_POST_FOR_TEXT);
            throw e;
        }
    }

    public @HostAccess.Export JsonNode getForJson(final @NotBlank String url) {
        hasTextOf(url, "url");
        MeterUtil.counter(execution_sdk_client_total, ScriptHttpClient.class, METHOD_GET_FOR_JSON);

        try {
            final JsonNode result = MeterUtil.timer(execution_sdk_client_time, ScriptHttpClient.class, METHOD_GET_FOR_JSON,
                    () -> restClient.getForObject(url, JsonNode.class));

            MeterUtil.counter(execution_sdk_client_success, ScriptHttpClient.class, METHOD_GET_FOR_JSON);
            return result;
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_client_failure, ScriptHttpClient.class, METHOD_GET_FOR_JSON);
            throw e;
        }
    }

    public @HostAccess.Export JsonNode postForJson(final @NotBlank String url, final @NotNull Object request) {
        hasTextOf(url, "url");
        notNullOf(request, "request");
        MeterUtil.counter(execution_sdk_client_total, ScriptHttpClient.class, METHOD_POST_FOR_JSON);

        try {
            final JsonNode result = MeterUtil.timer(execution_sdk_client_time, ScriptHttpClient.class, METHOD_POST_FOR_JSON,
                    () -> restClient.postForObject(url, request, JsonNode.class));

            MeterUtil.counter(execution_sdk_client_success, ScriptHttpClient.class, METHOD_POST_FOR_JSON);
            return result;
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_client_failure, ScriptHttpClient.class, METHOD_POST_FOR_JSON);
            throw e;
        }
    }

    public @HostAccess.Export HttpResponseEntity<String> exchange(
            final @NotBlank String url,
            final @NotBlank String method,
            final @Nullable Object request,
            final @NotNull Map<String, String> headers) {
        hasTextOf(url, "url");
        hasTextOf(method, "method");
        // notNullOf(request, "request");
        notNullOf(headers, "headers");
        MeterUtil.counter(execution_sdk_client_total, ScriptHttpClient.class, METHOD_EXCHANGE);

        try {
            final HttpResponseEntity<String> result = MeterUtil.timer(execution_sdk_client_time, ScriptHttpClient.class,
                    METHOD_EXCHANGE, () -> {
                        final HttpHeaders httpHeaders = new HttpHeaders();
                        safeMap(headers).forEach((key, value) -> httpHeaders.add(key, value));
                        final HttpEntity<?> entity = new HttpEntity<>(request, httpHeaders);
                        return restClient.exchange(url, HttpMethod.valueOf(method), entity, String.class);
                    });

            MeterUtil.counter(execution_sdk_client_success, ScriptHttpClient.class, METHOD_EXCHANGE);
            return result;
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_client_failure, ScriptHttpClient.class, METHOD_EXCHANGE);
            throw e;
        }
    }

    public static final int DEFAULT_MIN_CONNECT_TIMEOUT = 100; // Default:min(100ms)
    public static final int DEFAULT_MAX_CONNECT_TIMEOUT = 5 * 60 * 1000; // Default:min(5min)
    public static final int DEFAULT_MIN_READ_TIMEOUT = 100; // Default:min(100ms)
    public static final int DEFAULT_MAX_READ_TIMEOUT = 15 * 60 * 1000; // Default:max(15min)
    public static final int DEFAULT_MIN_RESPONSE_SIZE = 1; // Default:min(1B)
    public static final int DEFAULT_MAX_RESPONSE_SIZE = 10 * 1024 * 1024; // Default:max(10M)
}
