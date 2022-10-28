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

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotBlank;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.springframework.jdbc.core.JdbcTemplate;

import com.wl4g.rengine.client.collector.job.CollectJobExecutor.JobParamBase;
import com.wl4g.rengine.common.event.RengineEvent.EventLocation;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link SimpleJdbcCollectJobExecutor}
 * 
 * @author James Wong
 * @version 2022-10-26
 * @since v1.0.0
 * @see https://github1s.com/apache/commons-dbutils/tree/DBUTILS_1_7/src/test/java/org/apache/commons/dbutils
 */
@Slf4j
public class SimpleJdbcCollectJobExecutor extends CollectJobExecutor<SimpleJdbcCollectJobExecutor.SimpleJdbcJobParam>
        implements Closeable {

    private Map<String, JdbcTemplate> jdbcTemplateCaches;

    @Override
    protected EventJobType type() {
        return EventJobType.SIMPLE_JDBC;
    }

    @Override
    protected void execute(
            SimpleJdbcJobParam shardingParam,
            int currentShardingTotalCount,
            JobConfiguration jobConfig,
            JobFacade jobFacade,
            ShardingContext context) throws Exception {

        JdbcTemplate jdbcTemplate = obtainShardingJdbcTemplate(shardingParam, currentShardingTotalCount, jobConfig, context);
        List<Map<String, Object>> result = jdbcTemplate.queryForList(shardingParam.getSql());
        log.debug("Collect to result: {}", result);

        offer(shardingParam, jobConfig, jobFacade, context, result);
    }

    @Override
    protected BodyConverter getBodyConverter(
            SimpleJdbcJobParam shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        return BodyConverter.DEFAULT_STRING;
    }

    @Override
    protected EventLocation getEventLocation(
            SimpleJdbcJobParam shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        return EventLocation.builder().ipAddress(URI.create(shardingParam.getHikariConfig().getJdbcUrl()).getHost()).build();
    }

    protected JdbcTemplate obtainShardingJdbcTemplate(
            SimpleJdbcJobParam shardingParam,
            int currentShardingTotalCount,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) throws Exception {

        if (isNull(jdbcTemplateCaches)) {
            synchronized (this) {
                if (isNull(jdbcTemplateCaches)) {
                    jdbcTemplateCaches = new ConcurrentHashMap<>(currentShardingTotalCount);
                }
            }
        }

        JdbcTemplate jdbcTemplate = jdbcTemplateCaches.get(shardingParam.getName());
        if (isNull(jdbcTemplate)) {
            synchronized (this) {
                jdbcTemplate = jdbcTemplateCaches.get(shardingParam.getName());
                if (isNull(jdbcTemplate)) {
                    jdbcTemplate = new JdbcTemplate(new HikariDataSource(shardingParam.getHikariConfig()));
                    jdbcTemplate.setIgnoreWarnings(shardingParam.isIgnoreWarnings());
                    jdbcTemplate.setFetchSize(shardingParam.getFetchSize());
                    jdbcTemplate.setMaxRows(shardingParam.getMaxRows());
                    jdbcTemplate.setQueryTimeout(shardingParam.getQueryTimeout());
                    jdbcTemplate.setSkipResultsProcessing(shardingParam.isSkipResultsProcessing());
                    jdbcTemplate.setSkipUndeclaredResults(shardingParam.isSkipUndeclaredResults());
                    jdbcTemplate.setResultsMapCaseInsensitive(shardingParam.isResultsMapCaseInsensitive());
                    jdbcTemplateCaches.put(shardingParam.getName(), jdbcTemplate);
                }
            }
        }

        return jdbcTemplate;
    }

    @Override
    public void close() throws IOException {
        if (nonNull(jdbcTemplateCaches)) {
            log.info("Closing to target jdbc dataSources for : {}", jdbcTemplateCaches.keySet());
            jdbcTemplateCaches.forEach((name, jdbcTemplate) -> {
                try {
                    ((HikariDataSource) jdbcTemplate.getDataSource()).close();
                } catch (Exception e) {
                    log.warn(format("Unable to closing target jdbc dataSource. - %s", name), e);
                }
            });
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class SimpleJdbcJobParam extends JobParamBase {

        /**
         * If this variable is {@code false}, we will throw exceptions on SQL
         * warnings.
         */
        private boolean ignoreWarnings = true;

        /**
         * If this variable is set to a non-negative value, it will be used for
         * setting the fetchSize property on statements used for query
         * processing.
         */
        private int fetchSize = -1;

        /**
         * If this variable is set to a non-negative value, it will be used for
         * setting the maxRows property on statements used for query processing.
         */
        private int maxRows = -1;

        /**
         * If this variable is set to a non-negative value, it will be used for
         * setting the queryTimeout property on statements used for query
         * processing.
         */
        private int queryTimeout = -1;

        /**
         * If this variable is set to true, then all results checking will be
         * bypassed for any callable statement processing. This can be used to
         * avoid a bug in some older Oracle JDBC drivers like 10.1.0.2.
         */
        private boolean skipResultsProcessing = false;

        /**
         * If this variable is set to true then all results from a stored
         * procedure call that don't have a corresponding SqlOutParameter
         * declaration will be bypassed. All other results processing will be
         * take place unless the variable {@code skipResultsProcessing} is set
         * to {@code true}.
         */
        private boolean skipUndeclaredResults = false;

        /**
         * If this variable is set to true then execution of a CallableStatement
         * will return the results in a Map that uses case-insensitive names for
         * the parameters.
         */
        private boolean resultsMapCaseInsensitive = false;

        /**
         * The properties for {@link HikariConfig}
         */
        private HikariConfig hikariConfig = new HikariConfig() {
            {
                // Setup default collect JDBC to target URL.
                setJdbcUrl(
                        "jdbc:mysql://localhost:3306/test?useunicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
            }
        };

        /**
         * The collect to target JDBC SQL.
         */
        private @NotBlank String sql = "SELECT 1";
    }

}
