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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.evaluator.execution.sdk;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.proxy.ProxyObject;

import lombok.Builder.Default;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link ScriptContext}
 * 
 * @author James Wong
 * @version 2022-09-22
 * @since v3.0.0
 */
@ToString
@SuperBuilder
public class ScriptContext {
    private final @NotBlank String id;
    private final @NotBlank String type;
    private final @Default List<String> args = new ArrayList<>();
    private final @Default ScriptRengineEvent event = new ScriptRengineEvent("__default_empty_event",
            ScriptEventSource.builder().build());
    private final @Default ProxyObject attributes = ProxyObject.fromMap(new HashMap<>());

    public @HostAccess.Export String getId() {
        return id;
    }

    public @HostAccess.Export String getType() {
        return type;
    }

    public @HostAccess.Export List<String> getArgs() {
        return args;
    }

    public @HostAccess.Export ProxyObject getAttributes() {
        return attributes;
    }

    public @HostAccess.Export ScriptRengineEvent getEvent() {
        return event;
    }

    @ToString
    public static class ScriptRengineEvent {
        private @NotBlank String eventType;
        private @NotNull @Min(0) @NotNull Long observedTime;
        private @NotNull String body;
        private @NotNull ScriptEventSource eventSource = ScriptEventSource.builder().build();
        private @NotNull ProxyObject attributes = ProxyObject.fromMap(new HashMap<>());

        public ScriptRengineEvent(@NotBlank String eventType, @NotNull ScriptEventSource eventSource) {
            this(eventType, currentTimeMillis(), eventSource, null, new HashMap<>());
        }

        public ScriptRengineEvent(@NotBlank String eventType, @NotNull ScriptEventSource eventSource, @Nullable String body) {
            this(eventType, currentTimeMillis(), eventSource, body, emptyMap());
        }

        public ScriptRengineEvent(@NotBlank String eventType, @NotNull ScriptEventSource eventSource, @Nullable String body,
                @Nullable Map<String, String> attributes) {
            this(eventType, currentTimeMillis(), eventSource, body, attributes);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public ScriptRengineEvent(@NotBlank String eventType, @Min(0) Long observedTime, @NotNull ScriptEventSource eventSource,
                @Nullable String body, @Nullable Map<String, String> attributes) {
            isTrueOf(observedTime > 0, format("observedTime > 0, but is: %s", observedTime));
            this.eventType = hasTextOf(eventType, "eventType");
            this.observedTime = observedTime;
            this.body = body;
            this.eventSource = notNullOf(eventSource, "eventSource");
            this.attributes = ProxyObject.fromMap((Map) attributes);
        }

        public @HostAccess.Export String getEventType() {
            return eventType;
        }

        public @HostAccess.Export Long getObservedTime() {
            return observedTime;
        }

        public @HostAccess.Export String getBody() {
            return body;
        }

        public @HostAccess.Export ScriptEventSource getEventSource() {
            return eventSource;
        }

        public @HostAccess.Export ProxyObject getAttributes() {
            return attributes;
        }
    }

    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class ScriptEventSource {
        private @NotNull @Min(0) Long sourceTime;
        private @NotEmpty @Default List<String> principals = new ArrayList<>();
        private @NotNull @Default ScriptEventLocation location = ScriptEventLocation.builder().build();

        public @HostAccess.Export Long getSourceTime() {
            return sourceTime;
        }

        public @HostAccess.Export List<String> getPrincipals() {
            return principals;
        }

        public @HostAccess.Export ScriptEventLocation getLocation() {
            return location;
        }
    }

    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class ScriptEventLocation {
        private @NotBlank String ipAddress;
        private @NotNull Boolean ipv6;
        private @NotBlank String isp;
        private @NotBlank String domain;
        private @NotBlank String country;
        private @NotBlank String region;
        private @NotBlank String city;
        private @NotNull Float latitude;
        private @NotNull Float longitude;
        private @NotBlank String timezone;
        private @NotBlank String zipcode;
        private @NotNull Float elevation;

        public @HostAccess.Export String getIpAddress() {
            return ipAddress;
        }

        public @HostAccess.Export Boolean getIpv6() {
            return ipv6;
        }

        public @HostAccess.Export String getIsp() {
            return isp;
        }

        public @HostAccess.Export String getDomain() {
            return domain;
        }

        public @HostAccess.Export String getCountry() {
            return country;
        }

        public @HostAccess.Export String getRegion() {
            return region;
        }

        public @HostAccess.Export String getCity() {
            return city;
        }

        public @HostAccess.Export Float getLatitude() {
            return latitude;
        }

        public @HostAccess.Export Float getLongitude() {
            return longitude;
        }

        public @HostAccess.Export String getTimezone() {
            return timezone;
        }

        public @HostAccess.Export String getZipcode() {
            return zipcode;
        }

        public @HostAccess.Export Float getElevation() {
            return elevation;
        }
    }

}
