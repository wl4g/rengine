/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.rengine.service.security.access;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

/**
 * {@link SimplePermissionGrantedAuthority}
 * 
 * @author James Wong
 * @version 2023-03-11
 * @since v1.0.0
 */
public final class SimplePermissionGrantedAuthority
        implements GrantedAuthority, com.wl4g.rengine.common.entity.sys.User.GrantedAuthority {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final String permission;

    public SimplePermissionGrantedAuthority(String permission) {
        Assert.hasText(permission, "A granted authority(permission) textual representation is required");
        this.permission = permission;
    }

    @Override
    public String getAuthority() {
        return this.permission;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SimplePermissionGrantedAuthority) {
            return this.permission.equals(((SimplePermissionGrantedAuthority) obj).permission);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.permission.hashCode();
    }

    @Override
    public String toString() {
        return this.permission;
    }

}
