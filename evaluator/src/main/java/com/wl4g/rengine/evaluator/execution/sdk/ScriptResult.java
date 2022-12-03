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
package com.wl4g.rengine.evaluator.execution.sdk;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.LinkedHashMap;
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
@ToString(callSuper = true)
public class ScriptResult {
    private Boolean state;
    private Map<String, Object> valueMap;

    public @HostAccess.Export ScriptResult() {
    }

    public @HostAccess.Export ScriptResult(@NotNull Boolean state) {
        this.state = notNullOf(state, "state");
    }

    public @HostAccess.Export Boolean getState() {
        return state;
    }

    public @HostAccess.Export Map<String, Object> getValueMap() {
        if (isNull(valueMap)) {
            synchronized (this) {
                if (isNull(valueMap)) {
                    valueMap = new LinkedHashMap<>(4);
                }
            }
        }
        return valueMap;
    }

    public @HostAccess.Export ScriptResult addValue(String key, Object value) {
        if (!isBlank(key) && nonNull(value)) {
            getValueMap().put(key, value);
        }
        return this;
    }

    public @HostAccess.Export ScriptResult removeValue(String key) {
        getValueMap().get(key);
        return this;
    }

    public @HostAccess.Export ScriptResult reset() {
        this.state = null;
        getValueMap().clear();
        return this;
    }

    public @HostAccess.Export static ScriptResult newTrue() {
        return new ScriptResult(true);
    }

    public @HostAccess.Export static ScriptResult newFalse() {
        return new ScriptResult(false);
    }
}
