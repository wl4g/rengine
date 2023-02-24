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
import static com.wl4g.rengine.common.entity.WorkflowGraph.LogicalType.ALL_AND;
import static com.wl4g.rengine.common.entity.WorkflowGraph.LogicalType.ALL_OR;
import static com.wl4g.rengine.common.entity.WorkflowGraph.LogicalType.AND;
import static com.wl4g.rengine.common.entity.WorkflowGraph.LogicalType.OR;
import static com.wl4g.rengine.common.entity.WorkflowGraph.NodeType.BOOT;
import static com.wl4g.rengine.common.entity.WorkflowGraph.NodeType.FAILBACK;
import static com.wl4g.rengine.common.entity.WorkflowGraph.NodeType.LOGICAL;
import static com.wl4g.rengine.common.entity.WorkflowGraph.NodeType.PROCESS;
import static com.wl4g.rengine.common.entity.WorkflowGraph.NodeType.RELATION;
import static com.wl4g.rengine.common.entity.WorkflowGraph.NodeType.RUN;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.rengine.common.entity.WorkflowGraph;
import com.wl4g.rengine.common.entity.WorkflowGraph.BaseNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.BootNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.FailbackNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.LogicalNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.NodeConnection;
import com.wl4g.rengine.common.entity.WorkflowGraph.NodeType;
import com.wl4g.rengine.common.entity.WorkflowGraph.ProcessNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.RelationNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.RunNode;
import com.wl4g.rengine.common.exception.ExecutionGraphException;
import com.wl4g.rengine.common.exception.InvalidNodeRelationException;
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
    private @NotBlank String id;
    private @NotBlank String name;
    private Integer priority;
    private String prevId;
    private ExecutionGraph<?> prev;

    public ExecutionGraph(@NotNull BaseNode<?> node) {
        notNullOf(node, "node");
        this.id = hasTextOf(node.getId(), "id");
        this.name = hasTextOf(node.getName(), "name");
        // If the current node is a child of a logical node, priority is must
        // required.
        this.priority = node.getPriority();
    }

    /**
     * The parse to tree {@link ExecutionGraph} from {@link BaseNode<?>} flat
     * list.
     * 
     * @param graph
     * @return
     * @see https://www.java-success.com/00-%E2%99%A6-creating-tree-list-flattening-back-list-java/
     */
    public static ExecutionGraph<?> from(@NotNull WorkflowGraph graph) {
        notNullOf(graph, "workflowGraph");
        graph.validateForBasic();

        final List<BaseOperator<?>> flatNodes = safeList(graph.getNodes()).stream().map(n -> {
            switch (NodeType.of(n.getType())) {
            case BOOT:
                return new BootOperator((BootNode) n);
            case PROCESS:
                return new ProcessOperator((ProcessNode) n);
            case RELATION:
                return new RelationOperator((RelationNode) n);
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

        // TODO for validate?
        // Map<String, ExecutionGraph<?>> flatNodeMap =
        // safeList(flatNodes).stream().collect(toMap(n -> n.getId(), l -> l));

        // Coonvert to node connections.
        final Map<String, String> toConnectionMap = new LinkedHashMap<>();
        for (NodeConnection link : safeList(graph.getConnections())) {
            final String from = toConnectionMap.get(link.getTo());
            if (isBlank(from)) {
                toConnectionMap.put(link.getTo(), link.getFrom());
            } else { // The end operator
                // TODO validate?
                //// @formatter:off
                //  ExecutionGraph<?> end = flatNodeMap.get(link.getTo());
                //  if (!(end instanceof EndOperator)) {
                //      throw new InvalidNodeRelationException(format(
                //              "Invalid node connection relationship, only end nodes support multiple inputs. - ", end.getId()));
                //  }
                //// @formatter:on
            }
        }

        // Transform to childrens tree. (without end operator)
        final Map<String, BaseOperator<?>> treeNodes = new HashMap<>();
        for (BaseOperator<?> current : flatNodes) {
            current.setPrevId(toConnectionMap.get(current.getId()));
            treeNodes.put(current.getId(), current);
        }

        // Check the connection is valid.
        final List<String> invalidConnections = toConnectionMap.entrySet()
                .stream()
                .filter(e -> !treeNodes.containsKey(e.getKey()) || !treeNodes.containsKey(e.getValue()))
                .map(e -> format("(to %s from %s)", e.getValue(), e.getKey()))
                .collect(toList());
        if (!invalidConnections.isEmpty()) {
            throw new InvalidNodeRelationException(format("Invalid the node connection for : %s", invalidConnections));
        }

        // Set Up the tree children's parent.
        for (BaseOperator<?> current : flatNodes) {
            String prevId = current.getPrevId();
            if (!isBlank(prevId)) {
                final BaseOperator<?> prev = treeNodes.get(prevId);
                if (nonNull(prev)) {
                    current.setPrev(prev);
                    if (prev instanceof LogicalOperator) {
                        ((LogicalOperator<?>) prev).getNexts().add(current);
                    }
                    if (prev instanceof SingleNextOperator) {
                        // TODO validate?
                        //// @formatter:off
                        //  if (nonNull(prev.getNext())) {
                        //      throw new InvalidNodeRelationException(format(
                        //              "The next node of a non-relationship node is not allowed to have more than one of prev.id : %s",
                        //              prev.getId()));
                        //  }
                        //// @formatter:on
                        ((SingleNextOperator<?>) prev).setNext(current);
                    }
                    treeNodes.put(prevId, prev);
                    treeNodes.put(current.getId(), current);
                }
            }
        }

        // Find the root node (start/boot).
        ExecutionGraph<?> root = null;
        for (ExecutionGraph<?> node : treeNodes.values()) {
            if (node instanceof SingleNextOperator) {
                if (isNull(((SingleNextOperator<?>) node).getPrev())) {
                    root = node;
                    break;
                }
            }
        }

        // Notice: Each level of the node tree needs to be sorted separately to
        // ensure correctness when performing logical operations. For example:
        // the nodes that perform logical operations, the consequences of 'if (A
        // && B)' and 'if (B && A)' are completely different.
        for (BaseOperator<?> current : flatNodes) {
            if (current instanceof LogicalOperator) {
                final List<BaseOperator<?>> nexts = safeList(((LogicalOperator<?>) current).getNexts());
                Collections.sort(nexts, (o1, o2) -> {
                    Assert2.notNull(o1.getPriority(), format("priority is missing of (%s, %s)", o1.getId(), o1.getName()));
                    Assert2.notNull(o2.getPriority(), format("priority is missing of (%s, %s)", o1.getId(), o2.getName()));
                    return o1.getPriority() - o2.getPriority();
                });
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

        public abstract String getType();

        @Override
        public ExecutionGraphResult apply(ExecutionGraphContext context) {
            beforeExecution(context);
            ExecutionGraphResult result = execute(context);
            afterExecution(context, result);
            return result;
        }

        void beforeExecution(ExecutionGraphContext context) {
            // Add tracing
            if (context.getParameter().isTrace()) {
                context.beginTrace(this);
            }
        }

        void afterExecution(ExecutionGraphContext context, ExecutionGraphResult result) {
            // Add tracing..
            if (context.getParameter().isTrace()) {
                context.endTrace(this, result);
            }

            // Sets last result.
            context.setLastResult(result);
        }

        abstract ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context);

        protected ExecutionGraphResult doExecute(@NotNull final ExecutionGraphContext context) {
            try {
                // Setup current node info.
                context.setCurrentNode(this);

                // Actual execution rule script.
                return context.getHandler().apply(context);
            } catch (Exception e) {
                throw new ExecutionGraphException(this, e);
            }
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true, exclude = { "next" })
    public static abstract class SingleNextOperator<E extends SingleNextOperator<?>> extends BaseOperator<E> {
        private BaseOperator<?> next;

        public SingleNextOperator(@NotNull BaseNode<?> node) {
            super(node);
        }
    }

    /**
     * The bootstrap operator node, responsible for execution start and end.
     */
    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class BootOperator extends SingleNextOperator<BootOperator> {
        public BootOperator(@NotNull BootNode node) {
            super(node);
        }

        @Override
        public String getType() {
            return BOOT.name();
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
    public static class ProcessOperator extends SingleNextOperator<ProcessOperator> implements IRunOperator {
        private @NotBlank Long ruleId;

        public ProcessOperator(@NotNull ProcessNode node) {
            super(node);
            this.ruleId = notNullOf(node.getRuleId(), "ruleId");
        }

        @Override
        public String getType() {
            return PROCESS.name();
        }

        @Override
        public ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context) {
            notNullOf(context, "context");

            // Run script rule handler.
            final ExecutionGraphResult result = doExecute(context);
            if (result.getReturnState() == ReturnState.TRUE) {
                if (nonNull(getNext())) {
                    return getNext().apply(context);
                }
                return result;
            }

            return new ExecutionGraphResult(ReturnState.FALSE, result.getValueMap());
        }
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class RelationOperator extends ProcessOperator {
        public RelationOperator(@NotNull RelationNode node) {
            super(node);
        }

        @Override
        public String getType() {
            return RELATION.name();
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
    public static class FailbackOperator extends ProcessOperator {

        public FailbackOperator(@NotNull FailbackNode node) {
            super(node);
        }

        @Override
        public String getType() {
            return FAILBACK.name();
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
                return doExecute(context);
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

        @Override
        public String getType() {
            return LOGICAL.name();
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
        public String getType() {
            return AND.name();
        }

        @Override
        public ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context) {
            notNullOf(context, "context");
            ExecutionGraphResult result = null;
            for (BaseOperator<?> sub : safeList(getNexts())) {
                result = sub.apply(context);
                // If all children return true, true is finally
                // returned. If any node returns false, it ends and
                // returns.
                if (result.getReturnState() == ReturnState.FALSE) {
                    return new ExecutionGraphResult(ReturnState.FALSE, result.getValueMap());
                }
            }
            return new ExecutionGraphResult(ReturnState.TRUE, result.getValueMap());
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
        public String getType() {
            return OR.name();
        }

        @Override
        public ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context) {
            notNullOf(context, "context");
            ExecutionGraphResult result = null;
            for (BaseOperator<?> sub : safeList(getNexts())) {
                result = sub.apply(context);
                // If any child returns true, it will eventually return
                // true.
                if (result.getReturnState() == ReturnState.TRUE) {
                    return new ExecutionGraphResult(ReturnState.TRUE, result.getValueMap());
                }
            }
            return new ExecutionGraphResult(ReturnState.FALSE, result.getValueMap());
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
        public String getType() {
            return ALL_AND.name();
        }

        @Override
        public ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context) {
            notNullOf(context, "context");
            Boolean hasFalse = null;
            ExecutionGraphResult result = null;
            for (BaseOperator<?> sub : safeList(getNexts())) {
                result = sub.apply(context);
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
                return new ExecutionGraphResult(ReturnState.FALSE, result.getValueMap());
            }
            return new ExecutionGraphResult(ReturnState.TRUE, result.getValueMap());
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
        public String getType() {
            return ALL_OR.name();
        }

        @Override
        public ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context) {
            notNullOf(context, "context");
            Boolean hasTrue = null;
            ExecutionGraphResult result = null;
            for (BaseOperator<?> sub : safeList(getNexts())) {
                result = sub.apply(context);
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
                return new ExecutionGraphResult(ReturnState.TRUE, result.getValueMap());
            }
            return new ExecutionGraphResult(ReturnState.FALSE, result.getValueMap());
        }
    }

    public static interface IRunOperator {
        Long getRuleId();
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
    public static class RunOperator extends BaseOperator<RunOperator> implements IRunOperator {
        private @NotBlank Long ruleId;

        public RunOperator(@NotNull RunNode node) {
            super(node);
            this.ruleId = notNullOf(node.getRuleId(), "ruleId");
        }

        @Override
        public String getType() {
            return RUN.name();
        }

        @Override
        public ExecutionGraphResult execute(@NotNull final ExecutionGraphContext context) {
            notNullOf(context, "context");
            return doExecute(context);
        }
    }

}
