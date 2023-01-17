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
package com.wl4g.rengine.executor.rest;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.rengine.common.constants.RengineConstants.API_EXECUTOR_EXECUTE_BASE;
import static com.wl4g.rengine.common.constants.RengineConstants.API_EXECUTOR_EXECUTE_CUSTOM;
import static com.wl4g.rengine.executor.rest.EngineExecutionResource.PARAM_ARGS;
import static com.wl4g.rengine.executor.rest.EngineExecutionResource.PARAM_BEST_EFFORT;
import static com.wl4g.rengine.executor.rest.EngineExecutionResource.PARAM_CLIENT_ID;
import static com.wl4g.rengine.executor.rest.EngineExecutionResource.PARAM_CLIENT_SECRET;
import static com.wl4g.rengine.executor.rest.EngineExecutionResource.PARAM_SCENES_CODES;
import static com.wl4g.rengine.executor.rest.EngineExecutionResource.PARAM_TIMEOUT;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.wl4g.infra.common.remoting.uri.UriComponentsBuilder;
import com.wl4g.rengine.common.entity.ScheduleJob.ResultDescription;
import com.wl4g.rengine.executor.rest.EngineExecutionResource.RequestSettings;
import com.wl4g.rengine.executor.rest.EngineExecutionResource.ResponseSettings;

/**
 * {@link EngineExecutionResourceTests}
 * 
 * @author James Wong
 * @version 2023-01-16
 * @since v1.0.0
 */
public class EngineExecutionResourceTests {

    @Test
    public void testMergeValueMap() {
        final List<ResultDescription> result = new ArrayList<>();
        result.add(ResultDescription.builder()
                .success(true)
                .scenesCode("myScenesCode1")
                .valueMap(singletonMap("foo1", "bar1"))
                .reason("ok")
                .build());
        result.add(ResultDescription.builder()
                .success(true)
                .scenesCode("myScenesCode2")
                .valueMap(singletonMap("foo2", "bar2"))
                .reason("ok")
                .build());

        final List<ResultDescription> mergedResult = safeList(result).stream()
                .reduce(new ArrayList<ResultDescription>(), (acc, ele) -> {
                    acc.add(ele);
                    return acc;
                }, (acc1, acc2) -> {
                    // acc1.addAll(acc2);
                    return acc1;
                });
        System.out.println(mergedResult.size());
        System.out.println(mergedResult);
        assert mergedResult.size() == 2;
    }

    @Test
    public void testBuildGetExecuteCustomForDingtalkCallbackUrl() {
        final var dingtalkCallbackUrl = UriComponentsBuilder.fromPath(API_EXECUTOR_EXECUTE_BASE)
                .scheme("http")
                // .host("event.rengine.com")
                .host("localhost:28002")
                .path(API_EXECUTOR_EXECUTE_CUSTOM)
                .queryParam(PARAM_CLIENT_ID, "JVqEpEwIaqkEkeD5")
                .queryParam(PARAM_CLIENT_SECRET, "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77")
                .queryParam(PARAM_SCENES_CODES, "dingtalk_event_callback")
                .queryParam(PARAM_BEST_EFFORT, "true")
                .queryParam(PARAM_TIMEOUT, "60000")
                // echo '{"foo":"bar"}' | base64
                .queryParam(PARAM_ARGS, "eyJmb28iOiJiYXIifQo=")
                // echo '{"format":"json"}' | base64
                .queryParam(RequestSettings.PARAM_REQ_SETTINGS, "eyJmb28iOiJiYXIifQo=")
                // echo '{"templateKey":"dingtalk"}' | base64
                .queryParam(ResponseSettings.PARAM_RESP_SETTINGS, "eyJ0ZW1wbGF0ZUtleSI6ImRpbmd0YWxrIn0K")
                .build()
                .toUriString();

        final String cmd = format("curl -v \"%s\"", dingtalkCallbackUrl);
        System.out.println(cmd);
    }

    @Test
    public void testBuildPostExecuteCustomForDingtalkCallbackUrl() {
        final var dingtalkCallbackUrl = UriComponentsBuilder.fromPath(API_EXECUTOR_EXECUTE_BASE)
                .scheme("http")
                // .host("event.rengine.com")
                .host("localhost:28002")
                .path(API_EXECUTOR_EXECUTE_CUSTOM)
                // echo '{"format":"json"}' | base64
                .queryParam(RequestSettings.PARAM_REQ_SETTINGS, "eyJmb28iOiJiYXIifQo=")
                // echo '{"templateKey":"dingtalk"}' | base64
                .queryParam(ResponseSettings.PARAM_RESP_SETTINGS, "eyJ0ZW1wbGF0ZUtleSI6ImRpbmd0YWxrIn0K")
                .build()
                .toUriString();

        final Map<String, Object> body = new HashMap<>();
        body.put(PARAM_TIMEOUT, body);
        body.put(PARAM_CLIENT_ID, "JVqEpEwIaqkEkeD5");
        body.put(PARAM_CLIENT_SECRET, "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77");
        body.put(PARAM_SCENES_CODES, "dingtalk_event_callback");
        body.put(PARAM_BEST_EFFORT, "true");
        body.put(PARAM_TIMEOUT, "60000");
        // echo '{"foo":"bar"}' | base64
        body.put(PARAM_ARGS, "eyJmb28iOiJiYXIifQo=");

        final String cmd = format("curl -v -XPOST \\\n\t-H 'Content-Type: application/json' \\\n\t-d '%s' '%s'",
                toJSONString(body, true), dingtalkCallbackUrl);
        System.out.println(cmd);
    }

}
