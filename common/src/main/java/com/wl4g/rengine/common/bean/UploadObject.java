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
package com.wl4g.rengine.common.bean;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.wl4g.infra.common.validation.EnumValue;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link UploadObject}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v3.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
public class UploadObject extends BeanBase {

    @Schema(implementation = UploadObject.UploadType.class)
    private @NotBlank @EnumValue(enumCls = UploadObject.UploadType.class) String UploadType;
    // Save API front-end without objectPrefix.
    private @NotBlank @Schema(hidden = false, accessMode = AccessMode.READ_ONLY) String objectPrefix;
    private @NotBlank String filename;
    private @NotBlank String extension;
    private @Nullable List<String> labels;
    private @NotNull @Min(1) Long size;
    // private @Nullable String owner;
    // private @NotBlank String group;
    // private @Nullable String accessMode;
    private @Nullable String md5sum;
    private @Nullable String sha1sum;

    @Getter
    @AllArgsConstructor
    public static enum UploadType {
        USER_LIBRARY_WITH_GROOVY("library/groovy", asList(".groovy", ".jar")),

        USER_LIBRARY_WITH_JS("library/js", asList(".js")),

        TEST_DATASET("testset/csv", asList(".csv"));

        private final String prefix;
        private final List<String> extensions;

        @JsonCreator
        public static UploadType of(String type) {
            for (UploadType a : values()) {
                if (a.name().equalsIgnoreCase(type)) {
                    return a;
                }
            }
            throw new IllegalArgumentException(format("Invalid upload biz type for '%s'", type));
        }

    }

}
