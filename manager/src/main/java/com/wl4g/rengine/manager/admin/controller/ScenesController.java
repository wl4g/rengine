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

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.bean.Scenes;
import com.wl4g.rengine.manager.admin.model.DeleteScenes;
import com.wl4g.rengine.manager.admin.model.DeleteScenesResult;
import com.wl4g.rengine.manager.admin.model.QueryScenes;
import com.wl4g.rengine.manager.admin.model.SaveScenes;
import com.wl4g.rengine.manager.admin.model.SaveScenesResult;
import com.wl4g.rengine.manager.admin.service.ScenesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link ScenesController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v3.0.0
 */
@Tag(name = "ScenesAPI", description = "The Scenes management API")
@Slf4j
@RestController
@RequestMapping("/admin/scenes")
public class ScenesController {

    private @Autowired ScenesService scenesService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query project list.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    public RespBase<PageHolder<Scenes>> query(@Validated QueryScenes model) {
        log.info("called: model={}", model);
        RespBase<PageHolder<Scenes>> resp = RespBase.create();
        resp.setData(scenesService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save project model.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<SaveScenesResult> save(@Validated @RequestBody SaveScenes model) {
        log.info("called: model={}", model);
        RespBase<SaveScenesResult> resp = RespBase.create();
        resp.setData(scenesService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete project.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    public RespBase<DeleteScenesResult> delete(@Validated DeleteScenes model) {
        log.info("called: model={}", model);
        RespBase<DeleteScenesResult> resp = RespBase.create();
        resp.setData(scenesService.delete(model));
        return resp;
    }

}
