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
package com.wl4g.rengine.common.event;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertNotNull;

import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.wl4g.rengine.common.event.RengineEvent.EventSource;
import com.wl4g.rengine.common.event.RengineEvent.EventLocation;

/**
 * {@link RengineEventTests}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-06-07 v3.0.0
 * @since v1.0.0
 */
public class RengineEventTests {

    @Test
    public void testEventToJson() {
        RengineEvent event = new RengineEvent("iot_temp_warn1",
                EventSource.builder()
                        .time(currentTimeMillis())
                        .principals(singletonList("jameswong1234@gmail.com"))
                        .location(EventLocation.builder().ipAddress("1.1.1.1").zipcode("20500").build())
                        .build(),
                // A serious alarm occurs when the device temperature is greater
                // than 52℃
                "52");
        System.out.println(toJSONString(event));
    }

    @Test
    public void testEventFromJson() {
        String json = "{\"source\":{\"time\":1665849312303,\"principals\":[\"jameswong1234@gmail.com\"],\"location\":{\"ipAddress\":\"1.1.1.1\",\"ipv6\":null,\"isp\":null,\"domain\":null,\"country\":null,\"region\":null,\"city\":null,\"latitude\":null,\"longitude\":null,\"timezone\":null,\"zipcode\":\"20500\",\"elevation\":null}},\"type\":\"iotice_temp_warning\",\"observedTime\":1665849312304,\"body\":\"52\",\"attributes\":{}}";
        RengineEvent event = parseJSON(json, RengineEvent.class);
        System.out.println("         EventType: " + event.getType());
        System.out.println("      ObservedTime: " + event.getObservedTime());
        System.out.println("            Source: " + event.getSource());
        System.out.println("       Source.time: " + ((EventSource) event.getSource()).getTime());
        System.out.println(" Source.principals: " + ((EventSource) event.getSource()).getPrincipals());
        System.out.println("   Source.location: " + ((EventSource) event.getSource()).getLocation());
        System.out.println("        Attributes: " + event.getAttributes());
    }

    @Test
    public void testNotnullWithDefault() {
        EventSource source1 = new EventSource();
        assertNotNull(source1.getLocation());

        EventSource source2 = EventSource.builder().build();
        assertNotNull(source2.getLocation());

        RengineEvent event1 = new RengineEvent("test_event", source1);
        assertNotNull(event1.getSource());
    }

    @Test
    public void testValidateTypeRegexWithSuccess() {
        var matches = Pattern.matches(RengineEvent.EVENT_TYPE_REGEX, "iot_temp_warn");
        System.out.println(matches);
        Assertions.assertTrue(matches);

        matches = Pattern.matches(RengineEvent.EVENT_TYPE_REGEX, "iot-temp-warn");
        System.out.println(matches);
        Assertions.assertTrue(matches);

        matches = Pattern.matches(RengineEvent.EVENT_TYPE_REGEX, "temp_warn@iot");
        System.out.println(matches);
        Assertions.assertTrue(matches);

        matches = Pattern.matches(RengineEvent.EVENT_PRINCIPAL_REGEX, "jameswong12@gmail.com");
        System.out.println(matches);
        Assertions.assertTrue(matches);

        matches = Pattern.matches(RengineEvent.EVENT_PRINCIPAL_REGEX, "_jameswong12");
        System.out.println(matches);
        Assertions.assertTrue(matches);

        matches = Pattern.matches(RengineEvent.EVENT_LOCATION_COUNTRY_REGEX, "CN");
        System.out.println(matches);
        Assertions.assertTrue(matches);
    }

    @Test
    public void testEventValidateRegexWithFail() {
        // Invalid characters.
        var matches = Pattern.matches(RengineEvent.EVENT_TYPE_REGEX, "iot:temp:warn");
        System.out.println(matches);
        Assertions.assertFalse(matches);

        // Invalid characters.
        matches = Pattern.matches(RengineEvent.EVENT_TYPE_REGEX, "temp:warn@iot");
        System.out.println(matches);
        Assertions.assertFalse(matches);

        // Exceed max length.
        matches = Pattern.matches(RengineEvent.EVENT_TYPE_REGEX, "abcdefghijklmnopqrstuvwxyz:temp:warn@iot:generic:dev");
        System.out.println(matches);
        Assertions.assertFalse(matches);

        // Exceed max length.
        matches = Pattern.matches(RengineEvent.EVENT_PRINCIPAL_REGEX, "jameswong1234567890abcdefghijklmnopqrstuvwxyz@gmail.com");
        System.out.println(matches);
        Assertions.assertFalse(matches);

        // Exceed max length.
        matches = Pattern.matches(RengineEvent.EVENT_LOCATION_COUNTRY_REGEX, "China");
        System.out.println(matches);
        Assertions.assertFalse(matches);
    }

}
