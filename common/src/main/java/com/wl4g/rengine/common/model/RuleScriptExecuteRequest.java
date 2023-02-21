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
 * See the License for the specific engine governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.common.model;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.rengine.common.entity.Rule.RuleEngine;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link RuleScriptExecuteRequest}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class RuleScriptExecuteRequest extends ExecuteRequest {

    @NotNull
    RuleEngine engine;

    @NotNull
    Long ruleScriptId;

    @Schema(hidden = true, accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE)
    @JsonIgnore
    @Override
    public @Nullable Boolean getBestEffort() {
        // throw new UnsupportedOperationException();
        return false;
    }

    @Schema(hidden = true, accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE)
    @JsonIgnore
    @Override
    public void setBestEffort(@Nullable Boolean bestEffort) {
        // throw new UnsupportedOperationException();
    }

    @Override
    public RuleScriptExecuteRequest validate() {
        super.validate();
        notNullOf(engine, "engine");
        notNullOf(ruleScriptId, "ruleScriptId");
        return this;
    }

}
