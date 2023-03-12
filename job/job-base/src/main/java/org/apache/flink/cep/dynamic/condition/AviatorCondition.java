/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.cep.dynamic.condition;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.flink.annotation.Internal;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.util.StringUtils;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.exception.CompileExpressionErrorException;
import com.googlecode.aviator.exception.ExpressionSyntaxErrorException;
import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.rengine.common.event.RengineEvent;

import lombok.Getter;

/** Condition that accepts aviator expression. */
@Getter
@Internal
public class AviatorCondition<T> extends SimpleCondition<T> {

    private static final long serialVersionUID = 1L;

    /** The filter expression of the condition. */
    private final String expression;

    private transient Expression compiledExpression;

    public AviatorCondition(String expression) {
        this(expression, null);
    }

    public AviatorCondition(String expression, @Nullable String filterField) {
        this.expression = StringUtils.isNullOrWhitespaceOnly(filterField) ? requireNonNull(expression)
                : filterField + requireNonNull(expression);
        checkExpression(this.expression);
    }

    public Expression getCompiledExpression() {
        if (isNull(compiledExpression)) {
            synchronized (this) {
                if (isNull(compiledExpression)) {
                    this.compiledExpression = notNullOf(AviatorEvaluator.compile(expression, false), "compiledExpression");
                }
            }
        }
        return compiledExpression;
    }

    @Override
    public boolean filter(T eventBean) throws Exception {
        final List<String> variableNames = getCompiledExpression().getVariableNames();
        if (CollectionUtils2.isEmpty(variableNames)) {
            return true;
        }

        final RengineEvent event = (RengineEvent) eventBean;
        final Map<String, Object> variables = new HashMap<>();
        for (String variableName : variableNames) {
            // Object value = getVariableValue(eventBean, variableName);
            final String value = event.atAsText(".".concat(variableName));
            if (nonNull(value)) {
                variables.put(variableName, value);
            } else {
                throw new IllegalArgumentException(
                        format("Could't to get path expr value '%s' from event: %s", variableName, event));
            }
        }

        return (Boolean) getCompiledExpression().execute(variables);
    }

    private void checkExpression(String expression) {
        try {
            AviatorEvaluator.validate(expression);
        } catch (ExpressionSyntaxErrorException | CompileExpressionErrorException e) {
            throw new IllegalArgumentException("The expression of AviatorCondition is invalid: " + e.getMessage());
        }
    }

    //// @formatter:off
    //private Object getVariableValue(T propertyBean, String variableName) throws NoSuchFieldException, IllegalAccessException {
    //    Field field = propertyBean.getClass().getDeclaredField(variableName);
    //    field.setAccessible(true);
    //    return field.get(propertyBean);
    //}
    //// @formatter:on

}
