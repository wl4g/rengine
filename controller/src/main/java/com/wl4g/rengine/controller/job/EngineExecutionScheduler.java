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
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;

import com.google.common.collect.Iterables;
import com.wl4g.infra.common.lang.tuples.Tuple2;
import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;
import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor.CompleteResult;
import com.wl4g.rengine.client.core.RengineClient;
import com.wl4g.rengine.client.core.RengineClient.FailbackInfo;
import com.wl4g.rengine.common.entity.ScheduleJobLog;
import com.wl4g.rengine.common.entity.ScheduleJobLog.ExecutionScheduleJobLog;
import com.wl4g.rengine.common.entity.ScheduleJobLog.ResultInformation;
import com.wl4g.rengine.common.entity.ScheduleTrigger;
import com.wl4g.rengine.common.entity.ScheduleTrigger.ExecutionScheduleConfig;
import com.wl4g.rengine.common.entity.ScheduleTrigger.RunState;
import com.wl4g.rengine.common.entity.ScheduleTrigger.ScheduleType;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.ExecuteResult;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.controller.config.RengineControllerProperties.EngineClientSchedulerProperties;
import com.wl4g.rengine.controller.lifecycle.ElasticJobBootstrapBuilder.JobParameter;

import lombok.CustomLog;
import lombok.Getter;
import lombok.ToString;

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

    private SafeScheduledTaskPoolExecutor executor;

    protected SafeScheduledTaskPoolExecutor getExecutor() {
        if (isNull(executor)) {
            synchronized (this) {
                if (isNull(executor)) {
                    final EngineClientSchedulerProperties cr = getConfig().getClient();
                    this.executor = newDefaultScheduledExecutor(EngineExecutionScheduler.class.getSimpleName(),
                            cr.getConcurrency(), cr.getAcceptQueue());
                    log.info("Initialized schedule executor of concurrency: {}, acceptQueue: {}", cr.getConcurrency(),
                            cr.getAcceptQueue());
                }
            }
        }
        return executor;
    }

    @Override
    public String getType() {
        return ScheduleJobType.EXECUTION_SCHEDULER.name();
    }

    @SuppressWarnings("unchecked")
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
        final ExecutionScheduleConfig esc = ((ExecutionScheduleConfig) notNullOf(trigger.getProperties(), "cronTrigger"))
                .validate();

        try {
            log.info("Execution scheduling for : {}", trigger);
            final List<ExecuteRequest> shardingRequests = getShardings(currentShardingTotalCount, context, jobParameter, esc);

            // Build for execution jobs.
            final List<ExecutionWorker> jobs = shardingRequests.stream()
                    .map(request -> new ExecutionWorker(currentShardingTotalCount, context, trigger.getId(), jobLog.getId(),
                            getRengineClient(), request))
                    .collect(toList());

            // Submit execute requests job wait for completed
            doExecuteRequestJobs(trigger, jobLog, jobs, resultAndJobLog -> {
                final Set<ResultInformation> results = (Set<ResultInformation>) resultAndJobLog.getItem1();
                results.stream().forEach(rd -> rd.validate());
                final ScheduleJobLog _jobLog = (ScheduleJobLog) resultAndJobLog.getItem2();
                ((ExecutionScheduleJobLog) _jobLog.getDetail()).setResults(results);
            });

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

    protected ScheduleJobLog doExecuteRequestJobs(
            final ScheduleTrigger trigger,
            final ScheduleJobLog jobLog,
            final List<ExecutionWorker> jobs,
            final Consumer<Tuple2> saveJobLogPrepared) {

        final CompleteResult<ExecuteResult> result = getExecutor().submitForComplete(jobs, trigger.getMaxTimeoutMs());
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

        updateTriggerRunState(trigger.getId(), runState);

        final var mergedResultInfos = newHashSet(Iterables.concat(completedResultInfos, uncompletedResultInfos));
        log.debug("Merged result informations: {}", mergedResultInfos);

        return upsertSchedulingLog(trigger.getId(), jobLog.getId(), false, true, runState.isSuccess(),
                _jobLog -> saveJobLogPrepared.accept(new Tuple2(mergedResultInfos, _jobLog)));
    }

    @Override
    protected ScheduleJobLog newDefaultScheduleJobLog(final Long triggerId) {
        return ScheduleJobLog.builder()
                .triggerId(triggerId)
                .detail(ExecutionScheduleJobLog.builder().type(ScheduleType.EXECUTION_SCHEDULER.name()).build())
                .build();
    }

    private List<ExecuteRequest> getShardings(
            final int currentShardingTotalCount,
            final ShardingContext context,
            final JobParameter jobParameter,
            final ExecutionScheduleConfig ctc) {
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
            this.context = notNullOf(context, "context");
            this.triggerId = notNullOf(triggerId, "triggerId");
            this.jobLogId = notNullOf(jobLogId, "jobLogId");
            this.rengineClient = notNullOf(rengineClient, "rengineClient");
            this.request = notNullOf(request, "request");
        }

        @Override
        public ExecuteResult call() throws Exception {
            try {
                request.setRequestId(IdGenUtils.next());
                return (this.result = rengineClient.execute(beforeExecution(ExecuteRequest.builder()
                        .clientId(request.getClientId())
                        .clientSecret(request.getClientId())
                        .scenesCodes(request.getScenesCodes())
                        .bestEffort(request.getBestEffort())
                        .timeout(request.getTimeout())
                        .args(safeMap(request.getArgs()))
                        .build())));
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

        protected ExecuteRequest beforeExecution(final ExecuteRequest request) {
            return request;
        }
    }

}
