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
package com.wl4g.rengine.common.graph;

import static java.util.Collections.synchronizedMap;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import lombok.ToString;

/**
 * {@link WorkflowResult}
 * 
 * @author James Wong
 * @version 2022-09-29
 * @since v1.0.0
 */
@ToString
public class WorkflowResult {
    private final boolean isContinue;
    private Object value;
    private final Map<String, Object> attributes = synchronizedMap(new HashMap<>());

    public WorkflowResult(@NotNull boolean isContinue) {
        this.isContinue = isContinue;
    }

    public boolean isContinue() {
        return isContinue;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public WorkflowResult withValue(Object value) {
        setValue(value);
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public WorkflowResult addAttribute(String key, Object value) {
        if (!isBlank(key) && nonNull(value)) {
            getAttributes().put(key, value);
        }
        return this;
    }

    public WorkflowResult removeAttribute(String key) {
        getAttributes().get(key);
        return this;
    }

    public WorkflowResult reset() {
        this.value = null;
        getAttributes().clear();
        return this;
    }

}
