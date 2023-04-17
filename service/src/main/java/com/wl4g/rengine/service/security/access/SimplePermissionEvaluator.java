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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.service.security.access;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeArrayToList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.rengine.service.security.user.AuthenticationService.DEFAULT_EXTRA_AUTHORITY_ATTRIBUTE;
import static com.wl4g.rengine.service.security.user.AuthenticationService.isDefaultSuperAdministrator;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.split;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

/**
 * Although it is easy to implement the evaluate of data permissions this type,
 * only the permission evaluate of functions is currently implemented.
 * 
 * @author James Wong
 * @date 2023-03-11
 * @since v1.0.0
 */
public class SimplePermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        // The root user has the highest super authority.
        // TODO using strategy config.
        if (isDefaultSuperAdministrator(authentication.getName())) {
            return true;
        }
        final var principal = authentication.getPrincipal();
        final var permissionStr = (permission instanceof String) ? (String) permission : null;

        if (principal instanceof OAuth2AuthenticatedPrincipal) {
            // Matchs for OIDC standard authorities.
            final var oauth2Principal = (OAuth2AuthenticatedPrincipal) principal;
            for (GrantedAuthority authority : safeList(oauth2Principal.getAuthorities())) {
                if (StringUtils.equals(authority.getAuthority(), permissionStr)) {
                    return true;
                }
            }
            // Matchs for extra OIDC attributes authorities.
            final var extraAuthorities = oauth2Principal.getAttribute(DEFAULT_EXTRA_AUTHORITY_ATTRIBUTE);
            if (nonNull(extraAuthorities) && (extraAuthorities instanceof String)) {
                for (String authority : safeArrayToList(split(extraAuthorities.toString(), ","))) {
                    if (StringUtils.equals(authority, permissionStr)) {
                        return true;
                    }
                }
            }
        }

        // Matchs for default login authorities.
        if (principal instanceof UserDetails) {
            final var user = (UserDetails) principal;
            for (GrantedAuthority authority : safeList(user.getAuthorities())) {
                if (StringUtils.equals(authority.getAuthority(), permissionStr)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        // The purpose of using targetDomainObject=this here is to prevent the
        // targetDomainObject from being not null to meet the hasPermission
        // agreement (generally it is not null, otherwise it should return
        // false).
        return hasPermission(authentication, this, permission);
    }

}
