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
package com.wl4g.rengine.common.entity;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notEmpty;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.rengine.common.exception.InvalidNodeRelationException;
import com.wl4g.rengine.common.validation.ValidForEntityMarker;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkflowGraph extends BaseEntity {
    private static final long serialVersionUID = 1917204508937266181L;

    private @NotNull(groups = ValidForEntityMarker.class) @Min(value = 0, groups = ValidForEntityMarker.class) Long revision;

    private @NotNull @Min(0) Long workflowId;

    private @NotEmpty List<BaseNode<?>> nodes = new LinkedList<>();

    private @NotEmpty List<NodeConnection> connections = new LinkedList<>();

    /**
     * The extended attribute configuration of the workflow graph, for example,
     * calling
     * <b>{@link com.wl4g.rengine.executor.execution.sdk.notifier.DingtalkScriptMessageNotifier}</b>
     * in the execution node (script) of <b>dingtalk_workflow</b> to send group
     * messages, at this time, the <b>openConversationId</b>, <b>robotCode</b>,
     * etc. are required, which can be get from here.
     */
    private @Nullable Map<String, Object> attributes = new HashMap<>();

    public WorkflowGraph(@NotNull @Min(0) Long workflowId, @NotEmpty List<BaseNode<?>> nodes,
            @NotEmpty List<NodeConnection> connections) {
        this.workflowId = workflowId;
        this.nodes = nodes;
        this.connections = connections;
        validateForBasic();
    }

    @SuppressWarnings("unchecked")
    public <T extends WorkflowGraph> T validateForBasic() {
        isTrueOf(nonNull(workflowId) && workflowId >= 0, "workflowId >= 0");
        notEmpty(getNodes(), "graph.nodes");
        notEmpty(getConnections(), "graph.connections");

        // Check the node property (sush as:
        // type,id,name,priority,logical,ruleId etc) is missing.
        safeList(nodes).stream().forEach(n -> {
            try {
                n.validate();
            } catch (Throwable ex) {
                throw new IllegalArgumentException(format("Missing node property values for : %s", n));
            }
        });

        // Check the duplicate node ID.
        nodes.stream()
                .collect(groupingBy(n -> n.getId()))
                .entrySet()
                .stream()
                .filter(e -> safeList(e.getValue()).size() > 1)
                .findAny()
                .ifPresent(e -> {
                    throw new InvalidNodeRelationException(format("Duplicate node id of : %s", e.getKey()));
                });

        // Check the boot(start) node.
        final List<BootNode> bootNodes = nodes.stream()
                .filter(n -> n instanceof BootNode)
                .map(n -> (BootNode) n)
                .collect(toList());
        if (safeList(bootNodes).size() != 1) {
            throw new InvalidNodeRelationException(format("There must be one and only one boot(start) node of : %s", bootNodes));
        }

        //// @formatter:off
        //if (!isBlank(safeList(bootNodes).get(0).getPrevId())) {
        //    throw new InvalidNodeRelationException("The prevId value of start node must be empty.");
        //}
        //// Check the end node.
        //List<ExecutionGraph<?>> endNodes = nodes.stream().filter(n -> n instanceof EndOperator).collect(toList());
        //if (safeList(endNodes).size() != 1) {
        //    throw new InvalidNodeRelationException(format("There must be one and only one end node of : %s", endNodes));
        //}
        //
        //// Check the start-to-end reachable continuity.
        //Map<String, ExecutionGraph<?>> nodeMap = nodes.stream().collect(toMap(n -> n.getId(), n -> n));
        //for (Entry<String, ExecutionGraph<?>> ent : nodeMap.entrySet()) {
        //    ExecutionGraph<?> n = ent.getValue();
        //    if (!(n instanceof BootOperator) && isNull(nodeMap.get(n.getPrevId()))) {
        //        throw new InvalidNodeRelationException(format("Invalid node unreachable orphaned of : %s", n));
        //    }
        //}
        // @formatter:on

        return (T) this;
    }

    /**
     * The basic execution DAG(directed acyclic graph) graph node definition of
     * rule process.
     * 
     * @author James Wong
     * @version 2022-10-20
     * @since v1.0.0
     */
    @SuppressWarnings("unchecked")
    @Schema(oneOf = { BootNode.class, ProcessNode.class, FailbackNode.class, RelationNode.class, LogicalNode.class,
            RunNode.class }, discriminatorProperty = "@type")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type", visible = true)
    @JsonSubTypes({ @Type(value = BootNode.class, name = "BOOT"), @Type(value = ProcessNode.class, name = "PROCESS"),
            @Type(value = RelationNode.class, name = "RELATION"), @Type(value = FailbackNode.class, name = "FAILBACK"),
            @Type(value = LogicalNode.class, name = "LOGICAL"), @Type(value = RunNode.class, name = "RUN") })
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static abstract class BaseNode<E extends BaseNode<?>> implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(name = "@type", implementation = NodeType.class)
        @JsonProperty(value = "@type", access = Access.WRITE_ONLY)
        private @NotBlank String type;
        private @NotBlank String id;
        // If the current node is a child of a logical node, priority is must
        // required.
        private Integer priority;
        private @NotBlank String name = DEFAULT_NODE_NAME;
        private @Nullable Map<String, Object> attributes = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("top", "10px");
                put("left", "10px");
                put("color", "#5f5f5f");
            }
        };

        public E withId(String id) {
            this.id = id;
            return (E) this;
        }

        public E withName(String name) {
            this.name = name;
            return (E) this;
        }

        public E withPriority(Integer priority) {
            this.priority = priority;
            return (E) this;
        }

        public E withAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return (E) this;
        }

        public BaseNode<E> validate() {
            hasTextOf(type, "type");
            hasTextOf(id, "id");
            hasTextOf(name, "name");
            // notNullOf(priority, "priority");
            return this;
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class BootNode extends BaseNode<BootNode> {
        private static final long serialVersionUID = 422265264435899065L;

        public BootNode() {
            setType(NodeType.BOOT.name());
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class ProcessNode extends RunNode {
        private static final long serialVersionUID = 422265264435899065L;

        public ProcessNode() {
            setType(NodeType.PROCESS.name());
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class RelationNode extends ProcessNode {
        private static final long serialVersionUID = 422265264435899065L;

        public RelationNode() {
            setType(NodeType.RELATION.name());
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class FailbackNode extends ProcessNode {
        private static final long serialVersionUID = 422265264435899065L;

        public FailbackNode() {
            setType(NodeType.FAILBACK.name());
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class LogicalNode extends BaseNode<LogicalNode> {
        private static final long serialVersionUID = 4222652655435899065L;

        private @NotNull LogicalType logical;

        public LogicalNode() {
            setType(NodeType.LOGICAL.name());
        }

        public LogicalNode withLogical(LogicalType logical) {
            setLogical(logical);
            return this;
        }

        @Override
        public BaseNode<LogicalNode> validate() {
            notNullOf(logical, "logical");
            return super.validate();
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class RunNode extends BaseNode<RunNode> {
        private static final long serialVersionUID = 42226526447799065L;

        /**
         * The current this node corresponding script rule ID.
         */
        private @NotBlank Long ruleId;

        public RunNode() {
            setType(NodeType.RUN.name());
        }

        public RunNode withRuleId(Long ruleId) {
            setRuleId(ruleId);
            return this;
        }

        @Override
        public BaseNode<RunNode> validate() {
            notNullOf(ruleId, "ruleId");
            return super.validate();
        }
    }

    // Notice: It is recommended to disable the toString method, otherwise
    // swagger will generate the name of the example long enumeration type by
    // default.
    // @ToString
    @Getter
    @AllArgsConstructor
    public static enum LogicalType {

        AND("Short-circuit and operator, like the: '&&'"),

        OR("Short-circuit and operator, like the: '||'"),

        ALL_AND("Non short-circuit and operator, like the: '&'"),

        ALL_OR("Non short-circuit or operator, like the: '|'"),

        NOT("Non operator, like the: '!'");

        private final String description;

        @JsonCreator
        public static LogicalType of(String type) {
            for (LogicalType a : values()) {
                if (a.name().equalsIgnoreCase(type)) {
                    return a;
                }
            }
            throw new IllegalArgumentException(format("Invalid logical type for '%s'", type));
        }
    }

    @Getter
    @AllArgsConstructor
    public static enum NodeType {

        BOOT(BootNode.class),

        PROCESS(ProcessNode.class),

        RELATION(RelationNode.class),

        FAILBACK(FailbackNode.class),

        LOGICAL(LogicalNode.class),

        RUN(RunNode.class);

        private final Class<? extends BaseNode<? extends BaseNode<?>>> clazz;

        @JsonCreator
        public static NodeType of(String type) {
            for (NodeType a : values()) {
                if (a.name().equalsIgnoreCase(type)) {
                    return a;
                }
            }
            throw new IllegalArgumentException(format("Invalid node type for '%s'", type));
        }
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class NodeConnection {
        private @Nullable String name;
        private @NotBlank String to;
        private @NotBlank String from;
        private @Nullable @Default Map<String, Object> attributes = new HashMap<>(2);

        public NodeConnection(final @NotBlank String to, final @NotBlank String from) {
            this(null, from, to);
        }

        public NodeConnection(final @Nullable String name, final @NotBlank String from, final @NotBlank String to) {
            this.name = name;
            this.to = hasTextOf(to, "to");
            this.from = hasTextOf(from, "from");
        }
    }

    public static final String DEFAULT_NODE_NAME = "Unnamed Node";
}
