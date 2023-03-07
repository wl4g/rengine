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
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.eventbus;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Collections.singletonList;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.wl4g.infra.common.task.GenericTaskRunner;
import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;
import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.eventbus.config.ClientEventBusConfig;
import com.wl4g.rengine.eventbus.recorder.EventRecorder;

import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link AbstractEventBusService}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-30 v3.0.0
 * @since v1.0.0
 */
@CustomLog
@Getter
public abstract class AbstractEventBusService<R> implements RengineEventBusService<R>, Closeable {
    protected final ClientEventBusConfig config;
    protected final EventRecorder recorder;
    protected final SafeScheduledTaskPoolExecutor compactExecutor;

    public AbstractEventBusService(ClientEventBusConfig config, EventRecorder recorder) {
        this.config = notNullOf(config, "details");
        this.recorder = notNullOf(recorder, "recorder");
        this.compactExecutor = GenericTaskRunner.newDefaultScheduledExecutor(AbstractEventBusService.class.getSimpleName(), 1, 4);
        // Timing scheduling execution.
        this.compactExecutor.schedule(() -> recorder.compact(event -> doPublish(singletonList(event))),
                config.getRecorder().getCompaction().getDelaySeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void close() throws IOException {
        this.compactExecutor.shutdownNow();
    }

    @Override
    public List<R> publishs(List<RengineEvent> events) {
        try {
            recorder.padding(events);
            return doPublish(events);
        } catch (Exception e) {
            log.error(format("Unable to publish events. - %s", events), e);
            return null;
        }
    }

    protected abstract List<R> doPublish(List<RengineEvent> events) throws Exception;

}
