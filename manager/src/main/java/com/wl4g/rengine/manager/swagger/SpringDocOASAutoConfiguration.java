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
package com.wl4g.rengine.manager.swagger;

import org.springdoc.core.SpringDocUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.rengine.manager.swagger.SpringDocOASProperties.CustomOASConfig;
import com.wl4g.rengine.manager.swagger.SpringDocOASProperties.ProjectConfig;

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
        config.getCustomOASConfig().getReplaceClassConfig().forEach(
                (source, target) -> SpringDocUtils.getConfig().replaceWithClass(source, target));

        // Create custom OpenAPI.
        CustomOASConfig customConfig = config.getCustomOASConfig();
        ProjectConfig project = customConfig.getProject();
        OpenAPI openAPI = new OpenAPI();
        Info info = new Info()
                .title(project.getName().substring(0, 1).toUpperCase().concat(project.getName().substring(1)).concat(" APIs"))
                .version(project.getVersion())
                .description(project.getDescription())
                .termsOfService(project.getTermsOfService())
                .license(customConfig.getLicense())
                .contact(customConfig.getContact());

        Components securitySchemes = new Components();
        customConfig.getSecuritySchemes().entrySet().stream().filter(e -> e.getValue().isEnabled()).forEach(
                e -> securitySchemes.addSecuritySchemes(e.getKey(), e.getValue()));

        return openAPI.info(info).components(securitySchemes);
    }

}
