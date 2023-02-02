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

import static com.google.common.collect.Sets.newHashSet;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.task.GenericTaskRunner.newDefaultScheduledExecutor;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;

import com.google.common.collect.Iterables;
import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;
import com.wl4g.rengine.client.core.RengineClient;
import com.wl4g.rengine.client.core.RengineClient.FailbackInfo;
import com.wl4g.rengine.common.entity.ScheduleJob.ResultInformation;
import com.wl4g.rengine.common.entity.ScheduleJob.RunState;
import com.wl4g.rengine.common.entity.ScheduleTrigger;
import com.wl4g.rengine.common.entity.ScheduleTrigger.CronTriggerConfig;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.ExecuteResult;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.scheduler.config.RengineSchedulerProperties.EngineScheduleExecutorProperties;
import com.wl4g.rengine.scheduler.lifecycle.ElasticJobBootstrapBuilder.JobParameter;

import lombok.CustomLog;
import lombok.Getter;
import lombok.ToString;

/**
 * {@link EngineScheduleExecutor}
 * 
 * @author James Wong
 * @version 2023-01-11
 * @since v1.0.0
 */
@Getter
@CustomLog
public class EngineScheduleExecutor extends AbstractJobExecutor {

    private SafeScheduledTaskPoolExecutor executor;

    protected SafeScheduledTaskPoolExecutor getExecutor() {
        if (isNull(executor)) {
            synchronized (this) {
                if (isNull(executor)) {
                    final EngineScheduleExecutorProperties c = getConfig().getExecutor();
                    this.executor = newDefaultScheduledExecutor(EngineScheduleExecutor.class.getSimpleName(), c.getConcurrency(),
                            c.getAcceptQueue());
                }
            }
        }
        return executor;
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

        final JobParameter jobParameter = notNullOf(parseJSON(jobConfig.getJobParameter(), JobParameter.class), "jobParameter");
        final Long jobId = notNullOf(jobParameter.getJobId(), "jobId");

        updateSchedulingJobInfo(jobId, RunState.RUNNING, false, false, null);

        final ScheduleTrigger trigger = notNullOf(getScheduleTriggerService().get(jobParameter.getTriggerId()), "trigger");
        final CronTriggerConfig ctc = ((CronTriggerConfig) notNullOf(trigger.getProperties(), "cronTrigger")).validate();

        try {
            log.info("Execution job for : {}", trigger);
            final List<ExecuteRequest> shardingRequests = getShardings(currentShardingTotalCount, context, jobParameter, ctc);

            // Execution jobs with await all complete.
            final List<Callable<ExecuteResult>> jobs = shardingRequests.stream()
                    .map(request -> new ExecutionWorker(currentShardingTotalCount, context, trigger.getId(), jobId,
                            getRengineClient(), request))
                    .collect(toList());
            final var result = getExecutor().submitForComplete(jobs, ctc.getMaxTimeoutMs());

            final boolean isAllSuccess = result.getUncompleted().size() == 0
                    && result.getCompleted().stream().allMatch(er -> er.errorCount() == 0);
            RunState runState = RunState.FAILED;
            if (isAllSuccess) {
                runState = RunState.SUCCESS;
            } else {
                final boolean partSuccess = result.getCompleted().stream().anyMatch(er -> er.errorCount() == 0);
                runState = partSuccess ? RunState.PART_SUCCESS : RunState.FAILED;
            }

            final var completedResultInfos = safeList(result.getCompleted()).stream()
                    .map(er -> new ResultInformation(er.getRequestId(), er.getResults()))
                    .collect(toList());

            final var uncompletedResultInfos = safeList(result.getUncompleted()).stream()
                    .map(ew -> new ResultInformation(((ExecutionWorker) ew).getRequest().getRequestId(),
                            ((ExecutionWorker) ew).getResult().getResults()))
                    .collect(toList());

            updateSchedulingJobInfo(jobId, runState, false, true,
                    newHashSet(Iterables.concat(completedResultInfos, uncompletedResultInfos)));
        } catch (Exception e) {
            log.error(format(
                    "Failed to executing job of currentShardingTotalCount: %s, context: %s, triggerId: %s, schedulingJobId: %s, executingJobId: %s",
                    currentShardingTotalCount, context, trigger.getId(), jobId, jobId), e);
            updateSchedulingJobInfo(jobId, RunState.FAILED, false, true, null);
        }
    }

    private List<ExecuteRequest> getShardings(
            final int currentShardingTotalCount,
            final ShardingContext context,
            final JobParameter jobParameter,
            final CronTriggerConfig ctc) {
        // Assgin current sharding requests.
        final List<ExecuteRequest> totalRequests = safeList(ctc.getRequests());
        final List<ExecuteRequest> shardingRequests = new ArrayList<>(totalRequests.size());
        for (int i = 0; i < totalRequests.size(); i++) {
            if (i % currentShardingTotalCount == context.getShardingItem()) {
                shardingRequests.add(totalRequests.get(i));
            }
        }
        return shardingRequests;
    }

    @Getter
    @ToString
    public static class ExecutionWorker implements Callable<ExecuteResult> {
        private final int currentShardingTotalCount;
        private final ShardingContext context;
        private final Long triggerId;
        private final Long jobId;
        private final RengineClient rengineClient;
        private final ExecuteRequest request;
        private ExecuteResult result;

        public ExecutionWorker(int currentShardingTotalCount, ShardingContext context, Long triggerId, Long jobId,
                RengineClient rengineClient, ExecuteRequest request) {
            this.currentShardingTotalCount = currentShardingTotalCount;
            this.context = context;
            this.triggerId = triggerId;
            this.jobId = jobId;
            this.rengineClient = rengineClient;
            this.request = request;
        }

        @Override
        public ExecuteResult call() throws Exception {
            try {
                request.setRequestId(IdGenUtils.next());
                return (this.result = rengineClient.execute(ExecuteRequest.builder()
                        .clientId(request.getClientId())
                        .clientSecret(request.getClientId())
                        .scenesCodes(request.getScenesCodes())
                        .bestEffort(request.getBestEffort())
                        .timeout(request.getTimeout())
                        .args(safeMap(request.getArgs()))
                        .build()));
            } catch (Throwable ex) {
                final String errmsg = format(
                        "Failed to executing job of currentShardingTotalCount: %s, context: %s, triggerId: %s, jobId: %s",
                        currentShardingTotalCount, context, triggerId, jobId);
                log.error(errmsg, ex);
                return (this.result = RengineClient.DEFAULT_FAILBACK.apply(new FailbackInfo(request, ex)));
            }
        }
    }

}
