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
package com.wl4g;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.System.nanoTime;
import static java.util.Objects.isNull;

import java.util.concurrent.TimeUnit;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wl4g.rengine.evaluator.context.ApplicationBeansModule;
import com.wl4g.rengine.evaluator.context.shutdown.ShutdownHooks;
import com.wl4g.rengine.evaluator.context.shutdown.StartAbortedException;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link RengineEvaluator}
 * 
 * @author James Wong
 * @date 2022-09-17
 * @since v1.0.0
 * @see https://github.com/google/guice/wiki/Motivation
 * @see https://github1s.com/hivemq/hivemq-community-edition/blob/2022.1/src/main/java/com/hivemq/HiveMQServer.java
 */
@Slf4j
public class RengineEvaluator {

    private final Injector injector;

    public RengineEvaluator() {
        this.injector = notNullOf(Guice.createInjector(new ApplicationBeansModule()), "initInjector");
    }

    private void start() {
        log.info("Starting Rengine Evaluator ...");

        final long startTime = nanoTime();

        initShutdownHooks();
        if (injector.getInstance(ShutdownHooks.class).isShuttingDown()) {
            throw new StartAbortedException("User aborted.");
        }

        // // TODO
        // ExecutorFactory factory =
        // injector.getInstance(ExecutorFactory.class);
        // factory.getExecutor();

        log.info("Started Rengine Executor in {}ms", TimeUnit.NANOSECONDS.toMillis(nanoTime() - startTime));
    }

    public void stop() {
        if (isNull(injector)) {
            return;
        }
        log.info("Rengine Executor stopping ...");

        final ShutdownHooks hooks = injector.getInstance(ShutdownHooks.class);
        if (hooks.isShuttingDown()) { // Already shutdown.
            return;
        }

        hooks.runShutdownHooks();
    }

    private void initShutdownHooks() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "shutdownhook"));
    }

    public static void main(String[] args) {
        final RengineEvaluator evaluator = new RengineEvaluator();
        try {
            evaluator.start();
        } catch (StartAbortedException e) {
            log.info("Rengine Evaluator start was cancelled. {}", e.getMessage());
        }
    }

}
