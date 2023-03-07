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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.ControllerSchedule.RunState;
import com.wl4g.rengine.service.ControllerLogService;
import com.wl4g.rengine.service.ControllerScheduleService;
import com.wl4g.rengine.service.model.ControllerLogQuery;
import com.wl4g.rengine.service.model.ControllerScheduleQuery;
import com.wl4g.rengine.service.model.HomeQuery;
import com.wl4g.rengine.service.model.HomeQueryResult;
import com.wl4g.rengine.service.model.HomeQueryResult.ScheduleStateInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link HomeController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "HomeAPI", description = "The Home management API")
@Slf4j
@RestController
@RequestMapping("/v1/home")
public class HomeController {

    private @Autowired ControllerScheduleService controllerScheduleService;
    private @Autowired ControllerLogService controllerLogService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query home information.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    @PreAuthorize("hasAuthority('arn:rengine:home:query:v1')")
    public RespBase<HomeQueryResult> query(@Validated HomeQuery model) {
        log.debug("called: model={}", model);
        RespBase<HomeQueryResult> resp = RespBase.create();

        final var scheduleResult = controllerScheduleService.query(ControllerScheduleQuery.builder().pageSize(100).build());

        final var runningCount = safeList(scheduleResult.getRecords()).stream()
                .filter(s -> s.getRunState() == RunState.RUNNING)
                .count();
        final var schedCount = safeList(scheduleResult.getRecords()).stream()
                .filter(s -> s.getRunState() == RunState.SCHED)
                .count();
        final var failedCount = safeList(scheduleResult.getRecords()).stream()
                .filter(s -> s.getRunState() != RunState.FAILED)
                .count();
        final var successCount = safeList(scheduleResult.getRecords()).stream()
                .filter(s -> s.getRunState() == RunState.SUCCESS)
                .count();
        final var partSuccessCount = safeList(scheduleResult.getRecords()).stream()
                .filter(s -> s.getRunState() == RunState.PART_SUCCESS)
                .count();
        final var killedCount = safeList(scheduleResult.getRecords()).stream()
                .filter(s -> s.getRunState() == RunState.KILLED)
                .count();

        final var runningScheduleIds = safeList(scheduleResult.getRecords()).stream()
                .filter(s -> s.getRunState() == RunState.RUNNING)
                .map(s -> s.getId())
                .collect(toList());
        final var logResult = controllerLogService.query(ControllerLogQuery.builder().scheduleIds(runningScheduleIds).build());

        return resp.withData(HomeQueryResult.builder()
                .state(ScheduleStateInfo.builder()
                        .schedCount(schedCount)
                        .runningCount(runningCount)
                        .failedCount(failedCount)
                        .successCount(successCount)
                        .partSuccessCount(partSuccessCount)
                        .killedCount(killedCount)
                        .build())
                .logs(logResult.getRecords())
                .build());
    }

}
