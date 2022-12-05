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
package com.wl4g.rengine.client.springboot.intercept;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeArrayToList;
import static com.wl4g.infra.common.lang.Assert2.hasText;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.ClassUtils2.getMethod;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.invokeMethod;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.ReflectionUtils.makeAccessible;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.wl4g.infra.common.reflect.ObjectInstantiators;
import com.wl4g.rengine.client.core.RengineClient;
import com.wl4g.rengine.client.core.exception.ClientEvaluationException;
import com.wl4g.rengine.common.model.EvaluationResult;
import com.wl4g.rengine.common.util.IdGenUtil;

import lombok.CustomLog;

/**
 * {@link DefaultREvaluationHandler}
 * 
 * @author James Wong
 * @version 2022-11-02
 * @since v1.0.0
 */
@CustomLog
public class DefaultREvaluationHandler implements REvaluationHandler<REvaluation> {

    private final Map<Class<?>, Object> failbackCaching = new ConcurrentHashMap<>(16);
    private @Autowired Environment environment;
    private @Autowired RengineClient rengineClient;

    @Override
    public Object doIntercept(ProceedingJoinPoint jp, REvaluation annotation) throws Throwable {
        final String scenesCode = environment.resolvePlaceholders(trimToEmpty(annotation.scenesCode()));
        final long timeoutMs = annotation.timeout();
        final boolean bestEffort = annotation.bestEffort();
        final String paramsTemplate = annotation.paramsTemplate();
        final Class<? extends Function<Throwable, EvaluationResult>> failbackClazz = annotation.failback();
        hasText(scenesCode, "The evaluation parameter for scenesCode is missing.");
        isTrue(timeoutMs > 0, "The evaluation timeoutMs must > 0.");
        hasText(paramsTemplate, "The evaluation parameter for paramsTemplate is missing.");

        final String requestId = IdGenUtil.next();
        final Function<Throwable, EvaluationResult> failback = getFailback(failbackClazz);
        final Map<String, Object> args = buildEvaluateParams(jp, annotation, paramsTemplate);

        final EvaluationResult result = rengineClient.evaluate(requestId, singletonList(scenesCode), timeoutMs, bestEffort, args,
                failback);
        log.debug("Evaluated of result: {}, {} => {}", result, scenesCode, args);

        // Assertion evaluation result.
        if (result.getErrorCount() > 0) {
            throw new ClientEvaluationException(requestId, singletonList(scenesCode), timeoutMs, bestEffort,
                    format("Unable to operation, detected risk in your environment."));
        }

        return jp.proceed();
    }

    @SuppressWarnings("unchecked")
    protected Function<Throwable, EvaluationResult> getFailback(
            Class<? extends Function<Throwable, EvaluationResult>> failbackClazz) {
        if (isNull(failbackClazz)) {
            return null;
        }
        Function<Throwable, EvaluationResult> failback = (Function<Throwable, EvaluationResult>) failbackCaching
                .get(failbackClazz);
        if (isNull(failback)) {
            synchronized (this) {
                failback = (Function<Throwable, EvaluationResult>) failbackCaching.get(failbackClazz);
                if (isNull(failback)) {
                    failbackCaching.put(failbackClazz, failback = ObjectInstantiators.newInstance(failbackClazz));
                }
            }
        }
        return failback;
    }

    protected Map<String, Object> buildEvaluateParams(ProceedingJoinPoint jp, REvaluation annotation, String paramsTemplate) {
        return parseParamsTemplate(safeArrayToList(jp.getArgs()), (MethodSignature) jp.getSignature(), paramsTemplate);
    }

