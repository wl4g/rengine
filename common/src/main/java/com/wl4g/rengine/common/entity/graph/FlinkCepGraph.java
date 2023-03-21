/**
  * Copyright 2023 bejson.com 
  */
package com.wl4g.rengine.common.entity.graph;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.rengine.common.entity.graph.WorkflowGraph.GraphBase;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link FlinkCepGraph}
 * 
 * @author James Wong
 * @version 2023-03-13
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
public class FlinkCepGraph extends GraphBase {
    private static final long serialVersionUID = 3180228245679360435L;

    private @NotBlank String name;
    private @NotNull Quantifier quantifier;
    private @Nullable BaseCondition condition;
    private @NotEmpty List<Node> nodes;
    private @Nullable List<Edge> edges;
    private @Nullable Window window;
    private @NotNull AfterMatchStrategy afterMatchStrategy;
    private @NotNull PatternNodeType type;
    private int version;

    @SuppressWarnings("unchecked")
    @Override
    public <T extends GraphBase> T validate() {
        hasTextOf(name, "name");
        notNullOf(quantifier, "quantifier");

        quantifier.validate();
        notEmptyOf(nodes, "nodes");
        safeList(nodes).stream().forEach(n -> n.validate());

        // notEmptyOf(edges, "edges");
        safeList(edges).stream().forEach(e -> e.validate());

        notNullOf(afterMatchStrategy, "afterMatchStrategy");
        afterMatchStrategy.validate();

        notNullOf(type, "type");
        return (T) this;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class Quantifier {
        private @NotNull ConsumingStrategy consumingStrategy;
        private @Nullable Times times;
        private @Nullable BaseCondition untilCondition;
        private @NotEmpty List<QuantifierProperty> details;

        public Quantifier validate() {
            notNullOf(consumingStrategy, "consumingStrategy");
            notEmptyOf(details, "details");
            return this;
        }
    }

    /**
     * {@link org.apache.flink.cep.pattern.Quantifier.Times}
     */
    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class Times {
        private int from;
        private int to;
        private @Nullable Time windowTime;

        private Times(int from, int to, @Nullable Time windowTime) {
            isTrue(from > 0, "The from should be a positive number greater than 0.");
            isTrue(to >= from, "The to should be a number greater than or equal to from: " + from + ".");
            this.from = from;
            this.to = to;
            this.windowTime = windowTime;
        }

        public Times validate() {
            return this;
        }

        public static Times of(int from, int to, @Nullable Time windowTime) {
            return new Times(from, to, windowTime);
        }

        public static Times of(int times, @Nullable Time windowTime) {
            return new Times(times, times, windowTime);
        }
    }

    /**
     * {@link org.apache.flink.streaming.api.windowing.time.Time}
     */
    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Time {
        private @NotNull TimeUnit unit;
        private long size;

        public static Time millis(long milliseconds) {
            return new Time(TimeUnit.MILLISECONDS, milliseconds);
        }

        public static Time seconds(long seconds) {
            return new Time(TimeUnit.SECONDS, seconds);
        }

        public static Time minutes(long minutes) {
            return new Time(TimeUnit.MINUTES, minutes);
        }

        public static Time hours(long hours) {
            return new Time(TimeUnit.HOURS, hours);
        }

        public static Time days(long days) {
            return new Time(TimeUnit.DAYS, days);
        }
    }

    /**
     * {@link org.apache.flink.cep.dynamic.impl.json.deserializer.ConditionSpecStdDeserializer#deserialize()}
     * {@link org.apache.flink.cep.dynamic.impl.json.util.CepJsonUtils#objectMapper}
     */
    @Schema(oneOf = { AviatorCondition.class, ClassCondition.class }, discriminatorProperty = "type")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
    @JsonSubTypes({ @Type(value = AviatorCondition.class, name = "AVIATOR"),
            @Type(value = ClassCondition.class, name = "CLASS") })
    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public abstract static class BaseCondition {
        @Schema(name = "type", implementation = ConditionType.class)
        @JsonProperty(value = "type", access = Access.WRITE_ONLY)
        @NotNull
        private ConditionType type;
    }

    /**
     * {@link org.apache.flink.cep.dynamic.impl.json.spec.AviatorConditionSpec}
     * {@link org.apache.flink.cep.dynamic.condition.AviatorCondition}
     */
    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class AviatorCondition extends BaseCondition {
        private @NotBlank String expression;

        public AviatorCondition validate() {
            hasTextOf(expression, "expression");
            return this;
        }
    }

    /**
     * {@link org.apache.flink.cep.dynamic.impl.json.spec.ClassConditionSpec}
     */
    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class ClassCondition extends BaseCondition {
        private @NotBlank String className;
        private List<BaseCondition> nestedConditions;
        private String subClassName;

        public ClassCondition validate() {
            hasTextOf(className, "className");
            return this;
        }
    }

    //// @formatter:off
    //
    ///**
    // * {@link org.apache.flink.cep.dynamic.impl.json.spec.RichCompositeConditionSpec}
    // */
    //@Getter
    //@Setter
    //@SuperBuilder
    //@ToString
    //@NoArgsConstructor
    //public static class RichCompositeCondition extends ClassCondition {
    //    private List<BaseCondition> nestedConditions;
    //}
    //
    ///**
    // * {@link org.apache.flink.cep.dynamic.impl.json.spec.RichAndConditionSpec}
    // */
    //@Getter
    //@Setter
    //@SuperBuilder
    //@ToString
    //@NoArgsConstructor
    //public static class RichAndCondition extends RichCompositeCondition {
    //}
    //
    ///**
    // * {@link org.apache.flink.cep.dynamic.impl.json.spec.RichAndConditionSpec}
    // */
    //@Getter
    //@Setter
    //@SuperBuilder
    //@ToString
    //@NoArgsConstructor
    //public static class RichOrCondition extends RichCompositeCondition {
    //}
    //
    ///**
    // * {@link org.apache.flink.cep.dynamic.impl.json.spec.RichAndConditionSpec}
    // */
    //@Getter
    //@Setter
    //@SuperBuilder
    //@ToString
    //@NoArgsConstructor
    //public static class RichNotCondition extends RichCompositeCondition {
    //}
    //
    //
    ///**
    // * {@link org.apache.flink.cep.dynamic.impl.json.spec.SubTypeConditionSpec}
    // */
    //@Getter
    //@Setter
    //@SuperBuilder
    //@ToString
    //@NoArgsConstructor
    //public static class SubTypeCondition extends ClassCondition {
    //    private String subClassName;
    //}
    //
    //// @formatter:on

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class Node {
        private @NotBlank String name;
        private @NotNull Quantifier quantifier;
        private @NotNull BaseCondition condition;
        private @NotNull PatternNodeType type;
        private @Nullable @Default Map<String, Object> attributes = new HashMap<>();

        public Node validate() {
            hasTextOf(name, "name");
            notNullOf(quantifier, "quantifier");
            notNullOf(condition, "condition");
            notNullOf(type, "type");
            return this;
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class Edge {
        private @NotBlank String source;
        private @NotBlank String target;
        private @NotBlank ConsumingStrategy type;
        private @Nullable @Default Map<String, Object> attributes = new HashMap<>();

        public Edge validate() {
            hasTextOf(source, "source");
            hasTextOf(target, "target");
            notNullOf(type, "type");
            return this;
        }
    }

    /**
     * {@link org.apache.flink.cep.dynamic.impl.json.spec.AfterMatchSkipStrategySpec#toAfterMatchSkipStrategy}
     */
    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class AfterMatchStrategy {
        private @NotNull AfterMatchSkipStrategyType type;
        private String patternName;

        public AfterMatchStrategy validate() {
            notNullOf(type, "type");
            return this;
        }
    }

    /**
     * {@link org.apache.flink.cep.dynamic.impl.json.spec.AfterMatchSkipStrategySpec.AfterMatchSkipStrategyType}
     */
    public static enum AfterMatchSkipStrategyType {
        NO_SKIP, SKIP_TO_NEXT, SKIP_PAST_LAST_EVENT, SKIP_TO_FIRST, SKIP_TO_LAST;
    }

    /**
     * {@link org.apache.flink.cep.dynamic.impl.json.spec.NodeSpec.QuantifierProperty}
     */
    public static enum QuantifierProperty {
        SINGLE, LOOPING, TIMES, OPTIONAL, GREEDY
    }

    /**
     * {@link org.apache.flink.cep.dynamic.impl.json.spec.NodeSpec.ConsumingStrategy}
     */
    public static enum ConsumingStrategy {
        STRICT, SKIP_TILL_NEXT, SKIP_TILL_ANY, NOT_FOLLOW, NOT_NEXT
    }

    /**
     * {@link org.apache.flink.cep.dynamic.impl.json.spec.NodeSpec.PatternNodeType}
     */
    public static enum PatternNodeType {
        // ATOMIC Node is the basic Pattern
        ATOMIC,
        // COMPOSITE Node is a Graph
        COMPOSITE
    }

    /**
     * {@link org.apache.flink.cep.dynamic.impl.json.spec.ConditionSpec.ConditionType}
     * {@link org.apache.flink.cep.dynamic.impl.json.deserializer.ConditionSpecStdDeserializer#deserialize()}
     * {@link org.apache.flink.cep.dynamic.impl.json.util.CepJsonUtils#objectMapper}
     */
    public static enum ConditionType {
        AVIATOR, CLASS
    }

    /**
     * {@link org.apache.flink.cep.dynamic.impl.json.spec.WindowSpec}
     */
    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class Window {
        private @NotNull WithinType type;
        private @NotNull Time time;
    }

    /**
     * {@link org.apache.flink.cep.pattern.WithinType}
     */
    public static enum WithinType {
        // Interval corresponds to the maximum time gap between the previous and
        // current event.
        PREVIOUS_AND_CURRENT,
        // Interval corresponds to the maximum time gap between the first and
        // last event.
        FIRST_AND_LAST;
    }

}