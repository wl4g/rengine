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

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.sys.Role;
import com.wl4g.rengine.service.RoleService;
import com.wl4g.rengine.service.model.sys.RoleAssignMenu;
import com.wl4g.rengine.service.model.sys.RoleAssignUser;
import com.wl4g.rengine.service.model.sys.RoleDelete;
import com.wl4g.rengine.service.model.sys.RoleDeleteResult;
import com.wl4g.rengine.service.model.sys.RoleQuery;
import com.wl4g.rengine.service.model.sys.RoleSave;
import com.wl4g.rengine.service.model.sys.RoleSaveResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link RoleController}
 * 
 * @author James Wong
 * @date 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "RoleAPI", description = "The Role management API")
@Slf4j
@RestController
@RequestMapping("/v1/role")
public class RoleController {

    private @Autowired RoleService roleService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query roles.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    @PreAuthorize("hasPermission(#model,'arn:sys:role:read:v1')")
    public RespBase<PageHolder<Role>> query(@Validated RoleQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<Role>> resp = RespBase.create();
        resp.setData(roleService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save role.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    @PreAuthorize("hasPermission(#model,'arn:sys:role:write:v1')")
    public RespBase<RoleSaveResult> save(@Validated @RequestBody RoleSave model) {
        log.debug("called: model={}", model);
        RespBase<RoleSaveResult> resp = RespBase.create();
        resp.setData(roleService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete role.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    @PreAuthorize("hasPermission(#model,'arn:sys:role:delete:v1')")
    public RespBase<RoleDeleteResult> delete(@Validated @RequestBody RoleDelete model) {
        log.debug("called: model={}", model);
        RespBase<RoleDeleteResult> resp = RespBase.create();
        resp.setData(roleService.delete(model));
        return resp;
    }

    //// @formatter:off
    //// @SecurityRequirement(name = "default_oauth")
    //@Operation(description = "Find users by roleIds.")
    //@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    //@RequestMapping(path = { "users" }, produces = "application/json", method = { GET })
    //@PreAuthorize("hasPermission(#model,'arn:sys:role:user:read:v1')")
    //public RespBase<List<User>> users(@RequestParam("roleIds") List<Long> roleIds) {
    //    log.debug("called: roleIds={}", roleIds);
    //    return RespBase.<List<User>> create().withData(roleService.findUsersByRoleIds(roleIds));
    //}
    //// @formatter:on

    //// @formatter:off
    //// @SecurityRequirement(name = "default_oauth")
    //@Operation(description = "Find menus by roleIds.")
    //@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    //@RequestMapping(path = { "menus" }, produces = "application/json", method = { GET })
    //@PreAuthorize("hasPermission(#model,'arn:sys:role:menu:read:v1')")
    //public RespBase<List<Menu>> menus(@RequestParam("roleIds") List<Long> roleIds) {
    //    log.debug("called: roleIds={}", roleIds);
    //    return RespBase.<List<Menu>> create().withData(roleService.findMenusByRoleIds(roleIds));
    //}
    //// @formatter:on

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Assign users by roleId.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "assign/users" }, produces = "application/json", method = { POST })
    @PreAuthorize("hasPermission(#model,'arn:sys:role:user:write:v1')")
    public RespBase<List<Long>> assignUsers(@Validated @RequestBody RoleAssignUser model) {
        log.debug("called: model={}", model);
        return RespBase.<List<Long>> create().withData(roleService.assignUsers(model));
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Assign menus by roleId.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "assign/menus" }, produces = "application/json", method = { POST })
    @PreAuthorize("hasPermission(#model,'arn:sys:role:menu:write:v1')")
    public RespBase<List<Long>> assignMenus(@Validated @RequestBody RoleAssignMenu model) {
        log.debug("called: model={}", model);
        return RespBase.<List<Long>> create().withData(roleService.assignMenus(model));
    }

}
