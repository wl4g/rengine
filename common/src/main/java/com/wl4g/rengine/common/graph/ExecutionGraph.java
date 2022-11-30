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
package com.wl4g.rengine.common.graph;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.rengine.common.entity.WorkflowGraph;
import com.wl4g.rengine.common.entity.WorkflowGraph.BaseNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.BootNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.FailbackNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.LogicalNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.NodeConnection;
import com.wl4g.rengine.common.entity.WorkflowGraph.NodeType;
import com.wl4g.rengine.common.entity.WorkflowGraph.ProcessNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.RunNode;
import com.wl4g.rengine.common.exception.InvalidNodeRelationshipException;
import com.wl4g.rengine.common.graph.ExecutionGraphResult.ReturnState;

import lombok.CustomLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The basic execution DAG (directed acyclic graph) graph node definition of
 * rule process.
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, exclude = { "prev" })
public abstract class ExecutionGraph<E extends ExecutionGraph<?>>
        implements Function<ExecutionGraphContext, ExecutionGraphResult> {
    private String id;
    private String name;
    private String prevId;
    private ExecutionGraph<?> prev;

    public ExecutionGraph(@NotNull final BaseNode<?> node) {
        notNullOf(node, "node");
        this.id = hasTextOf(node.getId(), "id");
        this.name = hasTextOf(node.getName(), "name");
    }

    public static List<BaseNode<?>> validateEffective(List<BaseNode<?>> nodes) {
        notNullOf(nodes, "nodes");

        // Check for duplicate node ID.
        nodes.stream()
                .collect(groupingBy(n -> n.getId()))
                .entrySet()
                .stream()
                .filter(e -> safeList(e.getValue()).size() > 1)
                .findAny()
                .ifPresent(e -> {
                    throw new InvalidNodeRelationshipException(format("Duplicate node id of : %s", e.getKey()));
                });

        // @formatter:off
//        // Check for start node.
//        List<ExecutionGraph<?>> startNodes = nodes.stream().filter(n -> n instanceof BootOperator).collect(toList());
//        if (safeList(startNodes).size() != 1) {
//            throw new InvalidNodeRelationshipException(format("There must be one and only one start node of : %s", startNodes));
//        }
//        if (!isBlank(safeList(startNodes).get(0).getPrevId())) {
//            throw new InvalidNodeRelationshipException("The prevId value of start node must be empty.");
//        }
//
//        // Check for end node.
//        List<ExecutionGraph<?>> endNodes = nodes.stream().filter(n -> n instanceof EndOperator).collect(toList());
//        if (safeList(endNodes).size() != 1) {
//            throw new InvalidNodeRelationshipException(format("There must be one and only one end node of : %s", endNodes));
//        }
//
//        // Check for start-to-end reachable continuity.
//        Map<String, ExecutionGraph<?>> nodeMap = nodes.stream().collect(toMap(n -> n.getId(), n -> n));
//        for (Entry<String, ExecutionGraph<?>> ent : nodeMap.entrySet()) {
//            ExecutionGraph<?> n = ent.getValue();
//            if (!(n instanceof BootOperator) && isNull(nodeMap.get(n.getPrevId()))) {
//                throw new InvalidNodeRelationshipException(format("Invalid node unreachable orphaned of : %s", n));
//            }
//        }
        // @formatter:on

        return nodes;
    }

    /**
     * The parse to tree {@link ExecutionGraph} from {@link BaseNode<?>} flat
     * list.
     * 
     * @param workflow
     * @return
     * @see https://www.java-success.com/00-%E2%99%A6-creating-tree-list-flattening-back-list-java/
     */
    public static ExecutionGraph<?> from(final WorkflowGraph workflow) {
        if (isNull(workflow)) {
            return null;
        }
        notNullOf(workflow.getNodes(), "workflow");

        // Validate for effective.
        validateEffective(workflow.getNodes());

        List<BaseOperator<?>> flatNodes = safeList(workflow.getNodes()).stream().map(n -> {
            switch (NodeType.of(n.getType())) {
            case BOOT:
                return new BootOperator((BootNode) n);
            case PROCESS:
                return new ProcessOperator((ProcessNode) n);
            case FAILBACK:
                return new FailbackOperator((FailbackNode) n);
            case LOGICAL:
                LogicalNode rn = (LogicalNode) n;
                switch (rn.getLogical()) {
                case AND:
                    return new AndOperator(rn);
                case OR:
                    return new OrOperator(rn);
                case ALL_AND:
                    return new AllAndOperator(rn);
                case ALL_OR:
                    return new AllOrOperator(rn);
                default:
                    throw new Error("Should't to be here");
                }
            case RUN:
                return new RunOperator((RunNode) n);
            default:
                throw new Error("Should't to be here");
            }
        }).collect(toList());

        // Map<String, ExecutionGraph<?>> flatNodeMap =
        // safeList(flatNodes).stream().collect(toMap(n -> n.getId(), l -> l));

        Map<String, String> toConnectionMap = new LinkedHashMap<>();
        for (NodeConnection link : safeList(workflow.getConnections())) {
            String from = toConnectionMap.get(link.getTo());
            if (isBlank(from)) {
                toConnectionMap.put(link.getTo(), link.getFrom());
            } else { // The end operator
                // TODO 只校验就完了?
                // @formatter:off
//                ExecutionGraph<?> end = flatNodeMap.get(link.getTo());
//                if (!(end instanceof EndOperator)) {
//                    throw new InvalidNodeRelationshipException(format(
//                            "Invalid node connection relationship, only end nodes support multiple inputs. - ", end.getId()));
//                }
                // @formatter:on
            }
        }

        // Save all nodes to a map. (without end operator)
        Map<String, ExecutionGraph<?>> treeNodes = new HashMap<>();
        for (BaseOperator<?> current : flatNodes) {
            current.setPrevId(toConnectionMap.get(current.getId()));
            treeNodes.put(current.getId(), current);
        }

        // loop and assign parent/child relationships
        for (BaseOperator<?> current : flatNodes) {
            String prevId = current.getPrevId();
            if (!isBlank(prevId)) {
                ExecutionGraph<?> prev = treeNodes.get(prevId);
                if (nonNull(prev)) {
                    current.setPrev(prev);
                    if (prev instanceof LogicalOperator) {
                        ((LogicalOperator<?>) prev).getNexts().add(current);
                    }
                    if (prev instanceof SingleOperator) {
                        // @formatter:off
//                        if (nonNull(prev.getNext())) {
//                            throw new InvalidNodeRelationshipException(format(
//                                    "The next node of a non-relationship node is not allowed to have more than one of prev.id : %s",
//                                    prev.getId()));
//                        }
                        // @formatter:on
                        ((SingleOperator<?>) prev).setNext(current);
                    }
                    treeNodes.put(prevId, prev);
                    treeNodes.put(current.getId(), current);
                }
            }
        }

        // find the root. (start/boot node)
        ExecutionGraph<?> root = null;
        for (ExecutionGraph<?> node : treeNodes.values()) {
            if (node instanceof SingleOperator) {
                if (isNull(((SingleOperator<?>) node).getPrev())) {
                    root = node;
                    break;
                }
            }
        }

        return root;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static abstract class BaseOperator<E extends BaseOperator<?>> extends ExecutionGraph<E> {

        public BaseOperator(@Nullable BaseNode<?> node) {
            super(node);
        }

        @Override
        public ExecutionGraphResult apply(ExecutionGraphContext context) {
            beforeExecution(context);
            ExecutionGraphResult result = execute(context);
            afterExecution(context, result);
            return result;
        }

        void beforeExecution(ExecutionGraphContext context) {
            if (context.getParameter().isTrace()) {
                context.beginTrace(this);
            }
        }

        void afterExecution(ExecutionGraphContext context, ExecutionGraphResult result) {
            if (context.getParameter().isTrace()) {
                context.endTrace(this, result);
            }
        }

        abstract ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context);
    }

    @Getter
    @Setter
    @ToString(callSuper = true, exclude = { "next" })
    public static abstract class SingleOperator<E extends SingleOperator<?>> extends BaseOperator<E> {
        private BaseOperator<?> next;

        public SingleOperator(@NotNull BaseNode<?> node) {
            super(node);
        }
    }

    /**
     * The bootstrap operator node, responsible for execution start and end.
     */
    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class BootOperator extends SingleOperator<BootOperator> {
        public BootOperator(@NotNull BootNode node) {
            super(node);
        }

        @Override
        public ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context) {
            notNullOf(context, "context");
            notNull(getNext(), "The next is missing of current id : %s", getId());
            try {
                context.start();
                // and other init ...
                return getNext().apply(context);
            } finally {
                context.end();
                // and other release ...
            }
        }
    }

    /**
     * The similar equivalent pseudocode such as:
     * 
     * <pre>
     * boolean process(args) {
     *   if (checkCondition(args)) {
     *      if (next) {
     *          return next.process(args)
     *      }
     *      return true
     *   }
     *   return false
     * }
     * </pre>
     */
    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class ProcessOperator extends SingleOperator<ProcessOperator> {
        private @NotBlank String ruleId;

        public ProcessOperator(@NotNull ProcessNode node) {
            super(node);
            this.ruleId = hasTextOf(node.getRuleId(), "ruleId");
        }

        @Override
        public ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context) {
            notNullOf(context, "context");

            // setup current script rule ID.
            context.setRuleId(getRuleId());

            // Run script rule handler.
            final ReturnState result = context.getHandler().apply(context);
            if (result == ReturnState.TRUE) {
                if (nonNull(getNext())) {
                    return getNext().apply(context);
                }
                return new ExecutionGraphResult(result);
            }

            return new ExecutionGraphResult(ReturnState.FALSE);
        }
    }

    /**
     * The similar equivalent pseudocode such as:
     * 
     * <pre>
     * boolean process(args) {
     *   try {
     *     return next.process(args)
     *   } catch(e) {
     *     return fallback.process(args)
     *   }
     * }
     * </pre>
     */
    @CustomLog
    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class FailbackOperator extends SingleOperator<FailbackOperator> {
        private @NotBlank String ruleId;

        public FailbackOperator(@NotNull FailbackNode node) {
            super(node);
            this.ruleId = hasTextOf(node.getRuleId(), "ruleId");
        }

        @Override
        public ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context) {
            notNullOf(context, "context");
            notNull(getNext(), "The next is missing of current id : %s", getId());

            try {
                // Run script rule handler.
                return getNext().apply(context);
            } catch (Throwable e) {
                log.debug("Failback to execute of caused by : {}", e.getMessage());
                // setup current script rule ID.
                context.setRuleId(getRuleId());
                return new ExecutionGraphResult(context.getHandler().apply(context));
            }
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static abstract class LogicalOperator<E extends LogicalOperator<?>> extends BaseOperator<E> {
        private List<BaseOperator<?>> nexts = new LinkedList<>();

        public LogicalOperator(@NotNull BaseNode<?> node) {
            super(node);
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class AndOperator extends LogicalOperator<AndOperator> {
        public AndOperator(@NotNull LogicalNode node) {
            super(node);
        }

        @Override
        public ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context) {
            notNullOf(context, "context");
            for (BaseOperator<?> sub : safeList(getNexts())) {
                final ExecutionGraphResult result = sub.apply(context);
                // If all children return true, true is finally
                // returned. If any node returns false, it ends and
                // returns.
                if (result.getReturnState() == ReturnState.FALSE) {
                    return new ExecutionGraphResult(ReturnState.FALSE);
                }
            }
            return new ExecutionGraphResult(ReturnState.TRUE);
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class OrOperator extends LogicalOperator<AndOperator> {
        public OrOperator(@NotNull LogicalNode node) {
            super(node);
        }

        @Override
        public ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context) {
            notNullOf(context, "context");
            for (BaseOperator<?> sub : safeList(getNexts())) {
                final ExecutionGraphResult result = sub.apply(context);
                // If any child returns true, it will eventually return
                // true.
                if (result.getReturnState() == ReturnState.TRUE) {
                    return new ExecutionGraphResult(ReturnState.TRUE);
                }
            }
            return new ExecutionGraphResult(ReturnState.FALSE);
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class AllAndOperator extends LogicalOperator<AndOperator> {
        public AllAndOperator(@NotNull LogicalNode node) {
            super(node);
        }

        @Override
        public ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context) {
            notNullOf(context, "context");
            Boolean hasFalse = null;
            for (BaseOperator<?> sub : safeList(getNexts())) {
                final ExecutionGraphResult result = sub.apply(context);
                // If all children return true, true is finally
                // returned. If any node returns false, it ends and
                // returns. (If the current node returns false, the
                // subsequent nodes workflowstill the execution)
                if (isNull(hasFalse) && result.getReturnState() == ReturnState.FALSE) {
                    hasFalse = true;
                }
            }
            // false if there are no child nodes, or if any child nodes return
            // false.
            if (nonNull(hasFalse) && hasFalse) {
                return new ExecutionGraphResult(ReturnState.FALSE);
            }
            return new ExecutionGraphResult(ReturnState.TRUE);
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class AllOrOperator extends LogicalOperator<AndOperator> {
        public AllOrOperator(@NotNull LogicalNode node) {
            super(node);
        }

        @Override
        public ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context) {
            notNullOf(context, "context");
            Boolean hasTrue = null;
            for (BaseOperator<?> sub : safeList(getNexts())) {
                final ExecutionGraphResult result = sub.apply(context);
                // If any child returns true, it will eventually return
                // true. (If the current node returns true, the
                // subsequent nodes will still the execution)
                if (isNull(hasTrue) || result.getReturnState() == ReturnState.TRUE) {
                    hasTrue = true;
                }
            }
            // true if there are no child nodes, or if any child nodes return
            // true.
            if (nonNull(hasTrue) && hasTrue) {
                return new ExecutionGraphResult(ReturnState.TRUE);
            }
            return new ExecutionGraphResult(ReturnState.FALSE);
        }
    }

    /**
     * The similar equivalent pseudocode such as:
     * 
     * <pre>
     * boolean process(args) {
     *   return handler.execute(args)
     * }
     * </pre>
     */
    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class RunOperator extends BaseOperator<RunOperator> {
        private @NotBlank String ruleId;

        public RunOperator(@NotNull RunNode node) {
            super(node);
            this.ruleId = hasTextOf(node.getRuleId(), "ruleId");
        }

        @Override
        public ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context) {
            notNullOf(context, "context");

            // Setup current script rule ID.
            context.setRuleId(getRuleId());

            // Actual execution rule script.
            return new ExecutionGraphResult(context.getHandler().apply(context));
        }
    }

}
