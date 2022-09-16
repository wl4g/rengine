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
package com.wl4g.rengine.evaluator.context.shutdown;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import org.slf4j.MarkerFactory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Ordering;
import com.wl4g.rengine.evaluator.util.ThreadFactoryUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * A implementation for all shutdown hooks.
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v3.0.0
 * @see learning refer to:
 *      https://github.com/hivemq/hivemq-community-edition/blob/2022.1/src/main/java/com/hivemq/common/shutdown/ShutdownHooks.java
 */
@Singleton
@Slf4j
public class ShutdownHooks {
    private final @NotNull AtomicBoolean shuttingDown;
    private final @NotNull Multimap</* Priority */Integer, RengineShutdownHook> synchronousHooks;

    @Inject
    public ShutdownHooks() {
        this.shuttingDown = new AtomicBoolean(false);
        // High priorities first
        this.synchronousHooks = MultimapBuilder.SortedSetMultimapBuilder.treeKeys(Ordering.natural().reverse())
                .arrayListValues()
                .build();
    }

    public boolean isShuttingDown() {
        return shuttingDown.get();
    }

    /**
     * Adds a {@link RengineShutdownHook} to the shutdown hook registry
     *
     * @param rengineShutdownHook
     *            the {@link RengineShutdownHook} to add
     */
    public synchronized void add(final @NotNull RengineShutdownHook rengineShutdownHook) {
        if (shuttingDown.get()) {
            return;
        }
        checkNotNull(rengineShutdownHook, "A shutdown hook must not be null");
        log.trace("Adding shutdown hook {} with priority {}", rengineShutdownHook.name(), rengineShutdownHook.priority());
        synchronousHooks.put(rengineShutdownHook.priority().getValue(), rengineShutdownHook);
    }

    /**
     * Removes a {@link RengineShutdownHook} from the shutdown hook registry
     *
     * @param rengineShutdownHook
     *            the {@link RengineShutdownHook} to add
     */
    public synchronized void remove(final @NotNull RengineShutdownHook rengineShutdownHook) {
        if (shuttingDown.get()) {
            return;
        }
        checkNotNull(rengineShutdownHook, "A shutdown hook must not be null");

        log.trace("Removing shutdown hook {} with priority {}", rengineShutdownHook.name(), rengineShutdownHook.priority());
        synchronousHooks.values().remove(rengineShutdownHook);
    }

    /**
     * A registry of all Shutdown Hooks.
     */
    public @NotNull Multimap<Integer, RengineShutdownHook> getShutdownHooks() {
        return synchronousHooks;
    }

    public void runShutdownHooks() {
        shuttingDown.set(true);
        log.info("Shutting down Rengine Evaluator. Please wait, this could take a while...");
        final ScheduledExecutorService executorService = Executors
                .newSingleThreadScheduledExecutor(ThreadFactoryUtil.create("shutdown-log-executor"));
        executorService.scheduleAtFixedRate(() -> log.info(
                "Still shutting down Rengine Evaluator. Waiting for remaining tasks to be executed. Do not shutdown Rengine Evaluator."),
                10, 10, TimeUnit.SECONDS);
        for (final RengineShutdownHook runnable : synchronousHooks.values()) {
            log.trace(MarkerFactory.getMarker("SHUTDOWN_HOOK"), "Running shutdown hook {}", runnable.name());
            runnable.run();
        }
        executorService.shutdown();

        log.info("Shutdown completed.");
    }
}