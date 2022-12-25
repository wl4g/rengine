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
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.Future;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import com.wl4g.infra.common.graalvm.GraalPolyglotManager;
import com.wl4g.infra.common.graalvm.GraalPolyglotManager.ContextWrapper;
import com.wl4g.infra.common.task.CompleteTaskListener;
import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;

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

    private final SafeScheduledTaskPoolExecutor executor;
    private final GraalPolyglotManager graalPolyglotManager;

    public ScriptExecutor(@NotNull SafeScheduledTaskPoolExecutor executor, @NotNull GraalPolyglotManager graalPolyglotManager) {
        this.executor = notNullOf(executor, "executor");
        this.graalPolyglotManager = notNullOf(graalPolyglotManager, "graalPolyglotManager");
    }

    // public @HostAccess.Export Future<Object> submit(@NotNull Value jsLambda)
    // {
    // return this.runner.submit(() -> jsLambda.execute());
    // }

    public @HostAccess.Export Future<Object> submit(@NotNull Value jsLambda) {
        notNullOf(jsLambda, "jsLambda");
        final String script = jsLambda.toString();
        return this.executor.submit(() -> {
            ContextWrapper graalContext = null;
            try {
                graalContext = graalPolyglotManager.getContext();
                final Value jsFunction = graalContext.eval(
                        Source.newBuilder("js", script, "lambda_script.js").mimeType("application/javascript+module").build());
                return jsFunction.execute();
            } finally {
                if (nonNull(graalContext)) {
                    graalContext.close();
                }
            }
        });
    }

    public @HostAccess.Export void submitForComplete(@NotNull List<Value> jsLambdas, @Min(1) long timeoutMs) {
        notNullOf(jsLambdas, "jsLambdas");
        isTrueOf(timeoutMs > 1, "timeoutMs>1");
        final List<Runnable> jobs = safeList(jsLambdas).stream().map(jsLambda -> {
            final String script = jsLambda.toString();
            return (Runnable) () -> {
                try (Context graalContext = Context.newBuilder("js").allowAllAccess(true).build();) {
                    final Value jsFunction = graalContext.eval(Source.newBuilder("js", script, "lambda_script.js")
                            .mimeType("application/javascript+module")
                            .build());
                    jsFunction.execute();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
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
        final List<Runnable> jobs = safeList(jsLambdas).stream().map(jsLambda -> {
            final String script = jsLambda.toString();
            return (Runnable) () -> {
                try (Context graalContext = Context.newBuilder("js").allowAllAccess(true).build();) {
                    final Value jsFunction = graalContext.eval(Source.newBuilder("js", script, "lambda_script.js")
                            .mimeType("application/javascript+module")
                            .build());
                    jsFunction.execute();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            };
        }).collect(toList());
        this.executor.submitForComplete(jobs, listener, timeoutMs);
    }

}
