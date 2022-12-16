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
package com.wl4g.rengine.apiserver.admin.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.apiserver.admin.model.SaveIdentityProvider;
import com.wl4g.rengine.apiserver.admin.model.SaveIdentityProviderResult;
import com.wl4g.rengine.apiserver.admin.model.QueryIdentityProvider;
import com.wl4g.rengine.apiserver.admin.model.QueryIdentityProviderResult;
import com.wl4g.rengine.apiserver.admin.service.IdentityProviderService;

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
@RequestMapping("/admin/idp")
public class IdentityProviderController {

    private @Autowired IdentityProviderService identityProviderService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query Identity Provider setting.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    public RespBase<QueryIdentityProviderResult> query(@Validated QueryIdentityProvider model) {
        log.info("called: model={}", model);
        RespBase<QueryIdentityProviderResult> resp = RespBase.create();
        resp.setData(identityProviderService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save identity provider")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<SaveIdentityProviderResult> save(@Validated @RequestBody SaveIdentityProvider model) {
        log.info("called: model={}", model);
        RespBase<SaveIdentityProviderResult> resp = RespBase.create();
        resp.setData(identityProviderService.save(model));
        return resp;
    }

}
