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
package com.wl4g.rengine.executor.util;

import static com.wl4g.infra.common.reflect.ReflectionUtils2.findField;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.setField;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoDriverInformation;
import com.mongodb.client.MongoClient;
import com.mongodb.client.internal.MongoClientImpl;
import com.wl4g.rengine.executor.execution.ExecutionConfig;
import com.wl4g.rengine.executor.repository.MongoRepository;

/**
 * {@link TestDefaultBaseSetup}
 * 
 * @author James Wong
 * @version 2022-10-10
 * @since v1.0.0
 */
public abstract class TestDefaultBaseSetup {

    public static MongoRepository createMongoRepository() {
        MongoClient mongoClient = new MongoClientImpl(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017/rengine"))
                .build(), MongoDriverInformation.builder().build());

        MongoRepository mongoRepository = new MongoRepository();
        setField(findField(MongoRepository.class, null, MongoClient.class), mongoRepository, mongoClient, true);

        return mongoRepository;
    }

    public static ExecutionConfig createExecutionConfig() {
        return new ExecutionConfig() {
            @Override
            public @NotNull EngineConfig engine() {
                return new EngineConfig() {
                    @Override
                    public @NotBlank String scenesRulesCachedPrefix() {
                        return ExecutionConfig.DEFAULT_SCENES_RULES_CACHED_PREFIX;
                    }

                    @Override
                    public @NotNull @Min(0) Long scenesRulesCachedExpire() {
                        return ExecutionConfig.DEFAULT_SCENES_RULES_CACHED_EXPIRE;
                    }

                    @Override
                    public @NotNull @Min(0) @Max(65535) Integer executorThreadPools() {
                        return ExecutionConfig.DEFAULT_EXECUTOR_THREAD_POOLS;
                    }

                    @Override
                    public @NotNull @Min(0) @Max(1024) Integer perExecutorThreadPools() {
                        return ExecutionConfig.DEFAULT_PER_EXECUTOR_THREAD_POOLS;
                    }

                    @Override
                    public @NotNull @Min(0) @Max(100000) Integer maxQueryBatch() {
                        return ExecutionConfig.DEFAULT_MAX_QUERY_BATCH;
                    }

                    @Override
                    public @NotNull @Min(0) @Max(1) Float evaluateTimeoutOffsetRate() {
                        return ExecutionConfig.DEFAULT_TIMEOUT_OFFSET_RATE;
                    }

                };
            }

            @Override
            public @NotNull ScriptLogConfig log() {
                return new ScriptLogConfig() {
                    @Override
                    public @NotBlank String baseDir() {
                        return ExecutionConfig.DEFAULT_SCRIPT_LOG_BASE_DIR;
                    }

                    @Override
                    public @NotNull Boolean enableConsole() {
                        return ExecutionConfig.DEFAULT_SCRIPT_LOG_ENABLE_CONSOLE;
                    }

                    @Override
                    public @NotNull @Min(1024) Integer fileMaxSize() {
                        return ExecutionConfig.DEFAULT_SCRIPT_LOG_FILE_MAX_SIZE;
                    }

                    @Override
                    public @NotNull @Min(1) Integer fileMaxCount() {
                        return ExecutionConfig.DEFAULT_SCRIPT_LOG_FILE_MAX_COUNT;
                    }
                };
            }

            @Override
            public @NotNull NotifierConfig notifier() {
                return new NotifierConfig() {

                    @Override
                    public @NotNull @Min(0) Long refreshLockTimeout() {
                        return ExecutionConfig.DEFAULT_NOTIFIER_REFRESH_LOCK_TIMEOUT;
                    }

                    @Override
                    public @NotBlank String refreshedCachedPrefix() {
                        return ExecutionConfig.DEFAULT_NOTIFIER_REFRESHED_CACHED_PREFIX;
                    }

                    @Override
                    public @NotNull @Min(0) @Max(1) Float refreshedCachedExpireOffsetRate() {
                        return ExecutionConfig.DEFAULT_NOTIFIER_EXPIRE_OFFSET_RATE;
                    }
                };
            }

        };
    }

}
