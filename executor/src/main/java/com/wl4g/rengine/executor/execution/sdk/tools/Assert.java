/*
 * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>>
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
package com.wl4g.rengine.executor.execution.sdk.tools;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.infra.common.lang.Assert2;

/**
 * {@link Assert}
 * 
 * @author James Wong
 * @date 2023-01-29
 * @since v1.0.0
 */
public class Assert {
    public static final Assert DEFAULT = new Assert();

    private Assert() {
    }

    public @HostAccess.Export void isTrue(boolean expression, String fmtMessage, Object... args) {
        Assert2.isTrue(expression, fmtMessage, args);
    }

    public @HostAccess.Export void isTrue(boolean expression, Supplier<String> messageSupplier) {
        Assert2.isTrue(expression, messageSupplier);
    }

    public @HostAccess.Export void isTrueOf(boolean expression, String argName) {
        Assert2.isTrueOf(expression, argName);
    }

    public @HostAccess.Export void isNull(Object object, String fmtMessage, Object... args) {
        Assert2.isNull(object, fmtMessage, args);
    }

    public @HostAccess.Export void isNullOf(Object object, String argName) {
        Assert2.isNullOf(object, argName);
    }

    public @HostAccess.Export <T> T notNull(Object object, String fmtMessage, Object... args) {
        return Assert2.notNull(object, fmtMessage, args);
    }

    public @HostAccess.Export <T> T notNull(Object object, Supplier<String> messageSupplier) {
        return Assert2.notNull(object, messageSupplier);
    }

    public @HostAccess.Export <T> T notNullOf(Object object, String argName) {
        return Assert2.notNullOf(object, argName);
    }

    public @HostAccess.Export <T> T hasText(String text, String fmtMessage, Object... args) {
        return Assert2.hasText(text, fmtMessage, args);
    }

    public @HostAccess.Export <T> T hasText(String text, Supplier<String> messageSupplier) {
        return Assert2.hasText(text, messageSupplier);
    }

    public @HostAccess.Export <T> T hasTextOf(String text, String argName) {
        return Assert2.hasTextOf(text, argName);
    }

    public @HostAccess.Export void doesNotContain(String textToSearch, String substring, String fmtMessage, Object... args) {
        Assert2.doesNotContain(textToSearch, substring, fmtMessage, args);
    }

    public @HostAccess.Export void doesNotContainOf(String textToSearch, String substring, String argName) {
        Assert2.doesNotContainOf(textToSearch, substring, argName);
    }

    public @HostAccess.Export <T> T notEmpty(Object[] array, String fmtMessage, Object... args) {
        return Assert2.notEmpty(array, fmtMessage, args);
    }

    public @HostAccess.Export <T> T notEmpty(Object[] array, Supplier<String> messageSupplier) {
        return Assert2.notEmpty(array, messageSupplier);
    }

    public @HostAccess.Export <T> T notEmptyOf(Object[] array, String argName) {
        return Assert2.notEmptyOf(array, argName);
    }

    public @HostAccess.Export void noNullElements(Object[] array, String fmtMessage, Object... args) {
        Assert2.noNullElements(array, fmtMessage, args);
    }

    public @HostAccess.Export void noNullElementsOf(Object[] array, String argName) {
        Assert2.noNullElements(array, argName);
    }

    public @HostAccess.Export <T> T notEmpty(Collection<?> collection, String fmtMessage, Object... args) {
        return Assert2.notEmpty(collection, fmtMessage, args);
    }

    public @HostAccess.Export <T> T notEmptyOf(Collection<?> collection, String argName) {
        return Assert2.notEmpty(collection, argName);
    }

    public @HostAccess.Export <T> T notEmpty(Map<?, ?> map, String fmtMessage, Object... args) {
        return Assert2.notEmpty(map, fmtMessage, args);
    }

    public @HostAccess.Export <T> T notEmptyOf(Map<?, ?> map, String argName) {
        return Assert2.notEmptyOf(map, argName);
    }

}