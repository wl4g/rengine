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

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseFromNode;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link RengineEvent} For the design of common event model fields, refer to
 * such as see:
 * https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/logs/data-model.md#log-and-event-record-definition
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-30 v3.0.0
 * @since v1.0.0
 */
@Getter
@ToString
public class RengineEvent extends EventObject {
    private static final long serialVersionUID = 3242901223478600427L;

    private static final TypeReference<HashMap<String, String>> HASHMAP_TYPEREF = new TypeReference<HashMap<String, String>>() {
    };

    /**
     * Event type.
     */
    private @NotBlank String type;

    /**
     * Event observed time-stamp.
     */
    // private @NotNull @Min(0)
    // @PersistenceConverter(dateFormatter="yyMMddHHmmssSSS") Long observedTime;
    private @NotNull @Min(0) Long observedTime;

    /**
     * Event body.
     */
    private @Nullable String body;

    /**
     * Event extension attributes.
     */
    private @Nullable Map<String, String> attributes = new HashMap<>();

    public RengineEvent(@NotBlank String type, @NotNull EventSource source) {
        this(type, currentTimeMillis(), source, null, new HashMap<>());
    }

    public RengineEvent(@NotBlank String type, @NotNull EventSource source, @Nullable String body) {
        this(type, currentTimeMillis(), source, body, emptyMap());
    }

    public RengineEvent(@NotBlank String type, @Min(0) Long observedTime, @NotNull EventSource source, @Nullable String body,
            @Nullable Map<String, String> attributes) {
        super(notNullOf(source, "eventSource"));
        isTrueOf(observedTime > 0, format("observedTime > 0, but is: %s", observedTime));
        this.type = hasTextOf(type, "eventType");
        this.observedTime = observedTime;
        this.body = body;
        this.attributes = attributes;
    }

    public RengineEvent validate() {
        return RengineEvent.validate(this);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @JsonCreator
    public static RengineEvent fromJson(JsonNode node) {
        notNullOf(node, "node");

        // Event base
        String type = node.at("/type").asText();
        Long observedTime = node.at("/observedTime").asLong();

        String body = node.at("/body").asText();
        Map<String, String> attributes = parseJSON(node.at("/attributes").asText(), HASHMAP_TYPEREF);

        // Event source
        Long sourceTime = node.at("/source/time").asLong();
        List principals = parseFromNode(node, "/source/principals", List.class);
        EventLocation location = parseFromNode(node, "/source/location", EventLocation.class);

        return new RengineEvent(type, observedTime,
                EventSource.builder().time(sourceTime).principals(principals).location(location).build(), body, attributes);
    }

    public static RengineEvent validate(RengineEvent event) {
        // Basic validate.
        hasTextOf(event.getType(), "type");
        isTrue(Pattern.matches(EVENT_TYPE_REGEX, event.getType()), "Invalid event type '%s' does not satisfy the regex: %s",
                event.getType(), EVENT_TYPE_REGEX);
        notNullOf(event.getObservedTime(), "observedTime");
        isTrueOf(event.getObservedTime() > 0, "Must observedTime > 0");

        // Source validate.
        EventSource source = (EventSource) event.getSource();
        notNullOf(source.getTime(), "sourceTime");
        isTrueOf(source.getTime() > 0, "Must sourceTime > 0");
        notEmptyOf(source.getPrincipals(), "principals");

        // Location validate.
        EventLocation location = source.getLocation();
        if (!isBlank(location.getCountry())) {
            isTrue(Pattern.matches(EVENT_LOCATION_COUNTRY_REGEX, location.getCountry()),
                    "Invalid event location country '%s' does not satisfy the regex: %s", location.getCountry(),
                    EVENT_LOCATION_COUNTRY_REGEX);
        }
        if (!isBlank(location.getRegion())) {
            isTrue(Pattern.matches(EVENT_LOCATION_REGION_REGEX, location.getRegion()),
                    "Invalid event location region '%s' does not satisfy the regex: %s", location.getRegion(),
                    EVENT_LOCATION_REGION_REGEX);
        }
        if (!isBlank(location.getCity())) {
            isTrue(Pattern.matches(EVENT_LOCATION_CITY_REGEX, location.getCity()),
                    "Invalid event location city '%s' does not satisfy the regex: %s", location.getCity(),
                    EVENT_LOCATION_CITY_REGEX);
        }

        return event;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    public static class EventSource implements Serializable {
        private static final long serialVersionUID = -4689601246194850124L;
        // private @NotNull @Min(0)
        // @PersistenceConverter(dateFormatter="yyMMddHHmmssSSS") Long time;
        private @NotNull @Min(0) Long time;
        private @NotEmpty @Default List<String> principals = new ArrayList<>();
        private @Nullable @Default EventLocation location = EventLocation.builder().build();
    }

    /**
     * Fix for example:
     * 
     * <pre>
     *  IP2LocationRecord:
     *      IP Address = 1.1.1.1
     *      Country Short = US
     *      Country Long = United States of America
     *      Region = California
     *      City = Los Angeles
     *      ISP = Not_Supported
     *      Latitude = 34.05223
     *      Longitude = -118.24368
     *      Domain = Not_Supported
     *      ZipCode = 90001
     *      TimeZone = -07:00
     *      NetSpeed = Not_Supported
     *      IDDCode = Not_Supported
     *      AreaCode = Not_Supported
     *      WeatherStationCode = Not_Supported
     *      WeatherStationName = Not_Supported
     *      MCC = Not_Supported
     *      MNC = Not_Supported
     *      MobileBrand = Not_Supported
     *      Elevation = 0.0
     *      UsageType = Not_Supported
     *      AddressType = Not_Supported
     *      Category = Not_Supported
     * </pre>
     **/
    @Data
    @SuperBuilder
    @NoArgsConstructor
    public static class EventLocation implements Serializable {
        private static final long serialVersionUID = -5164248154057314473L;
        private @Nullable String ipAddress;
        private @Nullable Boolean ipv6;
        private @Nullable String isp;
        private @Nullable String domain;
        private @Nullable Float elevation;
        private @Nullable Float latitude;
        private @Nullable Float longitude;
        private @Nullable String timezone;
        private @Nullable String zipcode;
        private @Nullable String city;
        private @Nullable String region;
        private @Nullable String country;
    }

    /**
     * Limit validation regex, For example, the rowkey size of HBase should not
     * be too long.
     * 
     * @see {@link https://github.com/apache/hbase/blob/rel/2.1.2/hbase-common/src/main/java/org/apache/hadoop/hbase/HConstants.java#L599}
     * @see {@link com.wl4g.rengine.job.analytic.core.hbase.EventToMutationConverter#generateRowkey()}
     */
    public static final String EVENT_TYPE_REGEX = "^([@a-zA-Z0-9_-]){1,16}$";
    public static final String EVENT_PRINCIPAL_REGEX = "^[@a-zA-Z0-9._-]{1,24}$";
    public static final String EVENT_LOCATION_COUNTRY_REGEX = "^([a-zA-Z0-9_-]){1,2}$";
    public static final String EVENT_LOCATION_REGION_REGEX = "^([a-zA-Z0-9_-]){1,16}$";
    public static final String EVENT_LOCATION_CITY_REGEX = "^([a-zA-Z0-9_-]){1,16}$";

    // @Target(FIELD)
    // @Retention(RUNTIME)
    // @Documented
    // public static @interface PersistenceConverter {
    // String dateFormatter();
    // }

}
