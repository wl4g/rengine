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

import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.rengine.common.validation.ValidForEntityMarker;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link RuleScript}
 * 
 * @author James Wong
 * @version 2022-12-13
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class RuleScript extends BaseBean {
    private static final long serialVersionUID = -4525858823849417654L;
    private @NotNull(groups = ValidForEntityMarker.class) Long revision;
    private @NotNull Long ruleId;
    // Rule script program entry-point file
    private @NotNull Long entrypointUploadId;
    private @NotEmpty @Default List<Long> uploadIds = new ArrayList<>(1);

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class RuleScriptWrapper extends RuleScript {
        private static final long serialVersionUID = 1L;
        private List<UploadObject> uploads;

        public RuleScriptWrapper validate() {
            return validate(this);
        }

        public static RuleScriptWrapper validate(RuleScriptWrapper ruleScript) {
            notNullOf(ruleScript, "ruleScript");
            notNullOf(ruleScript.getRevision(), "revision");
            notEmptyOf(ruleScript.getUploads(), "uploads");
            return ruleScript;
        }
    }
}