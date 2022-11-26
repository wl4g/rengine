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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasText;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.JobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration;

import com.google.common.base.Preconditions;
import com.wl4g.infra.common.bean.KeyValue;
import com.wl4g.rengine.collector.config.CollectorProperties.ScrapeJobProperties;
import com.wl4g.rengine.collector.job.CollectJobExecutor.JobParamBase;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link ElasticJobBootstrapBuilder}
 * 
 * @author James Wong
 * @version 2022-10-26
 * @since v1.0.0
 * @see {@link com.wl4g.rengine.collector.config.ElasticJobBootstrapBuilder}
 * @see {@link org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobLiteAutoConfiguration}
 */
@Slf4j
public class ElasticJobBootstrapBuilder {
    final CollectorProperties config;
    final List<TracingConfiguration<?>> tracingConfigurations;
    final CoordinatorRegistryCenter registryCenter;

    public ElasticJobBootstrapBuilder(@NotNull CollectorProperties config, @NotNull CoordinatorRegistryCenter registryCenter,
            List<TracingConfiguration<?>> tracingConfigurations) {
        this.config = notNullOf(config, "config");
        this.registryCenter = notNullOf(registryCenter, "registryCenter");
        this.tracingConfigurations = tracingConfigurations;
    }

    /**
     * Create job bootstrap instances and register them into container.
     */
    public Map<String, JobBootstrap> build() {
        log.info("Initial Job bootstrap beans ...");
        TracingConfiguration<?> tracingConfig = getTracingConfiguration();
        return constructJobBootstraps(config, registryCenter, tracingConfig);
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
            final CollectorProperties config,
            final CoordinatorRegistryCenter registryCenter,
            final TracingConfiguration<?> tracingConfig) {
        // The logic of the two registrations of elasticJobType/elasticJobClass
        // from org.apache.shardingsphere.elasticjob:elastic-job-spring-starter
        // is modified here.
        // see:org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobBootstrapConfiguration#constructJobBootstraps
        return safeList(config.getScrapeJobConfigs()).stream()
                .map(scrapeConfig -> createRegisterTypedJob(scrapeConfig.getName(), scrapeConfig.getJobBootstrapBeanName(),
                        registryCenter, tracingConfig, scrapeConfig))
                .collect(toMap(kv -> (String) kv.getKey(), kv -> (JobBootstrap) kv.getValue()));
    }

    private KeyValue createRegisterTypedJob(
            final String jobName,
            final String jobBootstrapBeanName,
            final CoordinatorRegistryCenter registryCenter,
            final TracingConfiguration<?> tracingConfig,
            final ScrapeJobProperties<? extends JobParamBase> scrapeConfig) {
        JobConfiguration jobConfig = scrapeConfig.toJobConfiguration(jobName);
        jobExtraConfigurations(jobConfig, tracingConfig);
        if (isBlank(jobConfig.getCron())) {
            hasText(jobBootstrapBeanName, "The property [jobBootstrapBeanName] is required for One-off job.");
            return new KeyValue(jobBootstrapBeanName,
                    new OneOffJobBootstrap(registryCenter, scrapeConfig.getJobType().name(), jobConfig));
        } else {
            String beanName = !isBlank(jobBootstrapBeanName) ? jobBootstrapBeanName
                    : jobConfig.getJobName() + "-ScheduleJobBootstrap";
            return new KeyValue(beanName, new ScheduleJobBootstrap(registryCenter, scrapeConfig.getJobType().name(), jobConfig));
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

}
