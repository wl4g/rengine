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
package com.wl4g.rengine.collector.config;

import static com.wl4g.rengine.common.constants.RengineConstants.CONF_PREFIX_COLLECTOR;

import javax.sql.DataSource;

import org.apache.shardingsphere.elasticjob.lite.internal.snapshot.SnapshotService;
import org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobBootstrapConfiguration;
import org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ScheduleJobBootstrapStartupRunner;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.rengine.collector.job.PrometheusCollectJobExecutor;
import com.wl4g.rengine.collector.job.SimpleSSHCollectJobExecutor;
import com.wl4g.rengine.collector.job.SimpleHttpCollectJobExecutor;
import com.wl4g.rengine.collector.job.SimpleJdbcCollectJobExecutor;
import com.wl4g.rengine.collector.job.SimpleRedisCollectJobExecutor;
import com.wl4g.rengine.collector.job.SimpleTcpCollectJobExecutor;

/**
 * {@link CollectorAutoConfiguration}
 * 
 * @author James Wong
 * @version 2022-10-16
 * @since v1.0.0
 * @see {@link org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobLiteAutoConfiguration}
 */
@Configuration
public class CollectorAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = CONF_PREFIX_COLLECTOR)
    public CollectorProperties collectorProperties() {
        return new CollectorProperties();
    }

    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter(CollectorProperties config) {
        return new ZookeeperRegistryCenter(config.getZookeeper().toZookeeperConfiguration());
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(name = CONF_PREFIX_COLLECTOR + ".tracing.type", havingValue = "RDB")
    public TracingConfiguration<DataSource> tracingConfiguration(DataSource dataSource) {
        return new TracingConfiguration<>("RDB", dataSource);
    }

    @ConditionalOnProperty(name = CONF_PREFIX_COLLECTOR + ".dump.port")
    @Bean(initMethod = "listen", destroyMethod = "close")
    public SnapshotService snapshotService(CoordinatorRegistryCenter registryCenter, CollectorProperties config) {
        return new SnapshotService(registryCenter, config.getDump().getPort());
    }

    @Bean
    public ElasticJobBootstrapConfiguration elasticJobBootstrapConfiguration() {
        return new ElasticJobBootstrapConfiguration();
    }

    @Bean
    public ScheduleJobBootstrapStartupRunner scheduleJobBootstrapStartupRunner() {
        return new ScheduleJobBootstrapStartupRunner();
    }

    @Bean
    public SimpleHttpCollectJobExecutor simpleHttpCollectJobExecutor() {
        return new SimpleHttpCollectJobExecutor();
    }

    @Bean
    public PrometheusCollectJobExecutor prometheusCollectJobExecutor() {
        return new PrometheusCollectJobExecutor();
    }

    @Bean
    public SimpleJdbcCollectJobExecutor simpleJdbcCollectJobExecutor() {
        return new SimpleJdbcCollectJobExecutor();
    }

    @Bean
    public SimpleRedisCollectJobExecutor simpleRedisCollectJobExecutor() {
        return new SimpleRedisCollectJobExecutor();
    }

    @Bean
    public SimpleTcpCollectJobExecutor simpleTcpCollectJobExecutor() {
        return new SimpleTcpCollectJobExecutor();
    }

    @Bean
    public SimpleSSHCollectJobExecutor sshEventJobExeutor() {
        return new SimpleSSHCollectJobExecutor();
    }

}
