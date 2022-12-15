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
package com.wl4g.rengine.evaluator.execution;

import static com.wl4g.rengine.common.constants.RengineConstants.CONF_PREFIX_EVALUATOR;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.ConfigMapping.NamingStrategy;
import io.smallrye.config.WithDefault;

/**
 * {@link ExecutionConfig}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v1.0.0
 * @see https://quarkus.io/guides/properties-mappings
 * @see https://quarkus.io/guides/properties-extending-support#custom-properties-source
 */
@StaticInitSafe
@ConfigMapping(prefix = CONF_PREFIX_EVALUATOR + ".execution", namingStrategy = NamingStrategy.KEBAB_CASE)
public interface ExecutionConfig {

    @WithDefault(DEFAULT_THREAD_POOLS + "")
    @NotNull
    @Min(0)
    @Max(65535)
    Integer threadPools();

    @WithDefault(DEFAULT_MAX_QUERY_BATCH + "")
    @NotNull
    @Min(0)
    @Max(10_0000)
    Integer maxQueryBatch();

    @WithDefault(DEFAULT_TIMEOUT_OFFSET_RATE + "")
    @NotNull
    @Min(0)
    @Max(1)
    Float evaluateTimeoutOffsetRate();

    public static final int DEFAULT_THREAD_POOLS = 3;
    public static final int DEFAULT_MAX_QUERY_BATCH = 1024;
    public static final float DEFAULT_TIMEOUT_OFFSET_RATE = 0.1f;
}
