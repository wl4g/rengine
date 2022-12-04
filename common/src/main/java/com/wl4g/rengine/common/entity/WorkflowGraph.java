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

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notEmpty;
import static java.lang.String.format;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.Schema;
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
@ToString
@NoArgsConstructor
public class WorkflowGraph {
    private @NotEmpty List<BaseNode<?>> nodes = new LinkedList<>();
    private @NotEmpty List<NodeConnection> connections = new LinkedList<>();

    public WorkflowGraph(@NotEmpty final List<BaseNode<?>> nodes, @NotEmpty final List<NodeConnection> connections) {
        this.nodes = notEmpty(nodes, "nodes");
        this.connections = notEmpty(connections, "connections");
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
    @Schema(oneOf = { BootNode.class, ProcessNode.class, FailbackNode.class, LogicalNode.class, RunNode.class },
            discriminatorProperty = "@type")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type", visible = true)
    @JsonSubTypes({ @Type(value = BootNode.class, name = "BOOT"), @Type(value = ProcessNode.class, name = "PROCESS"),
            @Type(value = FailbackNode.class, name = "FAILBACK"), @Type(value = LogicalNode.class, name = "LOGICAL"),
            @Type(value = RunNode.class, name = "RUN") })
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

        public E withAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return (E) this;
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static abstract class BaseRunNode<E extends BaseRunNode<?>> extends BaseNode<E> {
        private static final long serialVersionUID = 42226526447799065L;

        /**
         * The current this node corresponding script rule ID.
         */
        private @NotBlank String ruleId;

        @SuppressWarnings("unchecked")
        public E withRuleId(String ruleId) {
            setRuleId(ruleId);
            return (E) this;
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
    public static class ProcessNode extends BaseRunNode<ProcessNode> {
        private static final long serialVersionUID = 422265264435899065L;

        public ProcessNode() {
            setType(NodeType.PROCESS.name());
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class FailbackNode extends BaseRunNode<FailbackNode> {
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

        private LogicalType logical;

        public LogicalNode() {
            setType(NodeType.LOGICAL.name());
        }

        public LogicalNode withLogical(LogicalType logical) {
            setLogical(logical);
            return this;
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class RunNode extends BaseRunNode<RunNode> {
        private static final long serialVersionUID = 42226526447799065L;

        public RunNode() {
            setType(NodeType.RUN.name());
        }
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static enum LogicalType {

        AND("Short-circuit and operator, equivalent to: '&&'"),

        OR("Short-circuit and operator, equivalent to: '||'"),

        ALL_AND("Non short-circuit and operator, equivalent to: '&'"),

        ALL_OR("Non short-circuit or operator, equivalent to: '|'"),

        NOT("Non operator, equivalent to: '!'");

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
    @ToString
    @AllArgsConstructor
    public static enum NodeType {

        BOOT(BootNode.class),

        PROCESS(ProcessNode.class),

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
        private @NotBlank @Default String name = DEFAULT_CONNECTION_NAME;
        private @NotBlank String to;
        private @NotBlank String from;
        private @Nullable @Default Map<String, Object> attributes = new HashMap<>();

        public NodeConnection(final @NotBlank String to, final @NotBlank String from) {
            this(DEFAULT_CONNECTION_NAME, from, to);
        }

        public NodeConnection(final @NotBlank String name, final @NotBlank String from, final @NotBlank String to) {
            this.name = hasTextOf(name, "name");
            this.to = hasTextOf(to, "to");
            this.from = hasTextOf(from, "from");
        }
    }

    public static final String DEFAULT_NODE_NAME = "Unnamed Node";
    public static final String DEFAULT_CONNECTION_NAME = "Unnamed Connection";
}
