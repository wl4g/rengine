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
package com.wl4g.rengine.service.security.authentication;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.lang.EnvironmentUtil.getStringProperty;
import static com.wl4g.rengine.common.constants.RengineConstants.API_V1_KUBERNETES_OPERATOR_BASE_URI;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.security.MessageDigest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.hash.Hashing;

import lombok.CustomLog;

/**
 * {@link SimpleKubernetesOperatorAuthenticationFilter}
 * 
 * @author James Wong
 * @version 2023-03-27
 * @since v1.0.0
 */
@CustomLog
public class SimpleKubernetesOperatorAuthenticationFilter extends OncePerRequestFilter {

    public static final RequestMatcher DEFAULT_MATCHER = new AntPathRequestMatcher(
            API_V1_KUBERNETES_OPERATOR_BASE_URI.concat("/**"));

    public static final String DEFAULT_OPERATOR_USER = "kubernetes-operator";

    public static final String OPERATOR_TOKEN_KEY = "operator.token";
    public static final String OPERATOR_TOKEN_VALUE = getStringProperty(OPERATOR_TOKEN_KEY);
    public static final String SIGN_PARAM = "s";
    public static final String TIME_PARAM = "t";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isKubernetesOperatorRequest(request)) {
            if (isBlank(OPERATOR_TOKEN_VALUE)) {
                log.error(format(
                        "The processing operator resource APIs request authentication failure, because operator token is must configure '%s' missing. - %s",
                        OPERATOR_TOKEN_KEY, request.getRequestURL().toString()));
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Authentication failed");
                return;
            }

            final String signature = getSignature(request);
            final String timestamp = getTimestamp(request);
            if (isValid(signature, timestamp)) {
                SecurityContextHolder.getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(DEFAULT_OPERATOR_USER, null, emptyList()));
            } else {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid signature");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isKubernetesOperatorRequest(HttpServletRequest request) {
        return DEFAULT_MATCHER.matches(request);
    }

    private String getSignature(HttpServletRequest request) {
        return request.getParameter(SIGN_PARAM);
    }

    private String getTimestamp(HttpServletRequest request) {
        return request.getParameter(TIME_PARAM);
    }

    @VisibleForTesting
    static boolean isValid(String signature, String timestamp) {
        if (isBlank(signature) || isBlank(timestamp)) {
            return false;
        }
        return MessageDigest.isEqual(Hashing.sha256().hashBytes(OPERATOR_TOKEN_VALUE.concat(timestamp).getBytes(UTF_8)).asBytes(),
                signature.getBytes());
    }

    public static final SimpleKubernetesOperatorAuthenticationFilter DEFAULT = new SimpleKubernetesOperatorAuthenticationFilter();

}
