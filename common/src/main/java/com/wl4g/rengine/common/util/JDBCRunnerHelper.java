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
package com.wl4g.rengine.common.util;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.findMethodNullable;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.invokeMethod;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.makeAccessible;
import static java.util.Objects.nonNull;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.StatementConfiguration;
import org.apache.commons.dbutils.handlers.MapListHandler;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link JDBCRunnerHelper}
 * 
 * @author James Wong
 * @version 2022-11-24
 * @since v1.0.0
 */
@Slf4j
@Getter
@ToString
public class JDBCRunnerHelper implements Closeable {

    private final DataSource dataSource;
    private final QueryRunner queryRunner;

    public JDBCRunnerHelper(final StatementConfiguration stmtConfig, final DataSource dataSource) {
        this.dataSource = notNullOf(dataSource, "dataSource");
        this.queryRunner = new QueryRunner(dataSource, false, stmtConfig);
    }

    @Override
    public void close() throws IOException {
        if (nonNull(dataSource)) {
            final Method closeMethod = findMethodNullable(dataSource.getClass(), "close");
            if (nonNull(closeMethod)) {
                makeAccessible(closeMethod);
                final Method getJdbcUrlMethod = findMethodNullable(dataSource.getClass(), "getJdbcUrl");
                if (nonNull(getJdbcUrlMethod)) {
                    makeAccessible(getJdbcUrlMethod);
                    log.info("Closing dataSource of jdbc url: {}", invokeMethod(getJdbcUrlMethod, dataSource));
                }
                invokeMethod(closeMethod, dataSource);
            }
        }
    }

    public List<Map<String, Object>> queryForList(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> result = queryRunner.query(dataSource.getConnection(), sql, new MapListHandler(), params);
        return result;
    }

}
