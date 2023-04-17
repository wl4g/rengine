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
package com.wl4g.rengine.executor.service;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.RuleScript.RuleScriptWrapper;
import com.wl4g.rengine.common.entity.Scenes.ScenesWrapper;
import com.wl4g.rengine.common.model.RuleScriptExecuteRequest;
import com.wl4g.rengine.common.model.WorkflowExecuteRequest;
import com.wl4g.rengine.common.model.WorkflowExecuteResult;
import com.wl4g.rengine.common.model.WorkflowExecuteResult.ResultDescription;

import io.smallrye.mutiny.Uni;

/**
 * {@link EngineExecutionService}
 * 
 * @author James Wong
 * @date 2022-09-17
 * @since v1.0.0
 */
public interface EngineExecutionService {

    Uni<RespBase<ResultDescription>> execute(final @NotNull RuleScriptExecuteRequest model);

    Uni<RespBase<WorkflowExecuteResult>> execute(final @NotNull WorkflowExecuteRequest model);

    Uni<List<RuleScriptWrapper>> findRuleScripts(@NotEmpty List<Long> ruleScriptIds);

    Uni<List<ScenesWrapper>> findScenesWorkflowGraphRules(@NotEmpty List<String> scenesCodes, @Min(1) @Max(1024) int revisions);

    //
    // --- Constants Definitions. ---
    //

    public static final TypeReference<List<ScenesWrapper>> SCENES_TYPE_REF = new TypeReference<List<ScenesWrapper>>() {
    };

}
