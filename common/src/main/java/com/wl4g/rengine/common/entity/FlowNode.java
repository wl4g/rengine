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

import static java.lang.String.format;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.rengine.common.entity.FlowNode.EndNode;
import com.wl4g.rengine.common.entity.FlowNode.ExecutionNode;
import com.wl4g.rengine.common.entity.FlowNode.OutputNode;
import com.wl4g.rengine.common.entity.FlowNode.RelationNode;
import com.wl4g.rengine.common.entity.FlowNode.StartNode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * The basic execution DAG(directed acyclic graph) graph node definition of rule
 * process.
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v1.0.0
 */
@Schema(oneOf = { RelationNode.class, OutputNode.class }, discriminatorProperty = "@type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type", visible = true)
@JsonSubTypes({ @Type(value = StartNode.class, name = "START"), @Type(value = EndNode.class, name = "END"),
        @Type(value = RelationNode.class, name = "RELATION"), @Type(value = ExecutionNode.class, name = "EXECUTION"),
        @Type(value = OutputNode.class, name = "OUTPUT") })
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public abstract class FlowNode implements Serializable {

    private static final long serialVersionUID = 1L;
    @Schema(name = "@type", implementation = FlowNodeType.class)
    @JsonProperty(value = "@type", access = Access.WRITE_ONLY)
    private @NotBlank String type;
    private @NotBlank String id;
    private @NotBlank String parentId;
    private @NotBlank @Default String name = "Node 1";
    private @NotBlank @Default String top = "0px";
    private @NotBlank @Default String left = "1px";
    private @NotBlank @Default String color = "blue";
    private @Nullable @Default Map<String, Object> attributes = new HashMap<>();

    @Getter
    @Setter
    @ToString(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    public static class StartNode extends FlowNode {
        private static final long serialVersionUID = 422265264435899065L;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    public static class EndNode extends FlowNode {
        private static final long serialVersionUID = 42226522235899065L;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    public static class RelationNode extends FlowNode {
        private static final long serialVersionUID = 4222652655435899065L;
        private RelationType relation;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    @SuperBuilder
    public static class ExecutionNode extends FlowNode {
        private static final long serialVersionUID = 42226526447799065L;
        // The current this node corresponding rule ID.
        private @NotBlank String ruleId;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    @SuperBuilder
    @NoArgsConstructor
    public static class OutputNode extends FlowNode {
        private static final long serialVersionUID = 422261164435899065L;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static enum RelationType {

        AND("The similar to relation operator: &&"),

        OR("The similar to relation operator: ||"),

        NOT("The similar to relation operator: !"),

        ALL("The similar to relation operator: &"),

        ANY("The similar to relation operator: |");

        private final String description;

        @JsonCreator
        public static RelationType of(String type) {
            for (RelationType a : values()) {
                if (a.name().equalsIgnoreCase(type)) {
                    return a;
                }
            }
            throw new IllegalArgumentException(format("Invalid operator type for '%s'", type));
        }

    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static enum FlowNodeType {

        START(StartNode.class),

        END(EndNode.class),

        RELATION(RelationNode.class),

        EXECUTION(ExecutionNode.class),

        OUTPUT(OutputNode.class);

        private final Class<? extends FlowNode> clazz;
    }

}
