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
package com.wl4g.rengine.common.entity;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.infra.common.validation.EnumValue;
import com.wl4g.rengine.common.entity.IdentityProvider.OAuth2Config;
import com.wl4g.rengine.common.entity.IdentityProvider.Saml2Config;

import io.swagger.v3.oas.annotations.media.Schema;
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
// 1.多态参见:https://swagger.io/docs/specification/data-models/inheritance-and-polymorphism/
// 2.对于swagger3注解,父类必须是抽象的，否则swagger3页面请求参数schemas展开后会以父类名重复展示3个.
@Schema(oneOf = { OAuth2Config.class, Saml2Config.class }, discriminatorProperty = "@kind")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@kind", visible = true)
@JsonSubTypes({ @Type(value = OAuth2Config.class, name = "oauth2"), @Type(value = Saml2Config.class, name = "saml2") })
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
public abstract class IdentityProvider extends BaseBean {
    private static final long serialVersionUID = 1L;

    @Schema(name = "@kind", implementation = IdPKind.class)
    @JsonProperty(value = "@kind")
    private @NotBlank @EnumValue(enumCls = IdPKind.class) String kind;

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class OAuth2Config extends IdentityProvider {
        private static final long serialVersionUID = 1L;
        private @NotBlank String authorizationUrl;
        private @NotBlank String tokenUrl;
        private @NotBlank String userinfoUrl;
        private @NotBlank String clientId;
        private @NotBlank String clientSecret;
        private @NotBlank String userId;
        private @NotBlank String userName;
        private @NotBlank String scopes;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class Saml2Config extends IdentityProvider {
        private static final long serialVersionUID = 1L;
        private @NotBlank String spMetadataUrl;
    }

    public static enum IdPKind {
        oauth2, saml2
    }

}
