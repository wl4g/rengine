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
public class EvaluatorMeterService extends PrometheusMeterFacade {

    @Inject
    public EvaluatorMeterService(PrometheusMeterRegistry meterRegistry,
            @ConfigProperty(name = "quarkus.application.name") String serviceId,
            @ConfigProperty(name = "quarkus.http.port") int port) {
        super(meterRegistry, serviceId, false, new InetUtils(new InetUtilsProperties()), port);
    }

    @Getter
    @AllArgsConstructor
    public static enum MetricsName {

        evaluation_total("evaluation_total", "The stats of evaluation total"),

        evaluation_success("evaluation_success", "The stats of evaluation success"),

        evaluation_failure("evaluation_suspicious", "The stats of evaluation failure"),

        evaluation_execute_time("evaluation_execute_time", "The stats of evaluation execute time"),

        evaluation_suspicious("evaluation_suspicious", "The stats of evaluation suspicious");

        private final String name;
        private final String help;
    }

    public static abstract class MetricsTag {
        public static final String ENGINE = "engine";
        public static final String SCENESCODE = "scenesCode";
         public static final String CLIENT_ID = "clientId";
        public static final String LIBRARY = "library";
    }

}
