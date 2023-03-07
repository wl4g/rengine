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
package com.wl4g.rengine.service.security.user;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.oidc.AddressStandardClaim;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.wl4g.infra.common.codec.CodecSource;
import com.wl4g.infra.common.crypto.asymmetric.RSACryptor;
import com.wl4g.infra.common.crypto.asymmetric.spec.KeyPairSpec;
import com.wl4g.infra.common.serialize.ProtostuffUtils;
import com.wl4g.rengine.service.security.RengineWebSecurityProperties;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link AuthenticationService}
 * 
 * @author James Wong
 * @version 2023-02-24
 * @since v1.0.0
 */
@Getter
@CustomLog
public class AuthenticationService {
    private final RSACryptor defaultRsaCryptor = new RSACryptor();
    private final RengineWebSecurityProperties config;
    private final RedisTemplate<String, byte[]> redisTemplate;

    public AuthenticationService(RengineWebSecurityProperties config, RedisTemplate<String, byte[]> redisTemplate) {
        this.config = notNullOf(config, "config");
        this.redisTemplate = notNullOf(redisTemplate, "redisTemplate");
    }

    public SecureInfo applySecret(@NotBlank String username) {
        final ValueOperations<String, byte[]> opsForValue = redisTemplate.opsForValue();
        final var secureCacheKey = buildSecretCacheKey(username);

        // @formatter:off
        final var existing = ProtostuffUtils.deserialize(opsForValue.get(secureCacheKey), SecureStoreInfo.class);
        if (nonNull(existing)) {
            return new SecureInfo(existing.getSc(), existing.getKeyPair().getPubHexString());
        }
        // @formatter:on

        final var secureStore = defaultRsaCryptor.generateKeyPair();
        final var secureCode = randomAlphanumeric(16);
        opsForValue.set(secureCacheKey, ProtostuffUtils.serialize(new SecureStoreInfo(secureStore, secureCode)),
                Duration.ofSeconds(config.getUser().getSecretCacheExpireSeconds()));

        return new SecureInfo(secureCode, secureStore.getPubHexString());
    }

    public CodecSource resolveCipher(
            @NotBlank String username,
            /* @NotBlank String secureCode, */
            @NotBlank String ciphertext,
            boolean cleanup) {

        hasTextOf(username, "username");
        // hasTextOf(secureCode, "secureCode");
        hasTextOf(ciphertext, "ciphertext");

        // Resolve cipher text(password) to plain.
        final var secureCacheKey = buildSecretCacheKey(username);

        // Load pre-login applyed secret.
        final var secureStore = ProtostuffUtils.deserialize(redisTemplate.opsForValue().get(secureCacheKey),
                SecureStoreInfo.class);

        // Check for existing secret.
        if (isNull(secureStore)) {
            throw new BadCredentialsException("The process timed out, please refresh the page try again.");
        }

        try {
            return defaultRsaCryptor.decrypt(secureStore.getKeyPair().getKeySpec(), CodecSource.fromHex(ciphertext));
        } finally {
            if (cleanup) {
                redisTemplate.delete(secureCacheKey);
            }
        }
    }

    private String buildSecretCacheKey(String username) {
        return config.getUser().getSecretCachePrefix().concat(username);
    }

    public static UserAuthInfo currentUserInfo() {
        final UserAuthInfo info = new UserAuthInfo();

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
    public static class UserAuthInfo {
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

    @Getter
    @Setter
    @ToString(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecureStoreInfo implements Serializable {
        private static final long serialVersionUID = 6807770559239079849L;
        private KeyPairSpec keyPair;
        private String sc;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecureInfo implements Serializable {
        private static final long serialVersionUID = 6807770559239079841L;
        private String sc; // random secure store code (key).
        private String st; // pubilc key hex.
    }

}
