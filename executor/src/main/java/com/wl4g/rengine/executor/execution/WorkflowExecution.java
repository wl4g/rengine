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

import javax.validation.constraints.NotNull;

import com.wl4g.rengine.common.entity.Workflow.WorkflowWrapper;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.WorkflowExecuteResult.ResultDescription;

/**
 * {@link WorkflowExecution}
 * 
 * @author James Wong
 * @date 2022-09-17
 * @since v1.0.0
 */
public interface WorkflowExecution {
    ResultDescription execute(
            final @NotNull ExecuteRequest executeRequest,
            final @NotNull WorkflowWrapper workflow,
            final boolean usingCache);
}
