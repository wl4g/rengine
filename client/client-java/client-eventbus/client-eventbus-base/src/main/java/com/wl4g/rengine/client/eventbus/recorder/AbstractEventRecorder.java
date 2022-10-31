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
package com.wl4g.rengine.client.eventbus.recorder;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.nonNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.wl4g.infra.common.log.SmartLogger;
import com.wl4g.rengine.client.eventbus.config.ClientEventBusConfig;
import com.wl4g.rengine.client.eventbus.recorder.store.EventStore;

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
        this.config = notNullOf(config, "config");
        final int threadPools = config.getStore().getCompaction().getThreadPools();
        final AtomicInteger counter = new AtomicInteger(0);
        this.executor = Executors.newFixedThreadPool((threadPools <= 1) ? 1 : threadPools,
                r -> new Thread(r, getClass().getSimpleName().concat("-" + counter.incrementAndGet())));
    }

    protected abstract EventStore getPaddingCache();

    protected abstract EventStore getCompletedCache();

    @Override
    public void close() throws IOException {
        if (nonNull(executor) && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    @Override
    public void compact() {
        executor.submit(new CompactionTask());
    }

    class CompactionTask implements Runnable {
        @Override
        public void run() {
            log.debug("Start compaction task ...");
            Iterator<Entry<String, Object>> it = getCompletedCache().iterator();
            while (it.hasNext()) {
                Entry<String, Object> entry = it.next();
                log.debug("Removing to completed send event for : {}", entry.getKey());
                getPaddingCache().remove(entry.getKey());
                getCompletedCache().remove(entry.getKey());
            }
        }
    }

}
