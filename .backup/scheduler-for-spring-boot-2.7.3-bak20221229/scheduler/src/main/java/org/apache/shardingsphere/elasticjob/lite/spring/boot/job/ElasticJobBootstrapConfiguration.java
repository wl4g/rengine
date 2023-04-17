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
package org.apache.shardingsphere.elasticjob.lite.spring.boot.job;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasText;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Map;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.common.base.Preconditions;
import com.wl4g.rengine.scheduler.config.CollectorProperties;
import com.wl4g.rengine.scheduler.config.CollectorProperties.ScrapeJobProperties;
import com.wl4g.rengine.scheduler.config.CollectorProperties.TracingProperties;
import com.wl4g.rengine.scheduler.job.CollectJobExecutor.JobParamBase;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link ElasticJobBootstrapConfiguration}
 * 
 * @author James Wong
 * @date 2022-10-26
 * @since v1.0.0 // * @see
 *        {@link org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobBootstrapConfiguration}
 * @see {@link org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobLiteAutoConfiguration}
 */
@Slf4j
public class ElasticJobBootstrapConfiguration implements SmartInitializingSingleton, ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    @Override
    public void afterSingletonsInstantiated() {
        log.info("Initial Job bootstrap beans ...");
        createJobBootstrapBeans();
        log.info("Job Bootstrap beans initialized.");
    }

    /**
     * Create job bootstrap instances and register them into container.
     */
    public void createJobBootstrapBeans() {
        CollectorProperties config = applicationContext.getBean(CollectorProperties.class);
        SingletonBeanRegistry singletonBeanRegistry = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        CoordinatorRegistryCenter registryCenter = applicationContext.getBean(CoordinatorRegistryCenter.class);
        TracingConfiguration<?> tracingConfig = getTracingConfiguration();
        constructJobBootstraps(config, singletonBeanRegistry, registryCenter, tracingConfig);
    }

    @SuppressWarnings("rawtypes")
    private TracingConfiguration<?> getTracingConfiguration() {
        Map<String, TracingConfiguration> tracingConfigurationBeans = applicationContext
                .getBeansOfType(TracingConfiguration.class);
        if (tracingConfigurationBeans.isEmpty()) {
            return null;
        }
        if (1 == tracingConfigurationBeans.size()) {
            return tracingConfigurationBeans.values().iterator().next();
        }
        throw new BeanCreationException(
                "More than one [org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration] beans found. "
                        + "Consider disabling [org.apache.shardingsphere.elasticjob.tracing.boot.ElasticJobTracingAutoConfiguration].");
    }

    private void constructJobBootstraps(
            final CollectorProperties config,
            final SingletonBeanRegistry singletonBeanRegistry,
            final CoordinatorRegistryCenter registryCenter,
            final TracingConfiguration<?> tracingConfig) {

        //
        // [Begin] REMOVE FEATURES.
        //
        // for (Map.Entry<String, ElasticJobConfigurationProperties> entry :
        // elasticJobProperties.getJobs().entrySet()) {
        // ElasticJobConfigurationProperties jobConfigurationProperties =
        // entry.getValue();
        // Preconditions.checkArgument(null !=
        // jobConfigurationProperties.getElasticJobClass()
        // ||
        // !Strings.isNullOrEmpty(jobConfigurationProperties.getElasticJobType()),
        // "Please specific [elasticJobClass] or [elasticJobType] under job
        // configuration.");
        // Preconditions.checkArgument(null ==
        // jobConfigurationProperties.getElasticJobClass()
        // ||
        // Strings.isNullOrEmpty(jobConfigurationProperties.getElasticJobType()),
        // "[elasticJobClass] and [elasticJobType] are mutually exclusive.");
        // if (null != jobConfigurationProperties.getElasticJobClass()) {
        // registerClassedJob(entry.getKey(),
        // entry.getValue().getJobBootstrapBeanName(), singletonBeanRegistry,
        // registryCenter, tracingConfig, jobConfigurationProperties);
        // } else if
        // (!Strings.isNullOrEmpty(jobConfigurationProperties.getElasticJobType()))
        // {
        // registerTypedJob(entry.getKey(),
        // entry.getValue().getJobBootstrapBeanName(), singletonBeanRegistry,
        // registryCenter, tracingConfig, jobConfigurationProperties);
        // }
        // }
        //
        // [End] REMOVE FEATURES.
        //

        // The logic of the two registrations of elasticJobType/elasticJobClass
        // from org.apache.shardingsphere.elasticjob:elastic-job-spring-starter
        // is modified here.
        // see:org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobBootstrapConfiguration#constructJobBootstraps
        safeList(config.getScrapeJobConfigs()).forEach(scrapeConfig -> registerTypedJob(scrapeConfig.getName(),
                scrapeConfig.getJobBootstrapBeanName(), singletonBeanRegistry, registryCenter, tracingConfig, scrapeConfig));
    }

    //
    // [Begin] REMOVE FEATURES.
    //
    // private void registerClassedJob(final String jobName, final String
    // jobBootstrapBeanName, final SingletonBeanRegistry singletonBeanRegistry,
    // final CoordinatorRegistryCenter registryCenter,
    // final TracingConfiguration<?> tracingConfig, final
    // ElasticJobConfigurationProperties jobConfigurationProperties) {
    // JobConfiguration jobConfig =
    // jobConfigurationProperties.toJobConfiguration(jobName);
    // jobExtraConfigurations(jobConfig, tracingConfig);
    // ElasticJob elasticJob =
    // applicationContext.getBean(jobConfigurationProperties.getElasticJobClass());
    // if (Strings.isNullOrEmpty(jobConfig.getCron())) {
    // Preconditions.checkArgument(!Strings.isNullOrEmpty(jobBootstrapBeanName),
    // "The property [jobBootstrapBeanName] is required for One-off job.");
    // singletonBeanRegistry.registerSingleton(jobBootstrapBeanName, new
    // OneOffJobBootstrap(registryCenter, elasticJob, jobConfig));
    // } else {
    // String beanName = !Strings.isNullOrEmpty(jobBootstrapBeanName) ?
    // jobBootstrapBeanName : jobConfig.getJobName() + "ScheduleJobBootstrap";
    // singletonBeanRegistry.registerSingleton(beanName, new
    // ScheduleJobBootstrap(registryCenter, elasticJob, jobConfig));
    // }
    // }
    //
    // [End] REMOVE FEATURES.
    //

    private void registerTypedJob(
            final String jobName,
            final String jobBootstrapBeanName,
            final SingletonBeanRegistry singletonBeanRegistry,
            final CoordinatorRegistryCenter registryCenter,
            final TracingConfiguration<?> tracingConfig,
            final ScrapeJobProperties<? extends JobParamBase> scrapeConfig) {

        JobConfiguration jobConfig = scrapeConfig.toJobConfiguration(jobName);
        jobExtraConfigurations(jobConfig, tracingConfig);

        if (isBlank(jobConfig.getCron())) {
            hasText(jobBootstrapBeanName, "The property [jobBootstrapBeanName] is required for One-off job.");
            singletonBeanRegistry.registerSingleton(jobBootstrapBeanName,
                    new OneOffJobBootstrap(registryCenter, scrapeConfig.getJobType().name(), jobConfig));
        } else {
            String beanName = !isBlank(jobBootstrapBeanName) ? jobBootstrapBeanName
                    : jobConfig.getJobName() + "-ScheduleJobBootstrap";
            singletonBeanRegistry.registerSingleton(beanName,
                    new ScheduleJobBootstrap(registryCenter, scrapeConfig.getJobType().name(), jobConfig));
        }
    }

    private void jobExtraConfigurations(final JobConfiguration jobConfig, final TracingConfiguration<?> tracingConfig) {
        if (null == tracingConfig) {
            return;
        }
        TracingProperties tracingProperties = applicationContext.getBean(TracingProperties.class);
        Preconditions.checkArgument(
                tracingProperties.getIncludeJobNames().isEmpty() || tracingProperties.getExcludeJobNames().isEmpty(),
                "[tracing.includeJobNames] and [tracing.excludeJobNames] are mutually exclusive.");

        if ((tracingProperties.getIncludeJobNames().isEmpty()
                || tracingProperties.getIncludeJobNames().contains(jobConfig.getJobName()))
                && !tracingProperties.getExcludeJobNames().contains(jobConfig.getJobName())) {
            jobConfig.getExtraConfigurations().add(tracingConfig);
        }
    }

}
