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
import com.wl4g.rengine.common.entity.WorkflowGraph;
import com.wl4g.rengine.service.WorkflowGraphService;
import com.wl4g.rengine.service.model.WorkflowDeleteGraph;
import com.wl4g.rengine.service.model.WorkflowGraphDeleteResult;
import com.wl4g.rengine.service.model.WorkflowGraphQuery;
import com.wl4g.rengine.service.model.WorkflowGraphSave;
import com.wl4g.rengine.service.model.WorkflowGraphLogfile;
import com.wl4g.rengine.service.model.WorkflowGraphLogfileResult;
import com.wl4g.rengine.service.model.WorkflowGraphResultSave;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link WorkflowGraphController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "WorkflowGraphAPI", description = "The workflow graph management API")
@Slf4j
@RestController
@RequestMapping("/admin/workflowgraph")
public class WorkflowGraphController {

    private @Autowired WorkflowGraphService workflowGraphService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query workflow graphs.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    public RespBase<PageHolder<WorkflowGraph>> query(@Validated WorkflowGraphQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<WorkflowGraph>> resp = RespBase.create();
        resp.setData(workflowGraphService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save workflow graph.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<WorkflowGraphResultSave> save(@Validated @RequestBody WorkflowGraphSave model) {
        log.debug("called: model={}", model);
        RespBase<WorkflowGraphResultSave> resp = RespBase.create();
        resp.setData(workflowGraphService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete workflow graph.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    public RespBase<WorkflowGraphDeleteResult> delete(@Validated @RequestBody WorkflowDeleteGraph model) {
        log.debug("called: model={}", model);
        RespBase<WorkflowGraphDeleteResult> resp = RespBase.create();
        resp.setData(workflowGraphService.delete(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Tail load for workflow graph log file.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "logtail" }, produces = "application/json", method = { GET })
    public RespBase<WorkflowGraphLogfileResult> logtail(@Validated WorkflowGraphLogfile model) {
        log.debug("called: model={}", model);
        RespBase<WorkflowGraphLogfileResult> resp = RespBase.create();
        resp.setData(workflowGraphService.logtail(model));
        return resp;
    }

}
