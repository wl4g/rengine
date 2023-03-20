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
package com.wl4g.rengine.executor.execution;

import static com.google.common.collect.Lists.newArrayList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_failure;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_success;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_total;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.BeforeDestroyed;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Iterables;
import com.wl4g.infra.common.lang.tuples.Tuple2;
import com.wl4g.infra.common.task.GenericTaskRunner;
import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;
import com.wl4g.rengine.common.entity.Scenes.ScenesWrapper;
import com.wl4g.rengine.common.entity.Workflow.WorkflowEngine;
import com.wl4g.rengine.common.entity.Workflow.WorkflowWrapper;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.common.model.WorkflowExecuteRequest;
import com.wl4g.rengine.common.model.WorkflowExecuteResult;
import com.wl4g.rengine.common.model.WorkflowExecuteResult.ResultDescription;
import com.wl4g.rengine.executor.meter.RengineExecutorMeterService;
import com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsTag;
import com.wl4g.rengine.executor.service.impl.ReactiveEngineExecutionServiceImpl;

import lombok.AllArgsConstructor;
import lombok.CustomLog;

/**
 * {@link LifecycleExecutionService}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v1.0.0
 * @see https://github.com/google/guice/wiki/Motivation
 */
@CustomLog
@Singleton
public class LifecycleExecutionService {

    @Inject
    BeanManager beanManager;

    @Inject
    EngineConfig config;

    @Inject
    RengineExecutorMeterService meterService;

    @Inject
    DefaultWorkflowExecution workflowExecution;

    SafeScheduledTaskPoolExecutor executionExecutor;

    @PostConstruct
    void init() {
        final int threads = config.executorThreadPools();
        final int acceptQueue = config.executorAcceptQueue();
        log.info("Initialzing execution executor of threads pool: {}, acceptQueue: {}", threads, acceptQueue);
        this.executionExecutor = GenericTaskRunner
                .newDefaultScheduledExecutor(ReactiveEngineExecutionServiceImpl.class.getSimpleName(), threads, acceptQueue);
    }

