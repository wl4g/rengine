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
package com.wl4g.rengine.service.security;

import static java.util.Objects.nonNull;

import java.time.Instant;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.oidc.AddressStandardClaim;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import lombok.CustomLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link AuthenticationUtils}
 * 
 * @author James Wong
 * @version 2023-02-24
 * @since v1.0.0
 */
@CustomLog
public abstract class AuthenticationUtils {

    public static UserAuthenticationInfo currentUserInfo() {
        final UserAuthenticationInfo info = new UserAuthenticationInfo();

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Load user user info : {}", authentication);

        if (nonNull(authentication)) {
            info.setName(authentication.getName());
            info.setAuthenticated(authentication.isAuthenticated());

            if (authentication.getDetails() instanceof WebAuthenticationDetails) {
                info.setSessionId(((WebAuthenticationDetails) authentication.getDetails()).getSessionId());
                info.setRemoteAddress(((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
            }

            final Object principal = authentication.getPrincipal();
            if (principal instanceof OAuth2AuthenticatedPrincipal) {
                info.setAttributes(((OAuth2AuthenticatedPrincipal) principal).getAttributes());
                if (principal instanceof OidcUser) {
                    final OidcUser oidcUser = ((OidcUser) principal);
                    info.setAuthenticatedAt(oidcUser.getAuthenticatedAt());
                    info.setExpiresAt(oidcUser.getExpiresAt());
                    final OidcUserInfo oidcUserInfo = oidcUser.getUserInfo();
                    info.setUserinfo(UserInfo.builder()
                            .locale(oidcUserInfo.getLocale())
                            .fullName(oidcUserInfo.getFullName())
                            .zoneInfo(oidcUserInfo.getZoneInfo())
                            .email(oidcUserInfo.getEmail())
                            .profile(oidcUserInfo.getProfile())
                            .subject(oidcUserInfo.getSubject())
                            .familyName(oidcUserInfo.getFamilyName())
                            .middleName(oidcUserInfo.getMiddleName())
                            .nickName(oidcUserInfo.getNickName())
                            .preferredUsername(oidcUserInfo.getPreferredUsername())
                            .picture(oidcUserInfo.getPicture())
                            .website(oidcUserInfo.getWebsite())
                            .emailVerified(oidcUserInfo.getEmailVerified())
                            .gender(oidcUserInfo.getGender())
                            .birthdate(oidcUserInfo.getBirthdate())
                            .phoneNumber(oidcUserInfo.getPhoneNumber())
                            .phoneNumberVerified(oidcUserInfo.getPhoneNumberVerified())
                            .updatedAt(oidcUserInfo.getUpdatedAt())
                            .givenName(oidcUserInfo.getGivenName())
                            .build());
                    final AddressStandardClaim oidcAddress = oidcUserInfo.getAddress();
                    info.setAddress(AddressInfo.builder()
                            .formatted(oidcAddress.getFormatted())
                            .streetAddress(oidcAddress.getStreetAddress())
                            .region(oidcAddress.getRegion())
                            .postalCode(oidcAddress.getPostalCode())
                            .locality(oidcAddress.getLocality())
                            .country(oidcAddress.getCountry())
                            .build());
                }
            }
            if (principal instanceof UserDetails) {
                // final UserDetails userDetails = (UserDetails) principal;
                // userDetails.getAuthorities();
            }
        }

        return info;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class UserAuthenticationInfo {
        private String name;
        private String sessionId;
        private Boolean authenticated;
        private Instant authenticatedAt;
        private Instant expiresAt;
        private String remoteAddress;
        private UserInfo userinfo;
        private AddressInfo address;
        private Map<String, Object> attributes;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class UserInfo {
        private String locale;
        private String fullName;
        private String zoneInfo;
        private String email;
        private String profile;
        private String subject;
        private String familyName;
        private String middleName;
        private String nickName;
        private String preferredUsername;
        private String picture;
        private String website;
        private Boolean emailVerified;
        private String gender;
        private String birthdate;
        private String phoneNumber;
        private Boolean phoneNumberVerified;
        private Instant updatedAt;
        private String givenName;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class AddressInfo {
        private String formatted;
        private String streetAddress;
        private String locality;
        private String region;
        private String postalCode;
        private String country;
    }

}
