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
package com.wl4g.rengine.client.collector.config;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static java.util.Objects.isNull;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.springframework.beans.factory.InitializingBean;

import com.wl4g.rengine.client.collector.job.EventJobExecutor.EventJobType;
import com.wl4g.rengine.client.collector.job.EventJobExecutor.JobParamBase;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link CollectorProperties}
 * 
 * @author James Wong
 * @version 2022-10-16
 * @since v1.0.0
 * @see {@link org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobProperties}
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CollectorProperties implements InitializingBean {

    private ZookeeperProperties zookeeper = new ZookeeperProperties();

    private TracingProperties tracing = new TracingProperties();

    private SnapshotDumpProperties dump = new SnapshotDumpProperties();

    private ScrapeJobProperties defaultScrapeJobConfig = new ScrapeJobProperties();

    private List<ScrapeJobProperties> scrapeJobConfigs = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        applyDefaultToProperties();
    }

    public void applyDefaultToProperties() {
        ScrapeJobProperties defaultJobConf = getDefaultScrapeJobConfig();

        // Merge default configuration to scrape job configuration.
        safeList(getScrapeJobConfigs()).forEach(jobConf -> {
            // if (isNull(jobConf.getName())) {
            // jobConf.setName(defaultJobConf.getName());
            // }
            if (isNull(jobConf.getType())) {
                jobConf.setType(defaultJobConf.getType());
            }
            if (isBlank(jobConf.getEventType())) {
                jobConf.setEventType(defaultJobConf.getEventType());
            }
            // if (isNull(jobConf.getElasticJobClass())) {
            // jobConf.setElasticJobClass(defaultJobConf.getElasticJobClass());
            // }
            if (isNull(jobConf.getDisabled())) {
                jobConf.setDisabled(defaultJobConf.getDisabled());
            }
            if (isNull(jobConf.getOverwrite())) {
                jobConf.setOverwrite(defaultJobConf.getOverwrite());
            }
            if (isNull(jobConf.getMonitorExecution())) {
                jobConf.setMonitorExecution(defaultJobConf.getMonitorExecution());
            }
            if (isNull(jobConf.getFailover())) {
                jobConf.setFailover(defaultJobConf.getFailover());
            }
            if (isNull(jobConf.getMisfire())) {
                jobConf.setMisfire(defaultJobConf.getMisfire());
            }
            // Merge event job configuration properties.
            if (isBlank(jobConf.getCron())) {
                jobConf.setCron(defaultJobConf.getCron());
            }
            if (isBlank(jobConf.getTimeZone())) {
                jobConf.setTimeZone(defaultJobConf.getTimeZone());
            }
            if (isBlank(jobConf.getJobBootstrapBeanName())) {
                jobConf.setJobBootstrapBeanName(defaultJobConf.getJobBootstrapBeanName());
            }
            if (jobConf.getShardingTotalCount() <= 0) {
                jobConf.setShardingTotalCount(defaultJobConf.getShardingTotalCount());
            }
            if (isBlank(jobConf.getShardingItemParameters())) {
                jobConf.setShardingItemParameters(defaultJobConf.getShardingItemParameters());
            }
            if (isBlank(jobConf.getJobParameter())) {
                jobConf.setJobParameter(defaultJobConf.getJobParameter());
            }
            if (jobConf.getMaxTimeDiffSeconds() <= 0) {
                jobConf.setMaxTimeDiffSeconds(defaultJobConf.getMaxTimeDiffSeconds());
            }
            if (jobConf.getReconcileIntervalMinutes() <= 0) {
                jobConf.setReconcileIntervalMinutes(defaultJobConf.getReconcileIntervalMinutes());
            }
            if (isBlank(jobConf.getJobShardingStrategyType())) {
                jobConf.setJobShardingStrategyType(defaultJobConf.getJobShardingStrategyType());
            }
            if (isBlank(jobConf.getJobExecutorServiceHandlerType())) {
                jobConf.setJobExecutorServiceHandlerType(defaultJobConf.getJobExecutorServiceHandlerType());
            }
            if (isBlank(jobConf.getJobErrorHandlerType())) {
                jobConf.setJobErrorHandlerType(defaultJobConf.getJobErrorHandlerType());
            }
            if (isEmpty(jobConf.getJobListenerTypes())) {
                jobConf.setJobListenerTypes(defaultJobConf.getJobListenerTypes());
            }
            if (isBlank(jobConf.getDescription())) {
                jobConf.setDescription(defaultJobConf.getDescription());
            }
            // Merge event job extra attributes.
            Map<String, String> cloneAttributes = new HashMap<>(defaultJobConf.getEventAttributes());
            cloneAttributes.putAll(jobConf.getEventAttributes());
            jobConf.setEventAttributes(cloneAttributes);

            // Merge static job parameters.
            jobConf.setStaticParams(defaultJobConf.getStaticParams());
        });
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class ZookeeperProperties {

        /**
         * Server list of ZooKeeper.
         *
         * <p>
         * Include IP addresses and ports, Multiple IP address split by comma.
         * For example: host1:2181,host2:2181
         * </p>
         */
        private @NotBlank String serverLists = "localhost:2181";

        /**
         * Namespace.
         */
        private @NotBlank String namespace = "rengine";

        /**
         * Base sleep time milliseconds.
         */
        private @Min(0) int baseSleepTimeMilliseconds = 1000;

        /**
         * Max sleep time milliseconds.
         */
        private @Min(0) int maxSleepTimeMilliseconds = 3000;

        /**
         * Max retry times.
         */
        private @Min(0) int maxRetries = 3;

        /**
         * Session timeout milliseconds.
         */
        private @Min(0) int sessionTimeoutMilliseconds;

        /**
         * Connection timeout milliseconds.
         */
        private @Min(0) int connectionTimeoutMilliseconds;

        /**
         * Zookeeper digest.
         */
        private @Nullable String digest;

        /**
         * Create ZooKeeper configuration.
         *
         * @return instance of ZooKeeper configuration
         */
        public ZookeeperConfiguration toZookeeperConfiguration() {
            ZookeeperConfiguration result = new ZookeeperConfiguration(serverLists, namespace);
            result.setBaseSleepTimeMilliseconds(baseSleepTimeMilliseconds);
            result.setMaxSleepTimeMilliseconds(maxSleepTimeMilliseconds);
            result.setMaxRetries(maxRetries);
            result.setSessionTimeoutMilliseconds(sessionTimeoutMilliseconds);
            result.setConnectionTimeoutMilliseconds(connectionTimeoutMilliseconds);
            result.setDigest(digest);
            return result;
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class TracingProperties {
        private String type = "RDB";
        private Set<String> includeJobNames = new HashSet<>();
        private Set<String> excludeJobNames = new HashSet<>();
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class SnapshotDumpProperties {
        private int port;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class ScrapeJobProperties {
        private String name;
        private EventJobType type = EventJobType.PROMETHEUS;

        // private Class<? extends ElasticJob> elasticJobClass;

        /**
         * The custom event type, which should correspond to Rengine manager,
         * evaluator, jobs.
         */
        private @NotBlank String eventType = "WITH_HTTP";

        /**
         * The custom event attached properties such as labels for Prometheus
         * scraping jobs.
         */
        private @Nullable Map<String, String> eventAttributes = new HashMap<>();
        private Boolean disabled = false;
        private Boolean overwrite = true;
        private Boolean monitorExecution = true;
        private Boolean failover = false;
        private Boolean misfire = false;
        private String cron = "0/5 * * * * ?";
        private String timeZone = "GMT+08:00";
        private String jobBootstrapBeanName;
        private int shardingTotalCount = 1;
        private String shardingItemParameters = "0=Beijing,1=Shanghai";
        private String jobParameter;
        private int maxTimeDiffSeconds = -1;
        private int reconcileIntervalMinutes;
        private String jobShardingStrategyType;
        private String jobExecutorServiceHandlerType;
        private String jobErrorHandlerType;
        private Collection<String> jobListenerTypes = new LinkedList<>();
        private String description = "The job that scrapes events remote over HTTP/TCP/SSH/Redis/JDBC etc.";

        // TODO spring yaml 多态配置实现

        /**
         * Feature the static scrape job parameters.
         */
        private List<JobParamBase> staticParams = new ArrayList<>();

        public JobConfiguration toJobConfiguration(final String jobName) {
            JobConfiguration result = JobConfiguration.builder()
                    .jobName(jobName)
                    .disabled(disabled)
                    .overwrite(overwrite)
                    .monitorExecution(monitorExecution)
                    .failover(failover)
                    .cron(cron)
                    .timeZone(timeZone)
                    .shardingTotalCount(shardingTotalCount)
                    .shardingItemParameters(shardingItemParameters)
                    .jobParameter(jobParameter)
                    .misfire(misfire)
                    .maxTimeDiffSeconds(maxTimeDiffSeconds)
                    .reconcileIntervalMinutes(reconcileIntervalMinutes)
                    .jobShardingStrategyType(jobShardingStrategyType)
                    .jobExecutorServiceHandlerType(jobExecutorServiceHandlerType)
                    .jobErrorHandlerType(jobErrorHandlerType)
                    .jobListenerTypes(jobListenerTypes)
                    .description(description)
                    .staticParams(staticParams)
                    .build();
            safeMap(eventAttributes).forEach((key, value) -> result.getProps().setProperty(key, value));
            return result;
        }
    }

}
