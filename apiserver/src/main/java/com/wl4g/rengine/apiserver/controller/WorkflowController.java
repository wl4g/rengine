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
package com.wl4g.rengine.apiserver.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.Workflow;
import com.wl4g.rengine.service.WorkflowService;
import com.wl4g.rengine.service.model.WorkflowDelete;
import com.wl4g.rengine.service.model.WorkflowDeleteResult;
import com.wl4g.rengine.service.model.WorkflowQuery;
import com.wl4g.rengine.service.model.WorkflowSave;
import com.wl4g.rengine.service.model.WorkflowSaveResult;

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
 * @since v1.0.0
 */
@Tag(name = "WorkflowAPI", description = "The workflow management API")
@Slf4j
@RestController
@RequestMapping("/v1/workflow")
public class WorkflowController {

    private @Autowired WorkflowService workflowService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query workflows.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    public RespBase<PageHolder<Workflow>> query(@Validated WorkflowQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<Workflow>> resp = RespBase.create();
        resp.setData(workflowService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save workflow.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<WorkflowSaveResult> save(@Validated @RequestBody WorkflowSave model) {
        log.debug("called: model={}", model);
        RespBase<WorkflowSaveResult> resp = RespBase.create();
        resp.setData(workflowService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete workflow.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    public RespBase<WorkflowDeleteResult> delete(@Validated @RequestBody WorkflowDelete model) {
        log.debug("called: model={}", model);
        RespBase<WorkflowDeleteResult> resp = RespBase.create();
        resp.setData(workflowService.delete(model));
        return resp;
    }

}
