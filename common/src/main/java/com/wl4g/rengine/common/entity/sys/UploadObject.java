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
 * @date 2022-08-29
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

    // Save API front-end without objectPrefix field.
    @Nullable
    @NotBlank(groups = ValidForEntityMarker.class)
    @Schema(hidden = false, accessMode = AccessMode.READ_ONLY)
    private String objectPrefix;

    // Saved without file version and extension.
    // e.g: common-lang-v2.js(common-lang)
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

        SCRIPTLOG("scriptlog", asList(ExtensionType.LOG)),

        MEDIA_IMAGE("media/image",
                asList(ExtensionType.MEDIA_PNG, ExtensionType.MEDIA_JPG, ExtensionType.MEDIA_JPEG, ExtensionType.MEDIA_BMP,
                        ExtensionType.MEDIA_TIF, ExtensionType.MEDIA_GIF, ExtensionType.MEDIA_PCX, ExtensionType.MEDIA_TGA,
                        ExtensionType.MEDIA_EXIF, ExtensionType.MEDIA_FPX, ExtensionType.MEDIA_SVG, ExtensionType.MEDIA_PSD,
                        ExtensionType.MEDIA_CDR, ExtensionType.MEDIA_PCD, ExtensionType.MEDIA_DXF, ExtensionType.MEDIA_UFO,
                        ExtensionType.MEDIA_EPS, ExtensionType.MEDIA_AI, ExtensionType.MEDIA_RAW, ExtensionType.MEDIA_WMF,
                        ExtensionType.MEDIA_WEBP, ExtensionType.MEDIA_AVIF, ExtensionType.MEDIA_APNG)),

        MEDIA_VIDEO("media/video",
                asList(ExtensionType.MEDIA_MP4, ExtensionType.MEDIA_3GP, ExtensionType.MEDIA_MPE, ExtensionType.MEDIA_MPEG,
                        ExtensionType.MEDIA_TS, ExtensionType.MEDIA_MOV, ExtensionType.MEDIA_WAV, ExtensionType.MEDIA_WMV,
                        ExtensionType.MEDIA_AVI, ExtensionType.MEDIA_ASF, ExtensionType.MEDIA_FLV, ExtensionType.MEDIA_WEBM,
                        ExtensionType.MEDIA_AVCHD, ExtensionType.MEDIA_RM, ExtensionType.MEDIA_RMVB, ExtensionType.MEDIA_DIV,
                        ExtensionType.MEDIA_DV, ExtensionType.MEDIA_DVX, ExtensionType.MEDIA_VOB, ExtensionType.MEDIA_MKV,
                        ExtensionType.MEDIA_LAVF, ExtensionType.MEDIA_CPK, ExtensionType.MEDIA_DIRAC, ExtensionType.MEDIA_RAM,
                        ExtensionType.MEDIA_QT, ExtensionType.MEDIA_FLI, ExtensionType.MEDIA_FLC, ExtensionType.MEDIA_MOD,
                        ExtensionType.MEDIA_DAT)),

        MEDIA_AUDIO("media/audio", asList(ExtensionType.MEDIA_A, ExtensionType.MEDIA_AUD, ExtensionType.MEDIA_MP3));

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

        // IMAGES

        MEDIA_PNG(true, ".png"),

        MEDIA_JPG(true, ".jpg"),

        MEDIA_JPEG(true, ".jpeg"),

        MEDIA_BMP(true, ".bmp"),

        MEDIA_TIF(true, ".tifl"),

        MEDIA_GIF(true, ".gifl"),

        MEDIA_PCX(true, ".pcxl"),

        MEDIA_TGA(true, ".tgal"),

        MEDIA_EXIF(true, ".exifl"),

        MEDIA_FPX(true, ".fpxl"),

        MEDIA_SVG(true, ".svgl"),

        MEDIA_PSD(true, ".psdl"),

        MEDIA_CDR(true, ".cdrl"),

        MEDIA_PCD(true, ".pcdl"),

        MEDIA_DXF(true, ".dxfl"),

        MEDIA_UFO(true, ".ufol"),

        MEDIA_EPS(true, ".epsl"),

        MEDIA_AI(true, ".ail"),

        MEDIA_RAW(true, ".rawl"),

        MEDIA_WMF(true, ".wmfl"),

        MEDIA_WEBP(true, ".webpl"),

        MEDIA_AVIF(true, ".avifl"),

        MEDIA_APNG(true, ".apng"),

        // VIDEOS

        MEDIA_MP4(true, ".mp4"),

        MEDIA_3GP(true, ".3gp"),

        MEDIA_MPE(true, ".mpe"),

        MEDIA_MPEG(true, ".mpeg"),

        MEDIA_TS(true, ".ts"),

        MEDIA_MOV(true, ".mov"),

        MEDIA_WAV(true, ".wav"),

        MEDIA_WMV(true, ".wmv"),

        MEDIA_AVI(true, ".avi"),

        MEDIA_ASF(true, ".asf"),

        MEDIA_FLV(true, ".flv"),

        MEDIA_WEBM(true, ".webm"),

        MEDIA_AVCHD(true, ".avcdh"),

        MEDIA_RM(true, ".rm"),

        MEDIA_RMVB(true, ".rmvb"),

        MEDIA_DIV(true, ".rmvb"),

        MEDIA_DV(true, ".rmvb"),

        MEDIA_DVX(true, ".rmvb"),

        MEDIA_VOB(true, ".vob"),

        MEDIA_MKV(true, ".mkv"),

        MEDIA_LAVF(true, ".lavf"),

        MEDIA_CPK(true, ".cpk"),

        MEDIA_DIRAC(true, ".dirac"),

        MEDIA_RAM(true, ".ram"),

        MEDIA_QT(true, ".qt"),

        MEDIA_FLI(true, ".fli"),

        MEDIA_FLC(true, ".flc"),

        MEDIA_MOD(true, ".mod"),

        MEDIA_DAT(true, ".dat"),

        // AUDIOS

        MEDIA_MP3(true, ".mp3"),

        MEDIA_A(true, ".a"),

        MEDIA_AUD(true, ".aud");

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
