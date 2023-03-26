// @formatter:off
///*
// * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.rengine.service.security.authentication;
//
//import static com.wl4g.infra.common.lang.StringUtils2.eqIgnCase;
//import static java.util.Objects.isNull;
//import static org.apache.commons.lang3.StringUtils.isBlank;
//
//import java.io.IOException;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.boot.autoconfigure.web.ServerProperties;
//
//import com.wl4g.infra.context.utils.SpringContextHolder;
//
///**
// * {@link SmartSessionIdFilter}
// * 
// * @author James Wong
// * @version 2023-03-26
// * @since v1.0.0
// */
//public class SmartSessionIdFilter implements Filter {
//
//    private ServerProperties serverProperties;
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        final String sessionName = getServerProperties().getServlet().getSession().getCookie().getName();
//
//        if (request instanceof HttpServletRequest) {
//            final HttpServletRequest httpRequest = (HttpServletRequest) request;
//            String sessionId = getSessionIdFromCookie(sessionName, httpRequest);
//            if (isBlank(sessionId)) {
//                sessionId = httpRequest.getParameter(sessionName);
//            }
//            if (!isBlank(sessionId)) {
//                // httpRequest.setAttribute(sessionName,
//                // Encodes.decodeBase64String(sessionId));
//                httpRequest.setAttribute(sessionName, sessionId);
//            }
//        }
//
//        chain.doFilter(request, response);
//    }
//
//    private String getSessionIdFromCookie(String sessionName, HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (eqIgnCase(sessionName, cookie.getName())) {
//                    return cookie.getValue();
//                }
//            }
//        }
//        return null;
//    }
//
//    private ServerProperties getServerProperties() {
//        if (isNull(serverProperties)) {
//            synchronized (this) {
//                if (isNull(serverProperties)) {
//                    this.serverProperties = SpringContextHolder.getBean(ServerProperties.class);
//                }
//            }
//        }
//        return serverProperties;
//    }
//
//    public static final SmartSessionIdFilter DEFAULT = new SmartSessionIdFilter();
//
//}
