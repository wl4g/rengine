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
import static java.util.Collections.singletonMap;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertNotNull;

import java.util.regex.Pattern;

import org.junit.Test;

import com.wl4g.rengine.common.event.RengineEvent.EventLocation;
import com.wl4g.rengine.common.event.RengineEvent.EventSource;

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
        System.out.println(toJSONString(DEFAULT_TEST_EVENT));
    }

    @Test
    public void testEventFromJson() {
        String json = "{\"source\":{\"time\":1678590267752,\"principals\":[\"jameswong1234@gmail.com\"],\"location\":{\"ipAddress\":\"1.1.1.1\",\"ipv6\":null,\"isp\":null,\"domain\":null,\"elevation\":null,\"latitude\":null,\"longitude\":null,\"timezone\":null,\"zipcode\":\"20500\",\"city\":null,\"region\":null,\"country\":null}},\"id\":\"iot_temp_warn1:1678590267753:jameswong1234@gmail.com\",\"type\":\"iot_temp_warn1\",\"observedTime\":1678590267753,\"body\":{\"value\":\"52\"},\"labels\":{}}";
        RengineEvent event = parseJSON(json, RengineEvent.class);
        System.out.println("         EventType: " + event.getType());
        System.out.println("      ObservedTime: " + event.getObservedTime());
        System.out.println("            Source: " + event.getSource());
        System.out.println("       Source.time: " + ((EventSource) event.getSource()).getTime());
        System.out.println(" Source.principals: " + ((EventSource) event.getSource()).getPrincipals());
        System.out.println("   Source.location: " + ((EventSource) event.getSource()).getLocation());
        System.out.println("            Labels: " + event.getLabels());
    }

    @Test
    public void testNotnullWithDefault() {
        EventSource source1 = new EventSource();
        assertNotNull(source1.getLocation());

        EventSource source2 = EventSource.builder().build();
        assertNotNull(source2.getLocation());

        RengineEvent event1 = RengineEvent.builder().type("test_event").source(source1).build();
        assertNotNull(event1.getSource());
    }

    @Test
    public void testValidateTypeRegexWithSuccess() {
        var matches = Pattern.matches(RengineEvent.EVENT_TYPE_REGEX, "iot_temp_warn");
        System.out.println(matches);
        assert matches;

        matches = Pattern.matches(RengineEvent.EVENT_TYPE_REGEX, "iot-temp-warn");
        System.out.println(matches);
        assert matches;

        matches = Pattern.matches(RengineEvent.EVENT_TYPE_REGEX, "temp_warn@iot");
        System.out.println(matches);
        assert matches;

        matches = Pattern.matches(RengineEvent.EVENT_PRINCIPAL_REGEX, "jameswong12@gmail.com");
        System.out.println(matches);
        assert matches;

        matches = Pattern.matches(RengineEvent.EVENT_PRINCIPAL_REGEX, "_jameswong12");
        System.out.println(matches);
        assert matches;

        matches = Pattern.matches(RengineEvent.EVENT_LOCATION_COUNTRY_REGEX, "CN");
        System.out.println(matches);
        assert matches;
    }

    @Test
    public void testEventValidateRegexWithFail() {
        // Invalid characters.
        var matches = Pattern.matches(RengineEvent.EVENT_TYPE_REGEX, "iot:temp:warn");
        System.out.println(matches);
        assert !matches;

        // Invalid characters.
        matches = Pattern.matches(RengineEvent.EVENT_TYPE_REGEX, "temp:warn@iot");
        System.out.println(matches);
        assert !matches;

        // Exceed max length.
        matches = Pattern.matches(RengineEvent.EVENT_TYPE_REGEX, "abcdefghijklmnopqrstuvwxyz:temp:warn@iot:generic:dev");
        System.out.println(matches);
        assert !matches;

        // Exceed max length.
        matches = Pattern.matches(RengineEvent.EVENT_PRINCIPAL_REGEX, "jameswong1234567890abcdefghijklmnopqrstuvwxyz@gmail.com");
        System.out.println(matches);
        assert !matches;

        // Exceed max length.
        matches = Pattern.matches(RengineEvent.EVENT_LOCATION_COUNTRY_REGEX, "China");
        System.out.println(matches);
        assert !matches;
    }

    @Test
    public void testAtAsText() {
        System.out.println(DEFAULT_TEST_EVENT.atAsText(".type"));
        System.out.println(DEFAULT_TEST_EVENT.atAsText(".source.principals"));
        String result3 = DEFAULT_TEST_EVENT.atAsText(".source.principals[0]");
        System.out.println(result3);
        assert "jameswong1234@gmail.com".equals(result3);
    }

    @Test
    public void testAtAsTextFailure() {
        String result = DEFAULT_TEST_EVENT.atAsText(".source.principals[11]");
        System.out.println(result);
        assert isNull(result);
    }

    static final RengineEvent DEFAULT_TEST_EVENT = RengineEvent.builder()
            .type("iot_temp_warn1")
            .source(EventSource.builder()
                    .time(currentTimeMillis())
                    .principals(singletonList("jameswong1234@gmail.com"))
                    .location(EventLocation.builder().ipAddress("1.1.1.1").zipcode("20500").build())
                    .build())
            // BsonEntitySerializers serious alarm occurs when the
            // device temperature is greater than 52℃
            .body(singletonMap("value", "52"))
            .build();

}
