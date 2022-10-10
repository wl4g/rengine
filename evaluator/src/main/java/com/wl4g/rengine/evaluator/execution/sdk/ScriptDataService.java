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

import static com.wl4g.infra.common.collection.CollectionUtils2.isEmpty;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.rengine.evaluator.service.AggregationService;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * {@link ScriptDataService}
 * 
 * @author James Wong
 * @version 2022-10-10
 * @since v3.0.0
 */
@ToString
@AllArgsConstructor
public class ScriptDataService {

    private final AggregationService aggregationService;

    public @HostAccess.Export List<Map<String, Object>> findAggregates(
            @NotBlank String eventType,
            @Nullable Map<String, Object> query) {
        hasTextOf(eventType, "eventType");

        // Merge query parameters.
        Map<String, Object> _query = new HashMap<>();
        if (!isEmpty(query)) {
            _query.putAll(query);
        }

        // TODO other parameters
        _query.put("eventType", eventType);

        return aggregationService.findList(_query);
    }

}
