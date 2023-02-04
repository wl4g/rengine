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
package com.wl4g.rengine.controller.util;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeArray;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * {@link Parameters}
 * 
 * @author James Wong
 * @version 2022-10-21
 * @since v3.0.0
 */
public abstract class Parameters {

    public static Map<String, String> parseToMap(String configItems) {
        Map<String, String> configMap = Maps.newHashMap();
        if (!isBlank(configItems)) {
            for (String kv : safeArray(String.class, split(configItems, ","))) {
                String[] parts = split(kv, "=");
                configMap.put(parts[0], parts[1]);
            }
        }
        return configMap;
    }

    /**
     * The group by properties key prefix and convert to nested map.
     * 
     * for example: </br>
     * 
     * <pre>
     * 
     * Original properties:
     *   {web-1.http.uri=http://192.168.8.1:9100/metrics, web-1.http.headers=Content-Type=text/html,X-Foo=Bar, web-2.http.uri=http://10.0.0.114:9100/metrics}
     * 
     * Sharding properties:
     *   web-1 => {http.headers=Content-Type=text/html,X-Foo=Bar, http.url=http://192.168.8.1:9100/metrics}
     *   web-2 => {http.url=http://192.168.8.2:9100/metrics}
     * </pre>
     * 
     * @param jobConfig
     * @param shardingContext
     * @return
     */
    public static Map<String, Map<String, String>> toGroupMapWithFirst(Map<String, String> original, String defaultGroup) {
        return safeMap(original).entrySet().stream().collect(groupingBy(e -> {
            String key = valueOf(e.getKey());
            int index = key.indexOf(".");
            return index < 0 ? defaultGroup : key.substring(0, index);
        }))
                .entrySet()
                .stream()
                .sorted((o1, o2) -> valueOf(o1.getKey()).compareTo(valueOf(o2.getKey())))
                .collect(toMap(e -> e.getKey(), e -> safeList(e.getValue()).stream().collect(toMap(e2 -> {
                    String key = valueOf(e2.getKey());
                    int index = key.indexOf(".");
                    return key.substring(index < 0 ? 0 : index + 1);
                }, e2 -> valueOf(e2.getValue())))));
    }

}
