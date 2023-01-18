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
package com.wl4g.rengine.scheduler.lifecycle;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeToList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.JobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.rengine.common.entity.ScheduleTrigger.CronTriggerConfig;
import com.wl4g.rengine.scheduler.config.RengineSchedulerProperties;
import com.wl4g.rengine.scheduler.exception.ScheduleException;
import com.wl4g.rengine.scheduler.job.AbstractJobExecutor.ExecutorJobType;
import com.wl4g.rengine.scheduler.job.EngineScheduleController;
import com.wl4g.rengine.scheduler.lifecycle.ElasticJobBootstrapBuilder.JobParameter;

import lombok.CustomLog;

/**
 * {@link GlobalScheduleJobManager}
 * 
 * @author James Wong
 * @version 2023-01-17
 * @since v1.0.0
 */
@CustomLog
public class GlobalScheduleJobManager implements ApplicationRunner {
    final RengineSchedulerProperties config;
    final List<TracingConfiguration<?>> tracingConfigurations;
    final CoordinatorRegistryCenter registryCenter;
    final ScheduleJobBootstrap controllerBootstrap;
    final Map<String, JobBootstrap> schedulerBootstrapRegistry;
    // final Map<String, JobBootstrap> executionBootstrapRegistry;

    public GlobalScheduleJobManager(final @NotNull RengineSchedulerProperties config,
            final @NotNull List<TracingConfiguration<?>> tracingConfigurations,
            final @NotNull CoordinatorRegistryCenter registryCenter) {
        this.config = notNullOf(config, "config");
        this.tracingConfigurations = notNullOf(tracingConfigurations, "tracingConfigurations");
        this.registryCenter = notNullOf(registryCenter, "registryCenter");
        this.controllerBootstrap = createJobBootstrap(config.getScheduleController()
                .toJobConfiguration(EngineScheduleController.class.getSimpleName() + "@ScheduleJob"));
        this.schedulerBootstrapRegistry = new ConcurrentHashMap<>(16);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        startController();
    }

    private void startController() throws Exception {
        log.info("Scheduling for controller job ...");
        controllerBootstrap.schedule();
    }

    @SuppressWarnings("unchecked")
    public <T extends JobBootstrap> T get(String jobName) {
        return (T) schedulerBootstrapRegistry.get(jobName);
    }

    @SuppressWarnings("unchecked")
    public <T extends JobBootstrap> T add(
            @NotNull ExecutorJobType jobType,
            @NotBlank String jobName,
            @NotNull CronTriggerConfig cronTrigger,
            @NotNull JobParameter jobParameter,
            boolean force) {
        notNullOf(jobType, "jobType");
        hasTextOf(jobName, "jobName");
        notNullOf(cronTrigger, "cronTrigger");
        notNullOf(jobParameter, "jobParameter");
        cronTrigger.validate();

        final JobConfiguration jobConfig = ElasticJobBootstrapBuilder.newDefaultJobConfig(jobType, jobName, cronTrigger,
                jobParameter);
        final JobBootstrap bootstrap = createJobBootstrap(jobConfig);
        final JobBootstrap existing = schedulerBootstrapRegistry.putIfAbsent(jobName, bootstrap);
        if (nonNull(existing)) {
            if (force) {
                log.warn("Shutdowning for existing job of {} ...", jobName);
                existing.shutdown();
            } else {
                throw new ScheduleException(format("Existing schedule job for %s", jobName));
            }
        }

        return (T) bootstrap;
    }

    public GlobalScheduleJobManager remove(String jobName) {
        schedulerBootstrapRegistry.remove(jobName);
        return this;
    }

    public List<String> start(String... jobNames) {
        log.info("Schedule job bootstrap starting ...");
        final List<String> _jobNames = safeToList(String.class, jobNames);
        return safeMap(schedulerBootstrapRegistry).entrySet()
                .stream()
                .filter(e -> _jobNames.isEmpty() || (!_jobNames.isEmpty() && _jobNames.contains(e.getKey())))
                .map(e -> {
                    try {
                        if (e.getValue() instanceof ScheduleJobBootstrap) {
                            ((ScheduleJobBootstrap) e.getValue()).schedule();
                            // } else if (e.getValue() instanceof
                            // OneOffJobBootstrap) {
                            // ((OneOffJobBootstrap) e.getValue()).execute();
                        }
                        log.info("Scheduled job bootstrap : {}", e.getKey());
                    } catch (Exception e1) {
                        log.error("Failed to scheduled job bootstrap : {}", e.getKey());
                        return null;
                    }
                    return e.getKey();
                })
                .filter(n -> !isBlank(n))
                .collect(toList());
    }

    public List<String> shutdown(String... jobNames) {
        log.info("Schedule job bootstrap shutdown ...");
        final List<String> _jobNames = safeToList(String.class, jobNames);
        return safeMap(schedulerBootstrapRegistry).entrySet()
                .stream()
                .filter(e -> _jobNames.isEmpty() || (!_jobNames.isEmpty() && _jobNames.contains(e.getKey())))
                .map(e -> {
                    try {
                        e.getValue().shutdown();
                        log.info("Shutdown job bootstrap : {}", e.getKey());
                    } catch (Exception e1) {
                        log.error("Failed to Shutdown job bootstrap : {}", e.getKey());
                        return null;
                    }
                    return e.getKey();
                })
                .filter(n -> !isBlank(n))
                .collect(toList());
    }

    @SuppressWarnings("unchecked")
    private <T extends JobBootstrap> T createJobBootstrap(JobConfiguration jobConfig) {
        final Map<String, JobBootstrap> bootstraps = new ElasticJobBootstrapBuilder(config, registryCenter, tracingConfigurations)
                .build(jobConfig);
        if (CollectionUtils2.isEmpty(bootstraps)) {
            throw new IllegalStateException(
                    format("Failed to create schedule job bootstrap, should't to be here. %s", jobConfig));
        }
        return (T) bootstraps.entrySet().iterator().next().getValue();
    }
}
