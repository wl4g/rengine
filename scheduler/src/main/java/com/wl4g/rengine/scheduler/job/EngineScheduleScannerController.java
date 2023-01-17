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
package com.wl4g.rengine.scheduler.job;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.task.QuartzUtils2.newDefaultJobDetail;
import static com.wl4g.infra.common.task.QuartzUtils2.newDefaultJobTrigger;
import static com.wl4g.infra.common.task.QuartzUtils2.newScheduler;
import static com.wl4g.rengine.scheduler.job.AbstractJobExecutor.ExecutorJobType.ENGINE_SCHEDULE_CONTROLLER;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import com.wl4g.rengine.client.core.RengineClient;
import com.wl4g.rengine.common.entity.ScheduleJob;
import com.wl4g.rengine.common.entity.ScheduleJob.RunState;
import com.wl4g.rengine.common.entity.ScheduleTrigger;
import com.wl4g.rengine.common.entity.ScheduleTrigger.CronTriggerConfig;
import com.wl4g.rengine.common.entity.ScheduleTrigger.TriggerType;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.ExecuteResult;
import com.wl4g.rengine.service.ScheduleJobService;
import com.wl4g.rengine.service.model.QueryScheduleTrigger;
import com.wl4g.rengine.service.model.SaveScheduleJob;
import com.wl4g.rengine.service.model.SaveScheduleJobResult;

import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link EngineScheduleScannerController}
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v1.0.0
 * @see {@link org.apache.shardingsphere.elasticjob.http.executor.HttpJobExecutor}
 */
@Getter
@CustomLog
public class EngineScheduleScannerController extends AbstractJobExecutor {

    // The engine execution scheduling local scheduler.
    private final Scheduler scheduler;

    public EngineScheduleScannerController() {
        super();
        // TODO use config
        // final int concurrency=config.getScrapeJobConfigs();
        final int concurrency = 2;
        this.scheduler = newScheduler(ENGINE_SCHEDULE_CONTROLLER.name(), concurrency, null, null, null);
        try {
            this.scheduler.start();
        } catch (SchedulerException e) {
            log.error("Failed to start engine execution scheduler.", e);
        }
    }

    @Override
    public String getType() {
        return ENGINE_SCHEDULE_CONTROLLER.name();
    }

