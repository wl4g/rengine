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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link EvaluationResult}
 * 
 * @author James Wong
 * @version 2022-09-21
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class EvaluationResult extends EvaluationBase {

    @NotNull
    @Default
    Integer errorCount = 0;

    @NotNull
    @Default
    List<ResultDescription> results = new ArrayList<>();

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class ResultDescription {

        // TODO 目前设计为场景ID
        // 未来演进为通用特征平台，是否可将此字段表示为 feature?
        // 特征平台的 feature 其实 rengine 就是 eventType (即前者一切数据类型揭:特征，后者一切数据类型揭:事件)
        // 参见eBay特征平台: https://mp.weixin.qq.com/s/UG4VJ3HuzcBhjLcmtVpLFw
        @NotNull
        String scenesCode;

        @NotNull
        Boolean success;

        @NotNull
        Map<String, Object> valueMap;
    }

    public static final String STATUS_PART_SUCCESS = "PartSuccess";
    public static final String STATUS_ALL_SUCCESS = "AllSuccess";
}
