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
package com.wl4g.rengine.executor.metrics;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.wl4g.infra.common.metrics.PrometheusMeterFacade;
import com.wl4g.infra.common.net.InetUtils;
import com.wl4g.infra.common.net.InetUtils.InetUtilsProperties;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {@linkplain @ApplicationScoped} vs {@linkplain @Singleton} see:
 * https://quarkus.io/guides/cdi#applicationscoped-and-singleton-look-very-similar-which-one-should-i-choose-for-my-quarkus-application
 * 
 * {@linkplain @Singleton}: Better performance because there is no client proxy.
 * 
 * @author Wangl.sir &lt;James Wong <jameswong1376@gmail.com>&gt;
 * @version 2022-07-12
 * @since v1.0.0
 */
// @ApplicationScoped
@Singleton
public class ExecutorMeterService extends PrometheusMeterFacade {

    @Inject
    public ExecutorMeterService(PrometheusMeterRegistry meterRegistry,
            @ConfigProperty(name = "quarkus.application.name") String serviceId,
            @ConfigProperty(name = "quarkus.http.port") int port) {
        super(meterRegistry, serviceId, false, new InetUtils(new InetUtilsProperties()), port);
    }

    @Getter
    @AllArgsConstructor
    public static enum MetricsName {

        execution_total("execution_total", "The stats of execute total"),

        execution_success("execution_success", "The stats of execute success"),

        execution_failure("execution_failure", "The stats of execute failure"),

        execution_time("execution_time", "The stats of execute execution time"),

        execution_sdk_datasource_facade_total("execution_sdk_datasource_facade_total",
                "The stats of datasource facade execute total"),

        execution_sdk_datasource_facade_success("execution_sdk_datasource_facade_success",
                "The stats of datasource facade execute success"),

        execution_sdk_datasource_facade_failure("execution_sdk_datasource_facade_failure",
                "The stats of datasource facade execute failure"),

        execution_sdk_datasource_facade_time("execution_sdk_datasource_facade_failure",
                "The stats of datasource facade execute time"),

        execution_sdk_notifier_total("execution_sdk_datasource_facade_total", "The stats of datasource facade execute total"),

        execution_sdk_notifier_success("execution_sdk_notifier_success", "The stats of notifier execute success"),

        execution_sdk_notifier_failure("execution_sdk_notifier_failure", "The stats of notifier execute failure"),

        execution_sdk_notifier_time("execution_sdk_notifier_failure", "The stats of notifier execute time"),

        execution_sdk_client_total("execution_sdk_client_total", "The stats of sdk client execute total"),

        execution_sdk_client_success("execution_sdk_client_success", "The stats of sdk client execute success"),

        execution_sdk_client_failure("execution_sdk_client_failure", "The stats of sdk client execute failure"),

        execution_sdk_client_time("execution_sdk_client_time", "The stats of sdk client execute time");

        private final String name;
        private final String help;
    }

    public static abstract class MetricsTag {
        public static final String DS_TYPE = "dsType";
        public static final String DS_NAME = "dsName";
        public static final String METHOD_NAME = "method";

        public static final String SDK_TYPE = "sdkType";

        public static final String ENGINE = "engine";

        public static final String SCENESCODE = "scenesCode";
        public static final String CLIENT_ID = "clientId";
        public static final String LIBRARY = "library";
    }

    public static final double[] DEFAULT_PERCENTILES = new double[] { 0.5, 0.9, 0.95 };

}
