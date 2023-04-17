/*
 * Copyright 2017 ~ 2025 the original author or authors. James Wong <wanglsir@gmail.com>
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
package com.wl4g.rengine.service.meter;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;

import com.wl4g.infra.common.metrics.PrometheusMeterFacade;
import com.wl4g.infra.common.net.InetUtils;
import com.wl4g.infra.common.net.InetUtils.InetUtilsProperties;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {@link RengineMeterService}
 * 
 * @author James Wong
 * @date 2023-02-04
 * @since v1.0.0
 */
public class RengineMeterService extends PrometheusMeterFacade {

    public RengineMeterService(PrometheusMeterRegistry meterRegistry, String serviceId, int port) {
        super(meterRegistry, hasTextOf(serviceId, "serviceId"), false, new InetUtils(new InetUtilsProperties()), port);
    }

    @Getter
    @AllArgsConstructor
    public static enum MetricsName {

        // Global master controller metrics.

        global_master_controller("global_master_controller_total", "The stats of schedule controller total"),

        global_master_controller_success("global_master_controller_success", "The stats of schedule controller success"),

        global_master_controller_failure("global_master_controller_failure", "The stats of schedule controller failure"),

        global_master_controller_time("global_master_controller_time", "The stats of schedule controller time");

        private final String name;
        private final String help;
    }

    public static abstract class MetricsTag {
        public static final String METHOD_NAME = "method";
    }

    public static final double[] DEFAULT_PERCENTILES = new double[] { 0.5, 0.9, 0.95 };

}
