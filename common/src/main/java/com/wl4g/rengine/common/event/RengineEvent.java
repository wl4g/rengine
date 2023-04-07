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

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseFromNode;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseToNode;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.replaceChars;
import static org.apache.commons.lang3.StringUtils.startsWith;

import java.io.Serializable;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.wl4g.infra.common.serialize.JacksonUtils;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@ToString(exclude = "_toJsonNode")
public class RengineEvent extends EventObject {
    private static final long serialVersionUID = 3242901223478600427L;

    private static final TypeReference<HashMap<String, String>> HASHMAP_TYPEREF = new TypeReference<HashMap<String, String>>() {
    };

    /**
     * Event ID.
     */
    private @NotBlank String id;

    /**
     * Event type.
     */
    private @NotBlank String type;

    /**
     * Event observed time-stamp.
     */
    private @NotNull @Min(-1) Long observedTime;

    /**
     * Event body data.
     */
    private @Nullable Map<String, Object> body;

    /**
     * Event extension labels.
     */
    private @Nullable Map<String, String> labels;

    /***
     * Convert to temporary json node tree format, such as for key path lookup.
     */
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private transient @JsonIgnore JsonNode _toJsonNode;

    public RengineEvent(@NotBlank String type, @NotNull EventSource source) {
        this(null, type, currentTimeMillis(), source, null, null);
    }

    public RengineEvent(@NotBlank String id, @NotBlank String type, @NotNull EventSource source) {
        this(id, type, currentTimeMillis(), source, null, null);
    }

    public RengineEvent(@NotBlank String type, @Nullable Map<String, Object> body) {
        this(null, type, currentTimeMillis(), null, body, null);
    }

    public RengineEvent(@NotBlank String id, @NotBlank String type, @Nullable Map<String, Object> body) {
        this(id, type, currentTimeMillis(), null, body, null);
    }

    public RengineEvent(@NotBlank String type, @NotNull EventSource source, @Nullable Map<String, Object> body) {
        this(null, type, currentTimeMillis(), source, body, null);
    }

    public RengineEvent(@NotBlank String id, @NotBlank String type, @NotNull EventSource source,
            @Nullable Map<String, Object> body) {
        this(id, type, currentTimeMillis(), source, body, null);
    }

    public RengineEvent(@NotBlank String id, @NotBlank String type, @NotNull EventSource source,
            @Nullable Map<String, Object> body, @Nullable Map<String, String> labels) {
        this(id, type, currentTimeMillis(), source, body, labels);
    }

    public RengineEvent(@Nullable String id, @NotBlank String type, @NotNull @Min(0) Long observedTime,
            @NotNull EventSource source, @Nullable Map<String, Object> body, @Nullable Map<String, String> labels) {
        super(notNullOf(source, "source"));
        // this.id = hasTextOf(id, "eventId");
        this.type = hasTextOf(type, "eventType");
        isTrueOf(nonNull(observedTime) && observedTime > 0, format("observedTime > 0, but is: %s", observedTime));
        this.observedTime = observedTime;
        this.id = isBlank(id) ? Builder.buildDefaultEventId(type, observedTime, source) : id;
        this.body = body;
        this.labels = labels;
    }

    public RengineEvent validate() {
        return RengineEvent.validate(this);
    }

    public @JsonIgnore JsonNode asJsonNode() {
        if (isNull(_toJsonNode)) {
            synchronized (this) {
                if (isNull(_toJsonNode)) {
                    this._toJsonNode = parseToNode(toJSONString(this));
                }
            }
        }
        return _toJsonNode;
    }

