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
package com.wl4g.rengine.controller.job;

import static com.google.common.collect.Sets.newHashSet;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.task.GenericTaskRunner.newDefaultScheduledExecutor;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import com.wl4g.rengine.common.entity.ScheduleJobLog;
import com.wl4g.rengine.common.entity.ScheduleJobLog.ClientScheduleJobLog;
import com.wl4g.rengine.common.entity.ScheduleJobLog.ClientScheduleJobLog.ResultInformation;
import com.wl4g.rengine.common.entity.ScheduleTrigger;
import com.wl4g.rengine.common.entity.ScheduleTrigger.ClientScheduleConfig;
import com.wl4g.rengine.common.entity.ScheduleTrigger.RunState;
import com.wl4g.rengine.common.entity.ScheduleTrigger.ScheduleType;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.ExecuteResult;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.controller.config.RengineControllerProperties.EngineClientSchedulerProperties;
import com.wl4g.rengine.controller.lifecycle.ElasticJobBootstrapBuilder.JobParameter;
import com.wl4g.rengine.service.model.SaveScheduleJobLogResult;

import lombok.CustomLog;
import lombok.Getter;
import lombok.ToString;

/**
 * {@link EngineClientScheduler}
 * 
 * @author James Wong
 * @version 2023-01-11
 * @since v1.0.0
 */
@Getter
@CustomLog
public class EngineClientScheduler extends AbstractJobExecutor {

    private SafeScheduledTaskPoolExecutor executor;

    protected SafeScheduledTaskPoolExecutor getExecutor() {
        if (isNull(executor)) {
            synchronized (this) {
                if (isNull(executor)) {
                    final EngineClientSchedulerProperties cr = getConfig().getClient();
                    this.executor = newDefaultScheduledExecutor(EngineClientScheduler.class.getSimpleName(), cr.getConcurrency(),
                            cr.getAcceptQueue());
                    log.info("Initialized schedule executor of concurrency: {}, acceptQueue: {}", cr.getConcurrency(),
                            cr.getAcceptQueue());
                }
            }
        }
        return executor;
    }

    @Override
    public String getType() {
        return SchedulerJobType.CLIENT_SCHEDULER.name();
    }

    @Override
    protected void execute(
            int currentShardingTotalCount,
            @NotNull JobConfiguration jobConfig,
            @NotNull JobFacade jobFacade,
            @NotNull ShardingContext context) throws Exception {

        final JobParameter jobParameter = notNullOf(parseJSON(jobConfig.getJobParameter(), JobParameter.class), "jobParameter");
        final Long triggerId = notNullOf(jobParameter.getTriggerId(), "triggerId");

        updateTriggerRunState(triggerId, RunState.RUNNING);
        final ScheduleJobLog jobLog = upsertSchedulingLog(triggerId, null, true, false, null, null);

        final ScheduleTrigger trigger = notNullOf(getScheduleTriggerService().get(triggerId), "trigger");
        final ClientScheduleConfig csc = ((ClientScheduleConfig) notNullOf(trigger.getProperties(), "cronTrigger")).validate();

        try {
            log.info("Execution job for : {}", trigger);
            final List<ExecuteRequest> shardingRequests = getShardings(currentShardingTotalCount, context, jobParameter, csc);

            // Execution jobs with await all complete.
            final List<ExecutionWorker> jobs = shardingRequests.stream()
                    .map(request -> new ExecutionWorker(currentShardingTotalCount, context, trigger.getId(), jobLog.getId(),
                            getRengineClient(), request))
                    .collect(toList());

            final var result = getExecutor().submitForComplete(jobs, trigger.getMaxTimeoutMs());
            log.info("Completed for result: {}", result);

            final var completed = safeList(result.getCompleted()).stream().collect(toList());
            final var uncompleted = safeList(result.getUncompleted()).stream().collect(toList());

            final boolean isAllSuccess = safeList(result.getUncompleted()).size() == 0
                    && completed.stream().filter(rd -> nonNull(rd)).allMatch(er -> er.errorCount() == 0);

            RunState runState = RunState.FAILED;
            if (isAllSuccess) {
                runState = RunState.SUCCESS;
            } else {
                final boolean partSuccess = completed.stream().filter(rd -> nonNull(rd)).anyMatch(er -> er.errorCount() == 0);
                runState = partSuccess ? RunState.PART_SUCCESS : RunState.FAILED;
            }

            final var completedResultInfos = completed.stream()
                    .filter(er -> nonNull(er))
                    .map(er -> new ResultInformation(er.getRequestId(), er.getResults()))
                    .collect(toList());

            final var uncompletedResultInfos = uncompleted.stream()
                    .map(ew -> new ResultInformation(((ExecutionWorker) ew).getRequest().getRequestId(),
                            ((ExecutionWorker) ew).getResult().getResults()))
                    .collect(toList());

            updateTriggerRunState(triggerId, runState);
            upsertSchedulingLog(triggerId, jobLog.getId(), false, true, runState.isSuccess(),
                    newHashSet(Iterables.concat(completedResultInfos, uncompletedResultInfos)));
        } catch (Throwable ex) {
            final String errmsg = format(
                    "Failed to executing requests job of currentShardingTotalCount: %s, context: %s, triggerId: %s, jobLogId: %s",
                    currentShardingTotalCount, context, trigger.getId(), jobLog.getId());
            if (log.isDebugEnabled()) {
                log.error(errmsg, ex);
            } else {
                log.error(format("%s. - reason: %s", errmsg, ex.getMessage()));
            }

            updateTriggerRunState(triggerId, RunState.FAILED);
            upsertSchedulingLog(triggerId, jobLog.getId(), false, true, false, null);
        }
    }

