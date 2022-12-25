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
package com.wl4g.rengine.executor.execution.engine;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static java.util.Objects.isNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.task.GenericTaskRunner;
import com.wl4g.infra.common.task.RunnerProperties;
import com.wl4g.infra.common.task.RunnerProperties.StartupMode;
import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;
import com.wl4g.rengine.executor.execution.ExecutionConfig;

import lombok.CustomLog;

/**
 * {@link GlobalExecutorManager}
 * 
 * @author James Wong
 * @version 2022-12-26
 * @since v1.0.0
 */
@CustomLog
@Singleton
public class GlobalExecutorManager implements Closeable {

    private final Map<String, GenericTaskRunner<RunnerProperties>> runnerCaching = new ConcurrentHashMap<>(4);

    @NotNull
    @Inject
    ExecutionConfig config;

    @Override
    public void close() throws IOException {
        safeMap(runnerCaching).forEach((name, runner) -> {
            try {
                runner.close();
            } catch (IOException e) {
                log.error("Failed to closing task runner of {}", name);
            }
        });
    }

    public SafeScheduledTaskPoolExecutor getExecutor(
            final @NotBlank String scenesCode,
            @Min(1) @Max(DEFAULT_POOL_LIMIT) int concurrency) {
        GenericTaskRunner<RunnerProperties> runner = runnerCaching.get(scenesCode);
        if (isNull(runner)) {
            synchronized (this) {
                if (isNull(runner = runnerCaching.get(scenesCode))) {
                    runner = new GenericTaskRunner<RunnerProperties>(new RunnerProperties(StartupMode.NOSTARTUP, concurrency, 0L,
                            (int) (concurrency * 1.5), new AbortPolicy())) {
                    };
                    runner.start();
                }
            }
        }
        return runner.getWorker();
    }

    public static final int DEFAULT_POOL_CONCURRENCY = 2;
    public static final int DEFAULT_POOL_LIMIT = 100;

}
