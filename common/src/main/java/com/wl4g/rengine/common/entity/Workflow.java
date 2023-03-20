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

import static com.wl4g.infra.common.lang.Assert2.notEmpty;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.rengine.common.entity.Rule.RuleWrapper;

import lombok.Builder.Default;
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
@ToString(callSuper = true)
@NoArgsConstructor
public class Workflow extends BaseEntity {
    private static final long serialVersionUID = -8038218208189261648L;
    private @NotNull Long scenesId;
    private @NotBlank String name;
    private @NotNull WorkflowEngine engine;

    public static enum WorkflowEngine {
        STANDARD, FLINK_CEP_GRAPH
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class WorkflowWrapper extends Workflow {
        private static final long serialVersionUID = 1L;
        private @Nullable @Default List<WorkflowGraphWrapper> graphs = new ArrayList<>(4);

        // The latest revision(version) is the first one after the aggregation
        // query in reverse order.
        @JsonIgnore
        public WorkflowGraphWrapper getEffectiveLatestGraph() {
            validate(this);
            return getGraphs().get(0);
        }

        public WorkflowWrapper validate() {
            return validate(this);
        }

        public static WorkflowWrapper validate(WorkflowWrapper workflow) {
            notNullOf(workflow, "workflow");
            notNullOf(workflow.getEngine(), "workflow.engine");
            notEmpty(workflow.getGraphs(), "workflow.graphs");
            for (WorkflowGraphWrapper graph : workflow.getGraphs()) {
                WorkflowGraphWrapper.validate(graph);
            }
            return workflow;
        }

    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class WorkflowGraphWrapper extends WorkflowGraph {
        private static final long serialVersionUID = 1L;
        private @Nullable List<RuleWrapper> rules = new ArrayList<>(4);

        public static WorkflowGraphWrapper validate(WorkflowGraphWrapper graph) {
            notNullOf(graph, "graph");
            graph.validateForBasic();
            notEmpty(graph.getRules(), "graph.rules");

            // Check for graph rules number.
            final long expectedRuleIdNodes = graph.getNodes()
                    .stream()
                    .filter(n -> n instanceof RunNode)
                    .map(n -> ((RunNode) n).getRuleId())
                    .collect(toSet())
                    .size();
            final long existingRuleIdNodes = graph.getRules().size();
            if (expectedRuleIdNodes != existingRuleIdNodes) {
                throw new IllegalArgumentException(
                        format("Invalid workflow graph rules configuration, expected number of rules: %s, but actual rules: %s",
                                expectedRuleIdNodes, existingRuleIdNodes));
            }

            // Check for rule script.
            for (RuleWrapper rule : graph.getRules()) {
                RuleWrapper.validate(rule);
            }
            return graph;
        }
    }

}
