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
import static com.wl4g.rengine.scheduler.job.AbstractJobExecutor.SchedulerJobType.ENGINE_EXECUTION_SCHEDULER_CONTROLLER;
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

import com.wl4g.infra.context.utils.SpringContextHolder;
import com.wl4g.rengine.client.core.RengineClient;
import com.wl4g.rengine.common.entity.SchedulingJob;
import com.wl4g.rengine.common.entity.SchedulingJob.RunState;
import com.wl4g.rengine.common.entity.SchedulingTrigger;
import com.wl4g.rengine.common.entity.SchedulingTrigger.CronTriggerConfig;
import com.wl4g.rengine.common.entity.SchedulingTrigger.TriggerType;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.ExecuteResult;
import com.wl4g.rengine.service.SchedulingJobService;
import com.wl4g.rengine.service.SchedulingTriggerService;
import com.wl4g.rengine.service.model.QuerySchedulingTrigger;
import com.wl4g.rengine.service.model.SaveSchedulingJob;
import com.wl4g.rengine.service.model.SaveSchedulingJobResult;

import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link EngineSchedulingControllerJobExecutor}
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v1.0.0
 * @see {@link org.apache.shardingsphere.elasticjob.http.executor.HttpJobExecutor}
 */
@Getter
@CustomLog
public class EngineSchedulingControllerJobExecutor extends AbstractJobExecutor {

    private final RengineClient rengineClient;
    private final SchedulingTriggerService schedulingTriggerService;
    private final SchedulingJobService schedulingJobService;
    // The engine execution scheduling local scheduler.
    private final Scheduler scheduler;

    public EngineSchedulingControllerJobExecutor() {
        super();
        this.rengineClient = SpringContextHolder.getBean(RengineClient.class);
        this.schedulingTriggerService = SpringContextHolder.getBean(SchedulingTriggerService.class);
        this.schedulingJobService = SpringContextHolder.getBean(SchedulingJobService.class);

        // TODO use config
        // final int concurrency=config.getScrapeJobConfigs();
        final int concurrency = 2;
        this.scheduler = newScheduler(ENGINE_EXECUTION_SCHEDULER_CONTROLLER.name(), concurrency, null, null, null);
        try {
            this.scheduler.start();
        } catch (SchedulerException e) {
            log.error("Failed to start engine execution scheduler.", e);
        }
    }

    @Override
    public String getType() {
        return ENGINE_EXECUTION_SCHEDULER_CONTROLLER.name();
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
        final List<SchedulingTrigger> shardingTriggers = schedulingTriggerService.findWithSharding(
                QuerySchedulingTrigger.builder().type(TriggerType.CRON.name()).enable(true).build(), currentShardingTotalCount,
                context.getShardingItem());
        log.info("Loaded the sharding triggers : {}", shardingTriggers);

        // Actual scheduling to engine execution job.
        safeList(shardingTriggers).parallelStream().forEach(t -> {
            final String jobId = t.getId() + "-job@" + EngineSchedulingJob.class.getSimpleName();
            final String triggerId = t.getId() + "-trigger@" + EngineSchedulingJob.class.getSimpleName();
            JobDetail jobDetail = null;
            Trigger jobTrigger = null;
            SaveSchedulingJob jobPrepared = null;

            try {
                final CronTriggerConfig ctc = (CronTriggerConfig) t.getProperties();
                log.info("Scheduling engine execution for : {}", ctc);

                // Build job
                jobDetail = newDefaultJobDetail(jobId, EngineSchedulingJob.class);
                final JobDataMap jobDataMap = new JobDataMap(safeMap(ctc.getArgs()));
                jobDataMap.put(KEY_RENGINE_CLIENT, rengineClient);
                jobDataMap.put(KEY_CRON_TRIGGER, ctc);
                jobDataMap.put(KEY_SCHEDULING_JOB_SERVICE, schedulingJobService);

                // Update the scheduling preparation info to the DB to ensure
                // that the execution job id is generated.
                jobPrepared = SaveSchedulingJob.builder().runState(RunState.PREPARED).build();
                log.debug("Updating to job for : {}", jobPrepared);
                final SaveSchedulingJobResult resultPrepared = schedulingJobService.save(jobPrepared);
                jobDataMap.put(KEY_CURRENT_JOB_ID, resultPrepared.getId());
                log.debug("Updated to job for : {} => {}", jobPrepared, resultPrepared);

                // Build trigger
                jobTrigger = newDefaultJobTrigger(triggerId, ctc.getCron(), ctc.getMisfire(), jobDataMap);

                // Scheduling job
                final Date firstFireTime = scheduler.scheduleJob(jobDetail, jobTrigger);

                // Update scheduled info to DB.
                final SchedulingJob jobSched = schedulingJobService.get(resultPrepared.getId());
                jobSched.setRunState(RunState.SCHED);
                jobSched.setSchedTime(new Date());
                jobSched.setFirstFireTime(firstFireTime);

                log.debug("Updating to job for : {}", jobSched);
                final SaveSchedulingJobResult resultSched = schedulingJobService.save(jobSched);
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
                SaveSchedulingJob jobSchedFailed = null;
                try {
                    jobSchedFailed = SaveSchedulingJob.builder()
                            .runState(RunState.SCHED)
                            .schedTime(new Date())
                            .remark(format("Failed to scheduling engine execution. reason: %s", e.getMessage()))
                            .build();
                    // The preparation information was not saved successful ?
                    // then new insert.
                    if (nonNull(jobPrepared)) {
                        jobSchedFailed.setId(jobPrepared.getId());
                    }
                    log.debug("Updating to failed job for : {}", jobSchedFailed);
                    final var resultSchedFailed = schedulingJobService.save(jobSchedFailed);
                    log.debug("Updated to failed job for : {} => {}", jobSchedFailed, resultSchedFailed);
                } catch (Exception e2) {
                    log.error(format("Failed to update failed scheduling job to DB. job: {}", jobSchedFailed), e2);
                }
            }
        });
    }

