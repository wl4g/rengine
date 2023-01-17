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
import com.wl4g.rengine.common.entity.ScheduleTrigger;
import com.wl4g.rengine.service.ScheduleTriggerService;
import com.wl4g.rengine.service.model.DeleteScheduleTrigger;
import com.wl4g.rengine.service.model.DeleteScheduleTriggerResult;
import com.wl4g.rengine.service.model.QueryScheduleTrigger;
import com.wl4g.rengine.service.model.SaveScheduleTrigger;
import com.wl4g.rengine.service.model.SaveScheduleTriggerResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link ScheduleTriggerController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "ScheduleTriggerAPI", description = "The ScheduleTrigger management API")
@Slf4j
@RestController
@RequestMapping("/admin/scheduleTrigger")
public class ScheduleTriggerController {

    private @Autowired ScheduleTriggerService scheduleTriggerService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query schedule triggers.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    public RespBase<PageHolder<ScheduleTrigger>> query(@Validated QueryScheduleTrigger model) {
        log.info("called: model={}", model);
        RespBase<PageHolder<ScheduleTrigger>> resp = RespBase.create();
        resp.setData(scheduleTriggerService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save schedule trigger.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<SaveScheduleTriggerResult> save(@Validated @RequestBody SaveScheduleTrigger model) {
        log.info("called: model={}", model);
        RespBase<SaveScheduleTriggerResult> resp = RespBase.create();
        resp.setData(scheduleTriggerService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete schedule trigger.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    public RespBase<DeleteScheduleTriggerResult> delete(@Validated @RequestBody DeleteScheduleTrigger model) {
        log.info("called: model={}", model);
        RespBase<DeleteScheduleTriggerResult> resp = RespBase.create();
        resp.setData(scheduleTriggerService.delete(model));
        return resp;
    }

}
