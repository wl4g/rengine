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
package com.wl4g.rengine.apiserver.controller.sys;

import static com.wl4g.rengine.common.constants.RengineConstants.API_V1_USER_BASE_URI;
import static com.wl4g.rengine.common.constants.RengineConstants.API_V1_USER_SECURE_URI;
import static com.wl4g.rengine.common.constants.RengineConstants.API_V1_USER_USERINFO_URI;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.infra.common.web.rest.RespBase.RetCode;
import com.wl4g.rengine.common.entity.sys.Role;
import com.wl4g.rengine.common.entity.sys.User;
import com.wl4g.rengine.service.UserService;
import com.wl4g.rengine.service.model.sys.UserAssignRole;
import com.wl4g.rengine.service.model.sys.UserDelete;
import com.wl4g.rengine.service.model.sys.UserDeleteResult;
import com.wl4g.rengine.service.model.sys.UserQuery;
import com.wl4g.rengine.service.model.sys.UserSave;
import com.wl4g.rengine.service.model.sys.UserSaveResult;
import com.wl4g.rengine.service.security.authentication.SmartRedirectStrategy;
import com.wl4g.rengine.service.security.user.AuthenticationService;
import com.wl4g.rengine.service.security.user.AuthenticationService.SecureInfo;
import com.wl4g.rengine.service.security.user.AuthenticationService.UserAuthInfo;
import com.wl4g.rengine.service.security.user.MongoUserDetailsManager;

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
@RequestMapping(API_V1_USER_BASE_URI)
public class UserController {

    private @Autowired UserService userService;
    private @Autowired MongoUserDetailsManager userDetailsManager;
    private @Autowired AuthenticationService authenticationService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query useres.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    @PreAuthorize("hasPermission(#model,'arn:sys:user:read:v1')")
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
    @PreAuthorize("hasPermission(#model,'arn:sys:user:write:v1')")
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
    @PreAuthorize("hasPermission(#model,'arn:sys:user:delete:v1')")
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
    @PreAuthorize("hasPermission(#model,'arn:sys:user:changePassword:v1')")
    public RespBase<Boolean> changePassword(
            @NotBlank @RequestParam("oldPassword") String oldPassword,
            @NotBlank @RequestParam("newPassword") String newPassword) {
        userDetailsManager.changePassword(oldPassword, newPassword);
        return RespBase.<Boolean> create().withCode(RetCode.OK).withData(true);
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Apply secure secret")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { API_V1_USER_SECURE_URI }, produces = "application/json", method = { GET })
    public RespBase<SecureInfo> secure(@NotBlank @RequestParam("username") String username) {
        return RespBase.<SecureInfo> create().withCode(RetCode.OK).withData(authenticationService.applySecret(username));
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Load current authentication user info.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { API_V1_USER_USERINFO_URI }, produces = "application/json", method = { GET })
    @PreAuthorize("isAuthenticated()")
    public RespBase<UserAuthInfo> userInfo(HttpServletRequest request) {
        return RespBase.<UserAuthInfo> create()
                .withCode(RetCode.OK)
                .withStatus(SmartRedirectStrategy.DEFAULT_AUTHORIZED_STATUS)
                .withData(userService.currentUserInfo());
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query roles by userId.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "roles" }, produces = "application/json", method = { GET })
    @PreAuthorize("hasPermission(#model,'arn:sys:user:role:read:v1')")
    public RespBase<List<Role>> roles(@RequestParam("userId") List<Long> userIds) {
        log.debug("called: userIds={}", userIds);
        return RespBase.<List<Role>> create().withData(userService.findRolesByUserIds(userIds));
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Assign roles by userId.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "assign/roles" }, produces = "application/json", method = { POST })
    @PreAuthorize("hasPermission(#model,'arn:sys:user:role:write:v1')")
    public RespBase<List<Long>> assignRoles(@Validated @RequestBody UserAssignRole model) {
        log.debug("called: model={}", model);
        return RespBase.<List<Long>> create().withData(userService.assignRoles(model));
    }

}
