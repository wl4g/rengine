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
package com.wl4g.rengine.service.security;

import javax.validation.constraints.NotNull;

import com.wl4g.rengine.common.constants.RengineConstants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link RengineWebSecurityProperties}
 * 
 * @author James Wong
 * @date 2022-08-28
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
public class RengineWebSecurityProperties {

    Boolean ignoreSwaggerAuth = true;

    @NotNull
    UserAuthenticationProperties user = new UserAuthenticationProperties();

    @NotNull
    OAuth2OidcProperties oidc = new OAuth2OidcProperties();

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class UserAuthenticationProperties {
        private String secretCachePrefix = RengineConstants.CACHE_LOGIN_SECRET_PREFIX;
        private Long secretCacheExpireSeconds = 60L;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class OAuth2OidcProperties {
        private String oauth2ClientCachePrefix = RengineConstants.CACHE_LOGIN_OIDC_PREFIX;
        private Long oauth2ClientCacheExpireSeconds = 1800L;
    }

}
