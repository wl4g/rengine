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

import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.rengine.common.entity.Rule.RuleEngine;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link Workflow}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
public class Workflow extends BaseBean {
    private static final long serialVersionUID = 1L;
    private @NotBlank String name;
    private @NotNull RuleEngine ruleEngine;
    private @Nullable List<Long> ruleIds;

    //
    // Temporary fields.
    //

    // TODO??
    @Schema(hidden = true, accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE)
    private @Nullable transient List<Rule> rules;

    // TODO??
    @Schema(hidden = true, accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE)
    private @Nullable transient WorkflowGraph graph;

}
