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
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;

import java.time.Duration;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.infra.common.jedis.JedisClientBuilder.JedisConfig;
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
public class DataSourceProperties extends BaseBean {
    static final long serialVersionUID = -7441054887057231030L;

    @NotBlank
    String name;

    @NotNull
    DataSourcePropertiesBase properties;

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

        @NotNull
        @Default
        JedisConfig jedisConfig = new JedisConfig();

        @Override
        public DataSourcePropertiesBase validate() {
            notNullOf(jedisConfig, "jedisConfig");
            notEmptyOf(jedisConfig.getNodes(), "nodes");
            // hasTextOf(jedisConfig.getUsername(), "username");
            hasTextOf(jedisConfig.getPassword(), "password");

            // Check redis nodes size.
            if (isEmpty(jedisConfig.getNodes())) {
                throw new ConfigRengineException(format(
                        "The number of redis single or cluster connection nodes must be >=1, but actual properties nodes : %s",
                        jedisConfig.getNodes()));
            }

            // @formatter:off
            // Check redis nodes size with cluster.
            //if (jedisConfig.getNodes().size() < 6) {
            //    throw new ConfigRengineException(format("The number of redis cluster connection nodes must be >=6, but actual properties nodes : %s",
            //                jedisConfig.getNodes()));
            //}
            // @formatter:on

            return this;
        }
    }

    /**
     * {@link org.apache.kafka.clients.producer.ProducerConfig#CONFIG}
     */
    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class KafkaDataSourceProperties extends DataSourcePropertiesBase {
        static final long serialVersionUID = -3103084992456055117L;

        @JsonProperty("key.serializer")
        @NotBlank
        @Default
        String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";

        @JsonProperty("value.serializer")
        @NotBlank
        @Default
        String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";

        @JsonProperty("bootstrap.servers")
        @NotBlank
        @Default
        String bootstrapServers = "localhost:9092";

        @JsonProperty("buffer.memory")
        @Min(0)
        @Default
        Long bufferMemory = 32 * 1024 * 1024L;

        @JsonProperty("retries")
        @Min(0)
        @Default
        Integer retries = Integer.MAX_VALUE;

        @JsonProperty("acks")
        @NotBlank
        @Default
        String acks = "all";

        @JsonProperty("compression.type")
        @NotBlank
        @Default
        String compressionType = "none";

        @JsonProperty("batch.size")
        @Min(0)
        @Default
        Integer batchSize = 16384;

        @JsonProperty("linger.ms")
        @Min(0)
        @Default
        Integer lingerMs = 0;

        @JsonProperty("delivery.timeout.ms")
        @Min(0)
        @Default
        Integer deliveryTimeoutMs = 120 * 1000;

        @JsonProperty("send.buffer")
        @Min(-1)
        @Default
        Integer sendBuffer = 128 * 1024;

        @JsonProperty("receive.buffer")
        @Min(-1)
        @Default
        Integer receiveBuffer = 32 * 1024;

        @JsonProperty("max.request.size")
        @Min(0)
        @Default
        Integer maxRequestSize = 1024 * 1024;

        @JsonProperty("reconnect.backoff.ms")
        @Min(0)
        @Default
        Long reconnectBackoffMs = 50L;

        @JsonProperty("reconnect.backoff.max.ms")
        @Min(0)
        @Default
        Long reconnectBackoffMaxMs = 1000L;

        @JsonProperty("retry.backoff.ms")
        @Min(0)
        @Default
        Long retryBackoffMs = 100L;

        @JsonProperty("max.block.ms")
        @Min(0)
        @Default
        Long maxBlockMs = 60 * 1000L;

        @JsonProperty("request.timeout.ms")
        @Min(0)
        @Default
        Integer requestTimeoutMs = 30 * 1000;

        @JsonProperty("metadata.max.idle.ms")
        @Min(5000)
        @Default
        Long metadataMaxAge = 5 * 60 * 1000L;

        @JsonProperty("metrics.sample.window.ms")
        @Min(0)
        @Default
        Long metricsSampleWindowMs = 3000L;

        @JsonProperty("metrics.recording.level")
        @Default
        String metricsRecordingLevel = "INFO";

        @JsonProperty("max.in.flight.requests.per.connection")
        @Min(1)
        @Default
        Integer maxInFlightRequestsPerConnection = 5;

        @JsonProperty("connections.max.idle.ms")
        @Default
        Long connectionsMaxIdleMs = 9 * 60 * 1000L;

        @JsonProperty("transaction.timeout.ms")
        @Min(0)
        @Default
        Integer transactionTimeout = 60000;

        public Map<String, Object> toConfigMap() {
            return parseMapObject(toJSONString(this));
        }

        @Override
        public DataSourcePropertiesBase validate() {
            hasTextOf(bootstrapServers, "bootstrapServers");
            isTrueOf(bufferMemory > 0, "bufferMemory>0");
            isTrueOf(retries > 0, "retries>0");
            isTrueOf(equalsAnyIgnoreCase(acks, "all", "1", "0", "-1"), "acks must in (all, 1, 0, -1)");
            // TODO
            return this;
        }
    }

}
