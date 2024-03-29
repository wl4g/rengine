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
package com.wl4g.rengine.service;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.entity.sys.Role;
import com.wl4g.rengine.common.entity.sys.User;
import com.wl4g.rengine.service.model.sys.UserAssignRole;
import com.wl4g.rengine.service.model.sys.UserDelete;
import com.wl4g.rengine.service.model.sys.UserDeleteResult;
import com.wl4g.rengine.service.model.sys.UserQuery;
import com.wl4g.rengine.service.model.sys.UserSave;
import com.wl4g.rengine.service.model.sys.UserSaveResult;
import com.wl4g.rengine.service.security.user.AuthenticationService.UserAuthInfo;

/**
 * {@link UserService}
 * 
 * @author James Wong
 * @date 2022-08-29
 * @since v1.0.0
 */
public interface UserService {

    PageHolder<User> query(@NotNull UserQuery model);

    UserSaveResult save(@NotNull UserSave model);

    UserDeleteResult delete(@NotNull UserDelete model);

    UserAuthInfo currentUserInfo();

    List<Role> findRolesByUserIds(@NotEmpty List<Long> userIds);

    List<Long> assignRoles(UserAssignRole model);

}
