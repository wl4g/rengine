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
package com.wl4g.rengine.common.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.bean.BaseBean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link SchedulingJob}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class SchedulingJob extends BaseBean {
    private static final long serialVersionUID = 1L;
    private @NotNull Long triggerId;
    private RunState runState;
    private Date schedTime;
    private Date firstFireTime;
    private String requestId;
    // private String clientId;
    // private String clienttSecret;
    private Date finishedTime;
    private List<ResultDescription> results;

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class ResultDescription {
        // TODO 目前设计为场景ID
        // 未来演进为通用特征平台，是否可将此字段表示为 feature?
        // rengine eventType(一切数据类型揭:事件) 相当于特征平台的 features (一切数据类型揭:特征)
        // 参见eBay特征平台: https://mp.weixin.qq.com/s/UG4VJ3HuzcBhjLcmtVpLFw
        @NotNull
        String scenesCode;

        @NotNull
        Boolean success;

        @Nullable
        Map<String, Object> valueMap;

        @Nullable
        String reason;
    }

    @Getter
    @ToString
    public static enum RunState {
        PREPARED, SCHED, RUNNING, SUCCESS, FAILED, KILLED
    }

}