    void destroy(@Observes @BeforeDestroyed(ApplicationScoped.class) ServletContext init) {
        if (nonNull(executionExecutor)) {
            try {
                this.executionExecutor.shutdown();
            } catch (Exception e) {
                log.error("Failed to shutdown execution executor.", e);
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public @NotNull WorkflowExecuteResult execute(
            final @NotNull WorkflowExecuteRequest workflowExecuteRequest,
            @NotEmpty final List<ScenesWrapper> sceneses) throws Exception {
        notNullOf(workflowExecuteRequest, "workflowExecuteRequest");
        notEmptyOf(sceneses, "sceneses");

        // Monitor task timeout interrupt.
        final CountDownLatch latch = new CountDownLatch(sceneses.size());

        // Submit to execution workers.
        final Map<String, Future<ResultDescription>> futures = sceneses.stream()
                .map(scenes -> new Tuple2(scenes.getScenesCode(),
                        executionExecutor.submit(new ExecutionRunner(latch, workflowExecuteRequest, scenes))))
                .collect(toMap(kv -> (String) kv.getItem1(), kv -> (Future) kv.getItem2()));

        final Map<String, ResultDescription> completed = new HashMap<>(futures.size());
        final Set<ResultDescription> uncompleted = new HashSet<>(futures.size());

        // Calculate the effective timeout time (ms), minus the network
        // transmission time-consuming deviation.
        final long effectiveTimeoutMs = (long) ((long) workflowExecuteRequest.getTimeout()
                * (1 - config.executeTimeoutOffsetRate()));

        final Iterator<Entry<String, Future<ResultDescription>>> it = futures.entrySet().iterator();
        if (!latch.await(effectiveTimeoutMs, MILLISECONDS)) { // Timeout(part-done)
            while (it.hasNext()) {
                final Entry<String, Future<ResultDescription>> entry = it.next();
                final Future<ResultDescription> future = entry.getValue();
                // Notice: future.isCancelled() a true when the submitted
                // task is rejected, see: #EMPTY_FUTURE_CANCELLED
                if (future.isDone()) {
                    // Collect for completed results.
                    completed.putIfAbsent(entry.getKey(), future.get());
                } else {
                    // Collect for uncompleted results.
                    uncompleted.add(ResultDescription.builder()
                            .scenesCode(entry.getKey())
                            .success(false)
                            .valueMap(emptyMap())
                            .reason(format("Execution time exceeded in %sms", effectiveTimeoutMs))
                            .build());
                }
            }
        } else { // All done
            while (it.hasNext()) {
                // Collect for completed results.
                final Entry<String, Future<ResultDescription>> entry = it.next();
                completed.putIfAbsent(entry.getKey(), entry.getValue().get());
            }
        }

        final List<String> scenesCodes = sceneses.stream().map(s -> s.getScenesCode()).collect(toList());
        if (uncompleted.size() > 0) {
            log.debug("The parts success executed workflow graph tasks are: {}. requestId: {}, clientId: {}, scenesCodes: {}",
                    uncompleted, scenesCodes);
        } else {
            log.debug("Executed workflow graph all tasks successfully. requestId: {}, clientId: {}, scenesCodes: {}", uncompleted,
                    scenesCodes);
        }

        final List<ResultDescription> allResults = newArrayList(Iterables.concat(completed.values(), uncompleted));
        final long totalSuccess = safeList(allResults).stream().map(rd -> rd.validate()).filter(rd -> rd.getSuccess()).count();
        return WorkflowExecuteResult.builder()
                .requestId(workflowExecuteRequest.getRequestId())
                .results(allResults)
                .description(totalSuccess == sceneses.size() ? "All executed successfully" : "There are failed executions")
                .build();
    }

    public WorkflowExecution getExecution(WorkflowEngine engine) {
        switch (engine) {
        default:
            return notNull(getBean(DefaultWorkflowExecution.class), "Could't obtain workflow execution of engine : %s", engine);
        }
    }

    @SuppressWarnings("unchecked")
    <T> T getBean(Class<T> subtype, Annotation... qualifiers) {
        // Set<Bean<?>> beans = beanManager.getBeans(beanType, qualifiers);
        // if (!beans.isEmpty()) {
        // return (T) beans.iterator().next();
        // }

        // 必须有其他地方注入引用，这里才能获取，否则报错 UnsatisfiedResolution
        // T bean = CDI.current().select(subtype).get();
        // if (nonNull(bean)) {
        // return bean;
        // }
        // throw new IllegalStateException(format("Could not obtain bean by '%s,
        // %s'", subtype, qualifiers));

        return (T) workflowExecution;
    }

    @AllArgsConstructor
    class ExecutionRunner implements Callable<ResultDescription> {
        final @NotNull CountDownLatch latch;
        final @NotNull WorkflowExecuteRequest workflowExecuteRequest;
        final @NotNull ScenesWrapper scenes;

        @Override
        public ResultDescription call() {
            final String scenesCode = scenes.getScenesCode();

            // TODO multi workflows execution supports? Temporarily only need to
            // support scenes to workflow as one-to-one.
            final WorkflowWrapper workflow = scenes.getEffectivePriorityWorkflow();

            final WorkflowEngine engine = workflow.getEngine();
            notNull(engine, "Please check if the configuration is correct, workflow engine is null.");

            try {
                // Buried-point: total workflowExecuteRequest.
                meterService.counter(execution_total.getName(), execution_total.getHelp(), MetricsTag.ENGINE, engine.name())
                        .increment();

                final WorkflowExecution execution = notNull(getExecution(engine),
                        "Could not load execution rule engine via %s of '%s'", engine.name(),
                        workflowExecuteRequest.getClientId());

                final ResultDescription result = execution.execute(workflowExecuteRequest, workflow, true);

                // Buried-point: success workflowExecuteRequest.
                meterService.counter(execution_success.getName(), execution_success.getHelp(), MetricsTag.ENGINE, engine.name())
                        .increment();

                result.setScenesCode(scenesCode);
                return result;
            } catch (Throwable e) {
                final String errmsg = format(
                        "Failed to execution %s engine of requestId: '%s', clientId: '%s', workflowId: '%s', scenesCode: '%s'. reason: %s",
                        engine.name(), workflowExecuteRequest.getRequestId(), workflowExecuteRequest.getClientId(),
                        workflow.getId(), scenesCode, getRootCauseMessage(e));
                log.error(errmsg, e);

                // Buried-point: failed workflowExecuteRequest.
                meterService.counter(execution_failure.getName(), execution_failure.getHelp(), MetricsTag.ENGINE, engine.name())
                        .increment();

                throw new RengineException(errmsg, e);
            } finally {
                latch.countDown();
            }
        }
    }

}
