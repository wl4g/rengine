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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeToList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.getField;
import static com.wl4g.rengine.controller.lifecycle.ElasticJobBootstrapBuilder.newDefaultJobConfig;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.ReflectionUtils.findField;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.shardingsphere.elasticjob.api.ElasticJob;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.executor.ElasticJobExecutor;
import org.apache.shardingsphere.elasticjob.executor.item.JobItemExecutor;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.JobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.internal.schedule.JobScheduler;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.rengine.common.entity.ControllerSchedule;
import com.wl4g.rengine.controller.config.RengineControllerProperties;
import com.wl4g.rengine.controller.exception.ScheduleException;
import com.wl4g.rengine.controller.job.AbstractJobExecutor;
import com.wl4g.rengine.controller.job.AbstractJobExecutor.ScheduleJobType;
import com.wl4g.rengine.controller.job.GlobalEngineScheduleController;
import com.wl4g.rengine.controller.lifecycle.ElasticJobBootstrapBuilder.JobParameter;

import lombok.CustomLog;

/**
 * {@link GlobalControllerJobManager}
 * 
 * @author James Wong
 * @version 2023-01-17
 * @since v1.0.0
 */
@CustomLog
public class GlobalControllerJobManager implements ApplicationRunner, Closeable {

    final RengineControllerProperties config;
    final List<TracingConfiguration<?>> tracingConfigurations;
    final CoordinatorRegistryCenter regCenter;
    final ScheduleJobBootstrap controllerBootstrap;
    final Map<Long, JobBootstrap> bootstrapRegistry;
    final Map<Long, InterProcessSemaphoreMutex> scheduleMutexLocksRegistry;

    public GlobalControllerJobManager(final @NotNull RengineControllerProperties config,
            final @NotNull List<TracingConfiguration<?>> tracingConfigurations,
            final @NotNull CoordinatorRegistryCenter registryCenter) {
        this.config = notNullOf(config, "config");
        this.tracingConfigurations = notNullOf(tracingConfigurations, "tracingConfigurations");
        this.regCenter = notNullOf(registryCenter, "registryCenter");
        final JobConfiguration jobConfig = config.getController()
                .toJobConfiguration(GlobalEngineScheduleController.class.getSimpleName());
        this.controllerBootstrap = createJobBootstrap(jobConfig);
        this.bootstrapRegistry = new ConcurrentHashMap<>(16);
        this.scheduleMutexLocksRegistry = new ConcurrentHashMap<>(16);
    }

    @Override
    public void close() throws IOException {
        safeMap(bootstrapRegistry).entrySet().forEach(e -> {
            try {
                final Field jobSchedulerField = findField(e.getValue().getClass(), "jobScheduler");
                final JobScheduler jobScheduler = getField(jobSchedulerField, e.getValue(), true);

                final Field jobExecutorField = findField(JobScheduler.class, "jobExecutor");
                final ElasticJobExecutor jobExecutor = getField(jobExecutorField, jobScheduler, true);

                final Field jobItemExecutorField = findField(ElasticJobExecutor.class, "jobItemExecutor");
                final JobItemExecutor<ElasticJob> jobItemExecutor = getField(jobItemExecutorField, jobExecutor, true);

                if (jobItemExecutor instanceof AbstractJobExecutor) {
                    ((AbstractJobExecutor) jobItemExecutor).close();
                }
            } catch (IOException ex) {
                log.warn(format("Unable to closing job item executor for scheduleId: %s", e.getKey()), ex);
            }
        });
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        startController();
    }

    private void startController() throws Exception {
        log.info("Scheduling for controller job ...");
        controllerBootstrap.schedule();
    }

    public boolean exists(Long scheduleId) {
        return bootstrapRegistry.containsKey(scheduleId);
    }

    @SuppressWarnings("unchecked")
    public <T extends JobBootstrap> T get(Long scheduleId) {
        return (T) bootstrapRegistry.get(scheduleId);
    }

