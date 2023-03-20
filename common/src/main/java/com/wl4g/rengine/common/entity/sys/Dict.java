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

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static java.lang.String.valueOf;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

import com.wl4g.rengine.common.entity.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link Dict}
 * 
 * @author James Wong
 * @version 2022-09-13
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class Dict extends BaseEntity {
    private static final long serialVersionUID = -5762348176963349685L;

    private @Nullable String nameEn;
    private @Nullable String nameZh;
    private @NotBlank DictType type;
    private @NotBlank String key;
    private @NotBlank String value;
    private @Nullable Integer sort;

    public static String buildCacheHashKey(@NotBlank String type, @NotBlank String key) {
        hasTextOf(type, "type");
        hasTextOf(key, "key");
        return valueOf(type).concat(":").concat(key);
    }

    public static enum DictType {

        MENU_CLASSIFY_TYPE,

        API_CONFIG_DEFINITION,

        ENGINE_TYPE,

        EXECUTOR_CUSTOM_RESP_TPL,

        OTHER_TYPE;
    }

}