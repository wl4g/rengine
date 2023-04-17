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
package com.wl4g.rengine.controller.config;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.common.constants.RengineConstants.TenantedHolder.getSlashKey;
import static com.wl4g.rengine.controller.util.Environments.resolveString;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.net.ssl.KeyManager;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.wl4g.rengine.controller.job.AbstractJobExecutor.ControllerJobType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link RengineControllerProperties}
 * 
 * @author James Wong
 * @date 2022-10-16
 * @since v1.0.0
 * @see {@link org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobProperties}
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RengineControllerProperties implements InitializingBean {

    private @Autowired Validator validator;
    private @Autowired Environment environment;

    private ZookeeperProperties zookeeper = new ZookeeperProperties();
    private TracingProperties tracing = new TracingProperties();
    private SnapshotDumpProperties dump = new SnapshotDumpProperties();
    private GlobalEngineScheduleControllerProperties controller = new GlobalEngineScheduleControllerProperties();
    private EnginePurgerProperties purger = new EnginePurgerProperties();
    private EngineClientSchedulerProperties client = new EngineClientSchedulerProperties();
    private EngineFlinkSchedulerProperties flink = new EngineFlinkSchedulerProperties();

    @Override
    public void afterPropertiesSet() throws Exception {
        resolveJobProperties();
        applyDefaultToProperties();
        validateProperties();
    }

    protected void resolveJobProperties() {
        controller.setCron(resolveString(environment, controller.getCron()));
        controller.setTimeZone(resolveString(environment, controller.getTimeZone()));
        controller.setJobBootstrapBeanName(resolveString(environment, controller.getJobBootstrapBeanName()));
        controller.setShardingItemParameters(resolveString(environment, controller.getShardingItemParameters()));
        controller.setJobParameter(resolveString(environment, controller.getJobParameter()));
        controller.setJobShardingStrategyType(resolveString(environment, controller.getJobShardingStrategyType()));
        controller.setJobExecutorServiceHandlerType(resolveString(environment, controller.getJobExecutorServiceHandlerType()));
        controller.setJobErrorHandlerType(resolveString(environment, controller.getJobErrorHandlerType()));

        final List<String> jobListenerTypes = safeList(controller.getJobListenerTypes());
        for (int i = 0; i < jobListenerTypes.size(); i++) {
            jobListenerTypes.set(i, resolveString(environment, jobListenerTypes.get(i)));
        }
        controller.setDescription(resolveString(environment, controller.getDescription()));
    }

    protected void applyDefaultToProperties() {
    }

    protected void validateProperties() {
        validator.validate(this);
        validateForSafeConstraints();
    }

    protected void validateForSafeConstraints() {
        // @formatter:off
        // The validate for duplicate job names.
        //List<String> duplicateJobNames = safeList(jobConfigs).stream()
        //        .collect(groupingBy(jobConf -> jobConf.getName()))
        //        .entrySet()
        //        .stream()
        //        .filter(e -> safeList(e.getValue()).size() > 1)
        //        .map(e -> e.getKey())
        //        .collect(toList());
        //isTrue(duplicateJobNames.isEmpty(), "The duplicate job names for : %s", duplicateJobNames);
        // @formatter:on

        // The validate for duplicate jobParam names.
        //
        // @formatter:off
        // List<String> duplicateJobParamNames = safeList(jobConfigs).stream()
        //         .flatMap(jobConf -> safeList(jobConf.getJobParams()).stream())
        //         .collect(groupingBy(jobParam -> jobParam.getName()))
        //         .entrySet()
        //         .stream()
        //         .filter(e -> safeList(e.getValue()).size() > 1)
        //         .map(e -> e.getKey())
        //         .collect(toList());
        // isTrue(duplicateJobParamNames.isEmpty(), "The duplicate job params names for : %s", duplicateJobParamNames);
        // @formatter:on

        // The validate for duplicate jobParam by job.
        // @formatter:off
        //safeList(jobConfigs).stream().forEach(jobConf -> {
        //    List<String> duplicateJobParamNames = safeList(jobConf.getJobParams()).stream()
        //            .collect(groupingBy(jobParam -> jobParam.getName()))
        //            .entrySet()
        //            .stream()
        //            .filter(e -> safeList(e.getValue()).size() > 1)
        //            .map(e -> e.getKey())
        //            .collect(toList());
        //    isTrue(duplicateJobParamNames.isEmpty(), "The duplicate job params names for : %s", duplicateJobParamNames);
        //});
        // @formatter:on
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
            ZookeeperConfiguration result = new ZookeeperConfiguration(serverLists, getSlashKey(namespace));
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
    public static class GlobalEngineScheduleControllerProperties {
        // Elastic-Job standard configuration.
        private Boolean disabled;
        private Boolean overwrite;
        private Boolean monitorExecution;
        private Boolean failover;
        private Boolean misfire;
        private String cron;
        private String timeZone;
        private String jobBootstrapBeanName;
        // When setup true, the shardingTotalCount will be ignored, and the will
        // be automatically allocated according to the number of cluster nodes
        // priority.
        private Boolean autoShardingTotalCount;
        private @Min(1) Integer shardingTotalCount;
        private String shardingItemParameters;
        private String jobParameter;
        private Integer maxTimeDiffSeconds;
        private Integer reconcileIntervalMinutes;
        private String jobShardingStrategyType;
        private String jobExecutorServiceHandlerType;
        private String jobErrorHandlerType;
        private Collection<String> jobListenerTypes = new LinkedList<>();
        private String description;

        public GlobalEngineScheduleControllerProperties() {
            setDisabled(false);
            setOverwrite(true);
            setMonitorExecution(true);
            setFailover(true);
            setMisfire(false);
            setCron(DEFAULT_CRON);
            setTimeZone(DEFAULT_TIME_ZONE);
            setJobBootstrapBeanName(null);
            // When setup true, the shardingTotalCount will be ignored,
            // and the will be automatically allocated according to the
            // number of cluster nodes priority.
            setAutoShardingTotalCount(true);
            setShardingTotalCount(1);
            setShardingItemParameters("0=Beijing,1=Shanghai");
            setJobParameter(null);
            setMaxTimeDiffSeconds(-1);
            setReconcileIntervalMinutes(0);
            setJobShardingStrategyType(null);
            setJobExecutorServiceHandlerType(null);
            setJobErrorHandlerType(null);
            setJobListenerTypes(new ArrayList<>());
            setDescription("The job engine execution schedule controller.");
        }

        public JobConfiguration toJobConfiguration(final String jobName) {
            hasTextOf(jobName, "jobName");
            final JobConfiguration jobConfig = JobConfiguration.builder()
                    .jobType(ControllerJobType.GLOBAL_BOOTSTRAPER)
                    .jobName(jobName)
                    .disabled(nonNull(disabled) ? disabled : false)
                    .overwrite(nonNull(overwrite) ? overwrite : true)
                    .monitorExecution(nonNull(monitorExecution) ? monitorExecution : true)
                    .failover(nonNull(failover) ? failover : true)
                    .misfire(nonNull(misfire) ? misfire : false)
                    .cron(isBlank(cron) ? "0/10 * * * * ?" : cron)
                    .timeZone(timeZone)
                    // When setup true, the shardingTotalCount will be ignored,
                    // and the will be automatically allocated according to the
                    // number of cluster nodes priority.
                    .autoShardingTotalCount(nonNull(autoShardingTotalCount) ? autoShardingTotalCount : true)
                    .shardingTotalCount(nonNull(shardingTotalCount) ? shardingTotalCount : 1)
                    .shardingItemParameters(shardingItemParameters)
                    .jobParameter(jobParameter)
                    .maxTimeDiffSeconds(nonNull(maxTimeDiffSeconds) ? maxTimeDiffSeconds : -1)
                    .reconcileIntervalMinutes(nonNull(reconcileIntervalMinutes) ? reconcileIntervalMinutes : 0)
                    .jobShardingStrategyType(jobShardingStrategyType)
                    .jobExecutorServiceHandlerType(jobExecutorServiceHandlerType)
                    .jobErrorHandlerType(jobErrorHandlerType)
                    .jobListenerTypes(jobListenerTypes)
                    .description(description)
                    .build();
            return jobConfig;
        }

        public static final String DEFAULT_CRON = "0/5 * * * * ?";
        public static final String DEFAULT_TIME_ZONE = "GMT+08:00";
    }

    @Getter
    @Setter
    @ToString
    public static class EnginePurgerProperties {
        private @Min(1) Integer logRetentionHours = 7 * 24;
        private @Min(1) Long logRetentionCount = 1_000_000L;
    }

    @Getter
    @Setter
    @ToString
    public static class EngineClientSchedulerProperties {
        private @Min(1) int concurrency = 10;
        private @Min(1) int acceptQueue = 2;
    }

    // see:com.nextbreakpoint.flinkclient1_15.api.ApiClient
    @Getter
    @Setter
    @ToString
    public static class EngineFlinkSchedulerProperties {
        private @NotBlank String endpoint = "http://localhost:8081";
        private @NotNull Boolean debugging = false;
        private @NotNull Boolean verifyingSsl = false;
        private @NotNull Integer connTimeout = 6_000;
        private @NotNull Integer readTimeout = 10_000;
        private @NotNull Integer writeTimeout = 900_000;
        private @Nullable InputStream sslCaCert;
        private @Nullable KeyManager[] keyManagers;
        private @Nullable String username;
        private @Nullable String password;
        private @Nullable String apiKey;
        private @Nullable String apiKeyPrefix;
        private @Nullable String accessToken;

        public EngineFlinkSchedulerProperties validate() {
            hasTextOf(endpoint, "endpoint");
            notNullOf(debugging, "debugging");
            notNullOf(verifyingSsl, "verifyingSsl");
            notNullOf(connTimeout, "connTimeout");
            notNullOf(readTimeout, "readTimeout");
            notNullOf(writeTimeout, "writeTimeout");
            return this;
        }
    }

}
