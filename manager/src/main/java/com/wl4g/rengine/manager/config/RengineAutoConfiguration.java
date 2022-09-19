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
package com.wl4g.rengine.manager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.wl4g.rengine.manager.constants.RengineManagerConstants;
import com.wl4g.rengine.manager.minio.MinioClientAutoConfiguration;

/**
 * {@link RengineAutoConfiguration}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v3.0.0
 */
@Configuration
@Import({ MinioClientAutoConfiguration.class })
public class RengineAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = RengineManagerConstants.CONF_PREFIX)
    public RegineProperties regineProperties() {
        return new RegineProperties();
    }

}