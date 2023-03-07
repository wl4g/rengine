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

import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.rengine.common.entity.RuleScript.RuleScriptWrapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link Rule}
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
public class Rule extends BaseEntity {
    private static final long serialVersionUID = -7441054887057231030L;
    private @NotBlank String name;
    private @NotNull RuleEngine engine;

    // Temporary fields.
    // @Schema(hidden = true, accessMode =
    // io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE)
    // private @Nullable transient List<UploadObject> uploads;

    public static enum RuleEngine {

        /**
         * Noice: The temporarily unable to support graalvm native mode.
         */
        // GROOVY,

        JS,

        FLINK_SQL,

        FLINK_CEP_SQL
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class RuleWrapper extends Rule {
        private static final long serialVersionUID = 1L;
        private List<RuleScriptWrapper> scripts;

        /**
         * Notice: The latest revision(version) is the first one after the
         * aggregation query in reverse order.
         * 
         * @return
         */
        @JsonIgnore
        public RuleScriptWrapper getEffectiveLatestScript() {
            validate(this);
            return getScripts().get(0);
        }

        public RuleWrapper validate() {
            return validate(this);
        }

        public static RuleWrapper validate(RuleWrapper rule) {
            notNullOf(rule, "rule");
            notNullOf(rule.getEngine(), "rule.engine");
            notNullOf(rule.getScripts(), "rule.scripts");
            for (RuleScriptWrapper script : rule.getScripts()) {
                RuleScriptWrapper.validate(script);
            }
            return rule;
        }
    }

}
