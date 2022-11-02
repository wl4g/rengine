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
package com.wl4g.rengine.client.springboot.intercept;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeArrayToList;
import static com.wl4g.infra.common.lang.Assert2.hasText;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.wl4g.rengine.client.core.RengineClient;
import com.wl4g.rengine.common.exception.EvaluationException;
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

    private @Autowired Environment environment;
    private @Autowired RengineClient rengineClient;

    @Override
    public Object doIntercept(ProceedingJoinPoint jp, REvaluation annotation) throws Throwable {
        final String scenesCode = environment.resolvePlaceholders(trimToEmpty(annotation.scenesCode()));
        final long timeoutMs = annotation.timeout();
        final boolean bestEffort = annotation.bestEffort();
        final String paramsTemplate = annotation.paramsTemplate();
        hasText(scenesCode, "The evaluation parameter for scenesCode is missing.");
        isTrue(timeoutMs > 0, "The evaluation timeoutMs must > 0.");
        hasText(paramsTemplate, "The evaluation parameter for paramsTemplate is missing.");
        final String requestId = IdGenUtil.next();

        final Map<String, String> args = buildEvaluateParams(jp, annotation, paramsTemplate);
        final EvaluationResult result = rengineClient.evaluate(requestId, scenesCode, bestEffort, timeoutMs, args);
        log.debug("Evaluated of result: {}, {} => {}", result, scenesCode, args);

        // Assertion evaluation result.
        if (result.getErrorCount() > 0) {
            throw new EvaluationException(requestId, scenesCode,
                    format("Unable to operation, detected risk in your environment."));
        }

        return jp.proceed();
    }

    protected Map<String, String> buildEvaluateParams(ProceedingJoinPoint jp, REvaluation annotation, String paramsTemplate) {
        return parseParamsTemplate(safeArrayToList(jp.getArgs()), paramsTemplate);
    }

    /**
     * Parse parameters template. </br>
     * 
     * <p>
     * for example:
     * 
     * <pre>
     *  params template: "{{  userId=#0, goodId=#1 }}"
     * 
     *       definition: void myCreate(String userId, String goodId, int count) {}
     * 
     *             call: myCreate("u100101", "G202202082139942", 2) {}
     * 
     *      parsed args: { "userId" => "u100101", "goodId" => "G202202082139942" }
     * </pre>
     * </p>
     * 
     * @param arguments
     * @param paramsTemplate
     * @return
     */
    static Map<String, String> parseParamsTemplate(List<Object> arguments, String paramsTemplate) {
        isTrue(startsWith(paramsTemplate, "{{") && endsWith(paramsTemplate, "}}"),
                "The synatax error, the parameters template must be followed by open '{{' and '}}' end.");
        paramsTemplate = paramsTemplate.substring(2, paramsTemplate.length() - 2);

        final Map<String, String> args = new LinkedHashMap<>(arguments.size());
        // Parse template parameters.
        for (String param : split(trimToEmpty(paramsTemplate), ",")) {
            final String[] keyValueParts = split(param, "=");
            isTrue(keyValueParts.length == 2, "The synatax error, the parameters template part of %s", asList(keyValueParts));

            final String name = trimToEmpty(keyValueParts[0]);
            final String placeholder = trimToEmpty(keyValueParts[1]);
            isTrue(startsWith(placeholder, "#"),
                    "The synatax error, the parameter template value placeholder of %s, must '#' start.", placeholder);
            final String parseIndexStr = placeholder.substring(1);
            try {
                Integer.parseInt(parseIndexStr);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        format("The synatax error, the parameter template value placeholder index of %s.", parseIndexStr), e);
            }
            isTrue(!args.values().contains(parseIndexStr), "The duplicate params template placeholder index: %s", parseIndexStr);
            isTrue(isNull(args.putIfAbsent(name, parseIndexStr)), "The duplicate params template name: %s", name);
        }

        // Setup actual parameter values.
        final Iterator<Entry<String, String>> it = args.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            final int paramIndex = Integer.parseInt(entry.getValue());
            final Object paramValue = arguments.get(paramIndex);
            entry.setValue(isNull(paramValue) ? "" : paramValue.toString());
        }

        return args;
    }

}
