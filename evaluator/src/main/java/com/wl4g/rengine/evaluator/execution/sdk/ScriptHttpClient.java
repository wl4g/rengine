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
package com.wl4g.rengine.evaluator.execution.sdk;

import org.graalvm.polyglot.HostAccess;

import com.fasterxml.jackson.databind.JsonNode;
import com.wl4g.infra.common.remoting.RestClient;

import lombok.ToString;

/**
 * {@link ScriptHttpClient}
 * 
 * @author James Wong
 * @version 2022-09-25
 * @since v3.0.0
 */
@ToString
public class ScriptHttpClient {

    private final RestClient restClient;

    public ScriptHttpClient() {
        this.restClient = new RestClient();
    }

    public ScriptHttpClient(boolean debug, int connectTimeout, int readTimeout, int maxResponseSize) {
        this.restClient = new RestClient(debug, connectTimeout, readTimeout, maxResponseSize);
    }

    public @HostAccess.Export String getAsText(String url) {
        return restClient.getForObject(url, String.class);
    }

    public @HostAccess.Export String postAsText(String url, Object request) {
        return restClient.postForObject(url, request, String.class);
    }

    public @HostAccess.Export JsonNode getAsJson(String url) {
        return restClient.getForObject(url, JsonNode.class);
    }

    public @HostAccess.Export JsonNode postAsJson(String url, Object request) {
        return restClient.postForObject(url, request, JsonNode.class);
    }

}
