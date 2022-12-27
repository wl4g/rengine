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
package com.wl4g.rengine.eventbus.recorder;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.wl4g.infra.common.function.CallbackFunction;
import com.wl4g.infra.common.log.SmartLogger;
import com.wl4g.infra.common.store.MapStore;
import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.eventbus.config.ClientEventBusConfig;

import lombok.AllArgsConstructor;

/**
 * {@link AbstractEventRecorder}
 * 
 * @author James Wong
 * @version 2022-10-31
 * @since v3.0.0
 */
public abstract class AbstractEventRecorder implements Closeable, EventRecorder {
    protected final SmartLogger log = getLogger(getClass());

    protected final ClientEventBusConfig config;
    protected final ExecutorService executor;

    public AbstractEventRecorder(ClientEventBusConfig config) {
        this.config = notNullOf(config, "properties");
        final int threadPools = config.getRecorder().getCompaction().getThreadPools();
        final AtomicInteger counter = new AtomicInteger(0);
        this.executor = Executors.newFixedThreadPool((threadPools <= 1) ? 1 : threadPools,
                r -> new Thread(r, getClass().getSimpleName().concat("-" + counter.incrementAndGet())));
    }

    protected abstract MapStore getPaddingStore();

    protected abstract MapStore getCompletedStore();

    @Override
    public void close() throws IOException {
        if (nonNull(executor) && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    @Override
    public void compact(CallbackFunction<RengineEvent> resender) {
        executor.submit(new CompactionTask(resender));
    }

    @AllArgsConstructor
    class CompactionTask implements Runnable {
        private final CallbackFunction<RengineEvent> resender;

        @Override
        public void run() {
            AtomicInteger total = new AtomicInteger(0);
            AtomicInteger success = new AtomicInteger(0);
            try {
                log.info("Start compaction task ...");

                Iterator<Entry<String, Serializable>> it = getCompletedStore().iterator();
                while (it.hasNext()) {
                    total.incrementAndGet();
                    Entry<String, Serializable> entry = it.next();
                    try {
                        log.debug("Resending to uncompleted send event for : {}", entry.getKey());
                        String eventJson = getPaddingStore().get(entry.getKey());
                        resender.process(parseJSON(eventJson, RengineEvent.class)); // Re-sending

                        log.debug("Removing to uncompleted send event for : {}", entry.getKey());
                        getPaddingStore().remove(entry.getKey());
                        getCompletedStore().remove(entry.getKey());

                        success.incrementAndGet();
                    } catch (Exception e) {
                        log.warn(format("Unable to resending. - %s", entry.getKey()), e);
                    }
                }

                log.info("Completed compaction tasks of : {}/{}", success, total);
            } catch (Exception e) {
                log.error(format("Failed to compaction tasks of : {}/{}", success, total), e);
            }
        }
    }

}
