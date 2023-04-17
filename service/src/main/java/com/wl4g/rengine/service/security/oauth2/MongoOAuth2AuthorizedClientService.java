/*
 * Copyright 2002-2020 the original author or authors.
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

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.concurrent.TimeUnit;

import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import com.wl4g.rengine.service.security.RengineWebSecurityProperties;

/**
 * 
 * A JDBC implementation of an {@link OAuth2AuthorizedClientService} that uses a
 * {@link JdbcOperations} for {@link OAuth2AuthorizedClient} persistence.
 *
 * <p>
 * <b>NOTE:</b> This {@code OAuth2AuthorizedClientService} depends on the table
 * definition described in
 * "classpath:org/springframework/security/oauth2/client/oauth2-client-schema.sql"
 * and therefore MUST be defined in the database schema.
 * 
 * @author James Wong
 * @date 2023-02-23
 * @since v1.0.0
 * @see {@link org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService}
 */
public class MongoOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {

    private final RengineWebSecurityProperties config;
    private final MongoClientRegistrationRepository clientRegistrationRepository;

    public MongoOAuth2AuthorizedClientService(RengineWebSecurityProperties config,
            ClientRegistrationRepository clientRegistrationRepository) {
        this.config = notNullOf(config, "config");
        this.clientRegistrationRepository = notNullOf(clientRegistrationRepository, "clientRegistrationRepository");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        return (T) parseJSON(clientRegistrationRepository.getRedisTemplate()
                .opsForValue()
                .get(buildOAuth2ClientCacheKey(config.getOidc().getOauth2ClientCachePrefix(), clientRegistrationId,
                        principalName)),
                OAuth2AuthorizedClient.class);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        final String registrationId = authorizedClient.getClientRegistration().getRegistrationId();
        final AuthenticatedPrincipal _principal = (AuthenticatedPrincipal) principal.getPrincipal(); // DefaultOidcUser,DefaultOAuth2User

        clientRegistrationRepository.getRedisTemplate()
                .opsForValue()
                .set(buildOAuth2ClientCacheKey(config.getOidc().getOauth2ClientCachePrefix(), registrationId,
                        _principal.getName()), toJSONString(authorizedClient),
                        config.getOidc().getOauth2ClientCacheExpireSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        clientRegistrationRepository.getRedisTemplate()
                .delete(buildOAuth2ClientCacheKey(config.getOidc().getOauth2ClientCachePrefix(), clientRegistrationId,
                        principalName));
    }

    public static String buildOAuth2ClientCacheKey(String prefix, String clientRegistrationId, String principalName) {
        hasTextOf(clientRegistrationId, "clientRegistrationId");
        hasTextOf(principalName, "principalName");
        prefix = isBlank(prefix) ? "" : prefix.concat(":");
        return prefix.concat(OAuth2AuthorizedClient.class.getSimpleName())
                .concat(":")
                .concat(clientRegistrationId)
                .concat(":")
                .concat(principalName);
    }

}
