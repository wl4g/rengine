package com.wl4g.rengine.executor.execution.sdk;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventObject;
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
 * {@link ScriptRengineEvent}
 * 
 * @author James Wong
 * @version 2022-11-15
 * @since v1.0.0
 */
@ToString
public class ScriptRengineEvent extends EventObject {
    private static final long serialVersionUID = -63891594867432009L;
    private @NotBlank String type;
    private @NotNull @Min(0) @NotNull Long observedTime;
    private @NotNull String body;
    private @NotNull ProxyObject attributes = ProxyObject.fromMap(new HashMap<>());

    public ScriptRengineEvent(@NotBlank String type, @NotNull ScriptEventSource source) {
        this(type, currentTimeMillis(), source, null, new HashMap<>());
    }

    public ScriptRengineEvent(@NotBlank String type, @NotNull ScriptEventSource source, @Nullable String body) {
        this(type, currentTimeMillis(), source, body, emptyMap());
    }

    public ScriptRengineEvent(@NotBlank String type, @NotNull ScriptEventSource source, @Nullable String body,
            @Nullable Map<String, String> attributes) {
        this(type, currentTimeMillis(), source, body, attributes);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ScriptRengineEvent(@NotBlank String type, @Min(0) Long observedTime, @NotNull ScriptEventSource source,
            @Nullable String body, @Nullable Map<String, String> attributes) {
        super(notNullOf(source, "eventSource"));
        this.type = hasTextOf(type, "eventType");
        isTrueOf(observedTime > 0, format("observedTime > 0, but is: %s", observedTime));
        this.observedTime = observedTime;
        this.body = body;
        this.attributes = ProxyObject.fromMap((Map) attributes);
    }

    public @HostAccess.Export String getType() {
        return type;
    }

    public @HostAccess.Export Long getObservedTime() {
        return observedTime;
    }

    public @HostAccess.Export String getBody() {
        return body;
    }

    public @HostAccess.Export ScriptEventSource getSource() {
        return (ScriptEventSource) super.getSource();
    }

    public @HostAccess.Export ProxyObject getAttributes() {
        return attributes;
    }

    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class ScriptEventSource implements Serializable {
        private static final long serialVersionUID = -63891594867432011L;
        private @NotNull @Min(0) Long time;
        private @NotEmpty @Default List<String> principals = new ArrayList<>();
        private @NotNull @Default ScriptEventLocation location = ScriptEventLocation.builder().build();

        public @HostAccess.Export Long getTime() {
            return time;
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
    public static class ScriptEventLocation implements Serializable {
        private static final long serialVersionUID = -63891594867422209L;
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
