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

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wl4g.infra.common.codec.CodecSource;
import com.wl4g.infra.common.crypto.asymmetric.spec.RSAKeyPairSpec;
import com.wl4g.infra.common.serialize.ProtostuffUtils;
import com.wl4g.rengine.service.UserService;
import com.wl4g.rengine.service.security.RengineWebSecurityProperties;

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
    private final RengineWebSecurityProperties config;
    private final RedisTemplate<String, byte[]> redisTemplate;
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UsernamePasswordAuthenticationProvider(RengineWebSecurityProperties config,
            RedisTemplate<String, byte[]> redisTemplate, UserDetailsService userDetailsService,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.config = notNullOf(config, "config");
        this.redisTemplate = notNullOf(redisTemplate, "redisTemplate");
        this.userDetailsService = notNullOf(userDetailsService, "userDetailsService");
        this.bCryptPasswordEncoder = notNullOf(bCryptPasswordEncoder, "bCryptPasswordEncoder");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String cipherPassword = (String) authentication.getCredentials();

        // Resolve cipher password to plain.
        final var secretCacheKey = UserService.buildSecretCacheKey(config, username);

        // Load pre-login applyed secret.
        final var secret = ProtostuffUtils.deserialize(redisTemplate.opsForValue().get(secretCacheKey), RSAKeyPairSpec.class);

        // Check for pre-login settings.
        if (isNull(secret)) {
            throw new BadCredentialsException("The login process timed out, please refresh the page and log in again.");
        }

        final var plainPasswordSource = UserService.DEFAULT_RSA_CRYPTOR.decrypt(secret.getKeySpec(),
                CodecSource.fromHex(cipherPassword));

        final UserDetails user = userDetailsService.loadUserByUsername(username);

        if (user == null || !bCryptPasswordEncoder.matches(plainPasswordSource.toString(), user.getPassword())) {
            throw new BadCredentialsException("Username or password is incorrect.");
        }
        log.info("Login success for : {}", user);

        return new UsernamePasswordAuthenticationToken(user, plainPasswordSource, user.getAuthorities());
    }

    // see:org.springframework.security.authentication.ProviderManager#authenticate()
    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }

}
