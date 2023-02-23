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
package com.wl4g.rengine.service.security;

import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_SECURITY_OAUTH2_CALLBACK_BASE_URI;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_SECURITY_OAUTH2_ENDPOINT_BASE_URI;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_SECURITY_PASSWORD_ENDPOINT_URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.service.IdentityProviderService;
import com.wl4g.rengine.service.security.authentication.SmartRedirectStrategy;
import com.wl4g.rengine.service.security.oauth2.MongoClientRegistrationRepository;
import com.wl4g.rengine.service.security.oauth2.MongoOAuth2AuthorizedClientService;
import com.wl4g.rengine.service.security.user.MongoUserDetailsManager;
import com.wl4g.rengine.service.security.user.UsernamePasswordAuthenticationProvider;

/**
 * {@link RengineWebSecurityConfiguration}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 * @see https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
 */
@Configuration
@ConditionalOnDefaultWebSecurity
// @EnableWebSecurity
public class RengineWebSecurityConfiguration implements WebSecurityCustomizer {

    @Override
    public void customize(WebSecurity web) {
        web.ignoring()
                .antMatchers("/hello/**", "/public/**", /* "/swagger-ui/**", */ "/actuator/**");
    }

    @Bean
    @ConfigurationProperties(prefix = RengineConstants.CONF_PREFIX + ".security")
    public RengineWebSecurityProperties rengineWebSecurityProperties() {
        return new RengineWebSecurityProperties();
    }

    @Bean
    public SecurityFilterChain customSecurityFilterChain(
            HttpSecurity http,
            UsernamePasswordAuthenticationProvider authenticationProvider) throws Exception {
        // http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(null);
        return http.csrf()
                .disable()
                .cors()
                .disable()
                .authenticationProvider(authenticationProvider)
                .authorizeRequests()
                // Login path without checking authentication.
                .antMatchers("/login**")
                .permitAll()
                // Any other path requests are check for authentication.
                .anyRequest()
                .authenticated()
                .and()
                // Enable the form of static password login.
                .formLogin()
                // .loginProcessingUrl(DEFAULT_SECURITY_PASSWORD_ENDPOINT_URI)
                // .defaultSuccessUrl(SmartRedirectStrategy.DEFAULT_SUCCESS_URI)
                .and()
                // @formatter:off
                .formLogin(customizer -> {
                    customizer.loginProcessingUrl(DEFAULT_SECURITY_PASSWORD_ENDPOINT_URI)
                            .defaultSuccessUrl(SmartRedirectStrategy.DEFAULT_SUCCESS_URI);
                    // Setup to success and failure smart redirect strategy.
                    SmartRedirectStrategy.configurer(customizer);
                })
                // @formatter:on
                // Enable the OAuth2 of authorization login.
                // .oauth2Login()
                // .authorizationEndpoint()
                // .baseUri(DEFAULT_SECURITY_OAUTH2_ENDPOINT_BASE_URI)
                // .and()
                // .loginProcessingUrl(DEFAULT_SECURITY_OAUTH2_CALLBACK_BASE_URI)
                // .defaultSuccessUrl(SmartRedirectStrategy.DEFAULT_SUCCESS_URI,
                // false)
                // .and()
                // @formatter:off
                .oauth2Login(customizer -> {
                    // If the custom start oauth2 redirect root path.
                    customizer.authorizationEndpoint()
                            // The base URI of the start OAuth2 authenticating
                            // request. The default as: /oauth2/authorization
                            // see:org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
                            .baseUri(DEFAULT_SECURITY_OAUTH2_ENDPOINT_BASE_URI)
                            .and()
                            // see:org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter#attemptAuthentication()
                            .loginProcessingUrl(DEFAULT_SECURITY_OAUTH2_CALLBACK_BASE_URI)
                            // Login to successful redirection uri.
                            // alwaysUse=false, It means that if the protected
                            // URL is accessed before the certification, it will
                            // be redirected to the URL.
                            .defaultSuccessUrl(SmartRedirectStrategy.DEFAULT_SUCCESS_URI, false);
                    // Setup to success and failure smart redirect strategy.
                    SmartRedirectStrategy.configurer(customizer);
                })
        // @formatter:on
                .build();
    }

    @Bean
    public ClientRegistrationRepository mongoClientRegistrationRepository(
            MongoTemplate mongoTemplate,
            RedisTemplate<String, String> redisTemplate,
            IdentityProviderService identityProviderService) {
        return new MongoClientRegistrationRepository(mongoTemplate, redisTemplate, identityProviderService);
    }

    @Bean
    public OAuth2AuthorizedClientService mongoOAuth2AuthorizedClientService(
            RengineWebSecurityProperties config,
            MongoClientRegistrationRepository clientRegistrationRepository) {
        return new MongoOAuth2AuthorizedClientService(config, clientRegistrationRepository);
    }

    @Bean
    public UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider(
            @Autowired(required = false) AuthenticationManager authenticationManager,
            MongoTemplate mongoTemplate) {
        final var bCryptPasswordEncoder = new BCryptPasswordEncoder(BCryptVersion.$2Y, 13);
        final var userDetailsService = new MongoUserDetailsManager(authenticationManager, mongoTemplate.getDb(),
                MongoCollectionDefinition.SYS_USERS.getName());
        return new UsernamePasswordAuthenticationProvider(userDetailsService, bCryptPasswordEncoder);
    }

}