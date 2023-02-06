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

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.infra.common.validation.EnumValue;
import com.wl4g.rengine.common.entity.ScheduleTrigger.ScheduleType;
import com.wl4g.rengine.common.model.ExecuteResult.ResultDescription;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link ScheduleJobLog}
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
public class ScheduleJobLog extends BaseBean {
    private static final long serialVersionUID = 1L;

    private @NotNull Long triggerId;
    private String jobName;
    private Date startupTime;
    private Date finishedTime;
    private Boolean success;

    public ScheduleJobLog validate() {
        return this;
    }

    @NotNull
    JogLogDetailBase<?> detail;

    @Schema(oneOf = { ExecutionScheduleJobLog.class, KafkaSubscribeScheduleJobLog.class }, discriminatorProperty = "type")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
    @JsonSubTypes({ @Type(value = ExecutionScheduleJobLog.class, name = "EXECUTION_SCHEDULER"),
            @Type(value = KafkaSubscribeScheduleJobLog.class, name = "KAFKA_SUBSCRIBE_SCHEDULER") })
    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static abstract class JogLogDetailBase<T extends JogLogDetailBase<T>> {
        @Schema(name = "type", implementation = ScheduleType.class)
        @JsonProperty(value = "type", access = Access.WRITE_ONLY)
        @NotNull
        private @NotBlank @EnumValue(enumCls = ScheduleType.class) String type;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecutionScheduleJobLog extends JogLogDetailBase<ExecutionScheduleJobLog> {
        private Collection<ResultInformation> results;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KafkaSubscribeScheduleJobLog extends JogLogDetailBase<KafkaSubscribeScheduleJobLog> {
        private ResultInformation result;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultInformation {
        private @NotBlank String requestId;
        private @NotNull List<ResultDescription> results;

        public ResultInformation validate() {
            hasTextOf(requestId, "requestId");
            notEmptyOf(results, "results");
            return this;
        }
    }

}
