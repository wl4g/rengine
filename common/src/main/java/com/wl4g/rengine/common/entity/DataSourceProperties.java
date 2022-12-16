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
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.Duration;
import java.util.Properties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.infra.common.jedis.JedisClientBuilder.JedisConfig;
import com.wl4g.rengine.common.entity.DataSourceProperties.JDBCDataSourceProperties;
import com.wl4g.rengine.common.entity.DataSourceProperties.MongoDataSourceProperties;
import com.wl4g.rengine.common.entity.DataSourceProperties.RedisClusterDataSourceProperties;
import com.wl4g.rengine.common.exception.ConfigRengineException;
import com.zaxxer.hikari.HikariConfig;

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
@Schema(oneOf = { MongoDataSourceProperties.class, JDBCDataSourceProperties.class, RedisClusterDataSourceProperties.class },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
@JsonSubTypes({ @Type(value = MongoDataSourceProperties.class, name = "MONGO"),
        @Type(value = JDBCDataSourceProperties.class, name = "JDBC"),
        @Type(value = RedisClusterDataSourceProperties.class, name = "REDIS_CLUSTER") })
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public abstract class DataSourceProperties extends BaseBean {
    static final long serialVersionUID = -7441054887057231030L;

    @Schema(name = "type", implementation = DataSourceType.class)
    @JsonProperty(value = "type", access = Access.WRITE_ONLY)
    @NotNull
    DataSourceType type;

    @NotBlank
    String name;

    public abstract DataSourceProperties validate();

    // Notice: It is recommended to disable the toString method, otherwise
    // swagger will generate the name of the example long enumeration type by
    // default.
    // @ToString
    @Getter
    @AllArgsConstructor
    public static enum DataSourceType {

        MONGO(MongoDataSourceProperties.class),

        JDBC(JDBCDataSourceProperties.class),

        REDIS_CLUSTER(RedisClusterDataSourceProperties.class);

        final Class<? extends DataSourceProperties> propertiesClass;

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

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class MongoDataSourceProperties extends DataSourceProperties {
        static final long serialVersionUID = 7657027064377820844L;

        /**
         * for example: mongodb://localhost:27017
         */
        @NotBlank
        String connectionString;

        @Override
        public DataSourceProperties validate() {
            hasTextOf(connectionString, "connectionString");
            return this;
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class JDBCDataSourceProperties extends DataSourceProperties {
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
        // see:com.zaxxer.hikari.HikariConfig
        //
        // Properties changeable at runtime through the HikariConfigMXBean
        String catalog;
        @Default
        long connectionTimeout = SECONDS.toMillis(10); // must >=250ms
        @Default
        long validationTimeout = SECONDS.toMillis(5); // must >=250ms
        @Default
        long idleTimeout = MINUTES.toMillis(10);
        long leakDetectionThreshold;
        @Default
        long maxLifetime = MINUTES.toMillis(30);
        @Default
        int maximumPoolSize = 5;
        @Default
        int minimumIdle = 1;
        @NotBlank
        String username;
        @NotBlank
        String password;
        // Properties NOT changeable at runtime
        long initializationFailTimeout;
        String connectionInitSql;
        String connectionTestQuery;
        String dataSourceClassName;
        String dataSourceJndiName;
        String driverClassName;
        String exceptionOverrideClassName;
        @NotBlank
        String jdbcUrl;
        String poolName;
        String schema;
        String transactionIsolationName;
        boolean isAutoCommit;
        boolean isReadOnly;
        boolean isIsolateInternalQueries;
        boolean isRegisterMbeans;
        boolean isAllowPoolSuspension;
        Properties dataSourceProperties;
        Properties healthCheckProperties;
        @Default
        long keepaliveTime = 0L;
        boolean sealed;

        @JsonIgnore
        volatile transient HikariConfig hikariConfig;

        public HikariConfig toHikariConfig() {
            if (isNull(hikariConfig)) {
                synchronized (this) {
                    if (isNull(hikariConfig)) {
                        hikariConfig = new HikariConfig();
                        hikariConfig.setCatalog(getCatalog());
                        hikariConfig.setConnectionTimeout(getConnectionTimeout());
                        hikariConfig.setIdleTimeout(getIdleTimeout());
                        hikariConfig.setLeakDetectionThreshold(getLeakDetectionThreshold());
                        hikariConfig.setMaxLifetime(getMaxLifetime());
                        hikariConfig.setMaximumPoolSize(getMaximumPoolSize());
                        hikariConfig.setMinimumIdle(getMinimumIdle());
                        hikariConfig.setPassword(getPassword());
                        hikariConfig.setUsername(getUsername());
                        hikariConfig.setValidationTimeout(getValidationTimeout());
                        hikariConfig.setConnectionTestQuery(getConnectionTestQuery());
                        hikariConfig.setConnectionInitSql(getConnectionInitSql());
                        hikariConfig.setDataSourceClassName(getDataSourceClassName());
                        if (nonNull(getDataSourceProperties())) {
                            hikariConfig.setDataSourceProperties(getDataSourceProperties());
                        }
                        hikariConfig.setDriverClassName(getDriverClassName());
                        hikariConfig.setJdbcUrl(getJdbcUrl());
                        hikariConfig.setAutoCommit(isAutoCommit());
                        hikariConfig.setAllowPoolSuspension(isAllowPoolSuspension());
                        hikariConfig.setInitializationFailTimeout(getInitializationFailTimeout());
                        hikariConfig.setIsolateInternalQueries(isIsolateInternalQueries());
                        if (nonNull(getHealthCheckProperties())) {
                            hikariConfig.setHealthCheckProperties(getHealthCheckProperties());
                        }
                        hikariConfig.setKeepaliveTime(getKeepaliveTime());
                        hikariConfig.setReadOnly(isReadOnly());
                        hikariConfig.setPoolName(getPoolName());
                        hikariConfig.setSchema(getSchema());
                        if (!isBlank(getExceptionOverrideClassName())) {
                            hikariConfig.setExceptionOverrideClassName(getExceptionOverrideClassName());
                        }
                    }
                }
            }
            return hikariConfig;
        }

        @Override
        public DataSourceProperties validate() {
            hasTextOf(getDriverClassName(), "driverClassName");
            hasTextOf(getJdbcUrl(), "jdbcUrl");
            hasTextOf(getUsername(), "username");
            hasTextOf(getPassword(), "password");
            return this;
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class RedisClusterDataSourceProperties extends DataSourceProperties {
        static final long serialVersionUID = -3103084992456055157L;

        @NotNull
        @Default
        JedisConfig jedisConfig = new JedisConfig();

        @Override
        public DataSourceProperties validate() {
            notNullOf(jedisConfig, "jedisConfig");
            notEmptyOf(jedisConfig.getNodes(), "nodes");
            // hasTextOf(jedisConfig.getUsername(), "username");
            hasTextOf(jedisConfig.getPassword(), "password");
            if (jedisConfig.getNodes().size() < 6) {
                throw new ConfigRengineException(
                        format("The number of redis cluster connection nodes must be >=6, but actual properties nodes : %s",
                                jedisConfig.getNodes()));
            }
            return this;
        }
    }

}
