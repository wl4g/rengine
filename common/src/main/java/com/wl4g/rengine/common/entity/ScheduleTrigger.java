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
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseMapObject;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.Max;
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

    private @NotBlank String name;
    // ElasticJob standard configuration.
    // private @NotBlank @Default String cron = DEFAULT_CRON;
    private @NotNull @Default Boolean monitorExecution = DEFAULT_MONITOR_EXECUTION;
    private @NotNull @Default Boolean failover = DEFAULT_FAILOVER;
    private @NotNull @Default Boolean misfire = DEFAULT_MISFIRE;
    private @NotBlank @Default String timeZone = DEFAULT_TIME_ZONE;
    private @NotNull @Default Integer maxTimeDiffSeconds = DEFAULT_MAX_TIME_DIFF_SECONDS;
    private @NotNull @Default Integer reconcileIntervalMinutes = DEFAULT_RECONCILE_INTERVAL_MINUTES;
    // The engine schedule extra configuration.
    private @NotNull @Min(1) @Default Long maxTimeoutMs = DEFAULT_MAX_TIMEOUT_MS;
    private RunState runState;

    public ScheduleTrigger validate() {
        // hasTextOf(getCron(), "cron");
        notNullOf(getMonitorExecution(), "monitorExecution");
        notNullOf(getFailover(), "failover");
        notNullOf(getMisfire(), "misfire");
        notNullOf(getTimeZone(), "timeZone");
        notNullOf(getMaxTimeDiffSeconds(), "maxTimeDiffSeconds");
        notNullOf(getReconcileIntervalMinutes(), "reconcileIntervalMinutes");
        isTrueOf(getMaxTimeoutMs() > 0, "maxTimeoutMs > 0");
        return this;
    }

    @NotNull
    TriggerPropertiesBase<?> properties;

    public static enum ScheduleType {
        EXECUTION_SCHEDULER, KAFKA_SUBSCRIBE_SCHEDULER, FLINK_SUBMIT_SCHEDULER,

        // Notice: The loop controller can be customized in the js rule codes.
        // /**
        // * Long-running tasks (infinite loop), for example, a controller
        // * workflow that calls the kubernetes client api to monitor resource
        // * changes, which usually never exits.
        // */
        // LOOP;
    }

    @Getter
    @ToString
    public static enum RunState {
        PREPARED, SCHED, FAILED_SCHED, RUNNING, PART_SUCCESS, SUCCESS, FAILED, KILLED;

        public boolean isSuccess() {
            return this == SUCCESS;
        }
    }

    @Schema(oneOf = { ExecutionScheduleConfig.class, FlinkSubmitScheduleConfig.class, KafkaSubscribeScheduleConfig.class },
            discriminatorProperty = "type")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
    @JsonSubTypes({ @Type(value = ExecutionScheduleConfig.class, name = "EXECUTION_SCHEDULER"),
            @Type(value = KafkaSubscribeScheduleConfig.class, name = "KAFKA_SUBSCRIBE_SCHEDULER"),
            @Type(value = FlinkSubmitScheduleConfig.class, name = "FLINK_SUBMIT_SCHEDULER") })
    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static abstract class TriggerPropertiesBase<T extends TriggerPropertiesBase<T>> {

        @Schema(name = "type", implementation = ScheduleType.class)
        @JsonProperty(value = "type", access = Access.WRITE_ONLY)
        @NotNull
        private @NotBlank @EnumValue(enumCls = ScheduleType.class) String type;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class ExecutionScheduleConfig extends TriggerPropertiesBase<ExecutionScheduleConfig> {
        // ElasticJob standard configuration.
        private @NotBlank @Default String cron = DEFAULT_CRON; // ScheduleJobBootstrap
        // Other configuration.
        private @NotNull List<ExecuteRequest> requests;

        public ExecutionScheduleConfig validate() {
            hasTextOf(getCron(), "cron");
            notNullOf(getRequests(), "request");
            return this;
        }
    }

    /**
     * Here is the properties configuration of kafka consumer see
     * {@link org.apache.kafka.clients.consumer.ConsumerConfig#CONFIG}, and the
     * properties configuration of kafka consumer see
     * {@link com.wl4g.rengine.common.entity.DataSourceProperties.KafkaDataSourceProperties}
     */
    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class KafkaSubscribeScheduleConfig extends TriggerPropertiesBase<KafkaSubscribeScheduleConfig> {

        // Common config options.

        @JsonProperty("key.deserializer")
        @NotBlank
        @Default
        String keySerializer = "org.apache.kafka.common.serialization.StringDeserializer";

        @JsonProperty("value.deserializer")
        @NotBlank
        @Default
        String valueSerializer = "org.apache.kafka.common.serialization.StringDeserializer";

        @JsonProperty("bootstrap.servers")
        @NotBlank
        @Default
        String bootstrapServers = "localhost:9092";

        /**
         * @see {@link org.apache.kafka.clients.ClientDnsLookup}
         */
        @JsonProperty("client.dns.lookup")
        @NotBlank
        @Default
        String clientDnsLookup = "use_all_dns_ips";

        @JsonProperty("metadata.max.age.ms")
        @NotNull
        @Default
        @Min(0)
        Long metadataMaxAgeMs = 5 * 60 * 1000L;

        @JsonProperty("send.buffer.bytes")
        @NotNull
        @Default
        @Min(-1)
        Integer sendBufferBytes = 128 * 1024;

        @JsonProperty("receive.buffer.bytes")
        @NotNull
        @Default
        @Min(-1)
        Integer receiveBufferBytes = 64 * 1024;

        @JsonProperty("client.id")
        String clientId;

        @JsonProperty("client.rack")
        @Default
        String clientRack = "";

        @JsonProperty("reconnect.backoff.ms")
        @NotNull
        @Default
        @Min(0)
        Long reconnectBackoffMs = 50L;

        @JsonProperty("reconnect.backoff.max.ms")
        @Min(0)
        @Default
        Long reconnectBackoffMaxMs = 1000L;

        @JsonProperty("retries")
        @Min(0)
        @Default
        Integer retries = Integer.MAX_VALUE;

        @JsonProperty("retry.backoff.ms")
        @Min(0)
        @Default
        Long retryBackoffMs = 100L;

        @JsonProperty("metrics.sample.window.ms")
        @Min(0)
        @Default
        Long metricsSampleWindowMs = 3000L;

        @JsonProperty("metrics.num.samples")
        @Min(1)
        @Default
        Integer metricsNumSamples = 2;

        @JsonProperty("metrics.recording.level")
        @NotBlank
        @Default
        String metricsRecordingLevel = "INFO";

        @JsonProperty("metric.reporters")
        @Default
        List<String> metricsReporters = emptyList();

        @JsonProperty("security.protocol")
        @NotBlank
        @Default
        String securityProtocol = "PLAINTEXT";

        @JsonProperty("socket.connection.setup.timeout.ms")
        @NotNull
        @Min(0)
        @Default
        Long socketConnectionSetupTimeoutMs = 10 * 1000L;

        @JsonProperty("socket.connection.setup.timeout.max.ms")
        @Min(0)
        @Default
        Long socketConnectionSetupTimeoutMaxMs = 30 * 1000L;

        @JsonProperty("connections.max.idle.ms")
        @NotNull
        @Default
        Long connectionsMaxIdleMs = 9 * 60 * 1000L;

        @JsonProperty("request.timeout.ms")
        @NotNull
        @Min(0)
        @Default
        Integer requestTimeoutMs = 30 * 1000;

        @JsonProperty("group.id")
        @NotBlank
        @Default
        String groupId = DEFAULT_GROUP_ID;

        @JsonProperty("group.instance.id")
        String groupInstanceId;

        @JsonProperty("max.poll.interval.ms")
        @NotNull
        @Min(1)
        @Default
        Integer maxPollIntervalMs = 300000;

        @JsonProperty("rebalance.timeout.ms")
        Integer rebalanceTimeoutMs;

        @JsonProperty("session.timeout.ms")
        @Default
        Integer sessionTimeoutMs = 45000;

        @JsonProperty("heartbeat.interval.ms")
        @Default
        Integer heartbeatIntervalMs = 3000;

        @JsonProperty("default.api.timeout.ms")
        @NotNull
        @Min(0)
        @Default
        Integer defaultApiTimeoutMs = 60 * 1000;

        // Consumer config options.

        @JsonProperty("enable.auto.commit")
        @Default
        Boolean enableAutoCommit = false;

        @JsonProperty("auto.commit.interval.ms")
        @Default
        @Min(0)
        Integer autoCommitIntervalMs = 5000;

        // in ("latest", "earliest", "none")
        @JsonProperty("auto.offset.reset")
        @NotBlank
        @Default
        String autoOffsetReset = "latest";

        @JsonProperty("fetch.min.bytes")
        @Default
        @Min(0)
        Integer fetchMinBytes = 1;

        @JsonProperty("fetch.max.bytes")
        @Default
        @Min(0)
        Integer fetchMaxBytes = 50 * 1024 * 1024;

        @JsonProperty("fetch.max.wait.ms")
        @Default
        @Min(0)
        Integer fetchMaxWaitMs = 500;

        // Other properties.

        @NotBlank
        List<String> topics;

        @NotNull
        @Default
        @Min(1)
        @Max(100)
        Integer concurrency = 1;

        @NotNull
        @Default
        Boolean autoAcknowledgment = true;

        @NotNull
        ExecuteRequest request;

        public Map<String, Object> toConfigMap() {
            return parseMapObject(toJSONString(this));
        }

        public KafkaSubscribeScheduleConfig validate() {
            // Common config options.
            hasTextOf(keySerializer, "keySerializer");
            hasTextOf(valueSerializer, "valueSerializer");
            hasTextOf(bootstrapServers, "bootstrapServers");
            hasTextOf(clientDnsLookup, "clientDnsLookup");
            isTrueOf(nonNull(metadataMaxAgeMs) && metadataMaxAgeMs >= 0, "metadataMaxAgeMs >= 0");
            isTrueOf(nonNull(sendBufferBytes) && sendBufferBytes >= -1, "sendBufferBytes >= -1");
            isTrueOf(nonNull(receiveBufferBytes) && receiveBufferBytes >= -1, "receiveBufferBytes >= -1");
            isTrueOf(nonNull(reconnectBackoffMs) && reconnectBackoffMs >= 0, "reconnectBackoffMs >= 0");
            isTrueOf(nonNull(reconnectBackoffMaxMs) && reconnectBackoffMaxMs >= 0, "reconnectBackoffMaxMs >= 0");
            isTrueOf(nonNull(retries) && retries >= 0, "retries >= 0");
            isTrueOf(nonNull(retryBackoffMs) && retryBackoffMs >= 0, "retryBackoffMs >= 0");
            isTrueOf(nonNull(metricsSampleWindowMs) && metricsSampleWindowMs >= 0, "metricsSampleWindowMs >= 0");
            isTrueOf(nonNull(metricsNumSamples) && metricsNumSamples >= 1, "retryBackoffMs >= 1");
            hasTextOf(metricsRecordingLevel, "metricsRecordingLevel");
            hasTextOf(securityProtocol, "securityProtocol");
            isTrueOf(nonNull(socketConnectionSetupTimeoutMs) && socketConnectionSetupTimeoutMs >= 0,
                    "socketConnectionSetupTimeoutMs >= 0");
            isTrueOf(nonNull(socketConnectionSetupTimeoutMaxMs) && socketConnectionSetupTimeoutMaxMs >= 0,
                    "socketConnectionSetupTimeoutMaxMs >= 0");
            isTrueOf(nonNull(connectionsMaxIdleMs) && connectionsMaxIdleMs >= 0, "connectionsMaxIdleMs >= 0");
            isTrueOf(nonNull(requestTimeoutMs) && requestTimeoutMs >= 0, "requestTimeoutMs >= 0");
            hasTextOf(groupId, "groupId");
            isTrueOf(nonNull(maxPollIntervalMs) && maxPollIntervalMs >= 1, "maxPollIntervalMs >= 1");
            // isTrueOf(nonNull(rebalanceTimeoutMs) && rebalanceTimeoutMs >= 1,
            // "rebalanceTimeoutMs >= 1");
            // isTrueOf(nonNull(sessionTimeoutMs) && sessionTimeoutMs >= 1,
            // "sessionTimeoutMs >= 1");
            // isTrueOf(nonNull(heartbeatIntervalMs) && heartbeatIntervalMs >=
            // 1, "heartbeatIntervalMs >= 1");
            isTrueOf(nonNull(defaultApiTimeoutMs) && defaultApiTimeoutMs >= 0, "defaultApiTimeoutMs >= 0");
            // Consumer config optiosn.
            notNullOf(enableAutoCommit, "enableAutoCommit");
            isTrueOf(nonNull(autoCommitIntervalMs) && autoCommitIntervalMs >= 0, "autoCommitIntervalMs >= 0");
            isTrueOf(equalsAnyIgnoreCase(autoOffsetReset, "latest", "earliest", "none"),
                    "autoOffsetReset must in ('latest', 'earliest', 'none')");
            isTrueOf(nonNull(fetchMinBytes) && fetchMinBytes >= 1, "fetchMinBytes >= 1");
            isTrueOf(nonNull(fetchMaxBytes) && fetchMaxBytes >= 0, "fetchMaxBytes >= 0");
            isTrueOf(nonNull(fetchMaxWaitMs) && fetchMaxWaitMs >= 0, "fetchMaxWaitMs >= 0");
            // Other properties.
            notEmptyOf(topics, "topics");
            isTrueOf(nonNull(concurrency) && concurrency >= 0 && concurrency <= 100, "concurrency >= 1 && concurrency <= 100");
            notNullOf(autoAcknowledgment, "autoAcknowledgment");
            notNullOf(request, "request");
            return this;
        }

        public static final String DEFAULT_GROUP_ID = "default-rengine-controller-subscriber";
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class FlinkSubmitScheduleConfig extends TriggerPropertiesBase<FlinkSubmitScheduleConfig> {

        public FlinkSubmitScheduleConfig validate() {
            return this;
        }
    }

    public static final String DEFAULT_CRON = "0/10 * * * * ?";
    public static final boolean DEFAULT_MONITOR_EXECUTION = true;
    public static final boolean DEFAULT_FAILOVER = true;
    public static final boolean DEFAULT_MISFIRE = false;
    public static final String DEFAULT_TIME_ZONE = "GMT+08:00";
    public static final int DEFAULT_MAX_TIME_DIFF_SECONDS = -1;
    public static final int DEFAULT_RECONCILE_INTERVAL_MINUTES = 0;
    public static final long DEFAULT_MAX_TIMEOUT_MS = 30_000L;
}
