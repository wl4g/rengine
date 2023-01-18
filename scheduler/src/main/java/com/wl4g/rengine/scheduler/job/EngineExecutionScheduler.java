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
package com.wl4g.rengine.scheduler.job;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;

import com.wl4g.rengine.common.entity.ScheduleJob;
import com.wl4g.rengine.common.entity.ScheduleJob.JobType;
import com.wl4g.rengine.common.entity.ScheduleJob.RunState;
import com.wl4g.rengine.common.entity.ScheduleTrigger;
import com.wl4g.rengine.common.entity.ScheduleTrigger.CronTriggerConfig;
import com.wl4g.rengine.scheduler.lifecycle.ElasticJobBootstrapBuilder.JobParameter;
import com.wl4g.rengine.service.model.SaveScheduleJob;
import com.wl4g.rengine.service.model.SaveScheduleJobResult;

import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link EngineExecutionScheduler}
 * 
 * @author James Wong
 * @version 2023-01-11
 * @since v1.0.0
 */
@Getter
@CustomLog
public class EngineExecutionScheduler extends AbstractJobExecutor {

    public EngineExecutionScheduler() {
    }

    @Override
    public String getType() {
        return ExecutorJobType.ENGINE_EXECUTION_SCHEDULER.name();
    }

    @Override
    protected void execute(
            int currentShardingTotalCount,
            @NotNull JobConfiguration jobConfig,
            @NotNull JobFacade jobFacade,
            @NotNull ShardingContext context) throws Exception {

        // Gets schedule job parameter.
        final JobParameter jobParameter = parseJSON(jobConfig.getJobParameter(), JobParameter.class);
        notNullOf(jobParameter, "jobParameter");
        Long schedulingJobId = jobParameter.getJobId();
        notNullOf(schedulingJobId, "schedulingJobId");

        runningForSchedulingJob(schedulingJobId);

        // Find schedule trigger.
        final ScheduleTrigger trigger = scheduleTriggerService.get(jobParameter.getTriggerId());
        notNullOf(trigger, "trigger");
        CronTriggerConfig ctc = ((CronTriggerConfig) trigger.getProperties()).validate();
        notNullOf(ctc, "cronTriggerConfig");

        final String jobName = trigger.getId() + "-" + EngineExecutionJob.class.getSimpleName() + "@OneOffJob";
        SaveScheduleJob jobPrepared = null;
        try {
            jobPrepared = preparedForExecutionJob(trigger.getId(), jobName);

            // Assgin sharding request indexs.
            final List<Integer> shardingRequests = getShardings(currentShardingTotalCount, context, jobParameter, ctc);

            log.info("Schedule execution job for {} : {}", jobName, trigger);
            // Start execution job.
            ctc.setCron(null); // One-off job is without cron.
            final OneOffJobBootstrap executionBootstrap = globalScheduleJobManager.add(ExecutorJobType.ENGINE_EXECUTION_JOB,
                    jobName, ctc, new JobParameter(trigger.getId(), jobPrepared.getId(), shardingRequests), true);

            executionBootstrap.execute();

            schedForExecutionJob(jobPrepared.getId());
        } catch (Exception e) {
            log.error(format(
                    "Failed to sched executing job for currentShardingTotalCount: %s, context: %s, triggerId: %s, jobName: %s, schedulingJobId: %s, executingJobId: %s",
                    currentShardingTotalCount, context, trigger.getId(), jobName, schedulingJobId, jobPrepared.getId()), e);
            if (nonNull(jobPrepared)) {
                failedSchedForExecutingJob(jobPrepared.getId());
            }
        }
    }

    private List<Integer> getShardings(
            final int currentShardingTotalCount,
            final ShardingContext context,
            final JobParameter jobParameter,
            final CronTriggerConfig ctc) {
        // Assgin sharding request indexs.
        final int totalRequests = ctc.getRequests().size();
        final List<Integer> shardingRequests = new ArrayList<>(totalRequests);
        for (int i = 0; i < totalRequests; i++) {
            if (i % currentShardingTotalCount == context.getShardingItem()) {
                shardingRequests.add(i);
            }
        }
        return shardingRequests;
    }

    private void runningForSchedulingJob(final Long schedulingJobId) {
        final ScheduleJob jobRunning = scheduleJobService.get(schedulingJobId);
        jobRunning.setRunState(RunState.RUNNING);

        log.debug("Updating to job for : {}", jobRunning);
        final SaveScheduleJobResult resultSched = scheduleJobService.save(jobRunning);
        log.debug("Updated to job for : {} => {}", jobRunning, resultSched);
    }

    private SaveScheduleJob preparedForExecutionJob(final Long triggerId, final String jobName) {
        // Update the scheduling preparation info to the DB to ensure
        // that the execution job id is generated.
        final SaveScheduleJob jobPrepared = SaveScheduleJob.builder()
                .triggerId(triggerId)
                .jobType(JobType.EXECUTING)
                .jobName(jobName)
                .runState(RunState.PREPARED)
                .build();

        log.debug("Updating to job for : {}", jobPrepared);
        final SaveScheduleJobResult resultPrepared = scheduleJobService.save(jobPrepared);
        log.debug("Updated to job for : {} => {}", jobPrepared, resultPrepared);
        return jobPrepared;
    }

    private void schedForExecutionJob(final Long executingJobId) {
        final ScheduleJob jobSched = scheduleJobService.get(executingJobId);
        jobSched.setRunState(RunState.SCHED);
        jobSched.setSchedTime(new Date());

        log.debug("Updating to job for : {}", jobSched);
        final SaveScheduleJobResult resultSched = scheduleJobService.save(jobSched);
        log.debug("Updated to job for : {} => {}", jobSched, resultSched);
    }

    private void failedSchedForExecutingJob(final Long executingJobId) {
        ScheduleJob jobFailedSched = null;
        try {
            jobFailedSched = scheduleJobService.get(executingJobId);
            jobFailedSched.setRunState(RunState.FAILED_SCHED);
            jobFailedSched.setFinishedTime(new Date());

            log.debug("Updating to job for : {}", jobFailedSched);
            final SaveScheduleJobResult resultSched = scheduleJobService.save(jobFailedSched);
            log.debug("Updated to job for : {} => {}", jobFailedSched, resultSched);
        } catch (Exception e2) {
            log.error(format("Failed to update failed sched executing job to DB. - %s", jobFailedSched), e2);
        }
    }

}
