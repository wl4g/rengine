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
package com.wl4g.rengine.server.admin.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.server.admin.model.AddWorkflow;
import com.wl4g.rengine.server.admin.model.AddWorkflowResult;
import com.wl4g.rengine.server.admin.model.DeleteWorkflow;
import com.wl4g.rengine.server.admin.model.DeleteWorkflowResult;
import com.wl4g.rengine.server.admin.model.QueryWorkflow;
import com.wl4g.rengine.server.admin.model.QueryWorkflowResult;
import com.wl4g.rengine.server.admin.service.WorkflowService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link WorkflowController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v3.0.0
 */
@Tag(name = "WorkflowAPI", description = "The workflow management API")
@Slf4j
@RestController
@RequestMapping("/admin/workflow")
public class WorkflowController {

    private @Autowired WorkflowService workflowService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query workflow list.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    public RespBase<QueryWorkflowResult> query(@Validated QueryWorkflow model) {
        log.info("called: model={}", model);
        RespBase<QueryWorkflowResult> resp = RespBase.create();
        resp.setData(workflowService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save workflows model.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<AddWorkflowResult> save(@Validated @RequestBody AddWorkflow model) {
        log.info("called: model={}", model);
        RespBase<AddWorkflowResult> resp = RespBase.create();
        resp.setData(workflowService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete project.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    public RespBase<DeleteWorkflowResult> delete(@Validated DeleteWorkflow model) {
        log.info("called: model={}", model);
        RespBase<DeleteWorkflowResult> resp = RespBase.create();
        resp.setData(workflowService.delete(model));
        return resp;
    }

}
