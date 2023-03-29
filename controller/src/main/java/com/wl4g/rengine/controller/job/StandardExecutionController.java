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

import java.io.IOException;
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
import com.wl4g.rengine.common.entity.ControllerLog;
import com.wl4g.rengine.common.entity.ControllerLog.ExecutionControllerLog;
import com.wl4g.rengine.common.entity.ControllerLog.ResultInformation;
import com.wl4g.rengine.common.entity.Controller;
import com.wl4g.rengine.common.entity.Controller.StandardExecutionConfig;
import com.wl4g.rengine.common.entity.Controller.RunState;
import com.wl4g.rengine.common.entity.Controller.ControllerType;
import com.wl4g.rengine.common.model.WorkflowExecuteRequest;
import com.wl4g.rengine.common.model.WorkflowExecuteResult;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.controller.config.RengineControllerProperties.EngineClientSchedulerProperties;
import com.wl4g.rengine.controller.lifecycle.ElasticJobBootstrapBuilder.JobParameter;

import lombok.CustomLog;
import lombok.Getter;
import lombok.ToString;

/**
 * {@link StandardExecutionController}
 * 
 * @author James Wong
 * @version 2023-01-11
 * @since v1.0.0
 */
@Getter
@CustomLog
public class StandardExecutionController extends AbstractJobExecutor {

    private SafeScheduledTaskPoolExecutor executor;

    @Override
    public String getType() {
        return ControllerType.STANDARD_EXECUTION.name();
    }

    protected SafeScheduledTaskPoolExecutor getExecutor() {
        if (isNull(executor)) {
            synchronized (this) {
                if (isNull(executor)) {
                    final EngineClientSchedulerProperties cr = getConfig().getClient();
                    this.executor = newDefaultScheduledExecutor(StandardExecutionController.class.getSimpleName(),
                            cr.getConcurrency(), cr.getAcceptQueue());
                    log.info("Initialized schedule executor of concurrency: {}, acceptQueue: {}", cr.getConcurrency(),
                            cr.getAcceptQueue());
                }
            }
        }
        return executor;
    }

