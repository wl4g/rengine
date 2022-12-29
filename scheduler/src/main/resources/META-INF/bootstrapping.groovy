/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <jameswong1376@gmail.com> Technology CO.LTD.
 * All rights reserved.
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

import static com.wl4g.infra.common.lang.ClassUtils2.isPresent
import static com.wl4g.infra.common.runtime.JvmRuntimeTool.isJvmInDebugging
import static org.springframework.boot.context.config.ConfigFileApplicationListener.*

import org.springframework.boot.Banner

import com.wl4g.infra.context.boot.listener.IBootstrappingConfigurer

/**
 * Rengine implementation of {@link IBootstrappingConfigurer}
 */
class RengineCollectorBootstrappingConfigurer implements IBootstrappingConfigurer {

    @Override
    def int getOrder() {
        return -100
    }

    @Override
    void defaultProperties(Properties prevDefaultProperties) {
        // Preset spring.config.name
        // for example: spring auto load for 'application-dev.yml/application-data-dev.yml'
        def configName = new StringBuffer("application,scheduler")

        // Preset spring.config.additional-location
        def additionalLocation = new StringBuffer("classpath:/")

        // if (isJvmInDebugging) {
        //     System.out.println("Activation configuration location 'classpath:/config-dev/' ...")
        //     additionalLocation.append(",classpath:/config-dev/")
        // } else {
        //     System.out.println("Activation configuration location 'classpath:/config-pro/' ...")
        //     additionalLocation.append(",classpath:/config-pro/")
        // }

        // Preset 'spring.config.additional-location', external resources does not override resources in classpath.
        prevDefaultProperties.put(CONFIG_NAME_PROPERTY, configName.toString())
        prevDefaultProperties.put(CONFIG_ADDITIONAL_LOCATION_PROPERTY, additionalLocation.toString())

        // ElasticJob is deeply integrated, and key configuration mandatory setup and are not allowed to be modified.
        // see:org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobLiteAutoConfiguration
        prevDefaultProperties.put("elasticjob.enabled", "true")
    }

}