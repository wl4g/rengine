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

import java.util.function.Function;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.rengine.client.core.RengineClient;
import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.common.model.EvaluationResult;

/**
 * {@link ClientCoreAutoConfiguration}
 * 
 * @author James Wong
 * @version 2022-10-17
 * @since v3.0.0
 */
@Configuration
@ConditionalOnClass(RengineClient.class)
public class ClientCoreAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = RengineConstants.CONF_PREFIX_CLIENT_CORE)
    public ClientCoreProperties clientCoreProperties() {
        return new ClientCoreProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public Function<Throwable, EvaluationResult> defaultFailback() {
        return new DefaultFailback();
    }

    @Bean
    @ConditionalOnMissingBean
    public RengineClient rengineClient(ClientCoreProperties config, Function<Throwable, EvaluationResult> failback) {
        return RengineClient.builder().config(config).failback(failback).build();
    }

    public static class DefaultFailback implements Function<Throwable, EvaluationResult> {
        @Override
        public EvaluationResult apply(Throwable t) {
            System.err.println("Failed to evaluation of reason: ");
            return null;
        }
    }

}