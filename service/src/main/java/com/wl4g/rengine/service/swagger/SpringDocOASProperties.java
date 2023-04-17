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
package com.wl4g.rengine.service.swagger;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * {@link SpringDocOASProperties}
 * 
 * @author James Wong
 * @date 2022-08-29
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SpringDocOASProperties {

    private CustomOASConfig customOASConfig = new CustomOASConfig();

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class CustomOASConfig {
        private ProjectConfig project = new ProjectConfig();
        private License license = new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0");
        private Contact contact = new Contact();
        private Map<Class<?>, Class<?>> replaceClassConfig = new HashMap<>();
        private Map<String, CustomSecurityScheme> securitySchemes = new HashMap<String, CustomSecurityScheme>() {
            private static final long serialVersionUID = 1L;
            {
                SecurityScheme defaultScheme = new CustomSecurityScheme().type(SecurityScheme.Type.OAUTH2)
                        .in(SecurityScheme.In.HEADER)
                        .flows(new OAuthFlows().authorizationCode(new OAuthFlow()
                                .authorizationUrl("http://localhost:8080/realms/master/protocol/openid-connect/user")
                                .tokenUrl("http://localhost:8080/realms/master/protocol/openid-connect/token")
                                .refreshUrl("http://localhost:8080/realms/master/protocol/openid-connect/refresh")
                                .scopes(new Scopes() {
                                    private static final long serialVersionUID = 1L;
                                    {
                                        put("read", "read scope");
                                        put("write", "write scope");
                                    }
                                })));
                put("default_oauth", (CustomSecurityScheme) defaultScheme);
            }
        };
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class ProjectConfig {
        private String name = "MySpringApplication";
        private String description = "My Spring Application";
        private String version = "0.0.1";
        private String encoding = "UTF-8";
        private String termsOfService = "http://swagger.io/terms/";
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class CustomSecurityScheme extends SecurityScheme {
        private boolean enabled = true;
    }

}