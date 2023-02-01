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
package com.wl4g.rengine.common.entity;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.infra.common.validation.EnumValue;
import com.wl4g.rengine.common.model.ExecuteRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * A configuration entity that actively triggers the execution of workflow. For
 * example, actively scrape prometheus indicators to realize monitoring and
 * alarming. </br>
 * </br>
 * 
 * For the passive execution of workflow, there is no need to configure a
 * trigger, and it can be requested directly through the SDK or custom executor
 * API. For the entry, see:
 * {@link com.wl4g.rengine.executor.rest.EngineExecutionResource#executeInternal}
 * and
 * {@link com.wl4g.rengine.executor.rest.EngineExecutionResource#doExecuteCustom}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class ScheduleTrigger extends BaseBean {
    private static final long serialVersionUID = 1L;

    @NotNull
    TriggerPropertiesBase<?> properties;

    public ScheduleTrigger validate() {
        getProperties().validate();
        return this;
    }

    @Schema(oneOf = { CronTriggerConfig.class }, discriminatorProperty = "type")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
    @JsonSubTypes({ @Type(value = CronTriggerConfig.class, name = "CRON") })
    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static abstract class TriggerPropertiesBase<T extends TriggerPropertiesBase<T>> {
        @Schema(name = "type", implementation = TriggerType.class)
        @JsonProperty(value = "type", access = Access.WRITE_ONLY)
        @NotNull
        private @NotBlank @EnumValue(enumCls = TriggerType.class) String type;

        public abstract T validate();
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class CronTriggerConfig extends TriggerPropertiesBase<CronTriggerConfig> {
        // ElasticJob standard configuration.
        private @NotBlank @Default String cron = DEFAULT_CRON;
        private @NotNull @Default Boolean monitorExecution = DEFAULT_MONITOR_EXECUTION;
        private @NotNull @Default Boolean failover = DEFAULT_FAILOVER;
        private @NotNull @Default Boolean misfire = DEFAULT_MISFIRE;
        private @NotBlank @Default String timeZone = DEFAULT_TIME_ZONE;
        private @NotNull @Default Integer maxTimeDiffSeconds = DEFAULT_MAX_TIME_DIFF_SECONDS;
        private @NotNull @Default Integer reconcileIntervalMinutes = DEFAULT_RECONCILE_INTERVAL_MINUTES;
        // Engine execution configuration.
        private @NotNull @Min(1) @Default Long maxTimeoutMs = DEFAULT_MAX_TIMEOUT_MS;
        private @NotNull @Min(1) @Default Long awaitMs = DEFAULT_AWAIT_MS;
        private @NotNull List<ExecuteRequest> requests;

        public CronTriggerConfig validate() {
            hasTextOf(getCron(), "cron");
            notNullOf(getMonitorExecution(), "monitorExecution");
            notNullOf(getFailover(), "failover");
            notNullOf(getMisfire(), "misfire");
            notNullOf(getTimeZone(), "timeZone");
            notNullOf(getMaxTimeDiffSeconds(), "maxTimeDiffSeconds");
            notNullOf(getReconcileIntervalMinutes(), "reconcileIntervalMinutes");
            notNullOf(getRequests(), "request");
            isTrueOf(getMaxTimeoutMs() > 0, "maxTimeoutMs > 0");
            isTrueOf(getAwaitMs() > 0, "awaitMs > 0");
            return this;
        }

        public static final String DEFAULT_CRON = "0/10 * * * * ?";
        public static final boolean DEFAULT_MONITOR_EXECUTION = true;
        public static final boolean DEFAULT_FAILOVER = true;
        public static final boolean DEFAULT_MISFIRE = false;
        public static final String DEFAULT_TIME_ZONE = "GMT+08:00";
        public static final int DEFAULT_MAX_TIME_DIFF_SECONDS = -1;
        public static final int DEFAULT_RECONCILE_INTERVAL_MINUTES = 0;
        public static final long DEFAULT_MAX_TIMEOUT_MS = 30_000L;
        public static final long DEFAULT_AWAIT_MS = 50L;
    }

    public static enum TriggerType {

        /**
         * The periodic tasks. For example, actively scrape prometheus
         * indicators to realize monitoring and alarming. </br>
         */
        CRON,

        // Notice: The loop controller can be customized in the js rule codes.
        // /**
        // * Long-running tasks (infinite loop), for example, a controller
        // * workflow that calls the kubernetes client api to monitor resource
        // * changes, which usually never exits.
        // */
        // LOOP;
    }

}
