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

import static com.wl4g.rengine.common.constants.RengineConstants.API_LOGIN_OAUTH2_CALLBACK_ENDPOINT_BASE;
import static com.wl4g.rengine.common.constants.RengineConstants.API_LOGIN_OAUTH2_ENDPOINT_BASE;
import static com.wl4g.rengine.common.constants.RengineConstants.API_LOGIN_PAGE_PATH;
import static com.wl4g.rengine.common.constants.RengineConstants.API_LOGIN_PASSWORD_ENDPOINT;
import static com.wl4g.rengine.common.constants.RengineConstants.API_V1_USER_BASE_URI;
import static com.wl4g.rengine.common.constants.RengineConstants.API_V1_USER_SECURE_URI;
import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.wl4g.infra.context.utils.SpringContextHolder;
import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.service.IdentityProviderService;
import com.wl4g.rengine.service.security.authentication.SimpleKubernetesOperatorAuthenticationFilter;
import com.wl4g.rengine.service.security.authentication.SmartHttpSessionIdResolver;
import com.wl4g.rengine.service.security.authentication.SmartRedirectStrategy;
import com.wl4g.rengine.service.security.oauth2.MongoClientRegistrationRepository;
import com.wl4g.rengine.service.security.oauth2.MongoOAuth2AuthorizedClientService;
import com.wl4g.rengine.service.security.user.AuthenticationService;
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
@ConditionalOnClass({ WebSecurityCustomizer.class, GlobalMethodSecurityConfiguration.class })
@EnableWebSecurity
public class RengineWebSecurityConfiguration implements WebSecurityCustomizer {

    @Value("${springdoc.api-docs.path:/v3/api-docs}")
    String springdocApiDocsPath;

    @Override
    public void customize(WebSecurity web) {
        web.ignoring()
                .antMatchers("/hello/**")
                .antMatchers("/public/**")
                .antMatchers("/actuator/**")
                .antMatchers(API_V1_USER_BASE_URI + API_V1_USER_SECURE_URI);

        final var config = SpringContextHolder.getBean(RengineWebSecurityProperties.class);
        if (config.getIgnoreSwaggerAuth()) {
            web.ignoring()
                    .antMatchers(springdocApiDocsPath)
                    .antMatchers(springdocApiDocsPath.concat("/**"))
                    .antMatchers("/swagger-ui/**");
        }
    }

    @Bean
    @ConfigurationProperties(prefix = RengineConstants.CONF_PREFIX + ".security")
    public RengineWebSecurityProperties rengineWebSecurityProperties() {
        return new RengineWebSecurityProperties();
    }

    @Bean
    public SmartHttpSessionIdResolver smartHttpSessionIdResolver() {
        return new SmartHttpSessionIdResolver();
    }

