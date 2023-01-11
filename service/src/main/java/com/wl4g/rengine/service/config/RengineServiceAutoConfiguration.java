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
package com.wl4g.rengine.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.service.minio.MinioClientAutoConfiguration;
import com.wl4g.rengine.service.mongo.CustomMongoConfigure;

/**
 * {@link RengineServiceAutoConfiguration}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Configuration
@Import({ MinioClientAutoConfiguration.class, CustomMongoConfigure.class })
public class RengineServiceAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = RengineConstants.CONF_PREFIX)
    public RengineServiceProperties rengineServiceProperties() {
        return new RengineServiceProperties();
    }

}