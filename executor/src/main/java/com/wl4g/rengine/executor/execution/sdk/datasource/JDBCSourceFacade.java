/**
 * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.executor.execution.sdk.datasource;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_datasource_facade_failure;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_datasource_facade_success;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_datasource_facade_total;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_datasource_facade_time;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.StatementConfiguration;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourcePropertiesBase;
import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourceType;
import com.wl4g.rengine.common.entity.DataSourceProperties.JDBCDataSourceProperties;
import com.wl4g.rengine.common.util.JDBCRunnerHelper;
import com.wl4g.rengine.executor.execution.ExecutionConfig;
import com.wl4g.rengine.executor.metrics.MeterUtil;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link JDBCSourceFacade}
 *
 * @author James Wong
 * @version 2022-10-10
 * @since v1.0.0
 */
@Getter
@CustomLog
@AllArgsConstructor
public class JDBCSourceFacade implements DataSourceFacade {

    final static String METHOD_FIND_LIST = "findList";
    final static String METHOD_INSERT = "insert";
    final static String METHOD_INSERT_BATCH = "insertBatch";
    final static String METHOD_UPDATE = "update";
    final static String METHOD_BATCH = "batch";

    final ExecutionConfig executionConfig;
    final String dataSourceName;
    final JDBCRunnerHelper helper;

    @Override
    public void close() throws IOException {
        if (nonNull(helper)) {
            log.info("Closing to jdbc data source for {} ...", dataSourceName);
            helper.close();
        }
    }

    public List<Map<String, Object>> findList(final @NotBlank String sql, final Object... params) throws SQLException {
        hasTextOf(sql, "sql");
        MeterUtil.counter(execution_sdk_datasource_facade_total, dataSourceName, DataSourceType.JDBC, METHOD_FIND_LIST);

        try (Connection conn = helper.getDataSource().getConnection();) {
            final List<Map<String, Object>> result = MeterUtil.timer(execution_sdk_datasource_facade_time, dataSourceName,
                    DataSourceType.JDBC, METHOD_FIND_LIST,
                    () -> helper.getQueryRunner().query(conn, sql, new MapListHandler(), params));

            MeterUtil.counter(execution_sdk_datasource_facade_success, dataSourceName, DataSourceType.JDBC, METHOD_FIND_LIST);
            return result;
        } catch (Throwable e) {
            MeterUtil.counter(execution_sdk_datasource_facade_failure, dataSourceName, DataSourceType.JDBC, METHOD_FIND_LIST);
            throw e;
        }
    }

    public Map<String, Object> insert(final @NotBlank String sql, final Object... params) throws SQLException {
        hasTextOf(sql, "sql");
        MeterUtil.counter(execution_sdk_datasource_facade_total, dataSourceName, DataSourceType.JDBC, METHOD_INSERT);

        try (Connection conn = helper.getDataSource().getConnection();) {
            final Map<String, Object> result = MeterUtil.timer(execution_sdk_datasource_facade_time, dataSourceName,
                    DataSourceType.JDBC, METHOD_INSERT,
                    () -> helper.getQueryRunner().insert(conn, sql, new MapHandler(), params));

            MeterUtil.counter(execution_sdk_datasource_facade_success, dataSourceName, DataSourceType.JDBC, METHOD_INSERT);
            return result;
        } catch (Throwable e) {
            MeterUtil.counter(execution_sdk_datasource_facade_failure, dataSourceName, DataSourceType.JDBC, METHOD_INSERT);
            throw e;
        }
    }

    public int[] insertBatch(final @NotBlank String sql, final Object[][] params) throws SQLException {
        hasTextOf(sql, "sql");
        MeterUtil.counter(execution_sdk_datasource_facade_total, dataSourceName, DataSourceType.JDBC, METHOD_INSERT_BATCH);

        try (Connection conn = helper.getDataSource().getConnection();) {
            final int[] result = MeterUtil.timer(execution_sdk_datasource_facade_time, dataSourceName, DataSourceType.JDBC,
                    METHOD_INSERT_BATCH, () -> helper.getQueryRunner().insertBatch(conn, sql, null, params));

            MeterUtil.counter(execution_sdk_datasource_facade_success, dataSourceName, DataSourceType.JDBC, METHOD_INSERT_BATCH);
            return result;
        } catch (Throwable e) {
            MeterUtil.counter(execution_sdk_datasource_facade_failure, dataSourceName, DataSourceType.JDBC, METHOD_INSERT_BATCH);
            throw e;
        }
    }

