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
package com.wl4g.rengine.evaluator.execution.sdk;

import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.HostAccess;

import com.fasterxml.jackson.databind.JsonNode;
import com.wl4g.infra.common.remoting.RestClient;

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

    private final RestClient restClient;

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

    public @HostAccess.Export String getForText(String url) {
        return restClient.getForObject(url, String.class);
    }

    public @HostAccess.Export String postForText(String url, Object request) {
        return restClient.postForObject(url, request, String.class);
    }

    public @HostAccess.Export JsonNode getForJson(String url) {
        return restClient.getForObject(url, JsonNode.class);
    }

    public @HostAccess.Export JsonNode postForJson(String url, Object request) {
        return restClient.postForObject(url, request, JsonNode.class);
    }

    public static final int DEFAULT_MIN_CONNECT_TIMEOUT = 100; // Default:min(100ms)
    public static final int DEFAULT_MAX_CONNECT_TIMEOUT = 5 * 60 * 1000; // Default:min(5min)
    public static final int DEFAULT_MIN_READ_TIMEOUT = 100; // Default:min(100ms)
    public static final int DEFAULT_MAX_READ_TIMEOUT = 15 * 60 * 1000; // Default:max(15min)
    public static final int DEFAULT_MIN_RESPONSE_SIZE = 1; // Default:min(1B)
    public static final int DEFAULT_MAX_RESPONSE_SIZE = 10 * 1024 * 1024; // Default:max(10M)
}
