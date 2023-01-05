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
package com.wl4g.rengine.executor.execution;

import static com.wl4g.rengine.common.constants.RengineConstants.CONF_PREFIX_EXECUTOR;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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
@ConfigMapping(prefix = CONF_PREFIX_EXECUTOR + ".execution", namingStrategy = NamingStrategy.KEBAB_CASE)
public interface ExecutionConfig {

    @WithDefault(DEFAULT_SCENES_RULES_CACHED_PREFIX)
    @NotBlank
    String scenesRulesCachedPrefix();

    @WithDefault(DEFAULT_SCENES_RULES_CACHED_EXPIRE + "")
    @NotNull
    @Min(0)
    Long scenesRulesCachedExpire();

    @WithDefault(DEFAULT_EXECUTOR_THREAD_POOLS + "")
    @NotNull
    @Min(0)
    @Max(1024)
    Integer executorThreadPools();

    @WithDefault(DEFAULT_PER_EXECUTOR_THREAD_POOLS + "")
    @NotNull
    @Min(0)
    @Max(1024)
    Integer perExecutorThreadPools();

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

    @NotNull
    ScriptLogConfig log();

    @NotNull
    NotifierConfig notifier();

    public static interface ScriptLogConfig {
        @WithDefault(DEFAULT_SCRIPT_LOG_BASE_DIR)
        @NotBlank
        String baseDir();

        @WithDefault(DEFAULT_SCRIPT_LOG_FILE_MAX_SIZE + "")
        @NotNull
        @Min(1024)
        Integer fileMaxSize();

        @WithDefault(DEFAULT_SCRIPT_LOG_FILE_MAX_COUNT + "")
        @NotNull
        @Min(1)
        Integer fileMaxCount();
    }

    public static interface NotifierConfig {
        @WithDefault(DEFAULT_NOTIFIER_REFRESH_LOCK_TIMEOUT + "")
        @NotNull
        @Min(0)
        Long refreshLockTimeout();

        @WithDefault(DEFAULT_NOTIFIER_REFRESHED_CACHED_PREFIX + "")

        @NotBlank
        String refreshedCachedPrefix();

        @WithDefault(DEFAULT_NOTIFIER_EXPIRE_OFFSET_RATE + "")
        @NotNull
        @Min(0)
        @Max(1)
        Float refreshedCachedExpireOffsetRate();
    }

    public static final String DEFAULT_SCENES_RULES_CACHED_PREFIX = "rengine:executor:scenes:rules:";
    public static final long DEFAULT_SCENES_RULES_CACHED_EXPIRE = 15 * 60 * 1000;
    public static final int DEFAULT_EXECUTOR_THREAD_POOLS = 10;
    public static final int DEFAULT_PER_EXECUTOR_THREAD_POOLS = 2;
    public static final int DEFAULT_MAX_QUERY_BATCH = 1024;
    public static final float DEFAULT_TIMEOUT_OFFSET_RATE = 0.1f;

    public static final String DEFAULT_SCRIPT_LOG_BASE_DIR = "/tmp/__rengine_script_log";
    public static final int DEFAULT_SCRIPT_LOG_FILE_MAX_SIZE = 512 * 1024 * 1024;
    public static final int DEFAULT_SCRIPT_LOG_FILE_MAX_COUNT = 10;

    public static final long DEFAULT_NOTIFIER_REFRESH_LOCK_TIMEOUT = 60 * 1000L;
    public static final String DEFAULT_NOTIFIER_REFRESHED_CACHED_PREFIX = "rengine:executor:sdk:notifier:refreshed:";
    public static final float DEFAULT_NOTIFIER_EXPIRE_OFFSET_RATE = 0.1f;
}
