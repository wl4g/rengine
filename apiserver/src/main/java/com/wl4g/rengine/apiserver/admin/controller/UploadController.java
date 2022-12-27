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

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.UploadObject;
import com.wl4g.rengine.apiserver.admin.model.DeleteUpload;
import com.wl4g.rengine.apiserver.admin.model.DeleteUploadResult;
import com.wl4g.rengine.apiserver.admin.model.QueryUpload;
import com.wl4g.rengine.apiserver.admin.model.SaveUpload;
import com.wl4g.rengine.apiserver.admin.model.SaveUploadResult;
import com.wl4g.rengine.apiserver.admin.service.UploadService;

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
@RequestMapping("/admin/upload")
@CrossOrigin
public class UploadController {

    private @Autowired UploadService uploadService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query upload file list.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    public RespBase<PageHolder<UploadObject>> query(@Validated QueryUpload model) {
        log.info("called: model={}", model);
        RespBase<PageHolder<UploadObject>> resp = RespBase.create();
        resp.setData(uploadService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(summary = "Preparing to upload file.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful",
            content = { @Content(mediaType = "application/json") }) })
    @RequestMapping(path = { "apply" }, method = { POST })
    public RespBase<SaveUploadResult> apply(@Validated @RequestBody SaveUpload model) {
        log.info("called: model={}", model);
        RespBase<SaveUploadResult> resp = RespBase.create();
        resp.setData(uploadService.apply(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete upload.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    public RespBase<DeleteUploadResult> delete(@Validated @RequestBody DeleteUpload model) {
        log.info("called: model={}", model);
        RespBase<DeleteUploadResult> resp = RespBase.create();
        resp.setData(uploadService.delete(model));
        return resp;
    }

}
