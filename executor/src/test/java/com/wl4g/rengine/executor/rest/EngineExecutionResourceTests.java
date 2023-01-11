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

import static com.wl4g.rengine.common.constants.RengineConstants.API_EXECUTOR_EXECUTE_BASE;
import static com.wl4g.rengine.common.constants.RengineConstants.API_EXECUTOR_EXECUTE_CUSTOM;
import static com.wl4g.rengine.executor.rest.EngineExecutionResource.PARAM_ARGS;
import static com.wl4g.rengine.executor.rest.EngineExecutionResource.PARAM_BEST_EFFORT;
import static com.wl4g.rengine.executor.rest.EngineExecutionResource.PARAM_CLIENT_ID;
import static com.wl4g.rengine.executor.rest.EngineExecutionResource.PARAM_CLIENT_SECRET;
import static com.wl4g.rengine.executor.rest.EngineExecutionResource.PARAM_SCENES_CODES;
import static com.wl4g.rengine.executor.rest.EngineExecutionResource.PARAM_TIMEOUT;

import org.junit.Test;

import com.wl4g.infra.common.remoting.uri.UriComponentsBuilder;
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
    public void testBuildDingtalkCallbackUrlWithExecuteCustom() {
        final var dingtalkCallbackUrl = UriComponentsBuilder.fromPath(API_EXECUTOR_EXECUTE_BASE)
                .scheme("https")
                .host("event.example.com")
                .path(API_EXECUTOR_EXECUTE_CUSTOM)
                .queryParam(PARAM_CLIENT_ID, "JVqEpEwIaqkEkeD5")
                .queryParam(PARAM_CLIENT_SECRET, "Uf6nJDyJQHKRP43ycl9vZ9zs7s1nyu77")
                .queryParam(PARAM_SCENES_CODES, "dingtalk_event_callback")
                .queryParam(PARAM_BEST_EFFORT, "true")
                .queryParam(PARAM_TIMEOUT, "60000")
                // echo '{"foo":"bar"}' | base64
                .queryParam(PARAM_ARGS, "eyJmb28iOiJiYXIifQo=")
                // 不应该这么设计？URL很容易会超长，应该从 ExecutionConfig.engine.templates
                // 获取，它属于配置数据(非用户数据)不会很多
                // echo '{"format":"json"}' | base64
                .queryParam(RequestSettings.PARAM_REQ_SETTINGS, "eyJmb28iOiJiYXIifQo=")
                // echo '{"templateKey":"dingtalk"}' | base64
                .queryParam(ResponseSettings.PARAM_RESP_SETTINGS, "eyJ0ZW1wbGF0ZUtleSI6ImRpbmd0YWxrIn0K")
                .build()
                .toUriString();
        System.out.println(dingtalkCallbackUrl);
    }

}
