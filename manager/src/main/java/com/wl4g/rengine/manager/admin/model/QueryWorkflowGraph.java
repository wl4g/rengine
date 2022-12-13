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
package com.wl4g.rengine.manager.admin.model;

import javax.annotation.Nullable;

import com.wl4g.rengine.common.entity.WorkflowGraph;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link QueryWorkflowGraph}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
public class QueryWorkflowGraph extends QueryBase<WorkflowGraph> {
    private @Nullable Long graphId;
    private @Nullable Long workflowId;

    // Notice: The disable reading and writing of the name field in the swagger
    // document. (because the workflow graph does not have a name field)
    @Schema(hidden = true, accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE)
    public String getName() {
        return super.getName();
    }

}
