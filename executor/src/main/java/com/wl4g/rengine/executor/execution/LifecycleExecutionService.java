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
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.evaluation_failure;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.evaluation_success;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.evaluation_total;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.common.collect.Iterables;
import com.wl4g.infra.common.bean.KeyValue;
import com.wl4g.infra.common.runtime.JvmRuntimeTool;
import com.wl4g.infra.common.task.GenericTaskRunner;
import com.wl4g.infra.common.task.RunnerProperties;
import com.wl4g.infra.common.task.RunnerProperties.StartupMode;
import com.wl4g.rengine.common.entity.Rule.RuleEngine;
import com.wl4g.rengine.common.entity.Scenes.ScenesWrapper;
import com.wl4g.rengine.common.entity.SchedulingJob.ResultDescription;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.ExecuteResult;
import com.wl4g.rengine.executor.metrics.ExecutorMeterService;
import com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsTag;
import com.wl4g.rengine.executor.service.impl.EngineExecutionServiceImpl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link LifecycleExecutionService}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v1.0.0
 * @see https://github.com/google/guice/wiki/Motivation
 */
@Slf4j
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

    GenericTaskRunner<RunnerProperties> executionRunner;

    @PostConstruct
    void init() {
        final int threads = config.engine().executorThreadPools();
        log.info("Initialzing execution threads pool for : {}", threads);
        this.executionRunner = new GenericTaskRunner<RunnerProperties>(new RunnerProperties(StartupMode.NOSTARTUP, threads)) {
            @Override
            protected String getThreadNamePrefix() {
                return EngineExecutionServiceImpl.class.getSimpleName();
            }
        };
        this.executionRunner.start();
    }

    void destroy(@Observes @BeforeDestroyed(ApplicationScoped.class) ServletContext init) {
        if (nonNull(executionRunner)) {
            try {
                this.executionRunner.close();
            } catch (IOException e) {
                log.error("Failed to closing executeRequest runner", e);
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public @NotNull ExecuteResult execute(
            final @NotNull ExecuteRequest executeRequest,
            @NotEmpty final List<ScenesWrapper> sceneses) throws Exception {
        notNullOf(executeRequest, "executeRequest");
        notEmptyOf(sceneses, "sceneses");

        final List<String> scenesCodes = sceneses.stream().map(s -> s.getScenesCode()).collect(toList());

        // Monitor task timeout interrupt.
        final CountDownLatch latch = new CountDownLatch(sceneses.size());

        // Submit to execution workers.
        final Map<String, Future<ResultDescription>> futures = sceneses.stream()
                .map(scenes -> new KeyValue(scenes.getScenesCode(),
                        executionRunner.getWorker().submit(new ExecutionRunner(latch, executeRequest, scenes))))
                .collect(toMap(kv -> kv.getKey(), kv -> (Future) kv.getValue()));

        // Collect for uncompleted results.
        final List<ResultDescription> uncompleteds = new ArrayList<>(futures.size());
        final long timeoutMs = (long) ((long) executeRequest.getTimeout() * (1 - config.engine().evaluateTimeoutOffsetRate()));

        if (!latch.await(timeoutMs, MILLISECONDS)) { // timeout?
            final Iterator<Entry<String, Future<ResultDescription>>> it = futures.entrySet().iterator();
            while (it.hasNext()) {
                final Entry<String, Future<ResultDescription>> entry = it.next();
                final Future<ResultDescription> future = entry.getValue();
                // Collect for uncompleted results.
                if (!future.isDone() || future.isCancelled()) {
                    // Not need to execution continue.
                    future.cancel(true);
                    it.remove();
                    uncompleteds.add(ResultDescription.builder()
                            .scenesCode(entry.getKey())
                            .success(false)
                            .valueMap(emptyMap())
                            .reason(format("Execution time exceeded in total %sms", timeoutMs))
                            .build());
                }
            }
            log.debug("The parts success executed workflow graph tasks are: {}. requestId: {}, clientId: {}, scenesCodes: {}",
                    uncompleteds, scenesCodes);
        } else {
            log.debug("Executed workflow graph all tasks successfully. requestId: {}, clientId: {}, scenesCodes: {}",
                    uncompleteds, scenesCodes);
        }

        // Collect for completed results.
        final List<ResultDescription> completeds = futures.values().stream().map(f -> {
            try {
                return f.get();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }).collect(toList());

        return ExecuteResult.builder()
                .requestId(executeRequest.getRequestId())
                .errorCount(uncompleteds.size())
                .results(newArrayList(Iterables.concat(completeds, uncompleteds)))
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
                meterService.counter(evaluation_total.getName(), evaluation_total.getHelp(), MetricsTag.ENGINE, engine.name())
                        .increment();

                final WorkflowExecution execution = getExecution(engine);
                notNull(execution, "Could not load execution rule engine via %s of '%s'", engine.name(),
                        executeRequest.getClientId());
                final ResultDescription result = execution.execute(executeRequest, scenes);

                // Buried-point: success executeRequest.
                meterService.counter(evaluation_success.getName(), evaluation_success.getHelp(), MetricsTag.ENGINE, engine.name())
                        .increment();

                return result;
            } catch (Throwable e) {
                if (JvmRuntimeTool.isJvmInDebugging) {
                    e.printStackTrace();
                }

                // Buried-point: failed executeRequest.
                meterService.counter(evaluation_failure.getName(), evaluation_failure.getHelp(), MetricsTag.ENGINE, engine.name())
                        .increment();

                final String errmsg = ExceptionUtils.getRootCauseMessage(e);
                throw new RengineException(
                        format("Failed to execution %s engine of requestId: '%s', clientId: '%s', scenesCode: '%s'. reason: %s",
                                engine.name(), executeRequest.getRequestId(), executeRequest.getClientId(),
                                scenes.getScenesCode(), errmsg),
                        e);
            } finally {
                latch.countDown();
            }
        }
    }

}
