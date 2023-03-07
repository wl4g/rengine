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
package com.wl4g.rengine.service.security.user;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.util.Objects.isNull;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.CustomLog;

/**
 * {@link UsernamePasswordAuthenticationProvider}
 * 
 * @author James Wong
 * @version 2023-02-22
 * @since v1.0.0
 */
@CustomLog
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private final MongoUserDetailsManager userDetailsManager;
    private final AuthenticationService authenticationService;

    public UsernamePasswordAuthenticationProvider(MongoUserDetailsManager userDetailsManager,
            AuthenticationService authenticationService) {
        this.userDetailsManager = notNullOf(userDetailsManager, "userDetailsManager");
        this.authenticationService = notNullOf(authenticationService, "authenticationService");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        // final String secureCode = authentication.getName(); // TODO
        final String cipherPassword = (String) authentication.getCredentials();

        final var plainPassword = authenticationService.resolveCipher(username, cipherPassword, true);

        final UserDetails user = userDetailsManager.loadUserByUsername(username);

        if (isNull(user) || !userDetailsManager.getPasswordEncoder().matches(plainPassword.toString(), user.getPassword())) {
            throw new BadCredentialsException("Username or password is incorrect.");
        }
        log.info("Login successful for : {}", user);

        return new UsernamePasswordAuthenticationToken(user, plainPassword, user.getAuthorities());
    }

    // see:org.springframework.security.authentication.ProviderManager#authenticate()
    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }

}
