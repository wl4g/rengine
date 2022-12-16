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
package com.wl4g.rengine.executor.execution.datasource;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.dbutils.StatementConfiguration;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.wl4g.rengine.common.entity.DataSourceProperties;
import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourceType;
import com.wl4g.rengine.common.entity.DataSourceProperties.JDBCDataSourceProperties;
import com.wl4g.rengine.common.util.HikariJDBCHelper;
import com.wl4g.rengine.executor.execution.ExecutionConfig;

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

    final ExecutionConfig executionConfig;
    final String dataSourceName;
    final HikariJDBCHelper jdbcHelper;

    @Override
    public void close() throws IOException {
        if (nonNull(jdbcHelper)) {
            log.info("Closing to mysql data source for {} ...", dataSourceName);
            jdbcHelper.close();
        }
    }

    public List<Map<String, Object>> findList(final @NotBlank String sql, final Object... params) throws SQLException {
        hasTextOf(sql, "sql");
        try (Connection conn = jdbcHelper.getDataSource().getConnection();) {
            return jdbcHelper.getQueryRunner().query(conn, sql, new MapListHandler(), params);
        }
    }

    public Map<String, Object> insert(final @NotBlank String sql, final Object... params) throws SQLException {
        hasTextOf(sql, "sql");
        try (Connection conn = jdbcHelper.getDataSource().getConnection();) {
            return jdbcHelper.getQueryRunner().insert(conn, sql, new MapHandler(), params);
        }
    }

    public int[] insertBatch(final @NotBlank String sql, final Object[][] params) throws SQLException {
        hasTextOf(sql, "sql");
        try (Connection conn = jdbcHelper.getDataSource().getConnection();) {
            return jdbcHelper.getQueryRunner().insertBatch(conn, sql, null, params);
        }
    }

    public int update(final @NotBlank String sql, Object... params) throws SQLException {
        hasTextOf(sql, "sql");
        try (Connection conn = jdbcHelper.getDataSource().getConnection();) {
            return jdbcHelper.getQueryRunner().update(conn, sql, params);
        }
    }

    public int[] batch(final @NotBlank String sql, final Object[][] params) throws SQLException {
        hasTextOf(sql, "sql");
        try (Connection conn = jdbcHelper.getDataSource().getConnection();) {
            return jdbcHelper.getQueryRunner().batch(conn, sql, params);
        }
    }

    @Singleton
    public static class JDBCSourceFacadeBuilder implements DataSourceFacadeBuilder {

        @Override
        public DataSourceFacade newInstnace(
                final @NotNull ExecutionConfig config,
                final @NotBlank String dataSourceName,
                final @NotNull DataSourceProperties dataSourceProperties) {
            notNullOf(config, "properties");
            hasTextOf(dataSourceName, "dataSourceName");

            final JDBCDataSourceProperties _config = (JDBCDataSourceProperties) dataSourceProperties;
            final HikariJDBCHelper jdbcHelper = new HikariJDBCHelper(
                    new StatementConfiguration(_config.getFetchDirection(), _config.getFetchSize(), _config.getMaxFieldSize(),
                            _config.getMaxRows(), safeLongToInt(_config.getQueryTimeoutMs())),
                    _config.toHikariConfig());

            return new JDBCSourceFacade(config, dataSourceName, jdbcHelper);
        }

        @Override
        public DataSourceType type() {
            return DataSourceType.JDBC;
        }
    }

}
