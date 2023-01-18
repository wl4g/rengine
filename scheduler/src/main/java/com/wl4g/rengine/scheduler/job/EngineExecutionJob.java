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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;

import com.wl4g.rengine.common.entity.ScheduleJob;
import com.wl4g.rengine.common.entity.ScheduleJob.RunState;
import com.wl4g.rengine.common.entity.ScheduleTrigger;
import com.wl4g.rengine.common.entity.ScheduleTrigger.CronTriggerConfig;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.ExecuteResult;
import com.wl4g.rengine.scheduler.lifecycle.ElasticJobBootstrapBuilder.JobParameter;
import com.wl4g.rengine.service.model.SaveScheduleJobResult;

import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link EngineExecutionJob}
 * 
 * @author James Wong
 * @version 2023-01-11
 * @since v1.0.0
 */
@Getter
@CustomLog
public class EngineExecutionJob extends AbstractJobExecutor {

    public EngineExecutionJob() {
    }

    @Override
    public String getType() {
        return ExecutorJobType.ENGINE_EXECUTION_JOB.name();
    }

    @Override
    protected void execute(
            int currentShardingTotalCount,
            @NotNull JobConfiguration jobConfig,
            @NotNull JobFacade jobFacade,
            @NotNull ShardingContext context) throws Exception {

        // Obtain job parameter.
        final JobParameter jobParameter = parseJSON(jobConfig.getJobParameter(), JobParameter.class);
        notNullOf(jobParameter, "jobParameter");
        Long execuingJobId = jobParameter.getJobId();
        notNullOf(execuingJobId, "execuingJobId");

        runningForExecutingJob(execuingJobId);

        // Find schedule trigger request.
        final ScheduleTrigger trigger = scheduleTriggerService.get(jobParameter.getTriggerId());
        notNullOf(trigger, "trigger");
        final CronTriggerConfig ctc = (CronTriggerConfig) trigger.getProperties();
        notNullOf(ctc, "cronTriggerConfig");

        // TODO use multi threads?
        getShardings(currentShardingTotalCount, context, jobParameter, ctc).parallelStream().forEach(request -> {
            try {
                final ExecuteResult result = rengineClient.execute(ExecuteRequest.builder()
                        .clientId(request.getClientId())
                        .clientSecret(request.getClientId())
                        .scenesCodes(request.getScenesCodes())
                        .bestEffort(request.getBestEffort())
                        .timeout(request.getTimeout())
                        .args(safeMap(request.getArgs()))
                        .build(), ex -> null); // TODO by bestEffort?

                successForExecutingJob(execuingJobId, result);
            } catch (Exception e) {
                log.error(format(
                        "Failed to sched executing job for currentShardingTotalCount: %s, context: %s, triggerId: %s, execuingJobId: %s",
                        currentShardingTotalCount, context, trigger.getId(), execuingJobId), e);
                failedForExecutingJob(execuingJobId);
            }
        });
    }

    private List<ExecuteRequest> getShardings(
            final int currentShardingTotalCount,
            final ShardingContext context,
            final JobParameter jobParameter,
            final CronTriggerConfig ctc) {
        final List<ExecuteRequest> totalRequests = safeList(ctc.getRequests());
        final List<ExecuteRequest> shardingRequests = new ArrayList<>(totalRequests.size());
        for (int i = 0; i < totalRequests.size(); i++) {
            if (safeList(jobParameter.getShardingRequests()).contains(i)) {
                shardingRequests.add(totalRequests.get(i));
            }
        }
        return shardingRequests;
    }

    private void runningForExecutingJob(final Long execuingJobId) {
        final ScheduleJob jobRunning = scheduleJobService.get(execuingJobId);
        jobRunning.setRunState(RunState.RUNNING);

        log.debug("Updating to job for : {}", jobRunning);
        final SaveScheduleJobResult resultSched = scheduleJobService.save(jobRunning);
        log.debug("Updated to job for : {} => {}", jobRunning, resultSched);
    }

    private void successForExecutingJob(final Long execuingJobId, final ExecuteResult result) {
        ScheduleJob jobSuccess = null;
        try {
            jobSuccess = scheduleJobService.get(execuingJobId);
            jobSuccess.setRunState(RunState.SUCCESS); // TODO is all
                                                      // success or part
                                                      // success?
            jobSuccess.setFinishedTime(new Date());
            // jobExecutionSuccess.setRequestId(result.getRequestId());
            jobSuccess.setResults(result.getResults());

            log.debug("Updating to job for : {}", jobSuccess);
            final SaveScheduleJobResult resultSched = scheduleJobService.save(jobSuccess);
            log.debug("Updated to job for : {} => {}", jobSuccess, resultSched);
        } catch (Exception e2) {
            log.error(format("Failed to update success executing job to DB. job: {}", jobSuccess), e2);
        }
    }

    private void failedForExecutingJob(final Long execuingJobId) {
        ScheduleJob jobFailed = null;
        try {
            jobFailed = scheduleJobService.get(execuingJobId);
            jobFailed.setRunState(RunState.FAILED);
            jobFailed.setFinishedTime(new Date());

            log.debug("Updating to job for : {}", jobFailed);
            final SaveScheduleJobResult resultSched = scheduleJobService.save(jobFailed);
            log.debug("Updated to job for : {} => {}", jobFailed, resultSched);
        } catch (Exception e2) {
            log.error(format("Failed to update failed executing job to DB. job: {}", jobFailed), e2);
        }
    }

}
