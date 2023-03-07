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
package com.wl4g.rengine.common.entity.sys;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.infra.common.validation.EnumValue;
import com.wl4g.rengine.common.entity.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link IdentityProvider}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class IdentityProvider extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String name;

    private ProviderDetailsBase details;

    // Ignore organization getter/setter.
    @JsonIgnore
    @Override
    public String getOrgCode() {
        return null;
    }

    @JsonIgnore
    @Override
    public void setOrgCode(String orgCode) {
    }

    // 1.多态参见:https://swagger.io/docs/specification/data-models/inheritance-and-polymorphism/
    // 2.对于swagger3注解,父类必须是抽象的，否则swagger3页面请求参数schemas展开后会以父类名重复展示3个.
    @Schema(oneOf = { OidcConfig.class, Saml2Config.class }, discriminatorProperty = "type")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
    @JsonSubTypes({ @Type(value = OidcConfig.class, name = "OIDC"), @Type(value = Saml2Config.class, name = "SAML2") })
    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static abstract class ProviderDetailsBase implements Serializable {
        private static final long serialVersionUID = 570;

        @Schema(name = "type", implementation = IdPKind.class)
        @JsonProperty(value = "type", access = Access.WRITE_ONLY)
        @NotNull
        private @NotBlank @EnumValue(enumCls = IdPKind.class) String type;
    }

    /**
     * {@link org.springframework.security.oauth2.client.registration.ClientRegistration}
     */
    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class OidcConfig extends ProviderDetailsBase {
        private static final long serialVersionUID = 570;
        private String registrationId;
        private String clientId;
        private String clientSecret;
        // private ClientAuthenticationMethod clientAuthenticationMethod;
        private AuthorizationGrantType authorizationGrantType;
        private String redirectUri;
        private @Default Set<String> scopes = Collections.emptySet();
        // More details details.
        private String authorizationUri;
        private String tokenUri;
        private @Default UserInfoEndpoint userInfoEndpoint = new UserInfoEndpoint();
        private String jwkSetUri;
        private String issuerUri;
        private @Default Map<String, Object> configurationMetadata = new HashMap<>(2);

        public ClientAuthenticationMethod getClientAuthenticationMethod() {
            if (AuthorizationGrantType.authorization_code.equals(authorizationGrantType) && !!isBlank(clientSecret)) {
                return ClientAuthenticationMethod.NONE;
            }
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        }

        public OidcConfig validate() {
            Assert2.notNull(this.authorizationGrantType, "authorizationGrantType cannot be null");
            if (AuthorizationGrantType.client_credentials.equals(this.authorizationGrantType)) {
                this.validateClientCredentialsGrantType();
            } else if (AuthorizationGrantType.password.equals(this.authorizationGrantType)) {
                this.validatePasswordGrantType();
            } else if (AuthorizationGrantType.implicit.equals(this.authorizationGrantType)) {
                this.validateImplicitGrantType();
            } else if (AuthorizationGrantType.authorization_code.equals(this.authorizationGrantType)) {
                this.validateAuthorizationCodeGrantType();
            }
            this.validateScopes();
            return this;
        }

        private void validateAuthorizationCodeGrantType() {
            Assert2.isTrue(AuthorizationGrantType.authorization_code.equals(this.authorizationGrantType),
                    () -> "authorizationGrantType must be " + AuthorizationGrantType.authorization_code.name());
            Assert2.hasText(registrationId, "registrationId cannot be empty");
            Assert2.hasText(clientId, "clientId cannot be empty");
            Assert2.hasText(redirectUri, "redirectUri cannot be empty");
            Assert2.hasText(getAuthorizationUri(), "authorizationUri cannot be empty");
            Assert2.hasText(getTokenUri(), "tokenUri cannot be empty");
        }

        private void validateImplicitGrantType() {
            Assert2.isTrue(AuthorizationGrantType.implicit.equals(authorizationGrantType),
                    () -> "authorizationGrantType must be " + AuthorizationGrantType.implicit.name());
            Assert2.hasText(registrationId, "registrationId cannot be empty");
            Assert2.hasText(clientId, "clientId cannot be empty");
            Assert2.hasText(redirectUri, "redirectUri cannot be empty");
            Assert2.hasText(getAuthorizationUri(), "authorizationUri cannot be empty");
        }

        private void validateClientCredentialsGrantType() {
            Assert2.isTrue(AuthorizationGrantType.client_credentials.equals(authorizationGrantType),
                    () -> "authorizationGrantType must be " + AuthorizationGrantType.client_credentials.name());
            Assert2.hasText(registrationId, "registrationId cannot be empty");
            Assert2.hasText(clientId, "clientId cannot be empty");
            Assert2.hasText(getTokenUri(), "tokenUri cannot be empty");
        }

        private void validatePasswordGrantType() {
            Assert2.isTrue(AuthorizationGrantType.password.equals(authorizationGrantType),
                    () -> "authorizationGrantType must be " + AuthorizationGrantType.password.name());
            Assert2.hasText(registrationId, "registrationId cannot be empty");
            Assert2.hasText(clientId, "clientId cannot be empty");
            Assert2.hasText(getTokenUri(), "tokenUri cannot be empty");
        }

        private void validateScopes() {
            if (scopes == null) {
                return;
            }
            for (String scope : scopes) {
                Assert2.isTrue(validateScope(scope), "scope \"" + scope + "\" contains invalid characters");
            }
        }

        private static boolean validateScope(String scope) {
            return scope == null || scope.chars()
                    .allMatch((c) -> withinTheRangeOf(c, 0x21, 0x21) || withinTheRangeOf(c, 0x23, 0x5B)
                            || withinTheRangeOf(c, 0x5D, 0x7E));
        }

        private static boolean withinTheRangeOf(int c, int min, int max) {
            return c >= min && c <= max;
        }

        @Getter
        @Setter
        @SuperBuilder
        @ToString
        @NoArgsConstructor
        public static class UserInfoEndpoint implements Serializable {
            private static final long serialVersionUID = 570;
            private String uri;
            private @Default AuthenticationMethod authenticationMethod = AuthenticationMethod.header;
            private String userNameAttributeName;
        }

        public static enum AuthorizationGrantType {
            authorization_code,

            @Deprecated
            implicit,

            refresh_token,

            client_credentials,

            password,

            jwt_bearer
        }

        public static enum AuthenticationMethod {
            header, form, query
        }

        public static enum ClientAuthenticationMethod {
            @Deprecated
            BASIC,

            CLIENT_SECRET_BASIC,

            POST,

            CLIENT_SECRET_POST,

            CLIENT_SECRET_JWT,

            PRIVATE_KEY_JWT,

            NONE
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class Saml2Config extends ProviderDetailsBase {
        private static final long serialVersionUID = 1L;
        private @NotBlank String spMetadataUrl;
    }

    public static enum IdPKind {
        OIDC, SAML2
    }

}
