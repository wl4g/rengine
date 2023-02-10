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

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.service.NotificationService;
import com.wl4g.rengine.service.model.NotificationDingtalkUserIdExchage;
import com.wl4g.rengine.service.model.NotificationDingtalkUserIdExchageResult;
import com.wl4g.rengine.service.model.NotificationQuery;
import com.wl4g.rengine.service.model.NotificationQueryResult;
import com.wl4g.rengine.service.model.NotificationSave;
import com.wl4g.rengine.service.model.NotificationSaveResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link NotificationController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "NotificationAPI", description = "The notification setting management API")
@Slf4j
@RestController
@RequestMapping("/admin/notification")
public class NotificationController {

    private @Autowired NotificationService notificationService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query notification settings.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    public RespBase<NotificationQueryResult> query(@Validated NotificationQuery model) {
        log.debug("called: model={}", model);
        RespBase<NotificationQueryResult> resp = RespBase.create();
        resp.setData(notificationService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save notification settings.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<NotificationSaveResult> save(@Validated @RequestBody NotificationSave model) {
        log.debug("called: model={}", model);
        RespBase<NotificationSaveResult> resp = RespBase.create();
        resp.setData(notificationService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Exchanging dingtalk userIds by mobiles.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "exchageDingtalkUserId" }, consumes = "application/json", produces = "application/json",
            method = { POST })
    public RespBase<NotificationDingtalkUserIdExchageResult> exchageDingtalkUserId(
            @RequestBody @Validated NotificationDingtalkUserIdExchage model) {
        log.debug("called: model={}", model);
        RespBase<NotificationDingtalkUserIdExchageResult> resp = RespBase.create();
        resp.setData(notificationService.exchageDingtalkUserId(model));
        return resp;
    }
}
