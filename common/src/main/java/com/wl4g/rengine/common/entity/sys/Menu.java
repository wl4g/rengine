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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wl4g.infra.common.function.TreeConvertor.TreeNode;
import com.wl4g.rengine.common.entity.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link Menu}
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Menu extends BaseEntity implements TreeNode<Long, Menu> {
    private static final long serialVersionUID = 381411777614066880L;

    private String nameEn;
    private String nameZh;
    private Integer type;
    private Long parentId;
    private Integer level;
    private String pageLocation;
    private String routePath;
    private String renderTarget;
    private String parentRoutePath;
    private String classify;
    private Integer sort;
    private String icon;
    private List<String> permissions;

    private List<Menu> childrens;

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public List<Menu> getChildrens() {
        return childrens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Menu menu = (Menu) o;
        return Objects.equals(getId(), menu.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public static final Long DEFAULT_ROOT_PARENT_ID = 0L;

}