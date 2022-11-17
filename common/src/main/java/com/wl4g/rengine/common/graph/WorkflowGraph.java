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
package com.wl4g.rengine.common.graph;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.rengine.common.entity.OperatorNode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The basic execution D AG(directed acyclic graph) graph node definition of
 * rule process.
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v1.0.0
 */
@Getter
@Setter
@ToString(callSuper = true, exclude = "parent")
@NoArgsConstructor
public class WorkflowGraph extends OperatorNode {
    private static final long serialVersionUID = 420565264435899065L;

    private final List<WorkflowGraph> childrens = new LinkedList<>();;

    private transient @JsonIgnore WorkflowGraph parent;

    public WorkflowGraph(@NotNull OperatorNode node) {
        notNullOf(node, "node");
        setType(node.getType());
        setId(node.getId());
        setParentId(node.getParentId());
        setName(node.getName());
        getAttributes().putAll(node.getAttributes());
    }

    public WorkflowResult process(WorkflowContext context) {
        // TODO
        for (OperatorNode node : safeList(childrens)) {
            WorkflowGraph graph = (WorkflowGraph) node;
            if (node instanceof EndNode) {
                EndNode n = (EndNode) node;
            } else if (node instanceof RelationNode) {
                // 实现 java.util.Predicate 接口来处理嵌套运算？
                RelationNode n = (RelationNode) node;
                switch (n.getRelation()) {
                case AND:// Short circuit and operation
                    for (WorkflowGraph sub : graph.getChildrens()) {
                        WorkflowResult result = sub.process(context);
                        // If all children return true, true is finally
                        // returned. If any node returns false, it ends and
                        // returns.
                        if (!result.isContinue()) {
                            return new WorkflowResult(false);
                        }
                    }
                    break;
                case OR: // Short circuit or operation
                    for (WorkflowGraph sub : graph.getChildrens()) {
                        WorkflowResult result = sub.process(context);
                        // If any child returns true, it will eventually return
                        // true.
                        if (result.isContinue()) {
                            return new WorkflowResult(false);
                        }
                    }
                    break;
                case ALL: // Non-short circuit and operation
                    Boolean isContinue = null;
                    for (WorkflowGraph sub : graph.getChildrens()) {
                        WorkflowResult result = sub.process(context);
                        // If all children return true, true is finally
                        // returned. If any node returns false, it ends and
                        // returns. (If the current node returns false, the
                        // subsequent nodes will still the execution)
                        if (isNull(isContinue) && !(isContinue = result.isContinue())) {
                            return new WorkflowResult(false);
                        }
                    }
                    break;
                case ANY: // Non-short circuit or operation
                    for (WorkflowGraph sub : graph.getChildrens()) {
                        WorkflowResult result = sub.process(context);
                        // If any child returns true, it will eventually return
                        // true. (If the current node returns true, the
                        // subsequent nodes will still the execution)
                        if (result.isContinue()) {
                            return new WorkflowResult(false);
                        }
                    }
                    break;
                case NOT: // Not and operation
                    break;
                }

            } else if (node instanceof OutputNode) {
                OutputNode n = (OutputNode) node;
            } else if (node instanceof ExecutionNode) {
                ExecutionNode n = (ExecutionNode) node;
                // TODO
                n.getRuleId();
            }
        }

        return new WorkflowResult(false);
    }

    public static List<OperatorNode> validateEffective(List<OperatorNode> nodes) {
        notNullOf(nodes, "nodes");

        // Checking for start node.
        List<OperatorNode> startNodes = nodes.stream().filter(n -> n instanceof StartNode).collect(toList());
        isTrue(startNodes.size() == 1, "There must be one and only one start node.");
        isTrue(isBlank(startNodes.get(0).getParentId()), "The parentId value of start node must be empty.");

        // Checking for end node.
        List<OperatorNode> endNodes = nodes.stream().filter(n -> n instanceof EndNode).collect(toList());
        isTrue(endNodes.size() == 1, "There must be one and only one end node.");

        // Checking for start-to-end reachable continuity.
        // TODO

        return nodes;
    }

    /**
     * The parse to tree {@link WorkflowGraph} from {@link OperatorNode} flat
     * list.
     * 
     * @param nodes
     * @return
     * @see https://www.java-success.com/00-%E2%99%A6-creating-tree-list-flattening-back-list-java/
     */
    public static WorkflowGraph from(List<OperatorNode> nodes) {
        if (isNull(nodes)) {
            return null;
        }
        validateEffective(nodes);

        // OperatorNode startNode = nodes.stream().filter(n -> n instanceof
        // StartNode).collect(toList()).stream().findFirst().get();
        // WorkflowGraph graph = new WorkflowGraph(startNode);

        List<WorkflowGraph> originalNodes = safeList(nodes).stream().map(n -> new WorkflowGraph(n)).collect(toList());

        // Save all nodes to a map
        Map<String, WorkflowGraph> tmpNodes = new HashMap<>();
        for (WorkflowGraph current : originalNodes) {
            tmpNodes.put(current.getId(), current);
        }

        // loop and assign parent/child relationships
        for (WorkflowGraph current : originalNodes) {
            String parentId = current.getParentId();
            if (!isBlank(parentId)) {
                WorkflowGraph parent = tmpNodes.get(parentId);
                if (nonNull(parent)) {
                    current.parent = parent;
                    parent.getChildrens().add(current);
                    tmpNodes.put(parentId, parent);
                    tmpNodes.put(current.getId(), current);
                }
            }
        }

        // find the root. (start node)
        WorkflowGraph root = null;
        for (WorkflowGraph node : tmpNodes.values()) {
            if (isNull(node.parent)) {
                root = node;
                break;
            }
        }

        return root;
    }

}
