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
//import static java.util.Objects.nonNull;
//import static org.apache.commons.lang3.StringUtils.isBlank;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.web.ServerProperties;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.session.SessionAuthenticationException;
//import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
//import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
//
///**
// * {@link SmartSessionAuthenticationStrategy}
// * 
// * @author James Wong
// * @date 2023-03-26
// * @since v1.0.0
// */
//public class SmartSessionAuthenticationStrategy implements SessionAuthenticationStrategy {
//
//    @Autowired
//    ServerProperties serverProperties;
//
//    @Override
//    public void onAuthentication(Authentication authentication, HttpServletRequest request, HttpServletResponse response)
//            throws SessionAuthenticationException {
//        final String sessionName = serverProperties.getServlet().getSession().getCookie().getName();
//        final String sessionId = request.getParameter(sessionName);
//        if (!isBlank(sessionId)) {
//            HttpSession session = request.getSession(false);
//            if (nonNull(session)) {
//                session.invalidate();
//            }
//            session = request.getSession(true);
//            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
//                    SecurityContextHolder.getContext());
//            request.changeSessionId();
//        }
//    }
//
//}
