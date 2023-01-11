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

import org.springdoc.core.SpringDocUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.infra.common.lang.StringUtils2;
import com.wl4g.rengine.service.swagger.SpringDocOASProperties.CustomOASConfig;
import com.wl4g.rengine.service.swagger.SpringDocOASProperties.ProjectConfig;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * {@link SpringDocOASAutoConfiguration}
 * 
 * @author James Wong
 * @version 2022-08-30
 * @since v1.0.0
 */
@Configuration
@ConditionalOnClass(OpenAPI.class)
public class SpringDocOASAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "springdoc")
    public SpringDocOASProperties springDocOASProperties(
            @Value("${spring.application.name}") String appName,
            ObjectProvider<BuildProperties> buildProperties) {
        return new SpringDocOASProperties();
    }

    @Bean
    public OpenAPI customOpenAPI(SpringDocOASProperties config) {
        // Replace with classes configuration.
        config.getCustomOASConfig()
                .getReplaceClassConfig()
                .forEach((source, target) -> SpringDocUtils.getConfig().replaceWithClass(source, target));

        // Create custom OpenAPI.
        final CustomOASConfig customConfig = config.getCustomOASConfig();
        final ProjectConfig project = customConfig.getProject();
        final OpenAPI openAPI = new OpenAPI();

        final Info info = new Info().title(transformDocTitle(project.getName()).concat(" APIs"))
                .version(project.getVersion())
                .description(project.getDescription())
                .termsOfService(project.getTermsOfService())
                .license(customConfig.getLicense())
                .contact(customConfig.getContact());

        final Components securitySchemes = new Components();
        customConfig.getSecuritySchemes()
                .entrySet()
                .stream()
                .filter(e -> e.getValue().isEnabled())
                .forEach(e -> securitySchemes.addSecuritySchemes(e.getKey(), e.getValue()));

        return openAPI.info(info).components(securitySchemes);
    }

    public static String transformDocTitle(String appName) {
        return StringUtils2.replaceGroups(appName, DEFAULT_TITLE_REGEX, gs -> {
            if (gs.getIndex() == 0 || gs.getIndex() == 3) {
                return gs.getGroupStr().toUpperCase();
            } else if (gs.getIndex() == 2) {
                return " ";
            }
            return gs.getGroupStr();
        }).trim();
    }

    public static final String DEFAULT_TITLE_REGEX = "([a-zA-Z0-9])([a-zA-Z0-9]+)([-_]*)([a-zA-Z0-9]?)([a-zA-Z0-9]*)";

}
