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
import com.wl4g.rengine.common.entity.ScheduleJob;
import com.wl4g.rengine.service.ScheduleJobService;
import com.wl4g.rengine.service.model.DeleteScheduleJob;
import com.wl4g.rengine.service.model.DeleteScheduleJobResult;
import com.wl4g.rengine.service.model.QueryScheduleJob;
import com.wl4g.rengine.service.model.SaveScheduleJob;
import com.wl4g.rengine.service.model.SaveScheduleJobResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link ScheduleJobController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "ScheduleJobAPI", description = "The ScheduleJob management API")
@Slf4j
@RestController
@RequestMapping("/admin/scheduleJob")
public class ScheduleJobController {

    private @Autowired ScheduleJobService scheduleJobService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query schedule jobs.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    public RespBase<PageHolder<ScheduleJob>> query(@Validated QueryScheduleJob model) {
        log.info("called: model={}", model);
        RespBase<PageHolder<ScheduleJob>> resp = RespBase.create();
        resp.setData(scheduleJobService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save schedule job.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<SaveScheduleJobResult> save(@Validated @RequestBody SaveScheduleJob model) {
        log.info("called: model={}", model);
        RespBase<SaveScheduleJobResult> resp = RespBase.create();
        resp.setData(scheduleJobService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete schedule job.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    public RespBase<DeleteScheduleJobResult> delete(@Validated @RequestBody DeleteScheduleJob model) {
        log.info("called: model={}", model);
        RespBase<DeleteScheduleJobResult> resp = RespBase.create();
        resp.setData(scheduleJobService.delete(model));
        return resp;
    }

}
