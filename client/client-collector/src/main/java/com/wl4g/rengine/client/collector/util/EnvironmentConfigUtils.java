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
package com.wl4g.rengine.client.collector.util;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.core.env.Environment;

/**
 * {@link EnvironmentConfigUtils}
 * 
 * @author James Wong
 * @version 2022-10-30
 * @since v3.0.0
 */
public abstract class EnvironmentConfigUtils {

    public static String resolveString(Environment environment, String placeholder) {
        if (isBlank(placeholder)) {
            return placeholder;
        }
        return environment.resolvePlaceholders(placeholder);
    }

    // public static String resolveInt(Environment environment, String
    // placeholder) {
    // String value = resolveString(environment, placeholder);
    // return TypeConverts.parseIntOrDefault(value);
    // }

}
