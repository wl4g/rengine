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
package com.wl4g.rengine.common.model;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder.Default;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link WorkflowExecuteResult}
 * 
 * @author James Wong
 * @version 2022-09-21
 * @since v1.0.0
 */
@Getter
@Setter
@ToString(callSuper = true, exclude = "_errorCount")
@SuperBuilder
@NoArgsConstructor
public class WorkflowExecuteResult extends BaseRequest {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    transient Long _errorCount;

    @NotNull
    @Default
    List<ResultDescription> results = new ArrayList<>(2);

    @Nullable
    String description;

    @JsonIgnore
    public Long errorCount() {
        if (isNull(_errorCount)) {
            synchronized (this) {
                if (isNull(_errorCount)) {
                    this._errorCount = safeList(results).stream().map(rd -> rd.validate()).filter(rd -> !rd.getSuccess()).count();
                }
            }
        }
        return _errorCount;
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class ResultDescription {
        // Notice: It is currently designed as a scene ID and will evolve into a
        // general feature platform in the future. Can this field be expressed
        // as feature???
        // rengine eventType (all data types as: events) is equivalent to the
        // features of the feature platform (all data types as: features)
        // eBay Features Platform see:
        // https://mp.weixin.qq.com/s/UG4VJ3HuzcBhjLcmtVpLFw
        @NotBlank
        String scenesCode;

        @NotNull
        Boolean success;

        @Nullable
        Map<String, Object> valueMap;

        @Nullable
        String reason;

        public ResultDescription validate() {
            hasTextOf(scenesCode, "scenesCode");
            notNullOf(success, "success");
            return this;
        }
    }

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_PART_SUCCESS = "PART_SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
}
