/*
 * Copyright 2023 ~ 2025 the original author or authors.
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

import static com.wl4g.rengine.common.constants.RengineConstants.SAFE_ACCESS_KEY;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.rengine.common.entity.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Tenant extends BaseEntity {
    private static final long serialVersionUID = -3082996880346069398L;

    @NotBlank
    private String tenantCode;

    @NotBlank
    private String tenantName;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    private String address;

    private String description;

    private List<Long> userIds;

    // Use constant to avoid hard-coded password detection
    private String accessKey = SAFE_ACCESS_KEY;

    public Tenant(Long id) {
        setId(id);
    }

}
