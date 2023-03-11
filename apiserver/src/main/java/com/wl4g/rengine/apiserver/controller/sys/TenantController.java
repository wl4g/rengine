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
import com.wl4g.rengine.common.entity.sys.Tenant;
import com.wl4g.rengine.service.TenantService;
import com.wl4g.rengine.service.model.sys.TenantDelete;
import com.wl4g.rengine.service.model.sys.TenantDeleteResult;
import com.wl4g.rengine.service.model.sys.TenantQuery;
import com.wl4g.rengine.service.model.sys.TenantSave;
import com.wl4g.rengine.service.model.sys.TenantSaveResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link TenantController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "TenantAPI", description = "The Tenant management API")
@Slf4j
@RestController
@RequestMapping("/v1/tenant")
public class TenantController {

    private @Autowired TenantService tenantService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query tenantes.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    @PreAuthorize("hasPermission(#model,'arn:sys:tenant:read:v1')")
    public RespBase<PageHolder<Tenant>> query(@Validated TenantQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<Tenant>> resp = RespBase.create();
        resp.setData(tenantService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save tenant.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    @PreAuthorize("hasPermission(#model,'arn:sys:tenant:write:v1')")
    public RespBase<TenantSaveResult> save(@Validated @RequestBody TenantSave model) {
        log.debug("called: model={}", model);
        RespBase<TenantSaveResult> resp = RespBase.create();
        resp.setData(tenantService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete tenant.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    @PreAuthorize("hasPermission(#model,'arn:sys:tenant:delete:v1')")
    public RespBase<TenantDeleteResult> delete(@Validated @RequestBody TenantDelete model) {
        log.debug("called: model={}", model);
        RespBase<TenantDeleteResult> resp = RespBase.create();
        resp.setData(tenantService.delete(model));
        return resp;
    }

}
