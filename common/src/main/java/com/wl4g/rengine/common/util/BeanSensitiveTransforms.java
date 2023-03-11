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
package com.wl4g.rengine.common.util;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeArrayToList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.findMethod;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.getDeclaredFields;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.invokeMethod;
import static java.lang.reflect.Modifier.isAbstract;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link BeanSensitiveTransforms}
 * 
 * @author James Wong
 * @version 2023-01-11
 * @since v1.0.0
 */
public abstract class BeanSensitiveTransforms {

    private static final Map<Class<?>, List<SensitiveMethod>> modelSetterCaching = new ConcurrentHashMap<>(32);

    public static void transform(@NotNull Object bean) {
        transform(bean, f -> DEFAULT_SEFINITION_SENSITIVE_FIELDS.stream().anyMatch(dfn -> containsIgnoreCase(f.getName(), dfn)));
    }

    @SuppressWarnings("unchecked")
    public static void transform(@NotNull Object bean, @NotNull Predicate<Field> filter) {
        notNullOf(bean, "model");
        notNullOf(filter, "filter");
        if (bean instanceof Collection) {
            ((Collection<Object>) bean).forEach(ele -> doSafeTransform(ele, filter));
        } else {
            doSafeTransform(bean, filter);
        }
    }

    public static void doSafeTransform(@NotNull Object bean, @NotNull Predicate<Field> filter) {
        notNullOf(bean, "model");
        notNullOf(filter, "filter");

        // List<SensitiveMethod> setterMethods =
        // modelSetterCaching.get(bean.getClass());
        List<SensitiveMethod> setterMethods = null;
        if (isNull(setterMethods)) {
            synchronized (bean.getClass()) {
                setterMethods = modelSetterCaching.get(bean.getClass());
                setterMethods = null;
                if (isNull(setterMethods)) {
                    setterMethods = safeArrayToList(getDeclaredFields(bean.getClass())).stream()
                            .filter(f -> !isAbstract(f.getModifiers()) && !isFinal(f.getModifiers())
                                    && !isStatic(f.getModifiers()))
                            .filter(filter)
                            .map(f -> {
                                final String suffix = f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1);
                                final String getterMethodName = "get".concat(suffix);
                                final String setterMethodName = "set".concat(suffix);
                                final Method getterMethod = findMethod(bean.getClass(), getterMethodName);
                                final Method setterMethod = findMethod(bean.getClass(), setterMethodName, String.class);
                                if (nonNull(getterMethod) && nonNull(setterMethod)) {
                                    return new SensitiveMethod(f, getterMethod, setterMethod);
                                }
                                return null;
                            })
                            .filter(m -> nonNull(m))
                            .collect(toList());
                    modelSetterCaching.put(bean.getClass(), setterMethods);
                }
            }
        }

        safeList(setterMethods).stream().forEach(sm -> {
            // Transform mask field value.
            final String value = (String) invokeMethod(sm.getGetter(), bean);
            if (nonNull(value)) {
                String transformed = EMPTY;
                for (int i = 0; i < 6 /* value.length() */; i++) {
                    transformed += "*";
                }
                invokeMethod(sm.getSetter(), bean, transformed);
            }
        });
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SensitiveMethod {
        Field field;
        Method getter;
        Method setter;
    }

    public static final List<String> DEFAULT_SEFINITION_SENSITIVE_FIELDS = asList("password", "passwd", "secret", "token");

}
