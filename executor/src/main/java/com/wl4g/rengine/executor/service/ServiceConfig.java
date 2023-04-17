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
package com.wl4g.rengine.executor.service;

import static com.wl4g.rengine.common.constants.RengineConstants.CONF_PREFIX_EXECUTOR;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.rengine.executor.execution.EngineConfig;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.ConfigMapping.NamingStrategy;
import io.smallrye.config.WithDefault;

/**
 * {@link EngineConfig}
 * 
 * @author James Wong
 * @date 2022-09-18
 * @since v1.0.0
 * @see https://quarkus.io/guides/details-mappings
 * @see https://quarkus.io/guides/details-extending-support#custom-details-source
 * @see {@link com.wl4g.rengine.service.config.RengineServiceProperties.ServiceProperties}
 */
@StaticInitSafe
@ConfigMapping(prefix = CONF_PREFIX_EXECUTOR + ".services", namingStrategy = NamingStrategy.KEBAB_CASE)
public interface ServiceConfig {

    @WithDefault(DEFAULT_DICT_CACHED_PREFIX)
    @NotBlank
    String dictCachedPrefix();

    @WithDefault(DEFAULT_DICT_CACHED_EXPIRE + "")
    @NotNull
    @Min(0)
    Long dictCachedExpire();

    public static final String DEFAULT_DICT_CACHED_PREFIX = "rengine:executor:service:dict";
    public static final long DEFAULT_DICT_CACHED_EXPIRE = 24 * 60 * 60 * 1000; // 24h

}
