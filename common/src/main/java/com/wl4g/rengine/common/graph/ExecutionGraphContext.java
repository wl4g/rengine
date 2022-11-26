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
package com.wl4g.rengine.common.graph;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.System.currentTimeMillis;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Process context.
 * 
 * @author James Wong
 * @version 2022-11-04
 * @since v3.0.0
 */
@Getter
@Setter
@ToString(callSuper = true)
public final class ExecutionGraphContext implements Serializable {
    private static final long serialVersionUID = -7452442617432048671L;

    /**
     * The execution executing start time.
     */
    private final Long startTime;

    /**
     * The execution paramaeter.
     */
    private final ExecutionGraphParameter parameter;

    /*
     ** The processing flow nodes debug information.
     */
    private final StringBuilder traceText;

    /**
     * The execution end time.
     */
    private Long endTime;

    public ExecutionGraphContext(@NotNull final ExecutionGraphParameter parameter) {
        this(currentTimeMillis(), parameter);
    }

    public ExecutionGraphContext(@Min(0) final long startTime, @NotNull final ExecutionGraphParameter parameter) {
        this.startTime = startTime;
        this.parameter = notNullOf(parameter, "parameter");
        this.traceText = new StringBuilder(1024);
        this.endTime = null;
    }

    public ExecutionGraphContext end() {
        return end(currentTimeMillis());
    }

    public ExecutionGraphContext end(final long endTime) {
        setEndTime(endTime);
        return this;
    }

    public ExecutionGraphContext addTraceNode(@NotNull final ExecutionGraph<?> node, @NotNull final ExecutionGraphResult result) {
        return addTraceNode(node, result, false);
    }

    public ExecutionGraphContext addTraceNode(
            @NotNull final ExecutionGraph<?> node,
            @NotNull final ExecutionGraphResult result,
            final boolean end) {
        traceText.append("{\"")
                .append(node.getName())
                .append("\", ")
                .append(node.getId())
                .append("@")
                .append(node.getClass().getSimpleName())
                .append(":")
                .append(result.getReturnState().getAlias())
                .append("}");
        if (!end) {
            traceText.append(" -> ");
        }
        return this;
    }

}
