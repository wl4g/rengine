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
package com.wl4g.rengine.client.springboot.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.wl4g.rengine.client.eventbus.LoggingEventBusService;
import com.wl4g.rengine.client.eventbus.RengineEventBusService;
import com.wl4g.rengine.client.eventbus.config.ClientEventBusConfig;
import com.wl4g.rengine.client.eventbus.recorder.EhcacheEventRecorder;
import com.wl4g.rengine.client.eventbus.recorder.EventRecorder;
import com.wl4g.rengine.common.constants.RengineConstants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link ClientEventbusAutoConfiguration}
 * 
 * @author James Wong
 * @version 2022-10-17
 * @since v3.0.0
 */
@Configuration
@ConditionalOnClass(RengineEventBusService.class)
@Import({ KafkaEventBusAutoConfiguration.class, PulsarEventBusAutoConfiguration.class, RabbitmqEventBusAutoConfiguration.class })
public class ClientEventbusAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = RengineConstants.CONF_PREFIX_CLIENT_EVENTBUS)
    public ClientEventbusProperties clientEventbusProperties() {
        return new ClientEventbusProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public EventRecorder encacheEventRecorder(ClientEventbusProperties config) {
        return new EhcacheEventRecorder(config);
    }

    @Bean
    public RengineEventBusService<?> loggingEventBusService(ClientEventbusProperties config, EventRecorder recorder) {
        return new LoggingEventBusService(config, recorder);
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class ClientEventbusProperties extends ClientEventBusConfig {
    }

}
