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

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.infra.common.web.rest.RespBase.RetCode;
import com.wl4g.rengine.common.entity.User;
import com.wl4g.rengine.service.UserService;
import com.wl4g.rengine.service.model.UserDelete;
import com.wl4g.rengine.service.model.UserDeleteResult;
import com.wl4g.rengine.service.model.UserQuery;
import com.wl4g.rengine.service.model.UserSave;
import com.wl4g.rengine.service.model.UserSaveResult;
import com.wl4g.rengine.service.security.AuthenticationUtils.UserAuthenticationInfo;
import com.wl4g.rengine.service.security.authentication.SmartRedirectStrategy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link UserController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "UserAPI", description = "The User management API")
@Slf4j
@RestController
@RequestMapping(UserService.DEFAULT_USER_BASE_URI_V1)
public class UserController {

    private @Autowired UserService userService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query useres.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    public RespBase<PageHolder<User>> query(@Validated UserQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<User>> resp = RespBase.create();
        resp.setData(userService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save user.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<UserSaveResult> save(@Validated @RequestBody UserSave model) {
        log.debug("called: model={}", model);
        RespBase<UserSaveResult> resp = RespBase.create();
        resp.setData(userService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete user.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    public RespBase<UserDeleteResult> delete(@Validated @RequestBody UserDelete model) {
        log.debug("called: model={}", model);
        RespBase<UserDeleteResult> resp = RespBase.create();
        resp.setData(userService.delete(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Change password.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "changePassword" }, produces = "application/json", method = { POST })
    public RespBase<Boolean> changePassword(
            @NotBlank @RequestParam("oldPassword") String oldPassword,
            @NotBlank @RequestParam("newPassword") String newPassword) {
        RespBase<Boolean> resp = RespBase.create();
        resp.setData(userService.changePassword(oldPassword, newPassword));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Apply secret")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { UserService.DEFAULT_APPLY_SECRET_URI }, produces = "application/json", method = { GET })
    public RespBase<String> applySecret(@NotBlank @RequestParam("username") String username) {
        return RespBase.<String> create().withCode(RetCode.OK).withData(userService.applySecret(username));
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Load current authentication user info.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { UserService.DEFAULT_USERINFO_URI }, produces = "application/json", method = { GET })
    public RespBase<UserAuthenticationInfo> userInfo(HttpServletRequest request) {
        return RespBase.<UserAuthenticationInfo> create()
                .withCode(RetCode.OK)
                .withStatus(SmartRedirectStrategy.DEFAULT_AUTHENTICATED_STATUS)
                .withData(userService.userInfo());
    }

}
