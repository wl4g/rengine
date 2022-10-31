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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.List;

import com.wl4g.rengine.client.eventbus.config.ClientEventBusConfig;
import com.wl4g.rengine.client.eventbus.recorder.store.EhCacheEventStore;
import com.wl4g.rengine.common.event.RengineEvent;

import lombok.Getter;

/**
 * {@link RedisEventRecorder}
 * 
 * @author James Wong
 * @version 2022-10-31
 * @since v3.0.0
 */
@Getter
public class RedisEventRecorder extends AbstractEventRecorder {
    private final EhCacheEventStore paddingCache;
    private final EhCacheEventStore completedCache;

    public RedisEventRecorder(ClientEventBusConfig config) {
        super(config);
        this.paddingCache = new EhCacheEventStore(config, "padding");
        this.completedCache = new EhCacheEventStore(config, "completed");
    }

    @Override
    public void close() throws IOException {
        if (nonNull(paddingCache)) {
            try {
                paddingCache.close();
            } catch (Exception e) {
                log.error("Unable to closing padding cache.", e);
            }
        }
        if (nonNull(completedCache)) {
            try {
                completedCache.close();
            } catch (Exception e) {
                log.error("Unable to closing completed cache.", e);
            }
        }
    }

    @Override
    public void padding(List<RengineEvent> events) {
        safeList(events).parallelStream().forEach(event -> paddingCache.put(event.getId(), event));
    }

    @Override
    public void completed(List<RengineEvent> events) {
        safeList(events).parallelStream().forEach(event -> completedCache.put(event.getId(), valueOf(currentTimeMillis())));
    }

}
