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
package com.wl4g.rengine.evaluator.execution.sdk;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.util.Collections.synchronizedMap;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.HostAccess;

import lombok.ToString;

/**
 * {@link ScriptResult}
 * 
 * @author James Wong
 * @version 2022-09-29
 * @since v1.0.0
 */
@ToString
public class ScriptResult {
    private final Boolean isContinue;
    private Object value;
    private final Map<String, Object> attributes = synchronizedMap(new HashMap<>());

    public @HostAccess.Export ScriptResult(@NotNull Boolean isContinue) {
        this.isContinue = notNullOf(isContinue, "isContinue");
    }

    public @HostAccess.Export boolean isContinue() {
        return isContinue;
    }

    public @HostAccess.Export Object getValue() {
        return value;
    }

    public @HostAccess.Export void setValue(Object value) {
        this.value = value;
    }

    public @HostAccess.Export ScriptResult withValue(Object value) {
        setValue(value);
        return this;
    }

    public @HostAccess.Export Map<String, Object> getAttributes() {
        return attributes;
    }

    public @HostAccess.Export ScriptResult addAttribute(String key, Object value) {
        if (!isBlank(key) && nonNull(value)) {
            getAttributes().put(key, value);
        }
        return this;
    }

    public @HostAccess.Export ScriptResult removeAttribute(String key) {
        getAttributes().get(key);
        return this;
    }

    public @HostAccess.Export ScriptResult reset() {
        this.value = null;
        getAttributes().clear();
        return this;
    }

}
