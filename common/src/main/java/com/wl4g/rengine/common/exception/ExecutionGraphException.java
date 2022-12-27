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
package com.wl4g.rengine.common.exception;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import com.wl4g.rengine.common.graph.ExecutionGraph;

import lombok.Getter;

/**
 * {@link ExecutionGraphException}
 * 
 * @author James Wong
 * @version 2022-09-22
 * @since v1.0.0
 */
@Getter
public class ExecutionGraphException extends RengineException {
    private static final long serialVersionUID = 5177120828249689148L;

    private ExecutionGraph<?> node;

    public ExecutionGraphException(ExecutionGraph<?> node, String message) {
        super(message);
        this.node = notNullOf(node, "node");
    }

    public ExecutionGraphException(ExecutionGraph<?> node, String message, Throwable cause) {
        super(message, cause);
        this.node = notNullOf(node, "node");
    }

    public ExecutionGraphException(ExecutionGraph<?> node, Throwable cause) {
        super(cause);
        this.node = notNullOf(node, "node");
    }
}
