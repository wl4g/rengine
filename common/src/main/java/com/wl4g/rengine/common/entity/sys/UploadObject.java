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
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.infra.common.validation.EnumValue;
import com.wl4g.rengine.common.entity.BaseEntity;
import com.wl4g.rengine.common.validation.ValidForEntityMarker;

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
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class UploadObject extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(implementation = UploadObject.UploadType.class)
    private @NotBlank @EnumValue(enumCls = UploadObject.UploadType.class) String uploadType;

    // Save API front-end without objectPrefix.
    @Nullable
    @NotBlank(groups = ValidForEntityMarker.class)
    @Schema(hidden = false, accessMode = AccessMode.READ_ONLY)
    private String objectPrefix;

    private @NotBlank String filename;
    private @NotBlank String extension;
    private @NotNull @Min(0) Long size;
    private @Nullable String owner;
    private @Nullable String group;
    private @Nullable String accessMode;
    private @Nullable String md5;
    private @Nullable String sha1;

    // Ignore getter/setter.

    @JsonIgnore
    @Override
    public String getNameEn() {
        return null;
    }

    @JsonIgnore
    @Override
    public void setNameEn(String nameEn) {
    }

    @JsonIgnore
    @Override
    public String getNameZh() {
        return null;
    }

    @JsonIgnore
    @Override
    public void setNameZh(String nameZh) {
    }

    @JsonIgnore
    @Override
    public Date getUpdateDate() {
        return null;
    }

    @JsonIgnore
    @Override
    public void setUpdateDate(Date updateDate) {
    }

    @JsonIgnore
    @Override
    public Long getUpdateBy() {
        return null;
    }

    @JsonIgnore
    @Override
    public void setUpdateBy(Long updateDate) {
    }

    @Getter
    @AllArgsConstructor
    public static enum UploadType {

        LIBJS("libjs", asList(ExtensionType.JS, ExtensionType.TS, ExtensionType.MJS)),

        LIBGROOVY("libgroovy", asList(ExtensionType.GROOVY, ExtensionType.JAR)),

        LIBPYTHON("libpython", asList(ExtensionType.PY)),

        LIBR("libr", asList(ExtensionType.R)),

        LIBRUBY("libruby", asList(ExtensionType.RUBY)),

        LIBGOLANG("libgolang", asList(ExtensionType.GOLANG)),

        LIBRUST("librust", asList(ExtensionType.RUST)),

        LIBCSHARP("libcsharp", asList(ExtensionType.CSHARP)),

        LIBC("libc", asList(ExtensionType.C, ExtensionType.C_H)),

        LIBCPP("libcpp", asList(ExtensionType.CPP)),

        LIBSH("libsh", asList(ExtensionType.SHELL)),

        TESTCSV("testcsv", asList(ExtensionType.CSV)),

        SCRIPTLOG("scriptlog", asList(ExtensionType.LOG));

        private final String prefix;
        private final List<ExtensionType> extensions;

        @JsonCreator
        public static UploadType of(final @NotBlank String type) {
            hasTextOf(type, "type");
            for (UploadType a : values()) {
                if (a.name().equalsIgnoreCase(type)) {
                    return a;
                }
            }
            throw new IllegalArgumentException(format("Invalid upload type for '%s'", type));
        }

    }

    @Getter
    @AllArgsConstructor
    public static enum ExtensionType {
        JAR(true, ".jar"),

        GROOVY(false, ".groovy"),

        PY(false, ".py"),

        R(false, ".r"),

        RUBY(false, ".rb"),

        GOLANG(false, ".go"),

        RUST(false, ".rs"),

        CSHARP(false, ".cs"),

        C(false, ".c"),

        C_H(false, ".h"),

        CPP(false, ".cpp"),

        SHELL(false, ".sh"),

        JS(false, ".js"),

        TS(false, ".ts"),

        MJS(false, ".mjs"),

        CSV(false, ".csv"),

        LOG(false, ".log"),

        OUT(false, ".out"),

        ERR(false, ".err"),

        STDOUT(false, ".stdout"),

        STDERR(false, ".stderr"),

        UNKNOWN(false, ".unknown");

        private final boolean binary;
        private final String suffix;

        @JsonCreator
        public static ExtensionType of(String type) {
            for (ExtensionType a : values()) {
                if (equalsAnyIgnoreCase(type, a.name().toLowerCase(), a.getSuffix())) {
                    return a;
                }
            }
            throw new IllegalArgumentException(format("Invalid extension type for '%s'", type));
        }

    }

}
