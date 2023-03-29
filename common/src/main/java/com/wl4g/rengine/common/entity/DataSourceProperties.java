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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseMapObject;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.replace;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.rengine.common.exception.ConfigRengineException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link DataSourceProperties}
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
public class DataSourceProperties extends BaseEntity {
    static final long serialVersionUID = -7441054887057231030L;

    @NotBlank
    String dsCode;

    @NotNull
    DataSourcePropertiesBase details;

    // Notice: It is recommended to disable the toString method, otherwise
    // swagger will generate the name of the example long enumeration type by
    // default.
    // @ToString
    @Getter
    @AllArgsConstructor
    public static enum DataSourceType {

        MONGO(MongoDataSourceProperties.class),

        JDBC(JDBCDataSourceProperties.class),

        REDIS(RedisDataSourceProperties.class),

        KAFKA(KafkaDataSourceProperties.class);

        final Class<? extends DataSourcePropertiesBase> propertiesClass;

        public static DataSourceType of(final @NotBlank String type) {
            hasTextOf(type, "type");
            for (DataSourceType a : values()) {
                if (a.name().equalsIgnoreCase(type)) {
                    return a;
                }
            }
            throw new IllegalArgumentException(format("Invalid upload type for '%s'", type));
        }
    }

    @Schema(oneOf = { MongoDataSourceProperties.class, JDBCDataSourceProperties.class, RedisDataSourceProperties.class,
            KafkaDataSourceProperties.class }, discriminatorProperty = "type")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
    @JsonSubTypes({ @Type(value = MongoDataSourceProperties.class, name = "MONGO"),
            @Type(value = JDBCDataSourceProperties.class, name = "JDBC"),
            @Type(value = RedisDataSourceProperties.class, name = "REDIS"),
            @Type(value = KafkaDataSourceProperties.class, name = "KAFKA") })
    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static abstract class DataSourcePropertiesBase {

        @Schema(name = "type", implementation = DataSourceType.class)
        @JsonProperty(value = "type", access = Access.WRITE_ONLY)
        @NotNull
        transient DataSourceType type;

        public abstract DataSourcePropertiesBase validate();
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class MongoDataSourceProperties extends DataSourcePropertiesBase {
        static final long serialVersionUID = 7657027064377820844L;

        /**
         * for example: mongodb://localhost:27017
         */
        @NotBlank
        String connectionString;

        @Override
        public DataSourcePropertiesBase validate() {
            hasTextOf(connectionString, "connectionString");
            return this;
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class JDBCDataSourceProperties extends DataSourcePropertiesBase {
        static final long serialVersionUID = -7444473046858372046L;

        /**
         * The direction for fetching rows from database tables.
         */
        Integer fetchDirection;

        /**
         * The number of rows that should be fetched from the database when more
         * rows are needed.
         */
        @Default
        Integer fetchSize = 10_0000;

        /**
         * The maximum number of bytes that can be returned for character and
         * binary column values.
         */
        @Default
        Integer maxFieldSize = 64;

        /**
         * The maximum number of rows that a <code>ResultSet</code> can produce.
         */
        @Default
        Integer maxRows = 1024;

        /**
         * The number of milis the driver will wait for execution.
         */
        @Default
        Long queryTimeoutMs = Duration.ofSeconds(15).toMillis();

        //
        // see:org.apache.commons.dbcp2.BasicDataSource
        //

        @NotBlank
        @Default
        String driverClassName = "com.mysql.cj.jdbc.Driver";

        @NotBlank
        @Default
        String jdbcUrl = "jdbc:mysql://localhost:3306/mysql?useUnicode=true&serverTimezone=UTC&characterEncoding=utf-8&useSSL=false";

        @NotBlank
        @Default
        String username = "root";

        @NotBlank
        @Default
        String password = "123456";

        @Default
        Long connectionTimeout = SECONDS.toMillis(30);

        @Default
        Long validationTimeout = SECONDS.toMillis(5);

        @Default
        String validationTestSql = "SELECT 1";

        @Default
        Long idleTimeout = MINUTES.toMillis(10);

        @Default
        Long softMinIdleTimeout = -1L;

        @Default
        Long maxConnLifeTime = -1L;

        @Default
        Long evictionRunsBetweenTime = -1L;

        @Default
        Integer initPoolSize = 5;

        @Default
        Integer maximumPoolSize = 20;

        @Default
        Integer minimumIdle = 1;

        @Default
        Boolean autoCommit = true;

        @Default
        Boolean cacheState = true;

        @Default
        Boolean testOnBorrow = false;

        @Default
        Boolean testOnCreate = false;

        @Default
        Boolean testOnReturn = false;

        @Default
        Boolean testWhileIdle = false;

        @Override
        public DataSourcePropertiesBase validate() {
            hasTextOf(getDriverClassName(), "driverClassName");
            hasTextOf(getJdbcUrl(), "jdbcUrl");
            hasTextOf(getUsername(), "username");
            hasTextOf(getPassword(), "password");
            isTrueOf(getMaximumPoolSize() > 0, "getMaximumPoolSize()>0");
            return this;
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class RedisDataSourceProperties extends DataSourcePropertiesBase {
        static final long serialVersionUID = -3103084992456055157L;
        private final static int DEFAULT_CONN_TIMEOUT = 10_000;
        private final static int DEFAULT_SO_TIMEOUT = 10_000;
        private final static int DEFAULT_MAX_ATTEMPTS = 3;
        private final static int DEFAULT_DATABASE = 0;
        private final static boolean DEFAULT_SAFE_MOE = true;

        private @Default List<String> nodes = new ArrayList<>();
        private @Nullable String username; // redis6.x
        private @Nullable String password;
        private @Nullable String clientName;
        private @Default int connTimeout = DEFAULT_CONN_TIMEOUT;
        private @Default int soTimeout = DEFAULT_SO_TIMEOUT;
        private @Default int maxAttempts = DEFAULT_MAX_ATTEMPTS;
        private @Default int database = DEFAULT_DATABASE;
        private @Default boolean safeMode = DEFAULT_SAFE_MOE;
        // see:redis.clients.jedis.JedisPoolConfig
        private @Default JedisPoolConfig poolConfig = new JedisPoolConfig();

        @Override
        public DataSourcePropertiesBase validate() {
            notEmptyOf(getNodes(), "nodes");
            if (isEmpty(getNodes())) {
                throw new ConfigRengineException(format(
                        "The number of redis single or cluster connection nodes must be >=1, but actual details nodes : %s",
                        getNodes()));
            }
            // hasTextOf(getUsername(), "username");
            hasTextOf(getPassword(), "password");
            // @formatter:off
            // Check redis nodes size with cluster.
            //if (getNodes().size() < 6) {
            //    throw new ConfigRengineException(format("The number of redis cluster connection nodes must be >=6, but actual details nodes : %s", getNodes()));
            //}
            // @formatter:on
            poolConfig.validate();
            return this;
        }

        @Getter
        @Setter
        @ToString
        @SuperBuilder
        @NoArgsConstructor
        public static class JedisPoolConfig {
            public static final int DEFAULT_MAX_TOTAL = 10;
            public static final int DEFAULT_MAX_IDLE = 5;
            public static final int DEFAULT_MIN_IDLE = 1;

            public static final boolean DEFAULT_LIFO = true;
            public static final boolean DEFAULT_FAIRNESS = false;
            public static final long DEFAULT_MAX_WAIT_MILLIS = 10_000L;
            public static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS = 1000L * 60L * 30L;
            public static final long DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = -1;
            public static final long DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT_MILLIS = 10L * 1000L;
            public static final int DEFAULT_NUM_TESTS_PER_EVICTION_RUN = 3;
            public static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = -1L;
            public static final boolean DEFAULT_TEST_ON_CREATE = false;
            public static final boolean DEFAULT_TEST_ON_BORROW = false;
            public static final boolean DEFAULT_TEST_ON_RETURN = false;
            public static final boolean DEFAULT_TEST_WHILE_IDLE = false;
            public static final boolean DEFAULT_BLOCK_WHEN_EXHAUSTED = true;
            public static final boolean DEFAULT_JMX_ENABLE = true;
            public static final String DEFAULT_JMX_NAME_PREFIX = "pool";
            public static final String DEFAULT_JMX_NAME_BASE = null;

            private @Default @Min(1) @Max(1000) Integer maxTotal = DEFAULT_MAX_TOTAL;
            private @Default Integer maxIdle = DEFAULT_MAX_IDLE;
            private @Default Integer minIdle = DEFAULT_MIN_IDLE;

            private @Default Boolean lifo = DEFAULT_LIFO;
            private @Default Boolean fairness = DEFAULT_FAIRNESS;
            private @Default @Min(1) Long maxWait = DEFAULT_MAX_WAIT_MILLIS;
            private @Default Long minEvictableIdleMs = DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
            private @Default Long evictorShutdownTimeoutMs = DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT_MILLIS;
            private @Default Long softMinEvictableIdleMs = DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
            private @Default Integer numTestsPerEvictionRun = DEFAULT_NUM_TESTS_PER_EVICTION_RUN;
            private @Default Long durationBetweenEvictionRunsMs = DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
            private @Default Boolean testOnCreate = DEFAULT_TEST_ON_CREATE;
            private @Default Boolean testOnBorrow = DEFAULT_TEST_ON_BORROW;
            private @Default Boolean testOnReturn = DEFAULT_TEST_ON_RETURN;
            private @Default Boolean testWhileIdle = DEFAULT_TEST_WHILE_IDLE;
            private @Default Boolean blockWhenExhausted = DEFAULT_BLOCK_WHEN_EXHAUSTED;

            public JedisPoolConfig validate() {
                isTrueOf(nonNull(maxTotal) && maxTotal >= 1 && maxTotal <= 1000, "maxTotal >= 1 && maxTotal <= 1000");
                isTrueOf(nonNull(maxWait) && maxWait >= 1, "maxWait >= 1");
                return this;
            }
        }
    }

    /**
     * Here is the details configuration of kafka producer see
     * {@link org.apache.kafka.clients.producer.ProducerConfig#CONFIG}, and the
     * details configuration of kafka consumer see
     * {@link com.wl4g.rengine.common.entity.Controller.KafkaSubscribeExecutionConfig}
     */
    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class KafkaDataSourceProperties extends DataSourcePropertiesBase {
        public static final long serialVersionUID = -3103084992456055117L;

        // Common config options.

        @JsonProperty("key_serializer")
        @NotBlank
        @Default
        String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";

        @JsonProperty("value_serializer")
        @NotBlank
        @Default
        String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";

        @JsonProperty("bootstrap_servers")
        @NotBlank
        @Default
        String bootstrapServers = "localhost:9092";

        /**
         * @see {@link org.apache.kafka.clients.ClientDnsLookup}
         */
        @JsonProperty("client_dns_lookup")
        @NotBlank
        @Default
        String clientDnsLookup = "use_all_dns_ips";

        @JsonProperty("metadata_max_age_ms")
        @NotNull
        @Default
        @Min(0)
        Long metadataMaxAgeMs = 5 * 60 * 1000L;

        @JsonProperty("send_buffer_bytes")
        @NotNull
        @Default
        @Min(-1)
        Integer sendBufferBytes = 128 * 1024;

        @JsonProperty("receive_buffer_bytes")
        @NotNull
        @Default
        @Min(-1)
        Integer receiveBufferBytes = 64 * 1024;

        @JsonProperty("client_id")
        String clientId;

        @JsonProperty("client_rack")
        @Default
        String clientRack = "";

        @JsonProperty("reconnect_backoff_ms")
        @NotBlank
        @Default
        @Min(0)
        Long reconnectBackoffMs = 50L;

        @JsonProperty("reconnect_backoff_max_ms")
        @Min(0)
        @Default
        Long reconnectBackoffMaxMs = 1000L;

        @JsonProperty("retries")
        @Min(0)
        @Default
        Integer retries = Integer.MAX_VALUE;

        @JsonProperty("retry_backoff_ms")
        @Min(0)
        @Default
        Long retryBackoffMs = 100L;

        @JsonProperty("metrics_sample_window_ms")
        @Min(0)
        @Default
        Long metricsSampleWindowMs = 3000L;

        @JsonProperty("metrics_num_samples")
        @Min(1)
        @Default
        Integer metricsNumSamples = 2;

        @JsonProperty("metrics_recording_level")
        @Default
        String metricsRecordingLevel = "INFO";

        @JsonProperty("metric_reporters")
        @Default
        List<String> metricsReporters = emptyList();

        @JsonProperty("security_protocol")
        @Default
        String securityProtocol = "PLAINTEXT";

        @JsonProperty("socket_connection_setup_timeout_ms")
        @Min(0)
        @Default
        Long socketConnectionSetupTimeoutMs = 10 * 1000L;

        @JsonProperty("socket_connection_setup_timeout_max_ms")
        @Min(0)
        @Default
        Long socketConnectionSetupTimeoutMaxMs = 30 * 1000L;

        @JsonProperty("connections_max_idle_ms")
        @Default
        Long connectionsMaxIdleMs = 9 * 60 * 1000L;

        @JsonProperty("request_timeout_ms")
        @Min(0)
        @Default
        Integer requestTimeoutMs = 30 * 1000;

        @JsonProperty("group_id")
        @Default
        String groupId = "default-rengine-controller";

        @JsonProperty("group_instance_id")
        String groupInstanceId;

        @JsonProperty("max_poll_interval_ms")
        @Default
        @Min(1)
        Integer maxPollIntervalMs = 300000;

        @JsonProperty("rebalance_timeout_ms")
        Integer rebalanceTimeoutMs;

        @JsonProperty("session_timeout_ms")
        @Default
        Integer sessionTimeoutMs = 45000;

        @JsonProperty("heartbeat_interval_ms")
        @Default
        Integer heartbeatIntervalMs = 3000;

        @JsonProperty("default_api_timeout_ms")
        @NotNull
        @Min(0)
        @Default
        Integer defaultApiTimeoutMs = 60 * 1000;

        // Producer config options.

        @JsonProperty("buffer_memory")
        @NotNull
        @Min(0)
        @Default
        Long bufferMemory = 32 * 1024 * 1024L;

        @JsonProperty("acks")
        @NotBlank
        @Default
        String acks = "all";

        @JsonProperty("compression_type")
        @NotBlank
        @Default
        String compressionType = "none";

        @JsonProperty("batch_size")
        @Min(0)
        @Default
        Integer batchSize = 16384;

        @JsonProperty("linger_ms")
        @Min(0)
        @Default
        Integer lingerMs = 0;

        @JsonProperty("delivery_timeout_ms")
        @Min(0)
        @Default
        Integer deliveryTimeoutMs = 120 * 1000;

        @JsonProperty("send_buffer")
        @Min(-1)
        @Default
        Integer sendBuffer = 128 * 1024;

        @JsonProperty("receive_buffer")
        @Min(-1)
        @Default
        Integer receiveBuffer = 32 * 1024;

        @JsonProperty("max_request_size")
        @Min(0)
        @Default
        Integer maxRequestSize = 1024 * 1024;

        @JsonProperty("max_block_ms")
        @Min(0)
        @Default
        Long maxBlockMs = 60 * 1000L;

        @JsonProperty("metadata_max_idle_ms")
        @Min(5000)
        @Default
        Long metadataMaxAge = 5 * 60 * 1000L;

        @JsonProperty("max_in_flight_requests_per_connection")
        @Min(1)
        @Default
        Integer maxInFlightRequestsPerConnection = 5;

        @JsonProperty("transaction_timeout_ms")
        @Min(0)
        @Default
        Integer transactionTimeout = 60000;

        public Map<String, Object> toProducerConfigProperties() {
            return safeMap(parseMapObject(toJSONString(this))).entrySet()
                    .stream()
                    .filter(e -> !isBlank(e.getKey()) && nonNull(e.getValue()))
                    .filter(e -> !"type".equalsIgnoreCase(e.getKey()))
                    .collect(toMap(e -> replace(e.getKey(), "_", "."), e -> e.getValue()));
        }

        @Override
        public KafkaDataSourceProperties validate() {
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
            // Producer config options.
            isTrueOf(nonNull(bufferMemory) && bufferMemory >= 0, "bufferMemory >= 0");
            isTrueOf(equalsAnyIgnoreCase(acks, "all", "1", "0", "-1"), "acks must in (all, 1, 0, -1)");
            hasTextOf(compressionType, "compressionType");
            isTrueOf(nonNull(batchSize) && batchSize >= 0, "batchSize >= 0");
            isTrueOf(nonNull(lingerMs) && lingerMs >= 0, "lingerMs >= 0");
            isTrueOf(nonNull(deliveryTimeoutMs) && deliveryTimeoutMs >= 0, "deliveryTimeoutMs >= 0");
            isTrueOf(nonNull(sendBuffer) && sendBuffer >= -1, "sendBuffer >= -1");
            isTrueOf(nonNull(receiveBuffer) && receiveBuffer >= -1, "receiveBuffer >= -1");
            isTrueOf(nonNull(maxRequestSize) && maxRequestSize >= 0, "maxRequestSize >= 0");
            isTrueOf(nonNull(maxBlockMs) && maxBlockMs >= 0, "maxBlockMs >= 0");
            isTrueOf(nonNull(metadataMaxAge) && metadataMaxAge >= 5000, "metadataMaxAge >= 5000");
            isTrueOf(nonNull(maxInFlightRequestsPerConnection) && maxInFlightRequestsPerConnection >= 1,
                    "maxInFlightRequestsPerConnection >= 1");
            isTrueOf(nonNull(transactionTimeout) && transactionTimeout >= 0, "transactionTimeout >= 0");
            return this;
        }
    }

}
