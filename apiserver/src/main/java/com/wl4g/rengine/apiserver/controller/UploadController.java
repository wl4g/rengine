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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.UploadObject;
import com.wl4g.rengine.service.UploadService;
import com.wl4g.rengine.service.model.DeleteUpload;
import com.wl4g.rengine.service.model.UploadDeleteResult;
import com.wl4g.rengine.service.model.UploadQuery;
import com.wl4g.rengine.service.model.UploadSave;
import com.wl4g.rengine.service.model.UploadSaveResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link UploadController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "UploadAPI", description = "The user upload management API")
@Slf4j
@RestController
@RequestMapping("/v1/upload")
@CrossOrigin
public class UploadController {

    private @Autowired UploadService uploadService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query upload files.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    @PreAuthorize("hasPermission(#model,'arn:rengine:upload:read:v1')")
    public RespBase<PageHolder<UploadObject>> query(@Validated UploadQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<UploadObject>> resp = RespBase.create();
        resp.setData(uploadService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(summary = "Preparing to upload file.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful",
            content = { @Content(mediaType = "application/json") }) })
    @RequestMapping(path = { "apply" }, method = { POST })
    @PreAuthorize("hasPermission(#model,'arn:rengine:upload:apply:v1')")
    public RespBase<UploadSaveResult> apply(@Validated @RequestBody UploadSave model) {
        log.debug("called: model={}", model);
        RespBase<UploadSaveResult> resp = RespBase.create();
        resp.setData(uploadService.apply(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete upload.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    @PreAuthorize("hasPermission(#model,'arn:rengine:upload:delete:v1')")
    public RespBase<UploadDeleteResult> delete(@Validated @RequestBody DeleteUpload model) {
        log.debug("called: model={}", model);
        RespBase<UploadDeleteResult> resp = RespBase.create();
        resp.setData(uploadService.delete(model));
        return resp;
    }

}
