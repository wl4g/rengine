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

import static com.wl4g.infra.common.reflect.ReflectionUtils2.findField;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.getField;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.infra.common.web.WebUtils2.ResponseType.isRespJSON;
import static com.wl4g.rengine.service.UserService.DEFAULT_LOAD_USERINFO_URI;
import static com.wl4g.rengine.service.UserService.DEFAULT_USER_BASE_URI_V1;
import static com.wl4g.rengine.service.security.AuthenticationUtils.currentUserInfo;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.wl4g.infra.common.remoting.uri.UriComponentsBuilder;
import com.wl4g.infra.common.web.WebUtils2;
import com.wl4g.infra.common.web.rest.RespBase;

import lombok.AllArgsConstructor;
import lombok.CustomLog;

/**
 * {@link SmartRedirectStrategy}
 * 
 * @author James Wong
 * @version 2023-02-24
 * @since v1.0.0
 */
@CustomLog
@AllArgsConstructor
public class SmartRedirectStrategy extends DefaultRedirectStrategy {

    private final boolean failureRedirectStrategy;

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        String redirectUrl = url;
        if (isBlank(URI.create(url).getScheme())) {
            redirectUrl = UriComponentsBuilder.fromUriString(WebUtils2.getRFCBaseURI(request, true))
                    .path(url)
                    .encode()
                    .build()
                    .toUriString();
        }
        if (isJSONResponse(request)) {
            if (failureRedirectStrategy) { // Authentication failure
                // see:org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler#saveException()
                final AuthenticationException authException = (AuthenticationException) request
                        .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
                WebUtils2.writeJson(response, toJSONString(RespBase.create()
                        .withStatus(DEFAULT_UNAUTHENTICATED_STATUS)
                        .withMessage(nonNull(authException) ? authException.getMessage() : "Unknown authentication failed")
                        .forMap()
                        .andPut(DEFAULT_REDIRECT_URI_KEY, redirectUrl)));
            } else { // Authentication success
                WebUtils2.writeJson(response,
                        toJSONString(RespBase.create()
                                .withStatus(DEFAULT_AUTHENTICATED_STATUS)
                                .withMessage("Login successful")
                                .forMap()
                                .andPut(DEFAULT_REDIRECT_URI_KEY, redirectUrl)
                                .andPut(DEFAULT_USERINFO_KEY, currentUserInfo())));
            }
        } else {
            response.sendRedirect(redirectUrl);
        }
    }

    /**
     * Determine is the JSON interactive strategy, or get it from
     * session(flexible API).
     *
     * @param request
     * @return
     */
    protected boolean isJSONResponse(HttpServletRequest request) {
        final boolean isRespJson = isRespJSON(request);
        log.debug("Using response json : {}", isRespJson);
        return isRespJson;
    }

    @SuppressWarnings("rawtypes")
    public static void configurer(AbstractAuthenticationFilterConfigurer configurer) {
        // Setup to success smart redirect strategy.
        final var successHandler = (SavedRequestAwareAuthenticationSuccessHandler) getField(
                findField(configurer.getClass(), "successHandler", AuthenticationSuccessHandler.class), configurer, true);
        successHandler.setDefaultTargetUrl(DEFAULT_SUCCESS_URI);
        successHandler.setAlwaysUseDefaultTargetUrl(false);
        successHandler.setRedirectStrategy(new SmartRedirectStrategy(false));

        //// @formatter:off
        // Init failure handler with default.
        //final Method updateAuthenticationDefaultsMethod = findMethod(configurer.getClass(), "updateAuthenticationDefaults");
        //makeAccessible(updateAuthenticationDefaultsMethod);
        //invokeMethod(updateAuthenticationDefaultsMethod, configurer);
        //
        //// Setup to failure smart redirect strategy.
        //final var failureHandler = (SimpleUrlAuthenticationFailureHandler) getField(
        //        findField(configurer.getClass(), "failureHandler", AuthenticationFailureHandler.class), configurer, true);
        //failureHandler.setRedirectStrategy(new SmartRedirectStrategy(true));
        //// @formatter:on

        // see:org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer#updateAuthenticationDefaults()
        final var failureHandler = new SimpleUrlAuthenticationFailureHandler("/login?error");
        failureHandler.setRedirectStrategy(new SmartRedirectStrategy(true));
        configurer.failureHandler(failureHandler);
    }

    public static final String DEFAULT_UNAUTHENTICATED_STATUS = "unauthenticated";
    public static final String DEFAULT_AUTHENTICATED_STATUS = "authenticated";
    public static final String DEFAULT_REDIRECT_URI_KEY = "redirect_uri";
    public static final String DEFAULT_USERINFO_KEY = "userinfo";

    public static final String DEFAULT_SUCCESS_URI = DEFAULT_USER_BASE_URI_V1.concat(DEFAULT_LOAD_USERINFO_URI);
}
