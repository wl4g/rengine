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

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.service.IdentityProviderService;
import com.wl4g.rengine.service.model.IdentityProviderQuery;
import com.wl4g.rengine.service.model.IdentityProviderQueryResult;
import com.wl4g.rengine.service.model.IdentityProviderSave;
import com.wl4g.rengine.service.model.IdentityProviderSaveResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link IdentityProviderController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "IdentityProviderAPI", description = "The Identity Provider setting management API")
@Slf4j
@RestController
@RequestMapping("/api/v1/idp")
public class IdentityProviderController {

    private @Autowired IdentityProviderService identityProviderService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query identity provider settings.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    public RespBase<IdentityProviderQueryResult> query(@Validated IdentityProviderQuery model) {
        log.debug("called: model={}", model);
        RespBase<IdentityProviderQueryResult> resp = RespBase.create();
        resp.setData(identityProviderService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save identity provider settings.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<IdentityProviderSaveResult> save(@Validated @RequestBody IdentityProviderSave model) {
        log.debug("called: model={}", model);
        RespBase<IdentityProviderSaveResult> resp = RespBase.create();
        resp.setData(identityProviderService.save(model));
        return resp;
    }

}
