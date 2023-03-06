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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.crypto.asymmetric.RSACryptor;
import com.wl4g.rengine.common.entity.User;
import com.wl4g.rengine.service.model.UserDelete;
import com.wl4g.rengine.service.model.UserDeleteResult;
import com.wl4g.rengine.service.model.UserQuery;
import com.wl4g.rengine.service.model.UserSave;
import com.wl4g.rengine.service.model.UserSaveResult;
import com.wl4g.rengine.service.security.AuthenticationUtils.UserAuthenticationInfo;
import com.wl4g.rengine.service.security.RengineWebSecurityProperties;

/**
 * {@link UserService}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
public interface UserService {

    PageHolder<User> query(@NotNull UserQuery model);

    UserSaveResult save(@NotNull UserSave model);

    UserDeleteResult delete(@NotNull UserDelete model);

    boolean changePassword(@NotBlank String oldPassword, @NotBlank String newPassword);

    String applySecret(@NotBlank String username);

    UserAuthenticationInfo userInfo();

    public static String buildSecretCacheKey(RengineWebSecurityProperties config, String username) {
        return config.getAuth().getSecretCachePrefix().concat(username);
    }

    public static final RSACryptor DEFAULT_RSA_CRYPTOR = new RSACryptor();
    public static final String DEFAULT_USER_BASE_URI_V1 = "/v1/user";
    public static final String DEFAULT_APPLY_SECRET_URI = "/applysecret";
    public static final String DEFAULT_USERINFO_URI = "/userinfo";

}
