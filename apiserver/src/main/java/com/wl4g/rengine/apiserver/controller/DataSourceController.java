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
import com.wl4g.rengine.common.entity.DataSourceProperties;
import com.wl4g.rengine.service.DataSourceService;
import com.wl4g.rengine.service.model.DataSourceDelete;
import com.wl4g.rengine.service.model.DataSourceDeleteResult;
import com.wl4g.rengine.service.model.DataSourceQuery;
import com.wl4g.rengine.service.model.DataSourceSave;
import com.wl4g.rengine.service.model.DataSourceSaveResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link DataSourceController}
 * 
 * @author James Wong
 * @date 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "DataSourceAPI", description = "The dataSource models management API")
@Slf4j
@RestController
@RequestMapping("/v1/dataSource")
public class DataSourceController {

    private @Autowired DataSourceService dataSourceService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query dataSources.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful",
            content = { @Content(mediaType = "application/json") }) })
    @RequestMapping(path = { "query" }, method = { GET })
    @PreAuthorize("hasPermission(#model,'arn:rengine:datasource:read:v1')")
    public RespBase<PageHolder<DataSourceProperties>> query(@Validated DataSourceQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<DataSourceProperties>> resp = RespBase.create();
        resp.setData(dataSourceService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save dataSource.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    @PreAuthorize("hasPermission(#model,'arn:rengine:datasource:write:v1')")
    public RespBase<DataSourceSaveResult> save(@Validated @RequestBody DataSourceSave model) {
        log.debug("called: model={}", model);
        RespBase<DataSourceSaveResult> resp = RespBase.create();
        resp.setData(dataSourceService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete dataSource.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    @PreAuthorize("hasPermission(#model,'arn:rengine:datasource:delete:v1')")
    public RespBase<DataSourceDeleteResult> delete(@Validated @RequestBody DataSourceDelete model) {
        log.debug("called: model={}", model);
        RespBase<DataSourceDeleteResult> resp = RespBase.create();
        resp.setData(dataSourceService.delete(model));
        return resp;
    }

}
