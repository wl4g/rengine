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
package com.wl4g.rengine.common.model;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.infra.common.validation.EnumValue;
import com.wl4g.rengine.common.model.EvaluationResult.GenericEvaluationResult;
import com.wl4g.rengine.common.model.EvaluationResult.ScoreEvaluationResult;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link EvaluationResult}
 * 
 * @author James Wong
 * @version 2022-09-21
 * @since v3.0.0
 */
// 1.多态参见:https://swagger.io/docs/specification/data-models/inheritance-and-polymorphism/
// 2.对于swagger3注解,父类必须是抽象的，否则swagger3页面请求参数schemas展开后会以父类名重复展示3个.
@Schema(oneOf = { GenericEvaluationResult.class, ScoreEvaluationResult.class }, discriminatorProperty = "@kind")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@kind", visible = true)
@JsonSubTypes({ @Type(value = GenericEvaluationResult.class, name = "oauth2"),
        @Type(value = ScoreEvaluationResult.class, name = "saml2") })
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
public abstract class EvaluationResult {

    @Schema(name = "@kind", implementation = EvaluationKind.class)
    @JsonProperty(value = "@kind")
    private @NotBlank @EnumValue(enumCls = EvaluationKind.class) String kind;

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class GenericEvaluationResult extends EvaluationResult {
        // private @NotBlank Map<String, Object> result;
        private @Nullable String result;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class ScoreEvaluationResult extends EvaluationResult {
        private @NotBlank Float score;
    }

}
