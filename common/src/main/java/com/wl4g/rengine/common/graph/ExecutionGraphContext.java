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

import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.validation.constraints.NotNull;

import com.wl4g.rengine.common.graph.ExecutionGraphResult.ReturnState;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Workflow handler graph context.
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
     * The handler paramaeter.
     */
    private final ExecutionGraphParameter parameter;

    /**
     * The execution handler.
     */
    private final Function<ExecutionGraphContext, ReturnState> handler;

    /**
     * The processing flow nodes trace spans.
     */
    private final Map<String, TraceSpan> traceSpans;

    /**
     * The execution graph operator node.
     */
    private ExecutionGraph<?> currentNode;

    /**
     * The handler executing start time.
     */
    private Long startTime;

    /**
     * The handler end time.
     */
    private Long endTime;

    public ExecutionGraphContext(@NotNull final ExecutionGraphParameter parameter,
            @NotNull final Function<ExecutionGraphContext, ReturnState> handler) {
        this.parameter = notNullOf(parameter, "parameter");
        this.handler = notNullOf(handler, "handler");
        this.traceSpans = new LinkedHashMap<>(8);
        this.endTime = null;
    }

    ExecutionGraphContext start() {
        return start(currentTimeMillis());
    }

    public ExecutionGraphContext start(final long startTime) {
        isTrue(startTime > 0, "startTime>0 miss, but is %s", startTime);
        setStartTime(startTime);
        return this;
    }

    public ExecutionGraphContext end() {
        return end(currentTimeMillis());
    }

    public ExecutionGraphContext end(final long endTime) {
        isTrue(endTime > 0, "endTime>0 miss, but is %s", endTime);
        setEndTime(endTime);
        return this;
    }

    public ExecutionGraphContext beginTrace(@NotNull final ExecutionGraph<?> node) {
        this.traceSpans.put(node.getId(), TraceSpan.builder().node(node).build());
        return this;
    }

    public ExecutionGraphContext endTrace(@NotNull final ExecutionGraph<?> node, @NotNull final ExecutionGraphResult result) {
        TraceSpan traceSpan = traceSpans.get(node.getId());
        if (isNull(traceSpan)) {
            throw new IllegalStateException(format("Could not trace span : %s@%s, you must first call #beginTrance()",
                    node.getId(), node.getClass().getSimpleName()));
        }
        traceSpan.setResult(result);
        return this;
    }

    public String asTraceText() {
        return asTraceText(false);
    }

    public String asTraceText(boolean isPretty) {
        StringBuilder traceText = new StringBuilder(traceSpans.size() * 32);
        final Iterator<Entry<String, TraceSpan>> it = traceSpans.entrySet().iterator();
        int stacks = 0;
        while (it.hasNext()) {
            TraceSpan traceSpan = it.next().getValue();
            if (isPretty) {
                if (stacks > 0) {
                    traceText.append("\n");
                }
                for (int i = 0, size = stacks++; i < size; i++) {
                    traceText.append("    ");
                }
            }
            if (isPretty) {
                traceText.append("→ ");
            }
            traceText.append("{\"")
                    .append(traceSpan.getNode().getName())
                    .append("\", ")
                    .append(traceSpan.getNode().getId())
                    .append("@")
                    .append(traceSpan.getNode().getClass().getSimpleName())
                    .append(":")
                    .append(traceSpan.getResult().getReturnState().getAlias())
                    .append("}");
            if (it.hasNext()) {
                if (!isPretty) {
                    traceText.append(" → ");
                }
            }
        }
        return traceText.toString();
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class TraceSpan {
        private @NotNull ExecutionGraph<?> node;
        private @NotNull ExecutionGraphResult result;
    }

}