    public int update(final @NotBlank String sql, Object... params) throws SQLException {
        hasTextOf(sql, "sql");
        MeterUtil.counter(execution_sdk_datasource_facade_total, dataSourceName, DataSourceType.JDBC, METHOD_UPDATE);

        try (Connection conn = helper.getDataSource().getConnection();) {
            final int result = MeterUtil.timer(execution_sdk_datasource_facade_time, dataSourceName, DataSourceType.JDBC,
                    METHOD_UPDATE, () -> helper.getQueryRunner().update(conn, sql, params));

            MeterUtil.counter(execution_sdk_datasource_facade_success, dataSourceName, DataSourceType.JDBC, METHOD_UPDATE);
            return result;
        } catch (Throwable e) {
            MeterUtil.counter(execution_sdk_datasource_facade_failure, dataSourceName, DataSourceType.JDBC, METHOD_UPDATE);
            throw e;
        }
    }

    public int[] batch(final @NotBlank String sql, final Object[][] params) throws SQLException {
        hasTextOf(sql, "sql");
        MeterUtil.counter(execution_sdk_datasource_facade_total, dataSourceName, DataSourceType.JDBC, METHOD_BATCH);

        try (Connection conn = helper.getDataSource().getConnection();) {
            final int[] result = MeterUtil.timer(execution_sdk_datasource_facade_time, dataSourceName, DataSourceType.JDBC,
                    METHOD_BATCH, () -> helper.getQueryRunner().batch(conn, sql, params));

            MeterUtil.counter(execution_sdk_datasource_facade_success, dataSourceName, DataSourceType.JDBC, METHOD_BATCH);
            return result;
        } catch (Throwable e) {
            MeterUtil.counter(execution_sdk_datasource_facade_failure, dataSourceName, DataSourceType.JDBC, METHOD_BATCH);
            throw e;
        }
    }

    @Singleton
    public static class JDBCSourceFacadeBuilder implements DataSourceFacadeBuilder {

        @Override
        public DataSourceFacade newInstnace(
                final @NotNull ExecutionConfig config,
                final @NotBlank String dataSourceName,
                final @NotNull DataSourcePropertiesBase dataSourceProperties) {
            notNullOf(config, "properties");
            hasTextOf(dataSourceName, "dataSourceName");

            final JDBCDataSourceProperties c = (JDBCDataSourceProperties) dataSourceProperties;
            final StatementConfiguration statementConfig = new StatementConfiguration(c.getFetchDirection(), c.getFetchSize(),
                    c.getMaxFieldSize(), c.getMaxRows(), safeLongToInt(c.getQueryTimeoutMs()));

            final BasicDataSource bds = new BasicDataSource();
            bds.setDriverClassName(c.getDriverClassName());
            bds.setUrl(c.getJdbcUrl());
            bds.setUsername(c.getUsername());
            bds.setPassword(c.getPassword());
            bds.setInitialSize(c.getInitPoolSize());
            bds.setMaxTotal(c.getMaximumPoolSize());
            bds.setMaxIdle(c.getMaximumPoolSize() / 2);
            bds.setMinIdle(c.getMinimumIdle());
            bds.setMinEvictableIdleTimeMillis(c.getIdleTimeout());
            bds.setSoftMinEvictableIdleTimeMillis(c.getSoftMinIdleTimeout());
            bds.setMaxConnLifetimeMillis(c.getMaxConnLifeTime());
            bds.setMaxWaitMillis(c.getConnectionTimeout());
            bds.setTimeBetweenEvictionRunsMillis(c.getEvictionRunsBetweenTime());
            bds.setValidationQuery(c.getValidationTestSql());
            bds.setValidationQueryTimeout(c.getValidationTimeout().intValue());
            bds.setAutoCommitOnReturn(c.getAutoCommit());
            bds.setCacheState(c.getCacheState());
            bds.setTestOnBorrow(c.getTestOnBorrow());
            bds.setTestOnCreate(c.getTestOnCreate());
            bds.setTestOnReturn(c.getTestOnReturn());
            bds.setTestWhileIdle(c.getTestWhileIdle());

            return new JDBCSourceFacade(config, dataSourceName, new JDBCRunnerHelper(statementConfig, bds));
        }

        @Override
        public DataSourceType type() {
            return DataSourceType.JDBC;
        }
    }

}