    @Override
    protected void execute(
            int currentShardingTotalCount,
            @NotNull JobConfiguration jobConfig,
            @NotNull JobFacade jobFacade,
            @NotNull ShardingContext context) throws Exception {

        // Scanning for current sharding scheduling triggers.
        log.info("Loading the sharding triggers for currentShardingTotalCount: {}, jobConfig: {}, jobFacade: {}, context: {}",
                currentShardingTotalCount, jobConfig, jobFacade, context);
        final List<ScheduleTrigger> shardingTriggers = scheduleTriggerService.findWithSharding(
                QueryScheduleTrigger.builder().type(TriggerType.CRON.name()).enable(true).build(), currentShardingTotalCount,
                context.getShardingItem());
        log.info("Loaded the sharding triggers : {}", shardingTriggers);

        // Actual scheduling to engine execution job.
        safeList(shardingTriggers).parallelStream().forEach(t -> {
            final String jobId = t.getId() + "-job@" + EngineSchedulingJob.class.getSimpleName();
            final String triggerId = t.getId() + "-trigger@" + EngineSchedulingJob.class.getSimpleName();
            JobDetail jobDetail = null;
            Trigger jobTrigger = null;
            SaveScheduleJob jobPrepared = null;

            try {
                final CronTriggerConfig ctc = (CronTriggerConfig) t.getProperties();
                log.info("Scheduling engine execution for : {}", ctc);

                // Build job
                jobDetail = newDefaultJobDetail(jobId, EngineSchedulingJob.class);
                final JobDataMap jobDataMap = new JobDataMap(safeMap(ctc.getArgs()));
                jobDataMap.put(KEY_RENGINE_CLIENT, rengineClient);
                jobDataMap.put(KEY_CRON_TRIGGER, ctc);
                jobDataMap.put(KEY_SCHEDULING_JOB_SERVICE, scheduleJobService);

                // Update the scheduling preparation info to the DB to ensure
                // that the execution job id is generated.
                jobPrepared = SaveScheduleJob.builder().runState(RunState.PREPARED).build();
                log.debug("Updating to job for : {}", jobPrepared);
                final SaveScheduleJobResult resultPrepared = scheduleJobService.save(jobPrepared);
                jobDataMap.put(KEY_CURRENT_JOB_ID, resultPrepared.getId());
                log.debug("Updated to job for : {} => {}", jobPrepared, resultPrepared);

                // Build trigger
                jobTrigger = newDefaultJobTrigger(triggerId, ctc.getCron(), ctc.getMisfire(), jobDataMap);

                // Scheduling job
                final Date firstFireTime = scheduler.scheduleJob(jobDetail, jobTrigger);

                // Update scheduled info to DB.
                final ScheduleJob jobSched = scheduleJobService.get(resultPrepared.getId());
                jobSched.setRunState(RunState.SCHED);
                jobSched.setSchedTime(new Date());
                jobSched.setFirstFireTime(firstFireTime);

                log.debug("Updating to job for : {}", jobSched);
                final SaveScheduleJobResult resultSched = scheduleJobService.save(jobSched);
                log.debug("Updated to job for : {} => {}", jobSched, resultSched);

            } catch (Exception e) {
                log.error(format("Failed to scheduling engine execution for jobId: %s, triggerId: %s", jobId, triggerId), e);

                if (nonNull(jobTrigger)) {
                    try {
                        scheduler.pauseTrigger(jobTrigger.getKey());
                    } catch (SchedulerException e2) {
                        log.warn(format("Failed to pause failed job triggers for jobId: %s, triggerId: %s", jobId, triggerId),
                                e2);
                    }
                }
                if (nonNull(jobDetail)) {
                    try {
                        scheduler.pauseJob(jobDetail.getKey());
                    } catch (SchedulerException e2) {
                        log.warn(format("Failed to pause failed job for jobId: %s, triggerId: %s", jobId, triggerId), e2);
                    }
                    try {
                        scheduler.deleteJob(jobDetail.getKey());
                    } catch (SchedulerException e2) {
                        log.warn(format("Failed to clean up failed job triggers for jobId: %s, triggerId: %s", jobId, triggerId),
                                e2);
                    }
                }

                // The also update the failed reason to DB when scheduling
                // fails.
                SaveScheduleJob jobSchedFailed = null;
                try {
                    jobSchedFailed = SaveScheduleJob.builder()
                            .runState(RunState.SCHED)
                            .schedTime(new Date())
                            .remark(format("Failed to scheduling engine execution. reason: %s", e.getMessage()))
                            .build();
                    // The preparation information was not saved successful?
                    // then new insert.
                    if (nonNull(jobPrepared)) {
                        jobSchedFailed.setId(jobPrepared.getId());
                    }
                    log.debug("Updating to failed job for : {}", jobSchedFailed);
                    final var resultSchedFailed = scheduleJobService.save(jobSchedFailed);
                    log.debug("Updated to failed job for : {} => {}", jobSchedFailed, resultSchedFailed);
                } catch (Exception e2) {
                    log.error(format("Failed to update failed scheduling job to DB. job: {}", jobSchedFailed), e2);
                }
            }
        });
    }

    public static class EngineScheduleControllerJobParam extends JobParamBase {
    }

    public static final String KEY_RENGINE_CLIENT = "__KEY_" + RengineClient.class.getName();
    public static final String KEY_CRON_TRIGGER = "__KEY_" + CronTriggerConfig.class.getName();
    public static final String KEY_SCHEDULING_JOB_SERVICE = "__KEY_" + ScheduleJobService.class.getName();
    public static final String KEY_CURRENT_JOB_ID = "__KEY_" + ScheduleJob.class.getName() + ".ID";

}
