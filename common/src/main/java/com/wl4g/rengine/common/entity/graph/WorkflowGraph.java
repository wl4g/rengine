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
package com.wl4g.rengine.common.entity.graph;

import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notEmpty;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.infra.common.validation.EnumValue;
import com.wl4g.rengine.common.entity.BaseEntity;
import com.wl4g.rengine.common.entity.Rule.RuleWrapper;
import com.wl4g.rengine.common.entity.Workflow.WorkflowEngine;
import com.wl4g.rengine.common.entity.graph.StandardGraph.RunNode;
import com.wl4g.rengine.common.validation.ValidForEntityMarker;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link BaseNodes}
 * 
 * @author James Wong
 * @version 2022-11-28
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkflowGraph extends BaseEntity {
    private static final long serialVersionUID = 1917204508937266181L;

    private @NotNull(groups = ValidForEntityMarker.class) @Min(value = 0, groups = ValidForEntityMarker.class) Long revision;
    private @NotNull @Min(0) Long workflowId;
    private @NotNull GraphBase details;

    /**
     * The extended attribute configuration of the workflow graph, for example,
     * calling
     * <b>{@link com.wl4g.rengine.executor.execution.sdk.notifier.DingtalkScriptMessageNotifier}</b>
     * in the execution node (script) of <b>dingtalk_workflow</b> to send group
     * messages, at this time, the <b>openConversationId</b>, <b>robotCode</b>,
     * etc. are required, which can be get from here.
     */
    private @Nullable @Default Map<String, Object> attributes = new HashMap<>();

    public WorkflowGraph(@NotNull @Min(0) Long workflowId) {
        this.workflowId = workflowId;
    }

    public WorkflowGraph validate() {
        isTrueOf(nonNull(revision) && revision >= 0, "revision >= 0");
        isTrueOf(nonNull(workflowId) && workflowId >= 0, "workflowId >= 0");
        notNullOf(details, "details");
        details.validate();
        return this;
    }

    @Schema(oneOf = { StandardGraph.class, FlinkCepGraph.class }, discriminatorProperty = "engine")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "engine", visible = true)
    @JsonSubTypes({ @Type(value = StandardGraph.class, name = "STANDARD_GRAPH"),
            @Type(value = FlinkCepGraph.class, name = "FLINK_CEP_GRAPH") })
    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public abstract static class GraphBase implements Serializable {
        private static final long serialVersionUID = 5150435693367183210L;

        @Schema(name = "engine", implementation = WorkflowEngine.class)
        @JsonProperty(value = "engine", access = Access.WRITE_ONLY)
        @NotNull
        private @NotBlank @EnumValue(enumCls = WorkflowEngine.class) String engine;

        public abstract <T extends GraphBase> T validate();
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class WorkflowGraphWrapper extends WorkflowGraph {
        private static final long serialVersionUID = 1L;
        private @Nullable List<RuleWrapper> rules = new ArrayList<>(4);

        public static WorkflowGraphWrapper validate(WorkflowGraphWrapper wrapper) {
            notNullOf(wrapper, "graph");
            wrapper.validate();
            notNullOf(wrapper.getDetails(), "graph.details");
            notEmpty(wrapper.getRules(), "graph.rules");

            // Check for rule script.
            for (RuleWrapper rule : wrapper.getRules()) {
                RuleWrapper.validate(rule);
            }

            if (wrapper.getDetails() instanceof StandardGraph) {
                return validate(wrapper, (StandardGraph) wrapper.getDetails());
            } else if (wrapper.getDetails() instanceof FlinkCepGraph) {
                // Notice: At present, there is no to implement the validate
                // of FlinkCepGraph. Because his real implementation is flink,
                // there is no need to go through here.
            }

            return wrapper;
        }

        private static WorkflowGraphWrapper validate(WorkflowGraphWrapper wrapper, StandardGraph graph) {
            // Check for graph rules number.
            final long expectedRuleIdNodes = graph.getNodes()
                    .stream()
                    .filter(n -> n instanceof RunNode)
                    .map(n -> ((RunNode) n).getRuleId())
                    .collect(toSet())
                    .size();
            final long existingRuleIdNodes = wrapper.getRules().size();
            if (expectedRuleIdNodes != existingRuleIdNodes) {
                throw new IllegalArgumentException(
                        format("Invalid workflow graph rules configuration, expected number of rules: %s, but actual rules: %s",
                                expectedRuleIdNodes, existingRuleIdNodes));
            }
            return wrapper;
        }
    }

}
