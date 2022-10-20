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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.rengine.common.entity.FlowNode.OperationFlowNode;
import com.wl4g.rengine.common.entity.FlowNode.OutputFlowNode;

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
 * @since v3.0.0
 */
@Schema(oneOf = { OperationFlowNode.class, OutputFlowNode.class }, discriminatorProperty = "@type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type", visible = true)
@JsonSubTypes({ @Type(value = OperationFlowNode.class, name = "OPERATION"),
        @Type(value = OutputFlowNode.class, name = "OUTPUT") })
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public abstract class FlowNode {
    @Schema(name = "@type", implementation = FlowNodeType.class)
    @JsonProperty(value = "@type")
    private String type;
    private String id;
    private String name;
    private String top;
    private String left;
    private String color;
    private @Default Map<String, Object> attributes = new HashMap<>();

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class OperationFlowNode extends FlowNode {
        private OperatorType operator;
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class OutputFlowNode extends FlowNode {
        private OperatorType operator;
    }

    /**
     * Operator Type Definitions for Rule DAG Execution Graphs.
     */
    @Getter
    @ToString
    @AllArgsConstructor
    public static enum OperatorType {

        AND("The similar to code operator: &&"),

        OR("The similar to code operator: ||"),

        NOT("The similar to code operator: !"),

        ALL("The similar to code operator: &"),

        ANY("The similar to code operator: |");

        private final String description;

        @JsonCreator
        public static OperatorType of(String type) {
            for (OperatorType a : values()) {
                if (a.name().equalsIgnoreCase(type)) {
                    return a;
                }
            }
            throw new IllegalArgumentException(format("Invalid operator type for '%s'", type));
        }

    }

    public static enum FlowNodeType {
        OPERATION, OUTPUT
    }

}