    /**
     * Parse parameters template. </br>
     * 
     * <p>
     * for example:
     * 
     * <pre>
     *  params template: "{{  userId=#0.userId, goodId=#0.goodId, count=#1 }}"
     * 
     *       definition: void myCreate(CreateOrder order) {}
     * 
     *             call: myCreate(CreateOrder.builder()
     *                      .userId("u100101")
     *                      .goodId("G202202082139942")
     *                      .build(),
     *                      2) {}
     * 
     *      parsed args: { "userId" => "u100101", "goodId" => "G202202082139942", "count" => 2 }
     * </pre>
     * </p>
     * 
     * @param arguments
     * @param signature
     * @param paramsTemplate
     * @return
     */
    public static Map<String, Object> parseParamsTemplate(
            @NotNull List<Object> arguments,
            @NotNull MethodSignature signature,
            @NotBlank String paramsTemplate) {
        notNullOf(arguments, "arguments");
        notNullOf(signature, "signature");
        hasTextOf(paramsTemplate, "paramsTemplate");

        final List<Class<?>> parameterTypes = safeArrayToList(signature.getMethod().getParameterTypes());
        isTrue(parameterTypes.size() == arguments.size(),
                "Synatax error, the parameters count must be equals arguments size. but %s == %s ?", parameterTypes.size(),
                arguments.size());

        isTrue(startsWith(paramsTemplate, "{{") && endsWith(paramsTemplate, "}}"),
                "Synatax error, the parameters template must be followed by open '{{' and '}}' end.");
        paramsTemplate = paramsTemplate.substring(2, paramsTemplate.length() - 2);

        // Parse template parameters.
        final Map<String, Object> args = new LinkedHashMap<>(arguments.size());
        for (String param : split(paramsTemplate, ",")) {
            final String[] keyValueParts = split(param, "=");
            isTrue(keyValueParts.length == 2, "Synatax error, the parameters template parts of %s", asList(keyValueParts));

            final String name = trimToEmpty(keyValueParts[0]);
            final String placeholder = trimToEmpty(keyValueParts[1]);
            isTrue(startsWith(placeholder, "#"), "Synatax error, the parameter template value placeholder of %s, must '#' start.",
                    placeholder);
            final String indexIndex = placeholder.substring(1, Math.max(placeholder.indexOf("."), 2));
            try {
                Integer.parseInt(indexIndex);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        format("Synatax error, the parameter template value placeholder index of '%s'.", indexIndex), e);
            }
            final String indexExpr = placeholder.substring(1, Math.max(placeholder.indexOf("."), placeholder.length()));
            isTrue(isNull(args.putIfAbsent(name, indexExpr)), "The duplicate params template name: '%s' -> '%s'", name,
                    placeholder);
        }

        // Setup actual parameter values.
        final Iterator<Entry<String, Object>> it = args.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Object> entry = it.next();
            final String indexExpr = (String) entry.getValue();
            if (contains(indexExpr, ".")) {
                final String[] indexExprParts = split(indexExpr, ".");
                isTrue(indexExprParts.length == 2, "Synatax error, the parameters template parts of %s", asList(indexExprParts));
                final int paramIndex = Integer.parseInt(indexExprParts[0]);
                final String paramProperty = indexExprParts[1];
                final Class<?> paramType = parameterTypes.get(paramIndex);
                final Object paramValue = arguments.get(paramIndex);
                if (nonNull(paramValue)) {
                    final String getMethodName = "get"
                            + paramProperty.substring(0, 1).toUpperCase().concat(paramProperty.substring(1));
                    final Method getMethod = getMethod(paramType, getMethodName);
                    notNull(getMethod, "Could not found exprssion get method for : {}.{}()", paramType.getName(), getMethodName);
                    makeAccessible(getMethod);
                    final Object propertyValue = invokeMethod(getMethod, arguments.get(paramIndex));
                    entry.setValue(isNull(propertyValue) ? "" : propertyValue.toString());
                }
            } else {
                final int paramIndex = Integer.parseInt(indexExpr);
                final Object paramValue = arguments.get(paramIndex);
                entry.setValue(paramValue);
            }
        }

        return args;
    }

}
