/*
 * Copyright 2017 ~ 2025 the original authors James Wong.
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
package com.wl4g.rengine.job;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import java.time.Duration;

import org.apache.flink.api.common.eventtime.BoundedOutOfOrdernessWatermarks;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.eventtime.WatermarksWithIdleness;

import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.common.event.RengineEvent.EventSource;

/**
 * {@link RengineEventWatermarks}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @date 2022-05-31 v3.0.0
 * @since v1.0.0
 */
public class RengineEventWatermarks extends BoundedOutOfOrdernessWatermarks<RengineEvent> {

    public RengineEventWatermarks(Duration outOfOrderness) {
        super(outOfOrderness);
    }

    @Override
    public void onEvent(RengineEvent event, long eventTimestamp, WatermarkOutput output) {
        notNullOf(event.getSource(), "eventSource");
        final Long sourceTime = ((EventSource) event.getSource()).getTime();
        notNullOf(sourceTime, "eventSource.time");
        super.onEvent(event, sourceTime, output);
    }

    /**
     * Creates a watermark strategy that generates
     * {@link RengineEventWatermarks} at all. This may be useful in scenarios
     * that do pure processing-time based stream processing.
     */
    public static WatermarkStrategy<RengineEvent> newWatermarkStrategy(Duration outOfOrderness, Duration idleTimeout) {
        // see:https://github.com/apache/flink/blob/release-1.14.4/docs/content/docs/connectors/datastream/kafka.md#idleness
        return ctx -> new WatermarksWithIdleness<>(new RengineEventWatermarks(outOfOrderness), idleTimeout);
    }

}
