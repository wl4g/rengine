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
package com.wl4g.rengine.common.graph;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.System.currentTimeMillis;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public final class WorkflowContext implements Serializable {
    private static final long serialVersionUID = -7452442617432048671L;

    /**
     * The evaluation executing start time.
     */
    private final long startTime = currentTimeMillis();

    /**
     * The evaluation executing trace ID.
     */
    private long traceId;

    /**
     * The evaluation workflow ID.
     */
    private long workflowId;

    /**
     * The evaluation paramaeter.
     */
    private WorkflowParameter parameter;

    /*
     ** The processing flow nodes debug information.
     */
    private StringBuilder processInfo = new StringBuilder();

    public WorkflowContext(long workflowId, WorkflowParameter parameter) {
        this.workflowId = workflowId;
        this.parameter = notNullOf(parameter, "parameter");
    }

}