    @Bean
    public SecurityFilterChain customSecurityFilterChain(
            HttpSecurity http,
            UsernamePasswordAuthenticationProvider userAuthcProvider) throws Exception {
        // http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(null);
        return http.csrf().disable().cors().disable()
        // @formatter:off
                //.addFilterBefore(smartSessionIdFilter, BasicAuthenticationFilter.class)
                //.addFilterBefore(smartSessionIdFilter, UsernamePasswordAuthenticationFilter.class)
                // Valid for all types of authentication filters.
                .addFilterBefore(SimpleKubernetesOperatorAuthenticationFilter.DEFAULT, AbstractPreAuthenticatedProcessingFilter.class)
                // @formatter:on
                .authenticationProvider(userAuthcProvider)
                .authorizeRequests()
                // Login path without checking authentication.
                //
                // Notice: The frontand and backend are deploy separately, this
                // setup will be meaningless. In fact, the jump to the login
                // page is controlled by the custom authenticationEntryPoint.
                //
                // .antMatchers("/login**")
                // .permitAll()
                // Any other path requests are check for authentication.
                .anyRequest()
                .authenticated()
                .and()
                // Enable the form of static password login.
                // @formatter:off
                // .formLogin()
                // .loginProcessingUrl(API_LOGIN_PASSWORD_ENDPOINT)
                // .defaultSuccessUrl(SmartRedirectStrategy.DEFAULT_SUCCESS_URI)
                // .and()
                // @formatter:on
                .formLogin(customizer -> {
                    customizer.loginProcessingUrl(API_LOGIN_PASSWORD_ENDPOINT)
                            .defaultSuccessUrl(SmartRedirectStrategy.DEFAULT_LOGIN_SUCCESS_URI);
                    // Setup to success and failure smart redirect strategy.
                    SmartRedirectStrategy.configure(customizer);
                })
                .exceptionHandling()
                // see:org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter
                // see:org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
                .authenticationEntryPoint((request, response, authEx) -> {
                    SmartRedirectStrategy.defaultInstanceOfUnauth.doSendRedirect(request, response, API_LOGIN_PAGE_PATH, false,
                            authEx);
                })
                .accessDeniedHandler((request, response, authEx) -> {
                    SmartRedirectStrategy.defaultInstanceOfAccessDenied.doSendRedirect(request, response, null, false,
                            new AccessDeniedException(format(
                                    "Sorry, you do not have permission to access this resource yet, you may need to contact the administrator to authorize this resource!"),
                                    authEx));
                })
                .and()
                .logout(customizer -> {
                    final var logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
                    logoutSuccessHandler.setRedirectStrategy(SmartRedirectStrategy.defaultInstanceOfUnauth);
                    customizer.logoutSuccessHandler(logoutSuccessHandler);
                })
                // @formatter:off
                // Enable the OAuth2 of authorization login.
                // .oauth2Login()
                // .authorizationEndpoint()
                // .baseUri(API_LOGIN_OAUTH2_ENDPOINT_BASE)
                // .and()
                // .loginProcessingUrl(API_LOGIN_OAUTH2_CALLBACK_ENDPOINT_BASE)
                // .defaultSuccessUrl(SmartRedirectStrategy.DEFAULT_SUCCESS_URI,
                // false)
                // .and()
                // @formatter:on
                .oauth2Login(customizer -> {
                    // If the custom start oauth2 redirect root path.
                    customizer.authorizationEndpoint()
                            // The base URI of the start OAuth2 authenticating
                            // request. The default as: /oauth2/authorization
                            // see:org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
                            .baseUri(API_LOGIN_OAUTH2_ENDPOINT_BASE)
                            .and()
                            // see:org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter#attemptAuthentication()
                            .loginProcessingUrl(API_LOGIN_OAUTH2_CALLBACK_ENDPOINT_BASE)
                            // Login to successful redirection uri.
                            // alwaysUse=false, It means that if the protected
                            // URL is accessed before the certification, it will
                            // be redirected to the URL.
                            .defaultSuccessUrl(SmartRedirectStrategy.DEFAULT_LOGIN_SUCCESS_URI, false);
                    // Setup to success and failure smart redirect strategy.
                    SmartRedirectStrategy.configure(customizer);
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
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(BCryptVersion.$2Y, 13);
    }

    @Bean
    public AuthenticationService authenticationService(
            RengineWebSecurityProperties config,
            RedisTemplate<String, byte[]> redisTemplate,
            MongoTemplate mongoTemplate) {
        return new AuthenticationService(config, redisTemplate, mongoTemplate);
    }

    @Bean
    public MongoUserDetailsManager mongoUserDetailsManager(
            RengineWebSecurityProperties config,
            BCryptPasswordEncoder passwordEncoder,
            AuthenticationService authenticationService,
            @Autowired(required = false) AuthenticationManager authenticationManager) {
        return new MongoUserDetailsManager(config, passwordEncoder, authenticationService, authenticationManager);
    }

    @Bean
    public UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider(
            MongoUserDetailsManager mongoUserDetailsManager,
            AuthenticationService authenticationService) {
        return new UsernamePasswordAuthenticationProvider(mongoUserDetailsManager, authenticationService);
    }

}