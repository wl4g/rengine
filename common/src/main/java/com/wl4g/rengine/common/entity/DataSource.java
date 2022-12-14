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
package com.wl4g.rengine.common.entity;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static java.lang.String.format;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.bean.BaseBean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link DataSource}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class DataSource extends BaseBean {
    private static final long serialVersionUID = -7441054887057231030L;
    private @NotBlank String name;
    private @NotNull DataSourceType type;
    private @NotNull Map<String, Object> properties;

    // Notice: It is recommended to disable the toString method, otherwise
    // swagger will generate the name of the example long enumeration type by
    // default.
    // @ToString
    @Getter
    @AllArgsConstructor
    public static enum DataSourceType {
        MONGO;

        public static DataSourceType of(final @NotBlank String type) {
            hasTextOf(type, "type");
            for (DataSourceType a : values()) {
                if (a.name().equalsIgnoreCase(type)) {
                    return a;
                }
            }
            throw new IllegalArgumentException(format("Invalid upload type for '%s'", type));
        }
    }

}
