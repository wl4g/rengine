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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.collector.util;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.util.Objects.nonNull;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.StatementConfiguration;
import org.apache.commons.dbutils.handlers.MapListHandler;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link HikariJdbcHelper}
 * 
 * @author James Wong
 * @version 2022-11-24
 * @since v1.0.0
 */
@Slf4j
@Getter
@ToString
public class HikariJdbcHelper implements Closeable {

    private final HikariDataSource dataSource;
    private final QueryRunner queryRunner;

    public HikariJdbcHelper(StatementConfiguration stmtConfig, HikariConfig hikariConfig) {
        this.dataSource = new HikariDataSource(notNullOf(hikariConfig, "hikariConfig"));
        this.queryRunner = new QueryRunner(dataSource, false, stmtConfig);
    }

    @Override
    public void close() throws IOException {
        if (nonNull(dataSource) && !dataSource.isClosed()) {
            log.info("Closing dataSource of jdbc url: {}", dataSource.getJdbcUrl());
            dataSource.close();
        }
    }

    public List<Map<String, Object>> queryForList(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> result = queryRunner.query(dataSource.getConnection(), sql, new MapListHandler(), params);
        return result;
    }

}
