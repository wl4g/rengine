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
package com.wl4g.rengine.executor.execution.sdk;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.executor.execution.engine.AbstractScriptEngine.SCIPRT_LOGGER_KEY_WORKFLOW_ID;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import com.wl4g.infra.common.graalvm.polyglot.GraalPolyglotManager;
import com.wl4g.infra.common.graalvm.polyglot.GraalPolyglotManager.ContextWrapper;
import com.wl4g.infra.common.task.CompleteTaskListener;
import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;
import com.wl4g.rengine.common.exception.ExecutionScriptException;

import lombok.ToString;

/**
 * {@link ScriptExecutor}
 * 
 * @author James Wong
 * @version 2022-09-25
 * @since v1.0.0
 */
@ToString
public class ScriptExecutor {
    final Long workflowId;
    final SafeScheduledTaskPoolExecutor executor;
    final GraalPolyglotManager graalPolyglotManager;

    public ScriptExecutor(final @NotNull Long workflowId, final @NotNull SafeScheduledTaskPoolExecutor executor,
            final @NotNull GraalPolyglotManager graalPolyglotManager) {
        this.workflowId = notNullOf(workflowId, "workflowId");
        this.executor = notNullOf(executor, "executor");
        this.graalPolyglotManager = notNullOf(graalPolyglotManager, "graalPolyglotManager");
    }

    public @HostAccess.Export Future<Object> submit(@NotNull Value jsLambda) {
        notNullOf(jsLambda, "jsLambda");

        final String script = jsLambda.toString();
        return this.executor.submit(() -> {
            // The same context restricted of graal.js does not allow
            // multi-threaded access, a new context must be used to execute in
            // the thread pool asynchronously.
            final Map<String, Object> metadata = singletonMap(SCIPRT_LOGGER_KEY_WORKFLOW_ID, workflowId);
            try (ContextWrapper graalContext = graalPolyglotManager.getContext(metadata);) {
                final Value jsFunction = graalContext
                        .eval(Source.newBuilder("js", script, "lambda0.js").mimeType("application/javascript+module").build());
                return jsFunction.execute();
            }
        });
    }

    public @HostAccess.Export void submitForComplete(@NotNull List<Value> jsLambdas, @Min(1) long timeoutMs) {
        notNullOf(jsLambdas, "jsLambdas");
        isTrueOf(timeoutMs > 1, "timeoutMs>1");

        final AtomicInteger index = new AtomicInteger(0);
        final List<Runnable> jobs = safeList(jsLambdas).stream().map(jsLambda -> {
            final String script = jsLambda.toString();
            return (Runnable) () -> {
                // The same context restricted of graal.js does not allow
                // multi-threaded access, a new context must be used to execute
                // in the thread pool asynchronously.
                final Map<String, Object> metadata = singletonMap(SCIPRT_LOGGER_KEY_WORKFLOW_ID, workflowId);
                try (ContextWrapper graalContext = graalPolyglotManager.getContext(metadata);) {
                    final Value jsFunction = graalContext
                            .eval(Source.newBuilder("js", script, format("lambda%s.js", index.getAndIncrement()))
                                    .mimeType("application/javascript+module")
                                    .build());
                    jsFunction.execute();
                } catch (Exception e) {
                    throw new ExecutionScriptException(e);
                }
            };
        }).collect(toList());

        this.executor.submitForComplete(jobs, timeoutMs);
    }

    public @HostAccess.Export void submitForComplete(
            @NotNull List<Value> jsLambdas,
            @NotNull CompleteTaskListener listener,
            @Min(1) long timeoutMs) {
        notNullOf(jsLambdas, "jsLambdas");
        notNullOf(listener, "listener");
        isTrueOf(timeoutMs > 1, "timeoutMs>1");

        final AtomicInteger index = new AtomicInteger(0);
        final List<Runnable> jobs = safeList(jsLambdas).stream().map(jsLambda -> {
            final String script = jsLambda.toString();
            return (Runnable) () -> {
                // The same context restricted of graal.js does not allow
                // multi-threaded access, a new context must be used to execute
                // in the thread pool asynchronously.
                final Map<String, Object> metadata = singletonMap(SCIPRT_LOGGER_KEY_WORKFLOW_ID, workflowId);
                try (ContextWrapper graalContext = graalPolyglotManager.getContext(metadata);) {
                    final Value jsFunction = graalContext
                            .eval(Source.newBuilder("js", script, format("lambda%s.js", index.getAndIncrement()))
                                    .mimeType("application/javascript+module")
                                    .build());
                    jsFunction.execute();
                } catch (Exception e) {
                    throw new ExecutionScriptException(e);
                }
            };
        }).collect(toList());

        this.executor.submitForComplete(jobs, listener, timeoutMs);
    }

}
