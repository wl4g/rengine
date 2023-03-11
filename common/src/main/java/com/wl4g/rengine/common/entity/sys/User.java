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
package com.wl4g.rengine.common.entity.sys;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.rengine.common.entity.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link User}
 * 
 * @author James Wong
 * @version 2022-09-13
 * @since v1.0.0
 * @see {@link com.wl4g.rengine.common.entity.sys.springframework.security.core.userdetails.User}
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class User extends BaseEntity {
    private static final long serialVersionUID = -5762348176963349685L;
    private @NotBlank String username; // email
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    private List<UserRole> userRoles;

    // Ignore organization getter/setter.
    @JsonIgnore
    @Override
    public String getOrgCode() {
        return null;
    }

    @JsonIgnore
    @Override
    public void setOrgCode(String orgCode) {
    }

    // --- TODO LDAP authority.---

    // --- TODO SAML authority.---

}