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
package com.wl4g.rengine.executor.meter;

import org.junit.BeforeClass;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

/**
 * {@link TestDefaultMeterSetup}
 * 
 * @author James Wong
 * @date 2022-12-31
 * @since v1.0.0
 */
public class TestDefaultMeterSetup {

    @BeforeClass
    public static RengineExecutorMeterService setup() {
        return MeterUtil.meterService = new RengineExecutorMeterService(new PrometheusMeterRegistry(PrometheusConfig.DEFAULT),
                "rengine-executor", 28002);
    }

}
