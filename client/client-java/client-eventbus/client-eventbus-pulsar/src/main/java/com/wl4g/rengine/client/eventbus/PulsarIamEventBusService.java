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
package com.wl4g.rengine.client.eventbus;

import java.util.concurrent.Future;

import org.springframework.beans.factory.InitializingBean;

import com.wl4g.rengine.client.eventbus.config.PulsarEventBusProperties;
import com.wl4g.rengine.client.eventbus.config.RengineEventBusProperties;
import com.wl4g.rengine.common.event.RengineEvent;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link LoggingRengineEventBusService}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-30 v3.0.0
 * @since v3.0.0
 */
@Getter
@CustomLog
@AllArgsConstructor
public class PulsarIamEventBusService implements RengineEventBusService<Void>, InitializingBean {

    private final RengineEventBusProperties eventBusConfig;
    private final PulsarEventBusProperties pulsarEventBusConfig;

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public Object getOriginal() {
        return null; // TODO
    }

    @Override
    public Future<Void> publish(RengineEvent event) {
        log.debug("Send: {}", event); // TODO
        return null;
    }

}