    public String atAsText(@NotBlank String jqExpr) {
        hasTextOf(jqExpr, "jqExpr");
        if (!startsWith(jqExpr, ".")) {
            // @formatter:off
            // throw new IllegalArgumentException(format("Invalid json jq expression '%s', must start with '.'", jqExpr));
            // @formatter:on
            jqExpr = ".".concat(jqExpr);
        }

        String expr = replaceChars(jqExpr, ".", "/");
        int arrayIndex = -1;
        final int start = expr.indexOf("[");
        final int end = expr.indexOf("]");
        if (start >= 0 && start < end) {
            arrayIndex = Integer.parseInt(expr.substring(start + 1, end));
            expr = expr.substring(0, start);
        }

        final JsonNode value = asJsonNode().at(expr);
        if (value instanceof TextNode) {
            return value.textValue();
        } else if (value instanceof ArrayNode) {
            if (arrayIndex >= 0) {
                final JsonNode arrayValue = ((ArrayNode) value).get(arrayIndex);
                return nonNull(arrayValue) ? arrayValue.asText() : null;
            } else {
                return ((ArrayNode) value).toString();
            }
        }

        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static RengineEvent fromJson(String json) {
        return fromJson(parseJSON(json, JsonNode.class));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @JsonCreator
    public static RengineEvent fromJson(JsonNode node) {
        notNullOf(node, "node");

        // Event base.
        final String id = node.at("/id").asText();
        final String type = node.at("/type").asText();
        final Long observedTime = node.at("/observedTime").asLong();

        final JsonNode bodyNode = node.at("/body");
        final Map<String, Object> bodyMap = (nonNull(bodyNode) && !bodyNode.isMissingNode() && !isBlank(bodyNode.toString()))
                ? parseJSON(bodyNode.toString(), JacksonUtils.MAP_OBJECT_TYPE_REF)
                : null;

        final JsonNode labelsNode = node.at("/labels");
        final Map<String, String> labels = (nonNull(labelsNode) && !labelsNode.isMissingNode() && !isBlank(labelsNode.asText()))
                ? parseJSON(labelsNode.asText(), HASHMAP_TYPEREF)
                : null;

        // Event source.
        final Long sourceTime = node.at("/source/time").asLong();
        final List principals = parseFromNode(node, "/source/principals", List.class);
        final EventLocation location = parseFromNode(node, "/source/location", EventLocation.class);

        return new RengineEvent(id, type, observedTime,
                EventSource.builder().time(sourceTime).principals(principals).location(location).build(), bodyMap, labels);
    }

    public static RengineEvent validate(RengineEvent event) {
        // Validate for basic.
        validateForEventType(event.getType());
        notNullOf(event.getObservedTime(), "observedTime");
        isTrueOf(event.getObservedTime() > 0, "Must observedTime > 0");

        // Validate for source.
        final EventSource source = (EventSource) event.getSource();
        isTrueOf(nonNull(source.getTime()) && source.getTime() > 0, "sourceTime > 0");
        // notEmptyOf(source.getPrincipals(), "principals");

        // Validate for location.
        final EventLocation location = source.getLocation();
        if (nonNull(location)) {
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
        }

        return event;
    }

    public static void validateForEventType(String eventType) {
        hasTextOf(eventType, "type");
        isTrue(Pattern.matches(EVENT_TYPE_REGEX, eventType), "Invalid event type '%s' does not satisfy the regex: %s", eventType,
                EVENT_TYPE_REGEX);
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    public static class EventSource implements Serializable {
        private static final long serialVersionUID = -4689601246194850124L;
        private @NotNull @Min(-1) Long time;
        private @Nullable List<String> principals;
        private @Nullable EventLocation location;
    }

    /**
     * For example:
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

    public static class Builder {
        private String id;
        private String type;
        private Long observedTime;
        private EventSource source;
        private Map<String, Object> body;
        private Map<String, String> labels;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder observedTime(Long observedTime) {
            this.observedTime = observedTime;
            return this;
        }

        public Builder source(EventSource source) {
            this.source = source;
            return this;
        }

        public Builder body(Map<String, Object> body) {
            this.body = body;
            return this;
        }

        public Builder labels(Map<String, String> labels) {
            this.labels = labels;
            return this;
        }

        public RengineEvent build() {
            if (isNull(observedTime)) {
                this.observedTime = currentTimeMillis();
            }
            if (isNull(source)) {
                this.source = EventSource.builder().build();
            }
            if (isNull(body)) {
                this.body = new LinkedHashMap<>();
            }
            if (isNull(labels)) {
                this.labels = new LinkedHashMap<>();
            }
            return new RengineEvent(id, type, observedTime, source, body, labels);
        }

        static String buildDefaultEventId(@NotBlank String eventType, @NotNull Long observedTime, @Nullable EventSource source) {
            hasTextOf(eventType, "eventType");
            isTrueOf(nonNull(observedTime) && observedTime > 0, "observedTime > 0");

            // @formatter:off
            //final String firstPrincipal = nonNull(source) ? safeList(source.getPrincipals()).stream().findFirst().orElse("") : "";
            // @formatter:on

            return format("%s@%s", observedTime, eventType);
        }
    }

    /**
     * Limit validation regex, For example, the rowkey size of HBase should not
     * be too long.
     * 
     * @see {@link https://github.com/apache/hbase/blob/rel/2.1.2/hbase-common/src/main/java/org/apache/hadoop/hbase/HConstants.java#L599}
     * @see {@link com.wl4g.rengine.job.hbase.EventToMutationConverter#generateRowkey()}
     */
    public static final String EVENT_TYPE_REGEX = "^([@a-zA-Z0-9_-]){1,16}$";
    public static final String EVENT_PRINCIPAL_REGEX = "^[@a-zA-Z0-9._-]{1,24}$";
    public static final String EVENT_LOCATION_COUNTRY_REGEX = "^([a-zA-Z0-9_-]){1,2}$";
    public static final String EVENT_LOCATION_REGION_REGEX = "^([a-zA-Z0-9_-]){1,16}$";
    public static final String EVENT_LOCATION_CITY_REGEX = "^([a-zA-Z0-9_-]){1,16}$";

}
