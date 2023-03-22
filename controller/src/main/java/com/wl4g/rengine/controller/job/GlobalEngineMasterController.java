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
package com.wl4g.rengine.controller.job;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.controller.job.AbstractJobExecutor.ScheduleJobType.GLOBAL_BOOTSTRAPER;
import static com.wl4g.rengine.service.meter.RengineMeterService.DEFAULT_PERCENTILES;
import static com.wl4g.rengine.service.meter.RengineMeterService.MetricsName.global_schedule_controller;
import static com.wl4g.rengine.service.meter.RengineMeterService.MetricsName.global_schedule_controller_failure;
import static com.wl4g.rengine.service.meter.RengineMeterService.MetricsName.global_schedule_controller_success;
import static com.wl4g.rengine.service.meter.RengineMeterService.MetricsName.global_schedule_controller_time;
import static com.wl4g.rengine.service.meter.RengineMeterService.MetricsTag.METHOD_NAME;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.constraints.NotNull;

import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.JobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;

import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.rengine.common.entity.ControllerSchedule;
import com.wl4g.rengine.common.entity.ControllerSchedule.RunState;
import com.wl4g.rengine.common.entity.ControllerSchedule.ScheduleType;
import com.wl4g.rengine.controller.config.RengineControllerProperties;
import com.wl4g.rengine.controller.lifecycle.ElasticJobBootstrapBuilder.JobParameter;
import com.wl4g.rengine.service.ControllerLogService;
import com.wl4g.rengine.service.model.ControllerLogDelete;
import com.wl4g.rengine.service.model.ControllerScheduleQuery;

import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link GlobalEngineMasterController}
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v1.0.0
 * @see {@link org.apache.shardingsphere.elasticjob.http.executor.HttpJobExecutor}
 */
@Getter
@CustomLog
public class GlobalEngineMasterController extends AbstractJobExecutor {

    private static final String METHOD_EXECUTE = "execute";

    @Override
    public String getType() {
        return GLOBAL_BOOTSTRAPER.name();
    }

    @Override
    protected void execute(
            int currentShardingTotalCount,
            @NotNull JobConfiguration jobConfig,
            @NotNull JobFacade jobFacade,
            @NotNull ShardingContext context) throws Exception {

        // Scanning for current sharding scheduling schedules.
        log.info("Loading the sharding schedules for currentShardingTotalCount: {}, jobConfig: {}, jobFacade: {}, context: {}",
                currentShardingTotalCount, jobConfig, jobFacade, context);

        getMeterService()
                .counter(global_schedule_controller.getName(), global_schedule_controller.getHelp(), METHOD_NAME, METHOD_EXECUTE)
                .increment();

        final List<ControllerSchedule> shardingSchedules = getControllerScheduleService()
                .findWithSharding(ControllerScheduleQuery.builder()
                        // .enable(true)
                        // .type(ScheduleType.GENERIC_EXECUTION.name())
                        .build(), currentShardingTotalCount, context.getShardingItem());
        log.info("Loaded the sharding schedules : {}", shardingSchedules);

        getMeterService()
                .timer(global_schedule_controller_time.getName(), global_schedule_controller_time.getHelp(), DEFAULT_PERCENTILES,
                        METHOD_NAME, METHOD_EXECUTE)
                .record(() -> {
                    final AtomicBoolean hasFailure = new AtomicBoolean(false);
                    safeList(shardingSchedules).stream().forEach(schedule -> {
                        final String jobName = buildJobName(schedule);
                        try {
                            // Any a schedule can only be scheduled by one
                            // cluster node at the same time to prevent repeated
                            // scheduling of multiple nodes. Of course, if the
                            // current node is down, it will be transferred to
                            // other nodes to continue to be scheduled according
                            // to the elastic-job mechanism.
                            final var mutexLock = getGlobalScheduleJobManager().getMutexLock(schedule.getId());

                            // The status of the schedule is disabled, you need
                            // to shtudown the scheduling job.
                            if (schedule.getEnable() == BaseBean.DISABLED) {
                                log.info("Disabling scheduling : {}", schedule.getId());
                                if (getGlobalScheduleJobManager().exists(schedule.getId())) {
                                    getGlobalScheduleJobManager().shutdown(schedule.getId());
                                    getGlobalScheduleJobManager().remove(schedule.getId());
                                    // When the schedule is disabled(cancelled),
                                    // the mutex should be released, to allow
                                    // binding (scheduling) by other nodes after
                                    // schedule e-enabling.
                                    try {
                                        mutexLock.release(); // [#MARK1]
                                    } catch (IllegalStateException e) {
                                        // Ignore
                                    }
                                }
                                return;
                            }

                            // 1). Binding is allowed as long as the this JVM is
                            // not bound to this schedule (even if disabled and
                            // then enabled).
                            //
                            // 2). If the this JVM is already bound to this
                            // schedule, then the binding is skipped. (Use
                            // non-reentrant locks to solved)
                            //
                            // 3). Because the current node (shard) binding
                            //
                            // scheduling is stateful, once the lock is
                            // acquired, there is no need to actively release
                            // it, unless the schedule is actively
                            // disabled(cancelled), or the current JVM exits
                            // (passive release). refer to: [#MARK1]
                            if (!getGlobalScheduleJobManager().exists(schedule.getId())
                                    && mutexLock.acquire(1, TimeUnit.MILLISECONDS)) {
                                updateTriggerRunState(schedule.getId(), RunState.PREPARED);

                                log.info("Scheduling schedule for {} : {}", jobName, schedule);
                                final JobBootstrap bootstrap = getGlobalScheduleJobManager().add(mutexLock,
                                        ScheduleJobType.get(ScheduleType.valueOf(schedule.getDetails().getType())), jobName,
                                        schedule, new JobParameter(schedule.getId()));

                                if (bootstrap instanceof ScheduleJobBootstrap) {
                                    ((ScheduleJobBootstrap) bootstrap).schedule();
                                } else if (bootstrap instanceof OneOffJobBootstrap) {
                                    ((OneOffJobBootstrap) bootstrap).execute();
                                } else {
                                    throw new IllegalStateException(
                                            format("Unsupported the schedule job bootstrap type of : %s", bootstrap));
                                }

                                updateTriggerRunState(schedule.getId(), RunState.SCHED);
                            } else {
                                log.debug("Schedule {} is already bound to the this JVM or other nodes.", schedule.getId());
                            }

                        } catch (Throwable e) {
                            hasFailure.set(true);
                            log.error(format("Failed to scheduling for currentShardingTotalCount: %s, context: %s, jobName: %s",
                                    currentShardingTotalCount, context, jobName), e);
                            updateTriggerRunState(schedule.getId(), RunState.FAILED_SCHED);
                        }
                    });

                    if (hasFailure.get()) {
                        getMeterService()
                                .counter(global_schedule_controller_failure.getName(),
                                        global_schedule_controller_failure.getHelp(), METHOD_NAME, METHOD_EXECUTE)
                                .increment();
                    } else {
                        getMeterService()
                                .counter(global_schedule_controller_success.getName(),
                                        global_schedule_controller_success.getHelp(), METHOD_NAME, METHOD_EXECUTE)
                                .increment();
                    }
                });

        // The start the log purger, ignore if condition not met.
        PurgeJobLogController.get(getConfig(), getControllerLogService(), (ZookeeperRegistryCenter) getRegCenter()).start();
    }

