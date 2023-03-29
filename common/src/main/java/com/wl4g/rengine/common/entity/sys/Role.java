/*
 * Copyright 2017 ~ 2025 the original authors James Wong.
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
package com.wl4g.rengine.common.entity.sys;

import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonView;
import com.wl4g.rengine.common.entity.BaseEntity;
import com.wl4g.rengine.common.entity.Markers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link Role}
 * 
 * @author James Wong
 * @version 2023-03-08
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class Role extends BaseEntity {
    private static final long serialVersionUID = 381411777614066880L;

    private @NotNull Integer type;
    private @NotBlank String roleCode;

    // The temporary wrap fields.

    private List<User> users;
    private List<Menu> menus;

    // TODO remove?
    private @JsonView(Markers.InternalMarker.class) List<UserRole> userRoles;
    private @JsonView(Markers.InternalMarker.class) List<MenuRole> menuRoles;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Role role = (Role) o;
        return Objects.equals(getId(), role.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}