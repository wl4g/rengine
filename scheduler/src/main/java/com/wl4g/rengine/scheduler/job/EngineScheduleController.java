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
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.scheduler.job.AbstractJobExecutor.ExecutorJobType.ENGINE_SCHEDULE_CONTROLLER;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;

import com.wl4g.rengine.common.entity.ScheduleJob;
import com.wl4g.rengine.common.entity.ScheduleJob.JobType;
import com.wl4g.rengine.common.entity.ScheduleJob.RunState;
//import com.wl4g.rengine.client.core.RengineClient;
//import com.wl4g.rengine.common.entity.ScheduleJob;
//import com.wl4g.rengine.service.ScheduleJobService;
import com.wl4g.rengine.common.entity.ScheduleTrigger;
import com.wl4g.rengine.common.entity.ScheduleTrigger.CronTriggerConfig;
import com.wl4g.rengine.common.entity.ScheduleTrigger.TriggerType;
import com.wl4g.rengine.scheduler.lifecycle.ElasticJobBootstrapBuilder.JobParameter;
import com.wl4g.rengine.service.model.QueryScheduleTrigger;
import com.wl4g.rengine.service.model.SaveScheduleJob;
import com.wl4g.rengine.service.model.SaveScheduleJobResult;

import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link EngineScheduleController}
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v1.0.0
 * @see {@link org.apache.shardingsphere.elasticjob.http.executor.HttpJobExecutor}
 */
@Getter
@CustomLog
public class EngineScheduleController extends AbstractJobExecutor {

    // The engine execution scheduling local scheduler.
    // private final Scheduler scheduler;

    public EngineScheduleController() {
        super();
        // TODO use config
        // final int concurrency=config.getScrapeJobConfigs();
        // final int concurrency = 2;
        // this.scheduler = newScheduler(ENGINE_SCHEDULE_CONTROLLER.name(),
        // concurrency, null, null, null);
        // try {
        // this.scheduler.start();
        // } catch (SchedulerException e) {
        // log.error("Failed to start engine execution scheduler.", e);
        // }
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
        safeList(shardingTriggers).parallelStream().forEach(trigger -> {
            final CronTriggerConfig ctc = ((CronTriggerConfig) trigger.getProperties()).validate();
            notNullOf(ctc, "cronTriggerConfig");

            final String jobName = trigger.getId() + "-" + EngineExecutionScheduler.class.getSimpleName() + "@ScheduleJob";
            SaveScheduleJob jobPrepared = null;
            try {
                jobPrepared = preparedForScheduleJob(trigger.getId(), jobName);

                log.info("Schedule scheduling job for {} : {}", jobName, trigger);

                final ScheduleJobBootstrap schedulerBootstrap = globalScheduleJobManager.add(
                        ExecutorJobType.ENGINE_EXECUTION_SCHEDULER, jobName, ctc,
                        new JobParameter(trigger.getId(), jobPrepared.getId()), true);

                schedulerBootstrap.schedule();

                schedForScheduleJob(jobPrepared.getId());
            } catch (Exception e) {
                log.error(
                        format("Failed to scheduling for currentShardingTotalCount: %s, context: %s, jobName: %s, triggerId: %s",
                                currentShardingTotalCount, context, jobName, trigger.getId()),
                        e);
                if (nonNull(jobPrepared)) {
                    failedSchedForScheduleJob(jobPrepared.getId());
                }
            }
        });
    }

    private SaveScheduleJob preparedForScheduleJob(final Long triggerId, final String jobName) {
        // Update the scheduling preparation info to the DB to ensure
        // that the execution job id is generated.
        final SaveScheduleJob jobPrepared = SaveScheduleJob.builder()
                .triggerId(triggerId)
                .jobType(JobType.SCHEDULING)
                .jobName(jobName)
                .runState(RunState.PREPARED)
                .build();

        log.debug("Updating to job for : {}", jobPrepared);
        final SaveScheduleJobResult resultPrepared = scheduleJobService.save(jobPrepared);
        log.debug("Updated to job for : {} => {}", jobPrepared, resultPrepared);
        return jobPrepared;
    }

    private void schedForScheduleJob(final Long scheduleJobId) {
        final ScheduleJob jobSched = scheduleJobService.get(scheduleJobId);
        jobSched.setRunState(RunState.SCHED);
        jobSched.setSchedTime(new Date());

        log.debug("Updating to job for : {}", jobSched);
        final SaveScheduleJobResult resultSched = scheduleJobService.save(jobSched);
        log.debug("Updated to job for : {} => {}", jobSched, resultSched);
    }

    private void failedSchedForScheduleJob(final Long scheduleJobId) {
        ScheduleJob jobFailedSched = null;
        try {
            jobFailedSched = scheduleJobService.get(scheduleJobId);
            jobFailedSched.setRunState(RunState.FAILED_SCHED);
            jobFailedSched.setFinishedTime(new Date());

            log.debug("Updating to job for : {}", jobFailedSched);
            final SaveScheduleJobResult resultSched = scheduleJobService.save(jobFailedSched);
            log.debug("Updated to job for : {} => {}", jobFailedSched, resultSched);
        } catch (Exception e2) {
            log.error(format("Failed to update failed sched scheduling job to DB. - %s", jobFailedSched), e2);
        }
    }

}
