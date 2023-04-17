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

import static java.lang.System.currentTimeMillis;

import org.junit.Test;

import com.wl4g.infra.common.remoting.exception.RestClientException;

/**
 * {@link ScriptHttpClientTests}
 * 
 * @author James Wong
 * @date 2022-09-30
 * @since v1.0.0
 */
public class ScriptHttpClientTests {

    @Test(expected = RestClientException.class, timeout = 5000 + 100)
    public void testPostAsJsonWithConnectTimeout() {
        System.out.println("begin: " + currentTimeMillis());
        ScriptHttpClient httpClient = new ScriptHttpClient(5000, 10000, 10240);
        // BsonEntitySerializers connection timeout occurs when trying to connect to a non-existent
        // address
        String result = httpClient.getForText("https://www.google12345678.com");
        System.out.println(result);
        System.out.println("end: " + currentTimeMillis());
    }

    @Test(expected = RestClientException.class, timeout = 10000 + 100)
    public void testPostAsJsonWithReadTimeout() {
        System.out.println("begin: " + currentTimeMillis());
        ScriptHttpClient httpClient = new ScriptHttpClient(5000, 10000, 10240);
        // The mainland network can connect to google, but the returned
        // data cannot be read by TCP.reset
        String result = httpClient.getForText("https://www.google.com");
        System.out.println(result);
        System.out.println("end: " + currentTimeMillis());
    }

}
