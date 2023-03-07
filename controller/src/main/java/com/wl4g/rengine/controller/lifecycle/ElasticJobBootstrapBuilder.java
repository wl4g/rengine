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
package com.wl4g.rengine.controller.lifecycle;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeArrayToList;
import static com.wl4g.infra.common.lang.Assert2.hasText;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.JobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration;

import com.google.common.base.Preconditions;
import com.wl4g.infra.common.lang.tuples.Tuple2;
import com.wl4g.rengine.common.entity.ControllerSchedule;
import com.wl4g.rengine.common.entity.ControllerSchedule.GenericExecutionScheduleConfig;
import com.wl4g.rengine.controller.config.RengineControllerProperties;
import com.wl4g.rengine.controller.job.AbstractJobExecutor.ScheduleJobType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link ElasticJobBootstrapBuilder}
 * 
 * @author James Wong
 * @version 2022-10-26
 * @since v1.0.0
 * @see {@link com.wl4g.rengine.controller.lifecycle.ElasticJobBootstrapBuilder}
 * @see {@link org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobLiteAutoConfiguration}
 */
public class ElasticJobBootstrapBuilder {
    final RengineControllerProperties config;
    final List<TracingConfiguration<?>> tracingConfigurations;
    final CoordinatorRegistryCenter registryCenter;

    public ElasticJobBootstrapBuilder(@NotNull RengineControllerProperties config,
            @NotNull CoordinatorRegistryCenter registryCenter, List<TracingConfiguration<?>> tracingConfigurations) {
        this.config = notNullOf(config, "details");
        this.registryCenter = notNullOf(registryCenter, "registryCenter");
        this.tracingConfigurations = tracingConfigurations;
    }

    /**
     * Create job bootstrap instances and register them into container.
     */
    public Map<String, JobBootstrap> build(final JobConfiguration... jobConfigs) {
        return constructJobBootstraps(config, registryCenter, getTracingConfiguration(), jobConfigs);
    }

    private TracingConfiguration<?> getTracingConfiguration() {
        if (isNull(tracingConfigurations) || tracingConfigurations.isEmpty()) {
            return null;
        }
        if (1 == tracingConfigurations.size()) {
            return tracingConfigurations.get(0);
        }
        throw new IllegalStateException(
                "More than one [org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration] beans found. "
                        + "Consider disabling [org.apache.shardingsphere.elasticjob.tracing.boot.ElasticJobTracingAutoConfiguration].");
    }

    private Map<String, JobBootstrap> constructJobBootstraps(
            final RengineControllerProperties config,
            final CoordinatorRegistryCenter registryCenter,
            final TracingConfiguration<?> tracingConfig,
            final JobConfiguration... jobConfigs) {
        // The logic of the two registrations of elasticJobType/elasticJobClass
        // from org.apache.shardingsphere.elasticjob:elastic-job-spring-starter
        // is modified here.
        // see:org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobBootstrapConfiguration#constructJobBootstraps
        return safeArrayToList(jobConfigs).stream()
                .map(jobConfig -> createRegisterTypedJob(registryCenter, tracingConfig, jobConfig))
                .collect(toMap(kv -> (String) kv.getItem1(), kv -> (JobBootstrap) kv.getItem2()));
    }

    private Tuple2 createRegisterTypedJob(
            final CoordinatorRegistryCenter registryCenter,
            final TracingConfiguration<?> tracingConfig,
            final JobConfiguration jobConfig) {
        hasText(jobConfig.getJobName(), "The property [jobBootstrapBeanName] is required for One-off job.");
        jobExtraConfigurations(jobConfig, tracingConfig);
        if (isBlank(jobConfig.getCron())) {
            return new Tuple2(jobConfig.getJobName(),
                    new OneOffJobBootstrap(registryCenter, jobConfig.getJobType().name(), jobConfig));
        } else {
            return new Tuple2(jobConfig.getJobName(),
                    new ScheduleJobBootstrap(registryCenter, jobConfig.getJobType().name(), jobConfig));
        }
    }

