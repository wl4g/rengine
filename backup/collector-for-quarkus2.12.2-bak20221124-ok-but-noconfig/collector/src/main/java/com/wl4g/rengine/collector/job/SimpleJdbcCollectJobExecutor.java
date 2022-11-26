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
package com.wl4g.rengine.collector.job;

import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;

import org.apache.commons.dbutils.StatementConfiguration;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;

import com.wl4g.infra.common.bean.ConfigBeanUtils;
import com.wl4g.rengine.collector.util.HikariJdbcHelper;
import com.wl4g.rengine.common.event.RengineEvent.EventLocation;
import com.zaxxer.hikari.HikariConfig;

import lombok.Getter;
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
@Singleton
public class SimpleJdbcCollectJobExecutor extends CollectJobExecutor<SimpleJdbcCollectJobExecutor.SimpleJdbcJobParam>
        implements Closeable {

    private Map<String, HikariJdbcHelper> jdbcHelperCaches;

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

        HikariJdbcHelper jdbcHelper = obtainShardingJdbcHelper(shardingParam, currentShardingTotalCount, jobConfig, context);
        List<Map<String, Object>> result = jdbcHelper.queryForList(shardingParam.getSql());
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

    protected HikariJdbcHelper obtainShardingJdbcHelper(
            SimpleJdbcJobParam shardingParam,
            int currentShardingTotalCount,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) throws Exception {

        if (isNull(jdbcHelperCaches)) {
            synchronized (this) {
                if (isNull(jdbcHelperCaches)) {
                    jdbcHelperCaches = new ConcurrentHashMap<>(currentShardingTotalCount);
                }
            }
        }

        HikariJdbcHelper jdbcHelper = jdbcHelperCaches.get(shardingParam.getName());
        if (isNull(jdbcHelper)) {
            synchronized (this) {
                jdbcHelper = jdbcHelperCaches.get(shardingParam.getName());
                if (isNull(jdbcHelper)) {
                    jdbcHelper = new HikariJdbcHelper(new StatementConfiguration(shardingParam.getFetchDirection(),
                            shardingParam.getFetchSize(), shardingParam.getMaxFieldSize(), shardingParam.getMaxRows(),
                            safeLongToInt(shardingParam.getQueryTimeout().toSeconds())), shardingParam.getHikariConfig());
                    jdbcHelperCaches.put(shardingParam.getName(), jdbcHelper);
                }
            }
        }

        return jdbcHelper;
    }

    @Override
    public void close() throws IOException {
        if (nonNull(jdbcHelperCaches)) {
            log.info("Closing to target jdbc dataSources for : {}", jdbcHelperCaches.keySet());
            jdbcHelperCaches.forEach((name, jdbcHelper) -> {
                try {
                    jdbcHelper.close();
                } catch (Exception e) {
                    log.warn(format("Unable to closing jdbc helper. - %s", name), e);
                }
            });
        }
    }

    @Getter
    @Setter
    @ToString
    public static class SimpleJdbcJobParam extends CollectJobExecutor.JobParamBase {

        /**
         * The direction for fetching rows from database tables.
         */
        private Integer fetchDirection;

        /**
         * The number of rows that should be fetched from the database when more
         * rows are needed.
         */
        private Integer fetchSize;

        /**
         * The maximum number of bytes that can be returned for character and
         * binary column values.
         */
        private Integer maxFieldSize = 64;

        /**
         * The maximum number of rows that a <code>ResultSet</code> can produce.
         */
        private Integer maxRows = 1024;

        /**
         * The number of seconds the driver will wait for execution.
         */
        private Duration queryTimeout = Duration.ofSeconds(15);

        /**
         * The properties for {@link HikariConfig}
         */
        private HikariConfig hikariConfig = new HikariConfig();

        /**
         * The collect to target JDBC SQL.
         */
        private @NotBlank String sql = "SELECT 1";

        /**
         * 注: 不能在这里设置默认值, 经测试发现, 启动服务初始化时调用 snakeyaml 解析后最终的属性值并非此处设置的值, 还是父类
         * {@link HikariConfig} 无参构造方法设置的默认值??? 然而这样会影响
         * {@link ConfigBeanUtils#configureWithDefault(Object, Object, Object)}
         * 合并时对初始值的检查而出BUG(即:当未设置 destObj 的默认值时, 也没有正常覆盖 defaultObj 的值).
         */
        public SimpleJdbcJobParam() {
            //@formatter:off
            // getHikariConfig().setJdbcUrl(
            //         "jdbc:mysql://localhost:3306/test?useunicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true");
            // getHikariConfig().setConnectionTimeout(30_000);
            // getHikariConfig().setIdleTimeout(600_000);
            // getHikariConfig().setInitializationFailTimeout(1);
            // getHikariConfig().setMinimumIdle(1);
            // getHikariConfig().setMaxLifetime(1800_000);
            // getHikariConfig().setMaximumPoolSize(1);
            // getHikariConfig().setValidationTimeout(5_000);
            // getHikariConfig().setLeakDetectionThreshold(0);
            //@formatter:on
        }

    }

}
