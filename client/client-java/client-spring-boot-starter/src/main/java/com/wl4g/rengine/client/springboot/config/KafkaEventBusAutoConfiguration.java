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
package com.wl4g.rengine.client.springboot.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import com.wl4g.rengine.eventbus.KafkaEventBusService;
import com.wl4g.rengine.eventbus.RengineEventBusService;
import com.wl4g.rengine.eventbus.config.ClientEventBusConfig;
import com.wl4g.rengine.eventbus.recorder.EventRecorder;

/**
 * {@link KafkaEventBusAutoConfiguration}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @date 2022-05-30 v3.0.0
 * @since v1.0.0
 */
@ConditionalOnClass(KafkaEventBusService.class)
public class KafkaEventBusAutoConfiguration {

    @Bean
    public RengineEventBusService<?> kafkaEventBusService(ClientEventBusConfig eventBusConfig, EventRecorder eventRecorder) {
        return new KafkaEventBusService(eventBusConfig, eventRecorder);
    }

}
