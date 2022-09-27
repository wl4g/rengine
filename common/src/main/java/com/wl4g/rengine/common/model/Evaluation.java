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
 * See the License for the specific engine governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.common.model;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.infra.common.validation.EnumValue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link Evaluation}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v3.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
public class Evaluation {

    @Schema(name = "@kind", implementation = EvaluationKind.class)
    @JsonProperty(value = "@kind")
    @NotBlank
    @EnumValue(enumCls = EvaluationKind.class)
    String kind;

    @Schema(name = "@engine", implementation = EvaluationEngine.class)
    @JsonProperty(value = "@engine")
    @NotBlank
    @EnumValue(enumCls = EvaluationEngine.class)
    String engine;

    @NotBlank
    String service;

    @NotBlank
    String scenesCode;

    @NotNull
    Scripting scripting;

    @Nullable
    Map<String, String> attributes;

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class Scripting {

        @NotBlank
        String mainFun;

        @NotEmpty
        List<String> args;
    }

}
