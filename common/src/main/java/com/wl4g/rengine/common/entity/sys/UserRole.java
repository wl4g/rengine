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

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.rengine.common.entity.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link UserRole}
 * 
 * @author James Wong
 * @date 2022-09-13
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class UserRole extends BaseEntity {
    private static final long serialVersionUID = -5762348176963349685L;
    private Long userId;
    private Long roleId;

    // The temporary wrap fields.
    private List<User> users;
    private List<Role> roles;

    @JsonIgnore
    @Override
    public Integer getEnable() {
        return null;
    }

    @JsonIgnore
    @Override
    public void setEnable(Integer enable) {
    }

    @JsonIgnore
    @Override
    public List<String> getLabels() {
        return null;
    }

    @JsonIgnore
    @Override
    public void setLabels(List<String> labels) {
    }

    @JsonIgnore
    @Override
    public Long getCreateBy() {
        return null;
    }

    @JsonIgnore
    @Override
    public Date getCreateDate() {
        return null;
    }

    @JsonIgnore
    @Override
    public Integer getDelFlag() {
        return null;
    }

    @JsonIgnore
    @Override
    public String getHumanCreateDate() {
        return null;
    }

    @JsonIgnore
    @Override
    public String getHumanUpdateDate() {
        return null;
    }

    @JsonIgnore
    @Override
    public String getRemark() {
        return null;
    }

    @JsonIgnore
    @Override
    public Long getUpdateBy() {
        return null;
    }

    @JsonIgnore
    @Override
    public Date getUpdateDate() {
        return null;
    }

    @JsonIgnore
    @Override
    public void setCreateBy(Long createBy) {
    }

    @JsonIgnore
    @Override
    public void setCreateDate(Date createDate) {
    }

    @JsonIgnore
    @Override
    public void setDelFlag(Integer delFlag) {
    }

    @JsonIgnore
    @Override
    public void setHumanCreateDate(String humanCreateDate) {
    }

    @JsonIgnore
    @Override
    public void setHumanUpdateDate(String humanUpdateDate) {
    }

    @JsonIgnore
    @Override
    public void setRemark(String remark) {
    }

    @JsonIgnore
    @Override
    public void setUpdateBy(Long updateBy) {
    }

    @JsonIgnore
    @Override
    public void setUpdateDate(Date updateDate) {
    }

}