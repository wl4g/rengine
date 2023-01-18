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
package com.wl4g.rengine.scheduler.config;

import static com.wl4g.rengine.common.constants.RengineConstants.CONF_PREFIX_SCHEDULER;

import java.util.List;

import javax.sql.DataSource;

import org.apache.shardingsphere.elasticjob.lite.internal.snapshot.SnapshotService;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.rengine.scheduler.job.EngineScheduleController;
import com.wl4g.rengine.scheduler.lifecycle.GlobalScheduleJobManager;

/**
 * {@link RengineSchedulerAutoConfiguration}
 * 
 * @author James Wong
 * @version 2022-10-16
 * @since v1.0.0
 * @see {@link org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobLiteAutoConfiguration}
 */
@Configuration
public class RengineSchedulerAutoConfiguration {

    // --- Scheduler configurations.

    @Bean
    @ConfigurationProperties(prefix = CONF_PREFIX_SCHEDULER)
    public RengineSchedulerProperties rengineSchedulerProperties() {
        return new RengineSchedulerProperties();
    }

    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter(RengineSchedulerProperties config) {
        return new ZookeeperRegistryCenter(config.getZookeeper().toZookeeperConfiguration());
    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(name = CONF_PREFIX_SCHEDULER + ".tracing.type", havingValue = "RDB")
    public TracingConfiguration<DataSource> tracingConfiguration(DataSource dataSource) {
        return new TracingConfiguration<>("RDB", dataSource);
    }

    @ConditionalOnProperty(name = CONF_PREFIX_SCHEDULER + ".dump.port")
    @Bean(initMethod = "listen", destroyMethod = "close")
    public SnapshotService snapshotService(CoordinatorRegistryCenter registryCenter, RengineSchedulerProperties config) {
        return new SnapshotService(registryCenter, config.getDump().getPort());
    }

    // --- Scheduler components.

    // @Bean
    // public ElasticJobBootstrapConfiguration
    // elasticJobBootstrapConfiguration() {
    // return new ElasticJobBootstrapConfiguration();
    // }
    //
    // @Bean
    // public ScheduleJobBootstrapStartupRunner
    // scheduleJobBootstrapStartupRunner() {
    // return new ScheduleJobBootstrapStartupRunner();
    // }

    @Bean
    public EngineScheduleController engineScheduleController() {
        return new EngineScheduleController();
    }

    @Bean
    public GlobalScheduleJobManager globalScheduleJobManager(
            RengineSchedulerProperties config,
            List<TracingConfiguration<?>> tracingConfigurations,
            CoordinatorRegistryCenter registryCenter) {
        return new GlobalScheduleJobManager(config, tracingConfigurations, registryCenter);
    }

}
