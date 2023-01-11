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

import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.notEmpty;

import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.rengine.common.entity.Workflow.WorkflowWrapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link Scenes}
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
public class Scenes extends BaseBean {
    private static final long serialVersionUID = -5069149346132378733L;
    private @NotBlank String scenesCode;
    private @NotBlank String name;

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class ScenesWrapper extends Scenes {
        private static final long serialVersionUID = 1718299975342519740L;
        private @Nullable List<WorkflowWrapper> workflows;

        /**
         * Notice: The temporarily designed to be one-to-one, i.e. the array
         * should have exactly one workflow element, using the first priority
         * workflow as a valid value. The advantage of using
         * List&lt;{@link WorkflowWrapper}&gt; here is to prepare for future
         * expansion into multiple priorities.
         * 
         * @return
         */
        @JsonIgnore
        public WorkflowWrapper getEffectivePriorityWorkflow() {
            return workflows.get(0);
        }

        public ScenesWrapper validate() {
            return validate(this);
        }

        public static ScenesWrapper validate(ScenesWrapper scenes) {
            notEmpty(scenes.getWorkflows(), "workflows");
            isTrue(scenes.getWorkflows().size() == 1, "workflows size must is 1, but size is %s", scenes.getWorkflows().size());
            WorkflowWrapper.validate(scenes.getEffectivePriorityWorkflow());
            return scenes;
        }
    }
}
