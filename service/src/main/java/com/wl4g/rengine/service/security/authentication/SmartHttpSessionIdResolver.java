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
package com.wl4g.rengine.service.security.authentication;

import static java.util.Arrays.asList;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.CookieSerializer.CookieValue;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionIdResolver;

import com.wl4g.infra.common.codec.Encodes;

/**
 * {@link SmartHttpSessionIdResolver}
 * 
 * @author James Wong
 * @date 2023-03-26
 * @since v1.0.0
 * @see {@link org.springframework.session.web.http.CookieHttpSessionIdResolver}
 */
public class SmartHttpSessionIdResolver implements HttpSessionIdResolver {

    private static final String WRITTEN_SESSION_ID_ATTR = SmartHttpSessionIdResolver.class.getName()
            .concat(".WRITTEN_SESSION_ID_ATTR");

    private static final CookieSerializer cookieSerializer = new DefaultCookieSerializer();

    @Autowired
    ServerProperties serverProperties;

    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        List<String> sessionIds = cookieSerializer.readCookieValues(request);
        if (sessionIds.isEmpty()) {
            sessionIds = asList(Encodes.decodeBase64String(request.getParameter(getSessionIdName())));
        }
        return sessionIds;
    }

    @Override
    public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        if (sessionId.equals(request.getAttribute(WRITTEN_SESSION_ID_ATTR))) {
            return;
        }
        request.setAttribute(WRITTEN_SESSION_ID_ATTR, sessionId);
        cookieSerializer.writeCookieValue(new CookieValue(request, response, sessionId));

        response.setHeader(getSessionIdName(), sessionId);
    }

    @Override
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        cookieSerializer.writeCookieValue(new CookieValue(request, response, ""));

        response.setHeader(getSessionIdName(), "");
    }

    private String getSessionIdName() {
        return serverProperties.getServlet().getSession().getCookie().getName();
    }

}
