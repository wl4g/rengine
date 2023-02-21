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
package com.wl4g.rengine.executor.execution.engine;

import javax.validation.constraints.NotNull;

import com.wl4g.rengine.common.entity.Rule.RuleWrapper;
import com.wl4g.rengine.common.graph.ExecutionGraphContext;
import com.wl4g.rengine.executor.execution.sdk.ScriptResult;

/**
 * {@link IEngine}
 * 
 * @author James Wong
 * @version 2022-09-22
 * @since v1.0.0
 */
public interface IEngine {
    ScriptResult execute(
            @NotNull final ExecutionGraphContext graphContext,
            @NotNull final RuleWrapper rule,
            final boolean usingCache);
}
