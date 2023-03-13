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
import static com.wl4g.infra.common.web.WebUtils2.ResponseType.isRespJSON;
import static com.wl4g.rengine.common.constants.RengineConstants.API_LOGIN_PAGE_PATH;
import static com.wl4g.rengine.common.constants.RengineConstants.API_V1_USER_BASE_URI;
import static com.wl4g.rengine.common.constants.RengineConstants.API_V1_USER_USERINFO_URI;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
import com.wl4g.infra.common.web.rest.RespBase.RetCode;
import com.wl4g.infra.context.utils.SpringContextHolder;
import com.wl4g.rengine.service.security.user.AuthenticationService;

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

    private final boolean isFailure;
    private final RetCode retCode;

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        // see:org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler#saveException()
        final AuthenticationException authEx = (AuthenticationException) request
                .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        doSendRedirect(request, response, url, true, authEx);
    }

    public void doSendRedirect(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotBlank String uri,
            boolean contextPath,
            @Nullable RuntimeException authEx) throws IOException {

        String redirectUri = uri;
        if (!isBlank(uri) && isBlank(URI.create(uri).getScheme())) {
            redirectUri = UriComponentsBuilder.fromUriString(WebUtils2.getRFCBaseURI(request, contextPath))
                    .path(uri)
                    // .encode()
                    .build()
                    .toUriString();
        }

        if (isJsonResponse(request)) {
            if (isFailure) { // Authentication failure, accessDenied or logout.
                final var respBody = RespBase.create()
                        .withCode(retCode)
                        .withStatus(DEFAULT_UNAUTHORIZED_STATUS)
                        .withMessage(nonNull(authEx) ? authEx.getMessage() : "Authentication failed")
                        .forMap();
                if (!isBlank(redirectUri)) {
                    respBody.put(DEFAULT_REDIRECT_URI_KEY, redirectUri);
                }
                WebUtils2.writeJson(response, respBody.withParent().asJson());
            } else { // Authentication success etc.
                final var respBody = RespBase.create()
                        .withCode(retCode)
                        .withStatus(DEFAULT_AUTHORIZED_STATUS)
                        .withMessage("Authentication successful")
                        .forMap()
                        .andPut(DEFAULT_USERINFO_KEY, getAuthenticationService().currentUserInfo());
                if (!isBlank(redirectUri)) {
                    respBody.put(DEFAULT_REDIRECT_URI_KEY, redirectUri);
                }
                WebUtils2.writeJson(response, respBody.withParent().asJson());
            }
        } else {
            if (isBlank(redirectUri)) {
                throw new IllegalStateException(format("The redirect uri is required."));
            }
            response.sendRedirect(redirectUri);
        }
    }

    /**
     * Determine is the JSON interactive strategy, or get it from
     * session(flexible API).
     *
     * @param request
     * @return
     */
    public static boolean isJsonResponse(HttpServletRequest request) {
        final boolean isRespJson = isRespJSON(request);
        log.debug("Using response json : {}", isRespJson);
        return isRespJson;
    }

    @SuppressWarnings("rawtypes")
    public static void configure(AbstractAuthenticationFilterConfigurer configurer) {
        // Setup to success smart redirect strategy.
        final var successHandler = (SavedRequestAwareAuthenticationSuccessHandler) getField(
                findField(configurer.getClass(), "successHandler", AuthenticationSuccessHandler.class), configurer, true);
        successHandler.setDefaultTargetUrl(DEFAULT_LOGIN_SUCCESS_URI);
        successHandler.setAlwaysUseDefaultTargetUrl(false);
        successHandler.setRedirectStrategy(defaultInstanceOfAuthed);

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
        final var failureHandler = new SimpleUrlAuthenticationFailureHandler(API_LOGIN_PAGE_PATH + "?error");
        failureHandler.setRedirectStrategy(defaultInstanceOfUnauth);
        configurer.failureHandler(failureHandler);
    }

    public static AuthenticationService getAuthenticationService() {
        return SpringContextHolder.getBean(AuthenticationService.class);
    }

    public static final SmartRedirectStrategy defaultInstanceOfAuthed = new SmartRedirectStrategy(false, RetCode.OK);
    public static final SmartRedirectStrategy defaultInstanceOfUnauth = new SmartRedirectStrategy(true, RetCode.UNAUTHC);
    public static final SmartRedirectStrategy defaultInstanceOfAccessDenied = new SmartRedirectStrategy(true, RetCode.UNAUTHZ);

    public static final String DEFAULT_UNAUTHORIZED_STATUS = "UNAUTHORIZED";
    public static final String DEFAULT_AUTHORIZED_STATUS = "AUTHORIZED";
    public static final String DEFAULT_REDIRECT_URI_KEY = "redirect_uri";
    public static final String DEFAULT_USERINFO_KEY = "userinfo";
    public static final String DEFAULT_LOGIN_SUCCESS_URI = API_V1_USER_BASE_URI.concat(API_V1_USER_USERINFO_URI);
}
