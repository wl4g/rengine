package org.apache.flink.cep.dynamic.impl.json.spec;

import java.util.EnumSet;
import javax.annotation.Nullable;
import org.apache.flink.cep.pattern.Quantifier;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

public class QuantifierSpec {
    private final Quantifier.ConsumingStrategy consumingStrategy;
    private final EnumSet<Quantifier.QuantifierProperty> properties;
    @Nullable
    private final Quantifier.Times times;
    @Nullable
    private final ConditionSpec untilCondition;

    public QuantifierSpec(@JsonProperty("consumingStrategy") Quantifier.ConsumingStrategy consumingStrategy,
            @JsonProperty("details") EnumSet<Quantifier.QuantifierProperty> properties,
            @Nullable @JsonProperty("times") Quantifier.Times times,
            @Nullable @JsonProperty("untilCondition") ConditionSpec untilCondition) {
        this.consumingStrategy = consumingStrategy;
        this.properties = properties;
        this.times = times;
        this.untilCondition = untilCondition;
    }

    public QuantifierSpec(Quantifier quantifier, Quantifier.Times times, IterativeCondition<?> untilCondition) {
        this.consumingStrategy = quantifier.getInnerConsumingStrategy();
        this.properties = EnumSet.noneOf(Quantifier.QuantifierProperty.class);
        for (Quantifier.QuantifierProperty property : Quantifier.QuantifierProperty.values()) {
            if (quantifier.hasProperty(property)) {
                this.properties.add(property);
            }
        }

        this.times = times == null ? null : Quantifier.Times.of(times.getFrom(), times.getTo(), times.getWindowTime());
        this.untilCondition = (untilCondition == null) ? null : new ClassConditionSpec(untilCondition);
    }

    public Quantifier.ConsumingStrategy getConsumingStrategy() {
        return this.consumingStrategy;
    }

    public EnumSet<Quantifier.QuantifierProperty> getProperties() {
        return this.properties;
    }

    @Nullable
    public Quantifier.Times getTimes() {
        return this.times;
    }

    @Nullable
    public ConditionSpec getUntilCondition() {
        return this.untilCondition;
    }


    
    
}