    private List<ExecuteRequest> getShardings(
            final int currentShardingTotalCount,
            final ShardingContext context,
            final JobParameter jobParameter,
            final ClientScheduleConfig ctc) {
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
    @ToString(exclude = { "rengineClient", "request", "result" })
    public static class ExecutionWorker implements Callable<ExecuteResult> {
        private final int currentShardingTotalCount;
        private final ShardingContext context;
        private final Long triggerId;
        private final Long jobLogId;
        private final RengineClient rengineClient;
        private final ExecuteRequest request;
        private ExecuteResult result;

        public ExecutionWorker(int currentShardingTotalCount, ShardingContext context, Long triggerId, Long jobLogId,
                RengineClient rengineClient, ExecuteRequest request) {
            this.currentShardingTotalCount = currentShardingTotalCount;
            this.context = context;
            this.triggerId = triggerId;
            this.jobLogId = jobLogId;
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
                        "Failed to executing request job of currentShardingTotalCount: %s, context: %s, triggerId: %s, jobLogId: %s, request: %s",
                        currentShardingTotalCount, context, triggerId, jobLogId, request);
                if (log.isDebugEnabled()) {
                    log.error(errmsg, ex);
                } else {
                    log.error(format("%s. - reason: %s", errmsg, ex.getMessage()));
                }

                return (this.result = RengineClient.DEFAULT_FAILBACK.apply(new FailbackInfo(request, ex)));
            }
        }
    }

    private ScheduleJobLog upsertSchedulingLog(
            final @NotNull Long triggerId,
            final Long jobLogId,
            final boolean updateStatupTime,
            final boolean updateFinishedTime,
            final Boolean success,
            final Collection<ResultInformation> results) {
        notNullOf(triggerId, "triggerId");
        ScheduleJobLog jogLog = null;
        SaveScheduleJobLogResult result = null;
        try {
            if (nonNull(jobLogId)) {
                jogLog = getScheduleJobLogService().get(jobLogId);
            } else {
                jogLog = ScheduleJobLog.builder()
                        .triggerId(triggerId)
                        .detail(ClientScheduleJobLog.builder().type(ScheduleType.CLIENT_SCHEDULER.name()).build())
                        .build();
                log.debug("Upserting to scheduling job info : {}", jogLog);
                result = getScheduleJobLogService().save(jogLog);
                jogLog.setId(result.getId());
            }
            if (updateStatupTime) {
                jogLog.setStartupTime(new Date());
            }
            if (updateFinishedTime) {
                jogLog.setFinishedTime(new Date());
            }
            if (nonNull(success)) {
                jogLog.setSuccess(success);
            }
            if (nonNull(results)) {
                results.stream().forEach(rd -> rd.validate());
                ((ClientScheduleJobLog) jogLog.getDetail()).setResults(results);
            }

            log.debug("Upserting to scheduling job log : {}", jogLog);
            result = getScheduleJobLogService().save(jogLog);
            log.debug("Upserted to scheduling job log : {} => {}", jogLog, result);

        } catch (Exception e2) {
            log.error(format("Failed to upsert scheduling job log to DB. - %s", jogLog), e2);
        }
        return jogLog;
    }

}
