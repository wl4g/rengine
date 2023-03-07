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
import com.wl4g.rengine.common.entity.Scenes;
import com.wl4g.rengine.service.ScenesService;
import com.wl4g.rengine.service.model.ScenesDelete;
import com.wl4g.rengine.service.model.ScenesDeleteResult;
import com.wl4g.rengine.service.model.ScenesQuery;
import com.wl4g.rengine.service.model.ScenesSave;
import com.wl4g.rengine.service.model.ScenesSaveResult;

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
 * @since v1.0.0
 */
@Tag(name = "ScenesAPI", description = "The Scenes management API")
@Slf4j
@RestController
@RequestMapping("/v1/scenes")
public class ScenesController {

    private @Autowired ScenesService scenesService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query sceneses.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    @PreAuthorize("hasAuthority('arn:rengine:scenes:query:v1')")
    public RespBase<PageHolder<Scenes>> query(@Validated ScenesQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<Scenes>> resp = RespBase.create();
        resp.setData(scenesService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save scenes.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    @PreAuthorize("hasAuthority('arn:rengine:scenes:save:v1')")
    public RespBase<ScenesSaveResult> save(@Validated @RequestBody ScenesSave model) {
        log.debug("called: model={}", model);
        RespBase<ScenesSaveResult> resp = RespBase.create();
        resp.setData(scenesService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete scenes.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    @PreAuthorize("hasAuthority('arn:rengine:scenes:delete:v1')")
    public RespBase<ScenesDeleteResult> delete(@Validated @RequestBody ScenesDelete model) {
        log.debug("called: model={}", model);
        RespBase<ScenesDeleteResult> resp = RespBase.create();
        resp.setData(scenesService.delete(model));
        return resp;
    }

}
