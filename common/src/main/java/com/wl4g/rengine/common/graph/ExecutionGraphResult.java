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
package com.wl4g.rengine.common.graph;

import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * {@link ExecutionGraphResult}
 * 
 * @author James Wong
 * @version 2022-09-29
 * @since v1.0.0
 */
@Getter
@ToString
public class ExecutionGraphResult {
    private final ReturnState returnState;
    private final Map<String, Object> valueMap = new LinkedHashMap<>(4);

    public ExecutionGraphResult(@NotNull final ReturnState returnState) {
        this(returnState, null);
    }

    public ExecutionGraphResult(@NotNull final ReturnState returnState, final @NotBlank String key,
            final @NotBlank String value) {
        this(returnState, singletonMap(key, value));
    }

    public ExecutionGraphResult(@NotNull final ReturnState returnState, @Nullable final Map<String, Object> valueMap) {
        this.returnState = returnState;
        if (nonNull(valueMap)) {
            this.valueMap.putAll(valueMap);
        }
    }

    public ExecutionGraphResult addValue(String key, Object value) {
        if (!isBlank(key) && nonNull(value)) {
            getValueMap().put(key, value);
        }
        return this;
    }

    public ExecutionGraphResult removeValue(String key) {
        getValueMap().get(key);
        return this;
    }

    public ExecutionGraphResult reset() {
        getValueMap().clear();
        return this;
    }

    @Getter
    @AllArgsConstructor
    @ToString(callSuper = true)
    public static enum ReturnState {
        TRUE("T"), FALSE("F");

        private final String alias;

        public static boolean isTrue(ReturnState state) {
            return nonNull(state) && state == TRUE;
        }

        public static ReturnState of(Boolean state) {
            return (nonNull(state) && state) ? TRUE : FALSE;
        }
    }
}
