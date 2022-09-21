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
package com.wl4g.rengine.common.event;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.wl4g.rengine.common.event.RengineEvent.EventSource;
import com.wl4g.rengine.common.event.RengineEvent.EventSourceIPLocation;

/**
 * {@link RengineEventTests}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-06-07 v3.0.0
 * @since v3.0.0
 */
public class RengineEventTests {

    @Test
    public void testEventToJson() {
        RengineEvent event = new RengineEvent("device_temp_warning",
                EventSource.builder()
                        .sourceTime(currentTimeMillis())
                        .principals(singletonList("admin"))
                        .ipLocation(EventSourceIPLocation.builder().address("1.1.1.1").zipcode("20500").build())
                        .build(),
                "A serious alarm occurs when the device temperature is greater than 52℃");
        System.out.println(toJSONString(event));
    }

    @Test
    public void testEventFromJson() {
        String json = "{\"source\":{\"sourceTime\":null,\"ipLocation\":{\"ipAddress\":\"1.1.1.1\",\"countryShort\":null,\"countryLong\":null,\"region\":\"Pennsylvania Avenue\",\"city\":\"Washington\",\"isp\":null,\"latitude\":null,\"longitude\":null,\"domain\":null,\"zipcode\":null,\"netspeed\":null,\"timezone\":null,\"iddcode\":null,\"areacode\":\"20500\",\"weatherstationcode\":null,\"weatherstationname\":null,\"mcc\":null,\"mnc\":null,\"mobilebrand\":null,\"elevation\":null,\"usagetype\":null,\"addresstype\":null,\"category\":null}},\"eventType\":\"device_temp_warning\",\"observedTime\":1663744904483,\"body\":\"A serious alarm occurs when the device temperature is greater than 52℃\",\"attributes\":{}}";
        RengineEvent event = parseJSON(json, RengineEvent.class);
        System.out.println("   EventType: " + event.getEventType());
        System.out.println("ObservedTime: " + event.getObservedTime());
        System.out.println("      Source: " + event.getSource());
        System.out.println("  Attributes: " + event.getAttributes());
    }

    @Test
    public void testNotnullWithDefault() {
        EventSource source1 = new EventSource();
        assertNotNull(source1.getIpLocation());

        EventSource source2 = EventSource.builder().build();
        assertNotNull(source2.getIpLocation());

        RengineEvent event1 = new RengineEvent("test_event", source1);
        assertNotNull(event1.getSource());
    }

}
