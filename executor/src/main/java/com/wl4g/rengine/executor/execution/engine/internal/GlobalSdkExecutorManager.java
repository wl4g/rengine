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
package com.wl4g.rengine.executor.execution.engine.internal;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static java.util.Objects.isNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.task.GenericTaskRunner;
import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;
import com.wl4g.rengine.executor.execution.EngineConfig;
import com.wl4g.rengine.executor.execution.sdk.ScriptExecutor;

import lombok.CustomLog;

/**
 * {@link GlobalSdkExecutorManager}
 * 
 * @author James Wong
 * @date 2022-12-26
 * @since v1.0.0
 */
@CustomLog
@Singleton
public class GlobalSdkExecutorManager implements Closeable {

    private final Map<Long, SafeScheduledTaskPoolExecutor> executorCaching = new ConcurrentHashMap<>(4);

    @NotNull
    @Inject
    EngineConfig config;

    @Override
    public void close() throws IOException {
        safeMap(executorCaching).forEach((name, executor) -> {
            try {
                executor.shutdown();
            } catch (Exception e) {
                log.error("Failed to shutdown task executor of {}", name);
            }
        });
    }

    public SafeScheduledTaskPoolExecutor getExecutor(
            final @NotNull Long workflowId,
            final @Min(1) @Max(DEFAULT_POOL_LIMIT) int concurrency) {
        SafeScheduledTaskPoolExecutor executor = executorCaching.get(workflowId);
        if (isNull(executor)) {
            synchronized (this) {
                if (isNull(executor = executorCaching.get(workflowId))) {
                    executor = GenericTaskRunner.newDefaultScheduledExecutor(ScriptExecutor.class.getSimpleName(), concurrency,
                            (int) (concurrency * 2));
                }
            }
        }
        return executor;
    }

    public static final int DEFAULT_POOL_CONCURRENCY = 2;
    public static final int DEFAULT_POOL_LIMIT = 100;

}
