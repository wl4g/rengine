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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.List;

import com.wl4g.infra.common.rocksdb.RocksDBService;
import com.wl4g.infra.common.store.RocksDBMapStore;
import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.eventbus.config.ClientEventBusConfig;

import lombok.Getter;

/**
 * {@link RocksDBEventRecorder}
 * 
 * @author James Wong
 * @version 2022-10-31
 * @since v3.0.0
 */
@Getter
public class RocksDBEventRecorder extends AbstractEventRecorder {
    private final RocksDBMapStore paddingStore;
    private final RocksDBMapStore completedStore;

    public RocksDBEventRecorder(ClientEventBusConfig config) {
        super(config);
        RocksDBService rocksDBService = new RocksDBService(config.getRecorder().getRocksdb());
        this.paddingStore = new RocksDBMapStore(rocksDBService, RengineEvent.class, "padding");
        this.completedStore = new RocksDBMapStore(rocksDBService, Long.class, "completed");
    }

    @Override
    public void close() throws IOException {
        if (nonNull(paddingStore)) {
            try {
                paddingStore.close();
            } catch (Exception e) {
                log.error("Unable to closing rocks store.", e);
            }
        }
        // if (nonNull(completedStore)) {
        // try {
        // completedStore.close();
        // } catch (Exception e) {
        // log.error("Unable to closing completed rocks store.", e);
        // }
        // }
    }

    @Override
    public void padding(List<RengineEvent> events) {
        safeList(events).parallelStream().forEach(event -> paddingStore.put(event.getId(), event));
    }

    @Override
    public void completed(List<RengineEvent> events) {
        safeList(events).parallelStream().forEach(event -> completedStore.put(event.getId(), currentTimeMillis()));
    }

}