    public static class EngineSchedulingJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            RengineClient rengineClient = null;
            CronTriggerConfig ctc = null;
            SchedulingJobService schedulingJobService = null;
            Long jobId = null;

            try {
                final JobDataMap jobDataMap = context.getTrigger().getJobDataMap();
                assert nonNull(jobDataMap);

                rengineClient = (RengineClient) jobDataMap.get(KEY_RENGINE_CLIENT);
                ctc = (CronTriggerConfig) jobDataMap.get(KEY_CRON_TRIGGER);
                schedulingJobService = (SchedulingJobService) jobDataMap.get(KEY_SCHEDULING_JOB_SERVICE);
                jobId = (Long) jobDataMap.get(KEY_CURRENT_JOB_ID);

                assert nonNull(rengineClient);
                assert nonNull(ctc);
                assert nonNull(schedulingJobService);
                assert nonNull(jobId);

                final ExecuteResult result = rengineClient.execute(ExecuteRequest.builder()
                        // .clientId("") // TODO
                        // .clientSecret("")
                        .scenesCodes(ctc.getScenesCodes())
                        .bestEffort(ctc.getBestEffort())
                        .timeout(ctc.getTimeout())
                        .args(safeMap(ctc.getArgs()))
                        .build(), ex -> null);

                // Update execution success info to DB.
                final SchedulingJob jobExecutionSuccess = schedulingJobService.get(jobId);
                jobExecutionSuccess.setRunState(RunState.SUCCESS);
                jobExecutionSuccess.setFinishedTime(new Date());
                jobExecutionSuccess.setRequestId(result.getRequestId());
                jobExecutionSuccess.setResults(result.getResults());

                log.debug("Updating to job for : {}", jobExecutionSuccess);
                final var resultSuccess = schedulingJobService.save(jobExecutionSuccess);
                log.debug("Updated to job for : {} => {}", jobExecutionSuccess, resultSuccess);

            } catch (Exception e) {
                log.error(format("Failed to execute engine execution for : jobId: %s, triggerKey: %s", jobId,
                        context.getTrigger().getKey()), e);

                // Update execution failed info to DB.
                SchedulingJob jobExecutionFailed = null;
                try {
                    jobExecutionFailed = schedulingJobService.get(jobId);
                    jobExecutionFailed.setRunState(RunState.FAILED);
                    jobExecutionFailed.setFinishedTime(new Date());

                    log.debug("Updating to job for : {}", jobExecutionFailed);
                    final var resultFailed = schedulingJobService.save(jobExecutionFailed);
                    log.debug("Updated to job for : {} => {}", jobExecutionFailed, resultFailed);
                } catch (Exception e2) {
                    log.error(format("Failed to update failed execution job to DB. job: %s", jobExecutionFailed), e2);
                }
            }
        }
    }

    public static class EngineSchedulingControllerJobParam extends JobParamBase {
    }

    public static final String KEY_RENGINE_CLIENT = "__KEY_" + RengineClient.class.getName();
    public static final String KEY_CRON_TRIGGER = "__KEY_" + CronTriggerConfig.class.getName();
    public static final String KEY_SCHEDULING_JOB_SERVICE = "__KEY_" + SchedulingJobService.class.getName();
    public static final String KEY_CURRENT_JOB_ID = "__KEY_" + SchedulingJob.class.getName() + ".ID";

}
