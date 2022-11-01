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

import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import java.util.List;
import java.util.concurrent.Future;

import com.wl4g.infra.common.log.SmartLogger;
import com.wl4g.infra.common.log.SmartLoggerFactory;
import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.eventbus.config.ClientEventBusConfig;
import com.wl4g.rengine.eventbus.recorder.EventRecorder;

import lombok.Getter;

/**
 * {@link AbstractEventBusService}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-30 v3.0.0
 * @since v1.0.0
 */
@Getter
public abstract class AbstractEventBusService<R> implements RengineEventBusService<R> {
    protected final SmartLogger log = SmartLoggerFactory.getLogger(getClass());

    protected final ClientEventBusConfig eventBusConfig;
    protected final EventRecorder recorder;

    public AbstractEventBusService(ClientEventBusConfig eventBusConfig, EventRecorder recorder) {
        this.eventBusConfig = notNullOf(eventBusConfig, "eventBusConfig");
        this.recorder = notNullOf(recorder, "recorder");
    }

    @Override
    public List<Future<R>> publish(List<RengineEvent> events) {
        recorder.padding(events);
        return doPublish(events);
    }

    protected abstract List<Future<R>> doPublish(List<RengineEvent> events);

}
