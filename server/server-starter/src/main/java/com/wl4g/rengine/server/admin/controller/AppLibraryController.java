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
package com.wl4g.rengine.server.admin.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.server.admin.model.UploadApply;
import com.wl4g.rengine.server.admin.model.UploadApplyResult;
import com.wl4g.rengine.server.admin.service.AppLibraryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link AppLibraryController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v3.0.0
 */
@Tag(name = "ApplicationLibraryAPI", description = "The application library management API")
@Slf4j
@RestController
@RequestMapping("/admin/library")
public class AppLibraryController {

    private @Autowired AppLibraryService libraryService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(summary = "Preparing to upload the user app-library file.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UploadApply.class)) }) })
    @RequestMapping(path = { "apply" }, method = { GET, POST })
    public RespBase<UploadApplyResult> apply(HttpServletRequest request, UploadApply model) {
        log.info("[{}:called:apply()] uri={}, model={}", request.getRequestURI(), model);
        RespBase<UploadApplyResult> resp = RespBase.create();
        resp.setData(libraryService.apply(model));
        return resp;
    }

}
