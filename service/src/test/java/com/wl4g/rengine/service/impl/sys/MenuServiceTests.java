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
package com.wl4g.rengine.service.impl.sys;

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import java.util.Collection;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.wl4g.rengine.common.entity.sys.Menu;
import com.wl4g.rengine.service.security.user.AuthenticationServiceTests;
import com.wl4g.rengine.service.util.TestDefaultMongoSetup;

/**
 * {@link MenuServiceTests}
 * 
 * @author James Wong
 * @version 2023-02-14
 * @since v1.0.0
 */
public class MenuServiceTests {

    static MenuServiceImpl menuService;
    static MongoTemplate mongoTemplate;

    @BeforeClass
    public static void init() {
        menuService = new MenuServiceImpl() {
            {
                this.mongoTemplate = TestDefaultMongoSetup.createMongoTemplate();
                this.authenticationService = AuthenticationServiceTests.getAuthenticationService();
            }
        };
    }

    @Test
    public void testLoadMenuTree() {
        SecurityContextHolder.setContext(new SecurityContext() {
            private static final long serialVersionUID = 1L;

            @Override
            public void setAuthentication(Authentication authentication) {
            }

            @Override
            public Authentication getAuthentication() {
                return new Authentication() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getName() {
                        return "root";
                    }

                    @Override
                    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public boolean isAuthenticated() {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    @Override
                    public Object getPrincipal() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public Object getDetails() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public Object getCredentials() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                };
            }
        });

        final List<Menu> menuTree = menuService.loadMenuTree();
        System.out.println(toJSONString(menuTree, true));
    }

}
