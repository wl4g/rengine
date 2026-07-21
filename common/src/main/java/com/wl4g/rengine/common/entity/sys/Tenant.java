/*
 * Copyright 2023 the original author or authors.
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

import static com.wl4g.rengine.common.constants.RengineConstants.*;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.rengine.common.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Tenant entity
 *
 * @author James Wong
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends BaseEntity {
    private static final long serialVersionUID = -123456789L;

    @NotBlank
    private String tenantCode;

    @NotBlank
    private String tenantName;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    private String address;

    private String description;

    /**
     * The password for the tenant's default admin user.
     * This field is only used during tenant creation and is not persisted.
     */
    @JsonIgnore
    private String adminPassword;

    // other fields and methods...
}
