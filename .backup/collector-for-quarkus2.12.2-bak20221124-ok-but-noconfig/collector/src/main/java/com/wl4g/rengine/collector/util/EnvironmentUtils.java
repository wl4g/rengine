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
package com.wl4g.rengine.collector.util;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.eclipse.microprofile.config.ConfigProvider;

/**
 * {@link EnvironmentUtils}
 * 
 * @author James Wong
 * @date 2022-10-30
 * @since v3.0.0
 */
public abstract class EnvironmentUtils {

    public static String resolveString(String placeholder) {
        if (isBlank(placeholder)) {
            return placeholder;
        }
        return ConfigProvider.getConfig().getConfigValue(placeholder).getValue();
    }

}