    @Override
    public void close() throws IOException {
        getExecutor().shutdownNow();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void execute(
            int currentShardingTotalCount,
            @NotNull JobConfiguration jobConfig,
            @NotNull JobFacade jobFacade,
            @NotNull ShardingContext context) throws Exception {

        final JobParameter jobParameter = notNullOf(parseJSON(jobConfig.getJobParameter(), JobParameter.class), "jobParameter");
        final Long controllerId = notNullOf(jobParameter.getControllerId(), "controllerScheduleId");

        updateControllerRunState(controllerId, RunState.RUNNING);
        final ControllerLog controllerLog = upsertControllerLog(controllerId, null, true, false, null, null);

        final Controller schedule = notNullOf(getControllerScheduleService().get(controllerId), "controller");
        final StandardExecutionConfig gesc = ((StandardExecutionConfig) notNullOf(schedule.getDetails(),
                "genericExecutionScheduleConfig")).validate();

        try {
            log.info("Execution scheduling for : {}", schedule);
            final List<WorkflowExecuteRequest> shardingRequests = getShardings(currentShardingTotalCount, context, jobParameter,
                    gesc);

            // Build for execution jobs.
            final List<ExecutionWorker> jobs = shardingRequests.stream()
                    .map(request -> new ExecutionWorker(currentShardingTotalCount, context, schedule.getId(),
                            controllerLog.getId(), getRengineClient(), request))
                    .collect(toList());

            // Submit execute requests job wait for completed
            doExecuteRequestJobs(schedule, controllerLog, jobs, resultAndJobLog -> {
                final Set<ResultInformation> results = (Set<ResultInformation>) resultAndJobLog.getItem1();
                results.stream().forEach(rd -> rd.validate());
                final ControllerLog _jobLog = (ControllerLog) resultAndJobLog.getItem2();
                ((ExecutionControllerLog) _jobLog.getDetails()).setResults(results);
            });

        } catch (Throwable ex) {
            final String errmsg = format(
                    "Failed to executing requests job of currentShardingTotalCount: %s, context: %s, controllerId: %s, controllerLogId: %s",
                    currentShardingTotalCount, context, schedule.getId(), controllerLog.getId());
            if (log.isDebugEnabled()) {
                log.error(errmsg, ex);
            } else {
                log.error(format("%s. - reason: %s", errmsg, ex.getMessage()));
            }

            updateControllerRunState(controllerId, RunState.FAILED);
            upsertControllerLog(controllerId, controllerLog.getId(), false, true, false, null);
        }
    }

    protected ControllerLog doExecuteRequestJobs(
            final Controller controller,
            final ControllerLog controllerLog,
            final List<ExecutionWorker> jobs,
            final Consumer<Tuple2> saveJobLogPrepared) {

        final CompleteResult<WorkflowExecuteResult> result = getExecutor().submitForComplete(jobs,
                controller.getMaxTimeoutMs());
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

        updateControllerRunState(controller.getId(), runState);

        final var mergedResultInfos = newHashSet(Iterables.concat(completedResultInfos, uncompletedResultInfos));
        log.debug("Merged result informations: {}", mergedResultInfos);

        return upsertControllerLog(controller.getId(), controllerLog.getId(), false, true, runState.isSuccess(),
                _jobLog -> saveJobLogPrepared.accept(new Tuple2(mergedResultInfos, _jobLog)));
    }

    @Override
    protected ControllerLog newDefaultControllerLog(final Long controllerId) {
        return ControllerLog.builder()
                .controllerId(controllerId)
                .details(ExecutionControllerLog.builder().type(ControllerType.STANDARD_EXECUTION.name()).build())
                .build();
    }

    private List<WorkflowExecuteRequest> getShardings(
            final int currentShardingTotalCount,
            final ShardingContext context,
            final JobParameter jobParameter,
            final StandardExecutionConfig ctc) {
        // Assgin current sharding requests.
        final List<WorkflowExecuteRequest> totalRequests = safeList(ctc.getRequests());
        final List<WorkflowExecuteRequest> shardingRequests = new ArrayList<>(totalRequests.size());
        for (int i = 0; i < totalRequests.size(); i++) {
            if (i % currentShardingTotalCount == context.getShardingItem()) {
                shardingRequests.add(totalRequests.get(i));
            }
        }
        return shardingRequests;
    }

    @Getter
    @ToString(exclude = { "rengineClient", "request", "result" })
    public static class ExecutionWorker implements Callable<WorkflowExecuteResult> {
        private final int currentShardingTotalCount;
        private final ShardingContext context;
        private final Long controllerId;
        private final Long controllerLogId;
        private final RengineClient rengineClient;
        private final WorkflowExecuteRequest request;
        private WorkflowExecuteResult result;

        public ExecutionWorker(int currentShardingTotalCount, ShardingContext context, Long controllerId, Long controllerLogId,
                RengineClient rengineClient, WorkflowExecuteRequest request) {
            this.currentShardingTotalCount = currentShardingTotalCount;
            this.context = notNullOf(context, "context");
            this.controllerId = notNullOf(controllerId, "controllerId");
            this.controllerLogId = notNullOf(controllerLogId, "controllerLogId");
            this.rengineClient = notNullOf(rengineClient, "rengineClient");
            this.request = notNullOf(request, "request");
        }

        @Override
        public WorkflowExecuteResult call() throws Exception {
            try {
                request.setRequestId(IdGenUtils.next());
                return (this.result = rengineClient.execute(beforeExecution(WorkflowExecuteRequest.builder()
                        .clientId(request.getClientId())
                        .clientSecret(request.getClientId())
                        .scenesCodes(request.getScenesCodes())
                        .bestEffort(request.getBestEffort())
                        .timeout(request.getTimeout())
                        .args(safeMap(request.getArgs()))
                        .build())));
            } catch (Throwable ex) {
                final String errmsg = format(
                        "Failed to executing request job of currentShardingTotalCount: %s, context: %s, controllerId: %s, controllerLogId: %s, request: %s",
                        currentShardingTotalCount, context, controllerId, controllerLogId, request);
                if (log.isDebugEnabled()) {
                    log.error(errmsg, ex);
                } else {
                    log.error(format("%s. - reason: %s", errmsg, ex.getMessage()));
                }

                return (this.result = RengineClient.DEFAULT_FAILBACK.apply(new FailbackInfo(request, ex)));
            }
        }

        protected WorkflowExecuteRequest beforeExecution(final WorkflowExecuteRequest request) {
            return request;
        }
    }

}
