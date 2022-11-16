/*
 * Copyright 2017 ~ 2025 the original authors James Wong.
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
package com.wl4g.rengine.eventbus;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.eventbus.config.ClientEventBusConfig;
import com.wl4g.rengine.eventbus.recorder.EventRecorder;

import lombok.AllArgsConstructor;
import lombok.CustomLog;

/**
 * {@link LoggingEventBusService}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-30 v3.0.0
 * @since v1.0.0
 */
@CustomLog
public class LoggingEventBusService extends AbstractEventBusService<Future<RengineEvent>> {

    public LoggingEventBusService(ClientEventBusConfig eventBusConfig, EventRecorder recorder) {
        super(eventBusConfig, recorder);
    }

    @Override
    public Object getOriginal() {
        return null;
    }

    @Override
    public List<Future<RengineEvent>> doPublish(List<RengineEvent> events) {
        log.info("(PRETEND) Sending event: {}", events);
        return safeList(events).stream().map(e -> new NoneFuture(e)).collect(toList());
    }

    @AllArgsConstructor
    static class NoneFuture implements Future<RengineEvent> {
        private final RengineEvent event;

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public RengineEvent get() throws InterruptedException, ExecutionException {
            return event;
        }

        @Override
        public RengineEvent get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return event;
        }
    }

}
