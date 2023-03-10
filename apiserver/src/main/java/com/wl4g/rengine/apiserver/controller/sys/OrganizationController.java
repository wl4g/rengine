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
import com.wl4g.rengine.common.entity.sys.Organization;
import com.wl4g.rengine.service.OrganizationService;
import com.wl4g.rengine.service.model.sys.OrganizationDelete;
import com.wl4g.rengine.service.model.sys.OrganizationDeleteResult;
import com.wl4g.rengine.service.model.sys.OrganizationQuery;
import com.wl4g.rengine.service.model.sys.OrganizationSave;
import com.wl4g.rengine.service.model.sys.OrganizationSaveResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link OrganizationController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "OrganizationAPI", description = "The Organization management API")
@Slf4j
@RestController
@RequestMapping("/v1/organization")
public class OrganizationController {

    private @Autowired OrganizationService organizationService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query organizationes.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    @PreAuthorize("hasAuthority('arn:sys:org:query:v1')")
    public RespBase<PageHolder<Organization>> query(@Validated OrganizationQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<Organization>> resp = RespBase.create();
        resp.setData(organizationService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save organization.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    @PreAuthorize("hasAuthority('arn:sys:org:save:v1')")
    public RespBase<OrganizationSaveResult> save(@Validated @RequestBody OrganizationSave model) {
        log.debug("called: model={}", model);
        RespBase<OrganizationSaveResult> resp = RespBase.create();
        resp.setData(organizationService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete organization.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    @PreAuthorize("hasAuthority('arn:sys:org:delete:v1')")
    public RespBase<OrganizationDeleteResult> delete(@Validated @RequestBody OrganizationDelete model) {
        log.debug("called: model={}", model);
        RespBase<OrganizationDeleteResult> resp = RespBase.create();
        resp.setData(organizationService.delete(model));
        return resp;
    }

}
