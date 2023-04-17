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
package com.wl4g.rengine.scheduler.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link ParamUtilsTests}
 * 
 * @author James Wong
 * @date 2022-10-22
 * @since v3.0.0
 */
public class ParamUtilsTests {

    @Test
    public void testGetShardingProps() {
        Map<String, String> original = new HashMap<>();
        String web1HttpUrl = "http://192.168.8.1:9100/metrics";
        original.put("web-1.http.url", web1HttpUrl);
        original.put("web-1.http.headers", "Content-Type=text/html,X-Foo=Bar");
        original.put("web-2.http.url", "http://192.168.8.2:9100/metrics");
        // Special case:
        original.put("http.url", "http://192.168.8.3:9100/metrics");
        original.put("url", "http://192.168.8.3:9100/metrics");

        Map<String, Map<String, String>> result = ParamUtils.toGroupMapWithFirst(original, "defaults");
        result.forEach((k, v) -> System.out.println(k + " => " + v));

        Assertions.assertEquals(result.get("web-1").get("http.url"), web1HttpUrl);
    }

}
