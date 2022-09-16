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
package com.wl4g.rengine.manager.admin.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.manager.admin.model.AddProject;
import com.wl4g.rengine.manager.admin.model.AddProjectResult;
import com.wl4g.rengine.manager.admin.model.DeleteProject;
import com.wl4g.rengine.manager.admin.model.DeleteProjectResult;
import com.wl4g.rengine.manager.admin.model.QueryProject;
import com.wl4g.rengine.manager.admin.model.QueryProjectResult;
import com.wl4g.rengine.manager.admin.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link ProjectController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v3.0.0
 */
@Tag(name = "ProjectAPI", description = "The project management API")
@Slf4j
@RestController
@RequestMapping("/admin/project")
public class ProjectController {

    private @Autowired ProjectService projectService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query project list.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    public RespBase<QueryProjectResult> query(@Validated QueryProject model) {
        log.info("called: model={}", model);
        RespBase<QueryProjectResult> resp = RespBase.create();
        resp.setData(projectService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save project model.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<AddProjectResult> save(@Validated @RequestBody AddProject model) {
        log.info("called: model={}", model);
        RespBase<AddProjectResult> resp = RespBase.create();
        resp.setData(projectService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete project.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    public RespBase<DeleteProjectResult> delete(@Validated DeleteProject model) {
        log.info("called: model={}", model);
        RespBase<DeleteProjectResult> resp = RespBase.create();
        resp.setData(projectService.delete(model));
        return resp;
    }

}
