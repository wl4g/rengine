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
package com.wl4g.rengine.service.model.sys;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.rengine.common.entity.sys.Menu;
import com.wl4g.rengine.common.entity.sys.MenuRole;
import com.wl4g.rengine.common.entity.sys.Role;
import com.wl4g.rengine.common.entity.sys.User;
import com.wl4g.rengine.common.entity.sys.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link RoleSave}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class RoleSave extends Role {
    private static final long serialVersionUID = -8089363065684705215L;

    // Notice: The disable reading and writing of the name field in the swagger
    // document. (because the rule script does not have a name field)
    @Schema(hidden = true, accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE)
    @JsonIgnore
    @Override
    public List<UserRole> getUserRoles() {
        return null;
    }

    @JsonIgnore
    public void setUserRoles(List<UserRole> userRoles) {
        super.setUserRoles(userRoles);
    }

    @Schema(hidden = true, accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE)
    @JsonIgnore
    @Override
    public List<MenuRole> getMenuRoles() {
        return null;
    }

    @JsonIgnore
    public void setMenuRoles(List<MenuRole> menuRoles) {
        super.setMenuRoles(menuRoles);
    }

    @JsonIgnore
    @Override
    public List<Menu> getMenus() {
        // TODO Auto-generated method stub
        return super.getMenus();
    }

    @JsonIgnore
    @Override
    public List<User> getUsers() {
        // TODO Auto-generated method stub
        return super.getUsers();
    }

    @JsonIgnore
    @Override
    public void setMenus(List<Menu> menus) {
        // TODO Auto-generated method stub
        super.setMenus(menus);
    }

    @JsonIgnore
    @Override
    public void setUsers(List<User> users) {
        // TODO Auto-generated method stub
        super.setUsers(users);
    }

}
