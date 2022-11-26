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
package com.wl4g.rengine.collector.util;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.StringUtils2.replace;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import com.wl4g.infra.context.utils.expression.SpelExpressions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link SpelVariables}
 * 
 * @author James Wong
 * @version 2022-10-31
 * @since v3.0.0
 */
public class SpelVariables {
    private final Map<String, InternalVariable> variableCache = new ConcurrentHashMap<>(64);

    public Map<String, InternalVariable> getVariableCache() {
        return unmodifiableMap(variableCache);
    }

    public SpelVariables from(Map<String, String> variables) {
        Map<String, InternalVariable> parseVariables = safeMap(variables).entrySet()
                .stream()
                .collect(toMap(e -> e.getKey(),
                        e -> new InternalVariable(e.getKey(), e.getValue(), () -> (String) defaultSpel.resolve(e.getValue()))));
        variableCache.putAll(parseVariables);
        return this;
    }

    public List<String> resolve(List<String> templates) {
        return safeList(templates).stream().map(tpl -> {
            String resolvedTpl = tpl;
            for (String varName : variableCache.keySet()) {
                String replaceVarName = "{{" + varName + "}}";
                try {
                    resolvedTpl = replace(resolvedTpl, replaceVarName, variableCache.get(varName).getFunc().call());
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return resolvedTpl;
        }).collect(toList());
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class InternalVariable {
        private String name;
        private String expression;
        private Callable<String> func;
    }

    private static final SpelExpressions defaultSpel = SpelExpressions.create();

}
