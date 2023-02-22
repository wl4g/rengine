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
package com.wl4g.rengine.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.web.SecurityFilterChain;

import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;

import lombok.CustomLog;

/**
 * {@link DefaultWebSecurityConfigure}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 * @see https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
 */
@Configuration
@ConditionalOnDefaultWebSecurity
// @EnableWebSecurity
public class DefaultWebSecurityConfigure implements WebSecurityCustomizer {

    @Override
    public void customize(WebSecurity web) {
        web.ignoring().antMatchers("/hello/**", "/public/**", "/swagger-ui/**", "/actuator/**");
    }

    @Bean
    public SecurityFilterChain customSecurityFilterChain(
            HttpSecurity http,
            CustomAuthenticationProvider customAuthenticationProvider) throws Exception {
        http.csrf()
                .disable()
                .cors()
                .disable()
                .authorizeRequests()
                // .antMatchers("/", "/login**", "/callback/", "/oauth/**")
                .antMatchers("/", "/login**")
                .permitAll()
                .and()
                .formLogin()
                .and()
                .oauth2Login();
        // If the custom start oauth2 redirect root path.
        // .authorizationEndpoint()
        // see:org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
        // .baseUri("/myroot/oauth2/authorization")
        http.authenticationProvider(customAuthenticationProvider);
        // http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(null);
        return http.build();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider(
            @Autowired(required = false) AuthenticationManager authenticationManager,
            MongoTemplate mongoTemplate) {
        final var bCryptPasswordEncoder = new BCryptPasswordEncoder(BCryptVersion.$2Y, 13);
        final var userDetailsService = new MongoUserDetailsManager(authenticationManager, mongoTemplate.getDb(),
                MongoCollectionDefinition.SYS_USERS.getName());
        return new CustomAuthenticationProvider(userDetailsService, bCryptPasswordEncoder);
    }

    @CustomLog
    public static class CustomAuthenticationProvider implements AuthenticationProvider {
        private UserDetailsService userDetailsService;
        private BCryptPasswordEncoder bCryptPasswordEncoder;

        public CustomAuthenticationProvider(UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
            this.userDetailsService = userDetailsService;
            this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        }

        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            final String username = authentication.getName();
            final String password = (String) authentication.getCredentials();

            final UserDetails user = userDetailsService.loadUserByUsername(username);

            if (user == null || !bCryptPasswordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("Username or password is incorrect.");
            }
            log.info("Login success for : {}", user);

            return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        }

        @Override
        public boolean supports(Class<?> aClass) {
            return true;
        }
    }

}