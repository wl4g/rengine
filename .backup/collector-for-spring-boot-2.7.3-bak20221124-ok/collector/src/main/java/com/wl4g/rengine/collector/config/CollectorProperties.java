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
package com.wl4g.rengine.collector.config;

import static com.wl4g.infra.common.bean.ConfigBeanUtils.configureWithDefault;
import static com.wl4g.infra.common.collection.CollectionUtils2.ensureMap;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.getField;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.isGenericModifier;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.setField;
import static com.wl4g.rengine.collector.util.EnvironmentUtils.resolveString;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.infra.common.reflect.ReflectionUtils2;
import com.wl4g.rengine.collector.job.CollectJobExecutor.EventJobType;
import com.wl4g.rengine.collector.job.CollectJobExecutor.JobParamBase;
import com.wl4g.rengine.collector.job.SimpleHttpCollectJobExecutor.SimpleHttpJobParam;
import com.wl4g.rengine.collector.job.SimpleJdbcCollectJobExecutor.SimpleJdbcJobParam;
import com.wl4g.rengine.collector.job.SimpleRedisCollectJobExecutor.SimpleRedisJobParam;
import com.wl4g.rengine.collector.job.SimpleSSHCollectJobExecutor.SimpleSSHJobParam;
import com.wl4g.rengine.collector.job.SimpleTcpCollectJobExecutor.SimpleTcpJobParam;
import com.wl4g.rengine.common.event.RengineEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link CollectorProperties}
 * 
 * @author James Wong
 * @date 2022-10-16
 * @since v1.0.0
 * @see {@link org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobProperties}
 */
@Getter
@Setter
@Slf4j
@ToString
@NoArgsConstructor
public class CollectorProperties implements InitializingBean {

    private @Autowired Validator validator;
    private @Autowired Environment environment;

    private ZookeeperProperties zookeeper = new ZookeeperProperties();

    private TracingProperties tracing = new TracingProperties();

    private SnapshotDumpProperties dump = new SnapshotDumpProperties();

    private GlobalScrapeJobProperties globalScrapeJobConfig = new GlobalScrapeJobProperties();