    private void jobExtraConfigurations(final JobConfiguration jobConfig, final TracingConfiguration<?> tracingConfig) {
        if (null == tracingConfig) {
            return;
        }
        Preconditions.checkArgument(
                config.getTracing().getIncludeJobNames().isEmpty() || config.getTracing().getExcludeJobNames().isEmpty(),
                "[tracing.includeJobNames] and [tracing.excludeJobNames] are mutually exclusive.");
        if ((config.getTracing().getIncludeJobNames().isEmpty()
                || config.getTracing().getIncludeJobNames().contains(jobConfig.getJobName()))
                && !config.getTracing().getExcludeJobNames().contains(jobConfig.getJobName())) {
            jobConfig.getExtraConfigurations().add(tracingConfig);
        }
    }

    public static JobConfiguration newDefaultJobConfig(
            @NotNull ScheduleJobType jobType,
            @NotBlank String jobName,
            @NotNull ControllerSchedule schedule,
            @NotNull JobParameter jobParameter) {
        hasTextOf(jobName, "jobName");
        notNullOf(schedule, "trigger");
        notNullOf(jobParameter, "jobParameter");

        String cron = null; // OneOffJobBootstrap
        if (schedule.getDetails() instanceof GenericExecutionScheduleConfig) { // ScheduleJobBootstrap
            cron = ((GenericExecutionScheduleConfig) schedule.getDetails()).getCron();
        }
        return JobConfiguration.builder()
                .jobType(jobType)
                .jobName(jobName)
                .cron(cron)
                .disabled(DEFAULT_DISABLED)
                .overwrite(DEFAULT_OVERWRITE)
                .monitorExecution(schedule.getMonitorExecution())
                .failover(schedule.getFailover())
                .misfire(schedule.getMisfire())
                .timeZone(schedule.getTimeZone())
                // When setup true, the shardingTotalCount will be ignored,
                // and the will be automatically allocated according to the
                // number of cluster nodes priority.
                .autoShardingTotalCount(DEFAULT_AUTO_SHARDING_TOTAL_COUNT)
                .shardingTotalCount(DEFAULT_SHARDING_TOTAL_COUNT)
                .shardingItemParameters(DEFAULT_SHARDING_ITEM_PARAMETERS)
                .jobParameter(toJSONString(jobParameter))
                .maxTimeDiffSeconds(schedule.getMaxTimeDiffSeconds())
                .reconcileIntervalMinutes(schedule.getReconcileIntervalMinutes())
                .jobShardingStrategyType(DEFAULT_JOB_SHARDING_STRATEGY_TYPE)
                .jobExecutorServiceHandlerType(DEFAULT_JOB_EXECUTOR_SERVICE_HANDLER_TYPE)
                .jobErrorHandlerType(DEFAULT_JOB_ERROR_HANDLER_TYPE)
                .jobListenerTypes(DEFAULT_JOB_LISTENER_TYPES)
                .description(format(isBlank(cron) ? "One off job for %s" : "Cron schedule job for %s", jobName))
                .build();
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class JobParameter {
        private Long scheduleId;

        public JobParameter(@NotNull Long scheduleId) {
            this.scheduleId = notNullOf(scheduleId, "scheduleId");
        }
    }

    public static final boolean DEFAULT_DISABLED = false;
    public static final boolean DEFAULT_OVERWRITE = true;
    public static final boolean DEFAULT_AUTO_SHARDING_TOTAL_COUNT = true;
    public static final int DEFAULT_SHARDING_TOTAL_COUNT = 1;
    public static final String DEFAULT_SHARDING_ITEM_PARAMETERS = null;
    // public static final String DEFAULT_JOB_PARAMETER = null;
    public static final String DEFAULT_JOB_SHARDING_STRATEGY_TYPE = null;
    public static final String DEFAULT_JOB_EXECUTOR_SERVICE_HANDLER_TYPE = null;
    public static final String DEFAULT_JOB_ERROR_HANDLER_TYPE = null;
    public static final Collection<String> DEFAULT_JOB_LISTENER_TYPES = emptyList();
}
