/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.rengine.service.security.oauth2;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.common.constants.RengineConstants.API_LOGIN_OAUTH2_CALLBACK_ENDPOINT_BASE;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import com.wl4g.infra.common.remoting.uri.UriComponentsBuilder;
import com.wl4g.infra.common.web.WebUtils2;
import com.wl4g.infra.context.utils.web.WebUtils3;
import com.wl4g.rengine.common.entity.sys.IdentityProvider;
import com.wl4g.rengine.common.entity.sys.IdentityProvider.OidcConfig;
import com.wl4g.rengine.common.entity.sys.IdentityProvider.OidcConfig.UserInfoEndpoint;
import com.wl4g.rengine.service.IdentityProviderService;

import lombok.Getter;

/**
 * {@link MongoClientRegistrationRepository}
 * 
 * @author James Wong
 * @version 2023-02-23
 * @since v1.0.0
 * @see {@link org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository}
 */
@Getter
public final class MongoClientRegistrationRepository implements ClientRegistrationRepository {

    private final MongoTemplate mongoTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final IdentityProviderService identityProviderService;

    public MongoClientRegistrationRepository(MongoTemplate mongoTemplate, RedisTemplate<String, String> redisTemplate,
            IdentityProviderService identityProviderService) {
        this.mongoTemplate = notNullOf(mongoTemplate, "mongoTemplate");
        this.redisTemplate = notNullOf(redisTemplate, "redisTemplate");
        this.identityProviderService = notNullOf(identityProviderService, "identityProviderService");
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        final IdentityProvider idp = identityProviderService.getRegistrationId(registrationId);
        if (isNull(idp)) {
            throw new IllegalArgumentException(format("No found OIDC client registration configuration for %s", registrationId));
        }

        final String defaultOAuth2RedirectUri = buildDefaultOAuth2RedirectUri(registrationId);

        final OidcConfig oidcClientRegistration = (OidcConfig) idp.getDetails();
        final UserInfoEndpoint userInfoEndpoint = oidcClientRegistration.getUserInfoEndpoint();
        return ClientRegistration.withRegistrationId(registrationId)
                .clientId(oidcClientRegistration.getClientId())
                .clientSecret(oidcClientRegistration.getClientSecret())
                .clientAuthenticationMethod(
                        new ClientAuthenticationMethod(oidcClientRegistration.getClientAuthenticationMethod().name()))
                .authorizationGrantType(new AuthorizationGrantType(oidcClientRegistration.getAuthorizationGrantType().name()))
                .scope(oidcClientRegistration.getScopes())
                .authorizationUri(oidcClientRegistration.getAuthorizationUri())
                .tokenUri(oidcClientRegistration.getTokenUri())
                .userInfoUri(oidcClientRegistration.getUserInfoEndpoint().getUri())
                .userInfoAuthenticationMethod(new AuthenticationMethod(userInfoEndpoint.getAuthenticationMethod().name()))
                // Automatically downgrade to use the default value, thought to
                // pass the configuration check.
                .redirectUri(isBlank(oidcClientRegistration.getRedirectUri()) ? defaultOAuth2RedirectUri
                        : oidcClientRegistration.getRedirectUri())
                .userNameAttributeName(isBlank(userInfoEndpoint.getUserNameAttributeName()) ? DEFAULT_USER_NAME_ATTRIBUTE
                        : userInfoEndpoint.getUserNameAttributeName())
                .jwkSetUri(oidcClientRegistration.getJwkSetUri())
                .issuerUri(oidcClientRegistration.getIssuerUri())
                .providerConfigurationMetadata(oidcClientRegistration.getConfigurationMetadata())
                .clientName(format("%s_OIDC_CLIENT", registrationId))
                .build();
    }

    // for example:
    // http://rengine.xxx.io/api/login/oauth2/callback/default_oidc
    public static String buildDefaultOAuth2RedirectUri(String registrationId) {
        final var currentRequest = WebUtils3.currentServletRequest();
        if (nonNull(currentRequest)) {
            return UriComponentsBuilder.fromUriString(WebUtils2.getRFCBaseURI(currentRequest, true))
                    .path(API_LOGIN_OAUTH2_CALLBACK_ENDPOINT_BASE.substring(0,
                            API_LOGIN_OAUTH2_CALLBACK_ENDPOINT_BASE.indexOf("*")))
                    .path(registrationId)
                    .build()
                    .toUriString();
        }
        return null;
    }

    public static final String DEFAULT_USER_NAME_ATTRIBUTE = "preferred_username"; // such-as:keycloack

}
