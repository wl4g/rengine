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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.Controller;
import com.wl4g.rengine.service.ControllerScheduleService;
import com.wl4g.rengine.service.model.ControllerScheduleDelete;
import com.wl4g.rengine.service.model.ControllerScheduleDeleteResult;
import com.wl4g.rengine.service.model.ControllerScheduleQuery;
import com.wl4g.rengine.service.model.ControllerScheduleSave;
import com.wl4g.rengine.service.model.ControllerScheduleSaveResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link ControllerController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "ControllerScheduleAPI", description = "The Controller management API")
@Slf4j
@RestController
@RequestMapping("/v1/controller")
public class ControllerController {

    private @Autowired ControllerScheduleService controllerScheduleService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query controllers.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    @PreAuthorize("hasPermission(#model,'arn:rengine:controller:read:v1')")
    public RespBase<PageHolder<Controller>> query(@Validated ControllerScheduleQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<Controller>> resp = RespBase.create();
        resp.setData(controllerScheduleService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save controllers.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    @PreAuthorize("hasPermission(#model,'arn:rengine:controller:write:v1')")
    public RespBase<ControllerScheduleSaveResult> save(@Validated @RequestBody ControllerScheduleSave model) {
        log.debug("called: model={}", model);
        RespBase<ControllerScheduleSaveResult> resp = RespBase.create();
        resp.setData(controllerScheduleService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete controllers.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    @PreAuthorize("hasPermission(#model,'arn:rengine:controller:delete:v1')")
    public RespBase<ControllerScheduleDeleteResult> delete(@Validated @RequestBody ControllerScheduleDelete model) {
        log.debug("called: model={}", model);
        RespBase<ControllerScheduleDeleteResult> resp = RespBase.create();
        resp.setData(controllerScheduleService.delete(model));
        return resp;
    }

}
