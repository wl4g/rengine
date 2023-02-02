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
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_failure;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_success;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_total;
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
import com.wl4g.rengine.common.entity.Rule.RuleEngine;
import com.wl4g.rengine.common.entity.Scenes.ScenesWrapper;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.ExecuteResult;
import com.wl4g.rengine.common.model.ExecuteResult.ResultDescription;
import com.wl4g.rengine.executor.metrics.ExecutorMeterService;
import com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsTag;
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
    ExecutionConfig config;

    @Inject
    ExecutorMeterService meterService;

    @Inject
    DefaultWorkflowExecution workflowExecution;

    SafeScheduledTaskPoolExecutor executionExecutor;

    @PostConstruct
    void init() {
        final int threads = config.engine().executorThreadPools();
        final int acceptQueue = config.engine().executorAcceptQueue();
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
    public @NotNull ExecuteResult execute(
            final @NotNull ExecuteRequest executeRequest,
            @NotEmpty final List<ScenesWrapper> sceneses) throws Exception {
        notNullOf(executeRequest, "executeRequest");
        notEmptyOf(sceneses, "sceneses");

        // Monitor task timeout interrupt.
        final CountDownLatch latch = new CountDownLatch(sceneses.size());

        // Submit to execution workers.
        final Map<String, Future<ResultDescription>> futures = sceneses.stream()
                .map(scenes -> new Tuple2(scenes.getScenesCode(),
                        executionExecutor.submit(new ExecutionRunner(latch, executeRequest, scenes))))
                .collect(toMap(kv -> (String) kv.getItem1(), kv -> (Future) kv.getItem2()));

        // Completed results.
        final Map<String, ResultDescription> completed = new HashMap<>(futures.size());
        // Uncompleted results.
        final Set<ResultDescription> uncompleted = new HashSet<>(futures.size());

        // Calculate the effective timeout time (ms), minus the network
        // transmission time-consuming deviation.
        final long effectiveTimeoutMs = (long) ((long) executeRequest.getTimeout()
                * (1 - config.engine().executeTimeoutOffsetRate()));

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
                } else { // Collect for uncompleted results.
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
        return ExecuteResult.builder()
                .requestId(executeRequest.getRequestId())
                .results(allResults)
                .description(totalSuccess == sceneses.size() ? "All executed successfully" : "There are failed executions")
                .build();
    }

    public WorkflowExecution getExecution(RuleEngine engine) {
        switch (engine) {
        default:
            return getBean(DefaultWorkflowExecution.class);
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
        final @NotNull ExecuteRequest executeRequest;
        final @NotNull ScenesWrapper scenes;

        @Override
        public ResultDescription call() {
            final RuleEngine engine = scenes.getEffectivePriorityWorkflow().getEngine();
            notNull(engine, "Please check if the configuration is correct, rule engine type of workflow is null.");

            try {
                // Buried-point: total executeRequest.
                meterService.counter(execution_total.getName(), execution_total.getHelp(), MetricsTag.ENGINE, engine.name())
                        .increment();

                final WorkflowExecution execution = getExecution(engine);
                notNull(execution, "Could not load execution rule engine via %s of '%s'", engine.name(),
                        executeRequest.getClientId());
                final ResultDescription result = execution.execute(executeRequest, scenes);

                // Buried-point: success executeRequest.
                meterService.counter(execution_success.getName(), execution_success.getHelp(), MetricsTag.ENGINE, engine.name())
                        .increment();

                return result;
            } catch (Throwable e) {
                final String errmsg = format(
                        "Failed to execution %s engine of requestId: '%s', clientId: '%s', scenesCode: '%s'. reason: %s",
                        engine.name(), executeRequest.getRequestId(), executeRequest.getClientId(), scenes.getScenesCode(),
                        getRootCauseMessage(e));
                log.error(errmsg, e);

                // Buried-point: failed executeRequest.
                meterService.counter(execution_failure.getName(), execution_failure.getHelp(), MetricsTag.ENGINE, engine.name())
                        .increment();

                throw new RengineException(errmsg, e);
            } finally {
                latch.countDown();
            }
        }
    }

    public static final long DEFAULT_EXECUTION_AWAIT = 20L;

}
