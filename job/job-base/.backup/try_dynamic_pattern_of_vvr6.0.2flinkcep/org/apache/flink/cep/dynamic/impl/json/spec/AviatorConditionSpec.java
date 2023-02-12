
package org.apache.flink.cep.dynamic.impl.json.spec;

import org.apache.flink.cep.dynamic.condition.AviatorCondition;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

public class AviatorConditionSpec extends ConditionSpec {
    private final String expression;

    public AviatorConditionSpec(@JsonProperty("expression") String expression) {
        super(ConditionSpec.ConditionType.AVIATOR);
        this.expression = expression;
    }

    public String getExpression() {
        return this.expression;
    }

    @SuppressWarnings("rawtypes")
    public IterativeCondition<?> toIterativeCondition(ClassLoader classLoader) {
        return (IterativeCondition<?>) new AviatorCondition(this.expression);
    }
}
