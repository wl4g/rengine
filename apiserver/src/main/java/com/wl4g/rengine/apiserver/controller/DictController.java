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
import com.wl4g.rengine.common.entity.Dict;
import com.wl4g.rengine.service.DictService;
import com.wl4g.rengine.service.model.DictDelete;
import com.wl4g.rengine.service.model.DictDeleteResult;
import com.wl4g.rengine.service.model.DictQuery;
import com.wl4g.rengine.service.model.DictSave;
import com.wl4g.rengine.service.model.DictSaveResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link DictController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "DictAPI", description = "The Dict management API")
@Slf4j
@RestController
@RequestMapping("/admin/dict")
public class DictController {

    private @Autowired DictService dictService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query dicts.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    public RespBase<PageHolder<Dict>> query(@Validated DictQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<Dict>> resp = RespBase.create();
        resp.setData(dictService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save dict.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<DictSaveResult> save(@Validated @RequestBody DictSave model) {
        log.debug("called: model={}", model);
        RespBase<DictSaveResult> resp = RespBase.create();
        resp.setData(dictService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete dict.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    public RespBase<DictDeleteResult> delete(@Validated @RequestBody DictDelete model) {
        log.debug("called: model={}", model);
        RespBase<DictDeleteResult> resp = RespBase.create();
        resp.setData(dictService.delete(model));
        return resp;
    }

}