    @SuppressWarnings("unchecked")
    public <T extends JobBootstrap> T add(
            @NotNull InterProcessSemaphoreMutex lock,
            @NotNull ScheduleJobType jobType,
            @NotBlank String jobName,
            @NotNull ControllerSchedule trigger,
            @NotNull JobParameter jobParameter) throws Exception {
        notNullOf(lock, "lock");
        notNullOf(jobType, "jobType");
        hasTextOf(jobName, "jobName");
        notNullOf(trigger, "trigger");
        notNullOf(jobParameter, "jobParameter");
        trigger.validate();

        final JobConfiguration jobConfig = newDefaultJobConfig(jobType, jobName, trigger, jobParameter);
        final JobBootstrap bootstrap = createJobBootstrap(jobConfig);
        final JobBootstrap existing = bootstrapRegistry.putIfAbsent(trigger.getId(), bootstrap);
        if (nonNull(existing)) {
            throw new ScheduleException(format("Already trigger '%s' scheduling for : %s", trigger.getId(), existing));
        }
        return (T) bootstrap;
    }

    public GlobalControllerJobManager remove(Long... scheduleIds) {
        final List<Long> _scheduleIds = safeToList(Long.class, scheduleIds);
        final var it = safeMap(bootstrapRegistry).entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, JobBootstrap> entry = it.next();
            if (_scheduleIds.contains(entry.getKey())) {
                it.remove();
                scheduleMutexLocksRegistry.remove(entry.getKey());
            }
        }
        return this;
    }

    public List<Long> start(Long... scheduleIds) {
        log.info("Schedule job bootstrap starting ...");
        final List<Long> _scheduleIds = safeToList(Long.class, scheduleIds);
        return safeMap(bootstrapRegistry).entrySet()
                .stream()
                .filter(e -> _scheduleIds.isEmpty() || (!_scheduleIds.isEmpty() && _scheduleIds.contains(e.getKey())))
                .map(e -> {
                    try {
                        if (nonNull(e.getValue()) && e.getValue() instanceof ScheduleJobBootstrap) {
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
                .filter(n -> nonNull(n))
                .collect(toList());
    }

    public List<Long> shutdown(Long... scheduleIds) {
        log.info("Schedule job bootstrap shutdown ...");
        final List<Long> _scheduleIds = safeToList(Long.class, scheduleIds);
        return safeMap(bootstrapRegistry).entrySet()
                .stream()
                .filter(e -> _scheduleIds.isEmpty() || (!_scheduleIds.isEmpty() && _scheduleIds.contains(e.getKey())))
                .map(e -> {
                    try {
                        ((ScheduleJobBootstrap) e.getValue()).shutdown();
                        log.info("Shutdown job bootstrap : {}", e.getKey());
                    } catch (Exception e1) {
                        log.error("Failed to Shutdown job bootstrap : {}", e.getKey());
                        return null;
                    }
                    return e.getKey();
                })
                .filter(n -> nonNull(n))
                .collect(toList());
    }

    public InterProcessSemaphoreMutex getMutexLock(final Long scheduleId) {
        InterProcessSemaphoreMutex mutex = scheduleMutexLocksRegistry.get(scheduleId);
        if (isNull(mutex)) {
            synchronized (this) {
                mutex = scheduleMutexLocksRegistry.get(scheduleId);
                if (isNull(mutex)) {
                    // Build path for bind trigger.
                    // see:https://curator.apache.org/curator-recipes/shared-lock.html
                    final String path = format("%s/%s", PATH_MUTEX_TRIGGERS, notNullOf(scheduleId, "scheduleId"));
                    scheduleMutexLocksRegistry.put(scheduleId,
                            (mutex = new InterProcessSemaphoreMutex(((ZookeeperRegistryCenter) regCenter).getClient(), path)));
                }
            }
        }
        return mutex;
    }

    @SuppressWarnings("unchecked")
    private <T extends JobBootstrap> T createJobBootstrap(JobConfiguration jobConfig) {
        try {
            final Map<String, JobBootstrap> bootstraps = new ElasticJobBootstrapBuilder(config, regCenter, tracingConfigurations)
                    .build(jobConfig);
            if (CollectionUtils2.isEmpty(bootstraps)) {
                throw new IllegalStateException(
                        format("Failed to create schedule job bootstrap, should't to be here. %s", jobConfig));
            }
            return (T) bootstraps.entrySet().iterator().next().getValue();
        } catch (Throwable e) {
            log.error("Failed to build job bootstrap.", e);
            throw e;
        }
    }

    public static final String PATH_MUTEX_TRIGGERS = "/mutex-triggers";

}
