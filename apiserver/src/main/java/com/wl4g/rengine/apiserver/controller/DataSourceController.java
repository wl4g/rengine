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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.DataSourceProperties;
import com.wl4g.rengine.service.DataSourceService;
import com.wl4g.rengine.service.model.DeleteDataSource;
import com.wl4g.rengine.service.model.DeleteDataSourceResult;
import com.wl4g.rengine.service.model.QueryDataSource;
import com.wl4g.rengine.service.model.SaveDataSource;
import com.wl4g.rengine.service.model.SaveDataSourceResult;

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
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "DataSourceAPI", description = "The dataSource models management API")
@Slf4j
@RestController
@RequestMapping("/admin/dataSource")
public class DataSourceController {

    private @Autowired DataSourceService dataSourceService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query dataSources.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful",
            content = { @Content(mediaType = "application/json") }) })
    @RequestMapping(path = { "query" }, method = { GET })
    public RespBase<PageHolder<DataSourceProperties>> query(@Validated QueryDataSource model) {
        log.info("called: model={}", model);
        RespBase<PageHolder<DataSourceProperties>> resp = RespBase.create();
        resp.setData(dataSourceService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save dataSource.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<SaveDataSourceResult> save(@Validated @RequestBody SaveDataSource model) {
        log.info("called: model={}", model);
        RespBase<SaveDataSourceResult> resp = RespBase.create();
        resp.setData(dataSourceService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete dataSource.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    public RespBase<DeleteDataSourceResult> delete(@Validated @RequestBody DeleteDataSource model) {
        log.info("called: model={}", model);
        RespBase<DeleteDataSourceResult> resp = RespBase.create();
        resp.setData(dataSourceService.delete(model));
        return resp;
    }

}
