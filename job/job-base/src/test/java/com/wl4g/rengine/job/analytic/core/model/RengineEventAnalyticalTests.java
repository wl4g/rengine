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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.job.analytic.core.model;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.singletonList;

import org.junit.Test;

import com.wl4g.rengine.common.event.RengineEvent;
import com.wl4g.rengine.common.event.RengineEvent.EventSource;
import com.wl4g.rengine.common.event.RengineEvent.EventLocation;

/**
 * {@link RengineEventAnalyticalTests}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-06-08 v3.0.0
 * @since v3.0.0
 */
public class RengineEventAnalyticalTests {

    @Test
    public void testAnalyticalModelToJson() {
        RengineEventAnalytical model = RengineEventAnalytical.builder()
                .event(new RengineEvent("device_temp_warning",
                        EventSource.builder()
                                .sourceTime(currentTimeMillis())
                                .principals(singletonList("admin"))
                                .location(EventLocation.builder().zipcode("20500").build())
                                .build(),
                        "A serious alarm occurs when the device temperature is greater than 52℃"))
                .build();
        System.out.println(toJSONString(model));
    }

    @Test
    public void testAnalyticalModelFromJson() {
        String json = "{\"event\":{\"source\":{\"sourceTime\":null,\"ipLocation\":{\"ipAddress\":\"1.1.1.1\",\"countryShort\":null,\"countryLong\":null,\"region\":\"Pennsylvania Avenue\",\"city\":\"Washington\",\"isp\":null,\"latitude\":null,\"longitude\":null,\"domain\":null,\"zipcode\":null,\"netspeed\":null,\"timezone\":null,\"iddcode\":null,\"areacode\":\"20500\",\"weatherstationcode\":null,\"weatherstationname\":null,\"mcc\":null,\"mnc\":null,\"mobilebrand\":null,\"elevation\":null,\"usagetype\":null,\"addresstype\":null,\"category\":null}},\"eventType\":\"device_temp_warning\",\"observedTime\":1663752299681,\"body\":\"A serious alarm occurs when the device temperature is greater than 52℃\",\"attributes\":{}}}";
        RengineEventAnalytical model = parseJSON(json, RengineEventAnalytical.class);
        System.out.println("   EventType: " + model.getEvent().getEventType());
        System.out.println("ObservedTime: " + model.getEvent().getObservedTime());
        System.out.println("      Source: " + model.getEvent().getSource());
        System.out.println("  Attributes: " + model.getEvent().getAttributes());
    }

}
