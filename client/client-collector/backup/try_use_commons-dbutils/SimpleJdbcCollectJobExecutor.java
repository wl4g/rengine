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
package com.wl4g.rengine.client.collector.job;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;
import javax.validation.constraints.NotBlank;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.StatementConfiguration;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.springframework.jdbc.core.JdbcTemplate;

import com.wl4g.rengine.client.collector.job.CollectJobExecutor.JobParamBase;
import com.wl4g.rengine.common.event.RengineEvent.EventLocation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link SimpleJdbcCollectJobExecutor}
 * 
 * @author James Wong
 * @version 2022-10-26
 * @since v1.0.0
 * @see https://github1s.com/apache/commons-dbutils/tree/DBUTILS_1_7/src/test/java/org/apache/commons/dbutils
 */
public class SimpleJdbcCollectJobExecutor extends CollectJobExecutor<SimpleJdbcCollectJobExecutor.SimpleJdbcJobParam> {

    @Override
    protected EventJobType type() {
        return EventJobType.SIMPLE_JDBC;
    }

    @Override
    protected void execute(
            SimpleJdbcJobParam shardingParam,
            JobConfiguration jobConfig,
            JobFacade jobFacade,
            ShardingContext context) throws Exception {

        final var jdbcTemplate = new JdbcTemplate();
//        jdbcTemplate.queryForList(getType())
        jdbcTemplate.setFetchSize(shardingParam.getFetchSize());
        jdbcTemplate.setMaxRows(shardingParam.getMaxRows());
        jdbcTemplate.setQueryTimeout(shardingParam.getQueryTimeout());
        
        // TODO
        StatementConfiguration samtConfig = new StatementConfiguration.Builder().fetchDirection(shardingParam.getFetchDirection())
                .fetchSize(shardingParam.getFetchSize())
                .maxFieldSize(shardingParam.getMaxFieldSize())
                .maxRows(shardingParam.getMaxRows())
                .queryTimeout(shardingParam.getQueryTimeout())
                .build();

 

    }

    @Override
    protected BodyConverter getBodyConverter(
            SimpleJdbcJobParam shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<String> getPrincipals(
            SimpleJdbcJobParam shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected EventLocation getEventLocation(
            SimpleJdbcJobParam shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class SimpleJdbcJobParam extends JobParamBase {

        /**
         * The collect JDBC to target URL.
         */
        private @NotBlank String jdbcUrl = "jdbc:mysql://localhost:3306/rengine?useunicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true";
        private boolean pmdKnownBroken = false;
        private Integer fetchDirection;
        private Integer fetchSize;
        private Integer maxFieldSize;
        private Integer maxRows;
        private Integer queryTimeout;
        private @NotBlank String sql = "SELECT 1";
    }

//    public static class SimpleDataSource implements DataSource {
//
//        @Override
//        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public <T> T unwrap(Class<T> iface) throws SQLException {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public boolean isWrapperFor(Class<?> iface) throws SQLException {
//            // TODO Auto-generated method stub
//            return false;
//        }
//
//        @Override
//        public Connection getConnection() throws SQLException {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public Connection getConnection(String username, String password) throws SQLException {
//            throw new UnsupportedOperationException();
//        }
//
//        @Override
//        public PrintWriter getLogWriter() throws SQLException {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public void setLogWriter(PrintWriter out) throws SQLException {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void setLoginTimeout(int seconds) throws SQLException {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public int getLoginTimeout() throws SQLException {
//            // TODO Auto-generated method stub
//            return 0;
//        }
//
//    }

}
