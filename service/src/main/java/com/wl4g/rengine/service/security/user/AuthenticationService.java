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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_MENUS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_USERS;
import static com.wl4g.rengine.common.util.BsonAggregateFilters.USER_MENU_LOOKUP_FILTERS;
import static com.wl4g.rengine.common.util.BsonAggregateFilters.USER_ROLE_MENU_LOOKUP_FILTERS;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.oidc.AddressStandardClaim;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.wl4g.infra.common.codec.CodecSource;
import com.wl4g.infra.common.crypto.asymmetric.RSACryptor;
import com.wl4g.infra.common.crypto.asymmetric.spec.KeyPairSpec;
import com.wl4g.infra.common.serialize.ProtostuffUtils;
import com.wl4g.rengine.common.entity.sys.Menu;
import com.wl4g.rengine.common.entity.sys.User;
import com.wl4g.rengine.common.util.BsonEntitySerializers;
import com.wl4g.rengine.service.model.BaseQuery;
import com.wl4g.rengine.service.mongo.QueryHolder;
import com.wl4g.rengine.service.security.RengineWebSecurityProperties;

import lombok.AllArgsConstructor;
import lombok.Builder.Default;
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
    private final MongoCollection<Document> userCollection;
    private final MongoCollection<Document> menuCollection;

    public AuthenticationService(RengineWebSecurityProperties config, RedisTemplate<String, byte[]> redisTemplate,
            MongoTemplate mongoTemplate) {
        this.config = notNullOf(config, "config");
        this.redisTemplate = notNullOf(redisTemplate, "redisTemplate");
        notNullOf(mongoTemplate, "mongoTemplate");
        this.userCollection = mongoTemplate.getDb().getCollection(SYS_USERS.getName());
        this.menuCollection = mongoTemplate.getDb().getCollection(SYS_MENUS.getName());
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

    public UserAuthInfo currentUserInfo() {
        final UserAuthInfo info = new UserAuthInfo();

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Load current authentication info : {}", authentication);

        if (nonNull(authentication)) {
            info.setName(authentication.getName());
            info.setAuthenticated(authentication.isAuthenticated());

            if (authentication.getDetails() instanceof WebAuthenticationDetails) {
                info.setSessionId(((WebAuthenticationDetails) authentication.getDetails()).getSessionId());
                info.setRemoteAddress(((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress());
            }

            final Object principal = authentication.getPrincipal();
            if (principal instanceof OAuth2AuthenticatedPrincipal) {
                final var oauth2Principal = (OAuth2AuthenticatedPrincipal) principal;
                info.setAttributes(oauth2Principal.getAttributes());
                if (principal instanceof OidcUser) {
                    final OidcUser oidcUser = ((OidcUser) principal);
                    info.setAuthenticatedAt(oidcUser.getAuthenticatedAt());
                    info.setExpiresAt(oidcUser.getExpiresAt());
                    final OidcUserInfo oidcUserInfo = oidcUser.getUserInfo();
                    info.setUserinfo(UserBasicInfo.builder()
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
                makeAuthorities(authentication, info, oauth2Principal.getAuthorities());
            }
            if (principal instanceof UserDetails) {
                final UserDetails userDetails = (UserDetails) principal;
                makeAuthorities(authentication, info, userDetails.getAuthorities());
            }
        }

        return info;
    }

    private void makeAuthorities(
            Authentication authentication,
            UserAuthInfo info,
            Collection<? extends GrantedAuthority> authorities) {

        info.getAuthorities().setIsSuperAdministrator(isDefaultSuperAdministrator(authentication.getName()));

        // Add roles and permissions.
        for (GrantedAuthority auth : safeList(authorities)) {
            if (auth instanceof SimpleGrantedAuthority) {
                info.getAuthorities().getRoles().add(auth.getAuthority());
            }
            // if (auth instanceof SimplePermissionGrantedAuthority) {
            // info.getAuthorities().getPermissions().add(auth.getAuthority());
            // }
        }

        // Add menus by user.
        //// @formatter:off
        //final User user = loadUserRoleMenusByUsername(authentication.getName());
        //final List<Menu> menus = safeList(user.getUserRoles()).stream()
        //        .flatMap(ur -> safeList(ur.getRoles()).stream())
        //        .flatMap(r -> safeList(r.getMenuRoles()).stream())
        //        .flatMap(mr -> safeList(mr.getMenus()).stream())
        //        .distinct()
        //        .collect(toSet());
        //// @formatter:on

        final List<Menu> menus = loadMenusByUsername(authentication.getName());
        info.getAuthorities()
                .getMenus()
                .addAll(safeList(menus).stream()
                        .map(m -> UserMenuInfo.builder()
                                .id(m.getId())
                                .nameEn(m.getNameEn())
                                .nameZh(m.getNameZh())
                                .type(m.getType())
                                .parentId(m.getParentId())
                                .level(m.getLevel())
                                .permissions(m.getPermissions())
                                .pageLocation(m.getPageLocation())
                                .routePath(m.getRoutePath())
                                .renderTarget(m.getRenderTarget())
                                .parentRoutePath(m.getParentRoutePath())
                                .classify(m.getClassify())
                                .sort(m.getSort())
                                .icon(m.getIcon())
                                .build())
                        .collect(toList()));
    }

    // @formatter:off
    // 深度嵌套子查询根据 username 查询 user 下 roles 及 menus.
    public User findUserRoleMenusByUsername(@NotBlank String username) {
        hasTextOf(username, "username");

        final var aggregates = new ArrayList<Bson>(2);
        aggregates.add(Aggregates.match(Filters.eq("username", username)));
        USER_ROLE_MENU_LOOKUP_FILTERS.stream().forEach(rs -> aggregates.add(rs.asDocument()));

        try (var cursor = userCollection.aggregate(aggregates)
                .map(userDoc -> BsonEntitySerializers.fromDocument(userDoc, com.wl4g.rengine.common.entity.sys.User.class))
                .cursor();) {
            if (!cursor.hasNext()) {
                throw new UsernameNotFoundException(format("The user %s not found", username));
            }
            final var user = cursor.next();
            if (cursor.hasNext()) {
                throw new IllegalStateException(format("The ambiguous users found, multiple names are %s", username));
            }
            return user;
        }
    }
    // @formatter:on

    public List<Menu> loadMenusByUsername(@NotBlank String username) {
        List<Menu> menus = null;
        if (isDefaultSuperAdministrator(username)) {
            // Has all permissions menus.
            menus = findMenusByUsernameWithSuperAdmin(username);
        } else {
            menus = findMenusByUsername(username);
        }
        return menus;
    }

    // 普通用户连接查询根据 username 查询 user 下的 roles 下的 menus.
    @SuppressWarnings("unchecked")
    public List<Menu> findMenusByUsername(@NotBlank String username) {
        hasTextOf(username, "username");

        final var aggregates = new ArrayList<Bson>(2);
        aggregates.add(Aggregates.match(Filters.eq("username", username)));
        USER_MENU_LOOKUP_FILTERS.stream().forEach(rs -> aggregates.add(rs.asDocument()));

        try (var cursor = userCollection.aggregate(aggregates)
                .map(menuDoc -> BsonEntitySerializers.fromDocument(menuDoc, Menu.class))
                .cursor();) {
            return IteratorUtils.toList(cursor);
        }
    }

    // 超级管理员用户连接查询根据 username 查询 user 下的 roles 下的 menus.
    @SuppressWarnings("unchecked")
    public List<Menu> findMenusByUsernameWithSuperAdmin(@NotBlank String username) {
        hasTextOf(username, "username");
        try (var cursor = menuCollection
                .find(QueryHolder.baseCriteria(BaseQuery.<Menu> builder().enable(true).build()).getCriteriaObject())
                .map(menuDoc -> BsonEntitySerializers.fromDocument(menuDoc, Menu.class))
                .cursor();) {
            return IteratorUtils.toList(cursor);
        }
    }

    public static boolean isDefaultSuperAdministrator(String username) {
        return StringUtils.equals(username, "root");
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
        private @Default UserAuthorityInfo authorities = UserAuthorityInfo.builder().build();
        private @Default UserBasicInfo userinfo = UserBasicInfo.builder().build();
        private @Default AddressInfo address = AddressInfo.builder().build();
        private @Default Map<String, Object> attributes = new HashMap<>();
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class UserAuthorityInfo {
        private @Default Boolean isSuperAdministrator = false;
        private @Default List<String> roles = new ArrayList<>();
        private @Default List<String> permissions = new ArrayList<>();
        private @Default List<UserMenuInfo> menus = new ArrayList<>();
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class UserMenuInfo {
        private Long id;
        private String nameEn;
        private String nameZh;
        private Integer type;
        private Long parentId;
        private Integer level;
        private List<String> permissions;
        private String pageLocation;
        private String routePath;
        private String renderTarget;
        private String parentRoutePath;
        private String classify;
        private Integer sort;
        private String icon;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class UserBasicInfo {
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

    public static final String DEFAULT_EXTRA_AUTHORITY_ATTRIBUTE = "extra_authorities";

}