    public static String buildJobName(final ControllerSchedule schedule) {
        return EngineGenericExecutionController.class.getSimpleName() + "-" + schedule.getId();
    }

    @CustomLog
    static class PurgeJobLogController {
        private static final String PATH_MUTEX_PURGE = "/mutex-purger";
        private static final Long DEFAULT_PURGE_INTERNAL_MS = Duration.ofHours(1).toMillis();
        private static PurgeJobLogController SINGLETON;
        private RengineControllerProperties config;
        private ControllerLogService controllerLogService;
        private InterProcessSemaphoreMutex purgerMutexLock;
        private Thread executor;
        private AtomicLong lastPurgeTime = new AtomicLong(0);

        public static PurgeJobLogController get(
                final @NotNull RengineControllerProperties config,
                final @NotNull ControllerLogService controllerLogService,
                final @NotNull ZookeeperRegistryCenter regCenter) {
            notNullOf(regCenter, "regCenter");
            if (isNull(SINGLETON)) {
                synchronized (PurgeJobLogController.class) {
                    if (isNull(SINGLETON)) {
                        SINGLETON = new PurgeJobLogController();
                        SINGLETON.config = notNullOf(config, "config");
                        SINGLETON.controllerLogService = notNullOf(controllerLogService, "controllerLogService");
                        // see:https://curator.apache.org/curator-recipes/shared-lock.html
                        SINGLETON.purgerMutexLock = new InterProcessSemaphoreMutex((regCenter).getClient(), PATH_MUTEX_PURGE);
                    }
                }
            }
            return SINGLETON;
        }

        public void start() {
            if (currentTimeMillis() - lastPurgeTime.get() > DEFAULT_PURGE_INTERNAL_MS) {
                log.debug("No need to start purge controller.");
                return;
            }
            this.lastPurgeTime.set(currentTimeMillis());
            if (isNull(executor)) {
                this.executor = new Thread(() -> {
                    try {
                        if (purgerMutexLock.acquire(1, TimeUnit.MILLISECONDS)) {
                            log.info("Purging schedule job logs for : {}", config.getPurger());

                            // Purge past logs according to configuration,
                            // keeping only the most recent period of time.
                            final var purgeUpperTime = currentTimeMillis()
                                    - Duration.ofHours(config.getPurger().getLogRetentionHours()).toMillis();

                            final var result = controllerLogService.delete(ControllerLogDelete.builder()
                                    .updateDateLower(new Date(1))
                                    // TODO notice timezone?
                                    .updateDateUpper(new Date(purgeUpperTime))
                                    .retentionCount(config.getPurger().getLogRetentionCount())
                                    .build());
                            log.info("Purged to scheduling job logs of count : {}", result.getDeletedCount());
                        }
                    } catch (Throwable ex) {
                        log.error(format("Failed to purge logs for : %s", config.getPurger()), ex);
                    } finally {
                        this.executor = null;
                        try {
                            this.purgerMutexLock.release();
                        } catch (Exception ex) {
                            log.error("Failed to release purger mutex lock.", ex);
                        }
                    }
                });
                this.executor.start();
            }
        }
    }

}
