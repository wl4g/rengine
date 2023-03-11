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
package com.wl4g.rengine.apiserver.controller.sys;

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
import com.wl4g.rengine.common.entity.sys.Menu;
import com.wl4g.rengine.service.MenuService;
import com.wl4g.rengine.service.model.sys.MenuDelete;
import com.wl4g.rengine.service.model.sys.MenuDeleteResult;
import com.wl4g.rengine.service.model.sys.MenuQuery;
import com.wl4g.rengine.service.model.sys.MenuSave;
import com.wl4g.rengine.service.model.sys.MenuSaveResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link MenuController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "MenuAPI", description = "The Menu management API")
@Slf4j
@RestController
@RequestMapping("/v1/menu")
public class MenuController {

    private @Autowired MenuService menuService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query menues.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    @PreAuthorize("hasPermission(#model,'arn:sys:menu:read:v1')")
    public RespBase<PageHolder<Menu>> query(@Validated MenuQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<Menu>> resp = RespBase.create();
        resp.setData(menuService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save menu.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    @PreAuthorize("hasPermission(#model,'arn:sys:menu:write:v1')")
    public RespBase<MenuSaveResult> save(@Validated @RequestBody MenuSave model) {
        log.debug("called: model={}", model);
        RespBase<MenuSaveResult> resp = RespBase.create();
        resp.setData(menuService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete menu.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    @PreAuthorize("hasPermission(#model,'arn:sys:menu:delete:v1')")
    public RespBase<MenuDeleteResult> delete(@Validated @RequestBody MenuDelete model) {
        log.debug("called: model={}", model);
        RespBase<MenuDeleteResult> resp = RespBase.create();
        resp.setData(menuService.delete(model));
        return resp;
    }

}
