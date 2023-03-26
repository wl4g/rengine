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
package com.wl4g.rengine.common.entity;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.infra.common.bean.BaseBean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link BaseEntity}
 * 
 * @author James Wong
 * @version 2023-03-08
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
public abstract class BaseEntity extends BaseBean {
    private static final long serialVersionUID = 2096342971580901508L;

    private @Nullable String nameEn;
    private @Nullable String nameZh;
    private Long tenantId;

    // Ignore getter/setter.

    @JsonIgnore
    @Override
    public String getOrgCode() {
        return null;
    }

    @JsonIgnore
    @Override
    public void setOrgCode(String orgCode) {
    }

    // @JsonIgnore
    // @Override
    // public Integer getDelFlag() {
    // return null;
    // }
    //
    // @JsonIgnore
    // @Override
    // public void setDelFlag(Integer delFlag) {
    // }

}