    private List<ScrapeJobProperties<JobParamBase>> scrapeJobConfigs = new ArrayList<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        applyDefaultToProperties();
        validateProperties();
        resolveJobScrapeProperties();
        // parseJobVariables();
    }

    protected void applyDefaultToProperties() {
        GlobalScrapeJobProperties globalJobConf = getGlobalScrapeJobConfig();

        // Merge default configuration to scrape job configuration.
        safeList(getScrapeJobConfigs()).forEach(jobConf -> {
            try {
                // if (isNull(jobConf.getName())) {
                // jobConf.setName(globalJobConf.getName());
                // }
                // if (isNull(jobConf.getElasticJobClass())) {
                // jobConf.setElasticJobClass(globalJobConf.getElasticJobClass());
                // }

                // Merge from default event type/attributes.
                if (isBlank(jobConf.getEventType())) {
                    jobConf.setEventType(globalJobConf.getEventType());
                }
                Map<String, String> cloneAttributes = new HashMap<>(globalJobConf.getEventAttributes());
                cloneAttributes.putAll(jobConf.getEventAttributes());
                jobConf.setEventAttributes(cloneAttributes);

                if (isNull(jobConf.getDisabled())) {
                    jobConf.setDisabled(globalJobConf.getDisabled());
                }
                if (isNull(jobConf.getOverwrite())) {
                    jobConf.setOverwrite(globalJobConf.getOverwrite());
                }
                if (isNull(jobConf.getMonitorExecution())) {
                    jobConf.setMonitorExecution(globalJobConf.getMonitorExecution());
                }
                if (isNull(jobConf.getFailover())) {
                    jobConf.setFailover(globalJobConf.getFailover());
                }
                if (isNull(jobConf.getMisfire())) {
                    jobConf.setMisfire(globalJobConf.getMisfire());
                }

                // Merge from default job configuration.
                if (isBlank(jobConf.getCron())) {
                    jobConf.setCron(globalJobConf.getCron());
                }
                if (isBlank(jobConf.getTimeZone())) {
                    jobConf.setTimeZone(globalJobConf.getTimeZone());
                }
                if (isBlank(jobConf.getJobBootstrapBeanName())) {
                    jobConf.setJobBootstrapBeanName(globalJobConf.getJobBootstrapBeanName());
                }
                // When setup true, the shardingTotalCount will be ignored, and
                // the will be automatically allocated according to the number
                // of cluster nodes priority.
                if (isNull(jobConf.getAutoShardingTotalCount())) {
                    jobConf.setAutoShardingTotalCount(globalJobConf.getAutoShardingTotalCount());
                }
                if (isNull(jobConf.getShardingTotalCount()) || jobConf.getShardingTotalCount() <= 0) {
                    jobConf.setShardingTotalCount(globalJobConf.getShardingTotalCount());
                }
                if (isBlank(jobConf.getShardingItemParameters())) {
                    jobConf.setShardingItemParameters(globalJobConf.getShardingItemParameters());
                }
                if (isBlank(jobConf.getJobParameter())) {
                    jobConf.setJobParameter(globalJobConf.getJobParameter());
                }
                if (isNull(jobConf.getMaxTimeDiffSeconds()) || jobConf.getMaxTimeDiffSeconds() <= 0) {
                    jobConf.setMaxTimeDiffSeconds(globalJobConf.getMaxTimeDiffSeconds());
                }
                if (isNull(jobConf.getReconcileIntervalMinutes()) || jobConf.getReconcileIntervalMinutes() <= 0) {
                    jobConf.setReconcileIntervalMinutes(globalJobConf.getReconcileIntervalMinutes());
                }
                if (isBlank(jobConf.getJobShardingStrategyType())) {
                    jobConf.setJobShardingStrategyType(globalJobConf.getJobShardingStrategyType());
                }
                if (isBlank(jobConf.getJobExecutorServiceHandlerType())) {
                    jobConf.setJobExecutorServiceHandlerType(globalJobConf.getJobExecutorServiceHandlerType());
                }
                if (isBlank(jobConf.getJobErrorHandlerType())) {
                    jobConf.setJobErrorHandlerType(globalJobConf.getJobErrorHandlerType());
                }
                if (isEmpty(jobConf.getJobListenerTypes())) {
                    jobConf.setJobListenerTypes(globalJobConf.getJobListenerTypes());
                }
                if (isBlank(jobConf.getDescription())) {
                    jobConf.setDescription(globalJobConf.getDescription());
                }

                // [MARK1]
                // Merge from default job parameters.
                for (int i = 0; i < jobConf.getJobParams().size(); i++) {
                    JobParamBase p = jobConf.getJobParams().get(i);
                    try {
                        JobParamBase init = p.getClass().getConstructor().newInstance(), merged = p;
                        if (p instanceof SimpleHttpJobParam) {
                            merged = configureWithDefault(init, p, globalJobConf.getJobParamConfigs().getSimpleHttp());
                        } else if (p instanceof SimpleJdbcJobParam) {
                            merged = configureWithDefault(init, p, globalJobConf.getJobParamConfigs().getSimpleJdbc());
                        } else if (p instanceof SimpleRedisJobParam) {
                            merged = configureWithDefault(init, p, globalJobConf.getJobParamConfigs().getSimpleRedis());
                        } else if (p instanceof SimpleTcpJobParam) {
                            merged = configureWithDefault(init, p, globalJobConf.getJobParamConfigs().getSimpleTcp());
                        } else if (p instanceof SimpleSSHJobParam) {
                            merged = configureWithDefault(init, p, globalJobConf.getJobParamConfigs().getSimpleSsh());
                        } else {
                            throw new Error(format("Should't to be here."));
                        }
                        jobConf.getJobParams().set(i, merged);
                    } catch (Exception e) {
                        throw new IllegalStateException(format("Could't to merge default to job param. - %s", p), e);
                    }
                }

            } catch (Exception e) {
                log.error("Failed to merge default properties to job configuration.", e);
                throw e;
            }
        });
    }

    protected void validateProperties() {
        validator.validate(this);
        validateForSafeConstraints();
    }

    protected void validateForSafeConstraints() {
        // The validate for event type.
        safeList(scrapeJobConfigs).stream().forEach(jobConf -> RengineEvent.validateForEventType(jobConf.getEventType()));

        // The validate for duplicate job names.
        List<String> duplicateJobNames = safeList(scrapeJobConfigs).stream()
                .collect(groupingBy(jobConf -> jobConf.getName()))
                .entrySet()
                .stream()
                .filter(e -> safeList(e.getValue()).size() > 1)
                .map(e -> e.getKey())
                .collect(toList());
        isTrue(duplicateJobNames.isEmpty(), "The duplicate job names for : %s", duplicateJobNames);

        // The validate for duplicate jobParam names.
        //
        // @formatter:off
        // List<String> duplicateJobParamNames = safeList(scrapeJobConfigs).stream()
        //         .flatMap(jobConf -> safeList(jobConf.getJobParams()).stream())
        //         .collect(groupingBy(jobParam -> jobParam.getName()))
        //         .entrySet()
        //         .stream()
        //         .filter(e -> safeList(e.getValue()).size() > 1)
        //         .map(e -> e.getKey())
        //         .collect(toList());
        // isTrue(duplicateJobParamNames.isEmpty(), "The duplicate job params names for : %s", duplicateJobParamNames);
        // @formatter:on
        //
        // The validate for duplicate jobParam by job.
        safeList(scrapeJobConfigs).stream().forEach(jobConf -> {
            List<String> duplicateJobParamNames = safeList(jobConf.getJobParams()).stream()
                    .collect(groupingBy(jobParam -> jobParam.getName()))
                    .entrySet()
                    .stream()
                    .filter(e -> safeList(e.getValue()).size() > 1)
                    .map(e -> e.getKey())
                    .collect(toList());
            isTrue(duplicateJobParamNames.isEmpty(), "The duplicate job params names for : %s", duplicateJobParamNames);
        });
    }

    protected void resolveJobScrapeProperties() {
        safeList(scrapeJobConfigs).forEach(jobConf -> {
            jobConf.setName(environment.resolvePlaceholders(jobConf.getName()));
            jobConf.setEventType(environment.resolvePlaceholders(jobConf.getEventType()));

            // Resolve event attributes.
            Map<String, String> attributes = ensureMap(jobConf.getEventAttributes());
            new HashMap<>(ensureMap(jobConf.getEventAttributes()))
                    .forEach((key, value) -> attributes.put(key, environment.resolvePlaceholders(value)));

            jobConf.setCron(resolveString(environment, jobConf.getCron()));
            jobConf.setTimeZone(resolveString(environment, jobConf.getTimeZone()));
            jobConf.setJobBootstrapBeanName(resolveString(environment, jobConf.getJobBootstrapBeanName()));
            jobConf.setShardingItemParameters(resolveString(environment, jobConf.getShardingItemParameters()));
            jobConf.setJobParameter(resolveString(environment, jobConf.getJobParameter()));
            jobConf.setJobShardingStrategyType(resolveString(environment, jobConf.getJobShardingStrategyType()));
            jobConf.setJobExecutorServiceHandlerType(resolveString(environment, jobConf.getJobExecutorServiceHandlerType()));
            jobConf.setJobErrorHandlerType(resolveString(environment, jobConf.getJobErrorHandlerType()));

            List<String> jobListenerTypes = safeList(jobConf.getJobListenerTypes());
            for (int i = 0; i < jobListenerTypes.size(); i++) {
                jobListenerTypes.set(i, environment.resolvePlaceholders(jobListenerTypes.get(i)));
            }

            jobConf.setDescription(environment.resolvePlaceholders(jobConf.getDescription()));

            // Resolve jobParams values.
            safeList(jobConf.getJobParams()).forEach(p -> {
                ReflectionUtils2.doFullWithFields(p, targetField -> isGenericModifier(targetField.getModifiers()),
                        (field, objOfField) -> {
                            if (String.class.isAssignableFrom(field.getType()) && !Modifier.isFinal(field.getModifiers())) {
                                setField(field, objOfField, resolveString(environment, getField(field, objOfField, true)), true);
                            }
                        });
            });
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
    public abstract static class ScrapeJobProperties<C extends JobParamBase> {
        private String name;

        // private Class<? extends ElasticJob> elasticJobClass;

        /**
         * The custom event type, which should correspond to Rengine manager,
         * evaluator, jobs.
         */
        private @NotBlank String eventType;

        /**
         * The custom event attached properties such as labels for Prometheus
         * scraping jobs.
         */
        private @Nullable Map<String, String> eventAttributes = new HashMap<>();
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
        private String description = "The job that scrapes events remote over HTTP/TCP/SSH/Redis/JDBC etc.";

        public abstract EventJobType getJobType();

        public abstract List<C> getJobParams();

        public JobConfiguration toJobConfiguration(final String jobName) {
            JobConfiguration result = JobConfiguration.builder()
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
                    .jobParams(getJobParams())
                    .build();
            ensureMap(eventAttributes).forEach((key, value) -> result.getProps().setProperty(key, value));
            return result;
        }
    }

    @Getter
    @Setter
    @ToString
    public static class GlobalScrapeJobProperties extends ScrapeJobProperties<JobParamBase> {

        private Map<String, String> jobVariables = new HashMap<>();

        private GlobalJobParamsProperties jobParamConfigs = new GlobalJobParamsProperties();

        public GlobalScrapeJobProperties() {
            setEventType("PROM");
            setEventAttributes(new HashMap<>());
            setDisabled(false);
            setOverwrite(true);
            setMonitorExecution(true);
            setFailover(true);
            setMisfire(false);
            setCron("0/10 * * * * ?");
            setTimeZone("GMT+08:00");
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
            setDescription("The job that scrapes events remote over HTTP/TCP/SSH/Redis/JDBC etc.");
        }

        @JsonIgnore
        @Override
        public EventJobType getJobType() {
            // return null;
            throw new UnsupportedOperationException();
        }

        @JsonIgnore
        @Override
        public List<JobParamBase> getJobParams() {
            // Ignore, see: MARK1
            // return emptyList();
            throw new UnsupportedOperationException();
        }

        @Getter
        @Setter
        @ToString
        public static class GlobalJobParamsProperties {

            private SimpleHttpJobParam simpleHttp = new SimpleHttpJobParam();

            private SimpleJdbcJobParam simpleJdbc = new SimpleJdbcJobParam() {
                {
                    // Setup default collect JDBC to target URL.
                    getHikariConfig().setJdbcUrl(
                            "jdbc:mysql://localhost:3306/test?useunicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true");
                    getHikariConfig().setConnectionTimeout(30_000);
                    getHikariConfig().setIdleTimeout(600_000);
                    getHikariConfig().setInitializationFailTimeout(1);
                    getHikariConfig().setMinimumIdle(3);
                    getHikariConfig().setMaxLifetime(1800_000);
                    getHikariConfig().setMaximumPoolSize(1);
                    getHikariConfig().setValidationTimeout(5_000);
                    getHikariConfig().setLeakDetectionThreshold(0);
                }
            };

            private SimpleRedisJobParam simpleRedis = new SimpleRedisJobParam();

            private SimpleTcpJobParam simpleTcp = new SimpleTcpJobParam();

            private SimpleSSHJobParam simpleSsh = new SimpleSSHJobParam();
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class SimpleHttpScrapeJobProperties extends ScrapeJobProperties<SimpleHttpJobParam> {

        /**
         * Feature the static scrape job parameters.
         */
        private List<SimpleHttpJobParam> jobParams = new ArrayList<>();

        @Override
        public EventJobType getJobType() {
            return EventJobType.SIMPLE_HTTP;
        }

        @Override
        public List<SimpleHttpJobParam> getJobParams() {
            return jobParams;
        }

    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class SimpleJdbcScrapeJobProperties extends ScrapeJobProperties<SimpleJdbcJobParam> {

        /**
         * Feature the static scrape job parameters.
         */
        private List<SimpleJdbcJobParam> jobParams = new ArrayList<>();

        @Override
        public EventJobType getJobType() {
            return EventJobType.SIMPLE_JDBC;
        }

        @Override
        public List<SimpleJdbcJobParam> getJobParams() {
            return jobParams;
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class SimpleRedisScrapeJobProperties extends ScrapeJobProperties<SimpleRedisJobParam> {

        /**
         * Feature the static scrape job parameters.
         */
        private List<SimpleRedisJobParam> jobParams = new ArrayList<>();

        @Override
        public EventJobType getJobType() {
            return EventJobType.SIMPLE_REDIS;
        }

        @Override
        public List<SimpleRedisJobParam> getJobParams() {
            return jobParams;
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class SimpleTcpScrapeJobProperties extends ScrapeJobProperties<SimpleTcpJobParam> {

        /**
         * Feature the static scrape job parameters.
         */
        private List<SimpleTcpJobParam> jobParams = new ArrayList<>();

        @Override
        public EventJobType getJobType() {
            return EventJobType.SIMPLE_TCP;
        }

        @Override
        public List<SimpleTcpJobParam> getJobParams() {
            return jobParams;
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class SimpleSSHScrapeJobProperties extends ScrapeJobProperties<SimpleSSHJobParam> {

        /**
         * Feature the static scrape job parameters.
         */
        private List<SimpleSSHJobParam> jobParams = new ArrayList<>();

        @Override
        public EventJobType getJobType() {
            return EventJobType.SIMPLE_SSH;
        }

        @Override
        public List<SimpleSSHJobParam> getJobParams() {
            return jobParams;
        }
    }

}
