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
package com.wl4g.rengine.executor.execution.sdk.tools;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.HostAccess;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Builder.Default;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link PrometheusParser}
 * 
 * @author James Wong
 * @version 2022-12-15
 * @since v1.0.0
 */
public class PrometheusParser {
    private static final PrometheusParser DEFAULT = new PrometheusParser();

    public static @HostAccess.Export PrometheusParser getInstance() {
        return DEFAULT;
    }

    public @HostAccess.Export PrometheusParser() {
    }

    /**
     * Conversion to Prometheus(0.0.4/OpenMetrics1.0) to Event.Body JSON
     * 
     * <pre>
     *   # HELP go_gc_duration_seconds A summary of the GC invocation durations.
     *   # TYPE go_gc_duration_seconds summary
     *   go_gc_duration_seconds{quantile="0"} 4.1449e-05
     *   go_gc_duration_seconds{quantile="0.25"} 5.3531e-05
     *   go_gc_duration_seconds{quantile="0.5"} 0.000144173
     *   go_gc_duration_seconds{quantile="0.75"} 0.000376855
     *   go_gc_duration_seconds{quantile="1"} 0.005432199
     *   go_gc_duration_seconds_sum 0.009511142
     *   go_gc_duration_seconds_count 11
     *   # HELP go_goroutines Number of goroutines that currently exist.
     *   # TYPE go_goroutines gauge
     *   go_goroutines 6
     *   # HELP go_info Information about the Go environment.
     *   # TYPE go_info gauge
     *   go_info{version="go1.11.2"} 1
     * </pre>
     * 
     * @see {@link io.prometheus.client.exporter.HTTPServer.HTTPMetricHandler#handle()}
     * @see {@link io.prometheus.client.exporter.common.TextFormat#writeFormat()}
     * @return
     */
    public @HostAccess.Export List<PrometheusMetrics> parse(String metricsString) {
        if (isBlank(metricsString)) {
            return emptyList();
        }
        final List<PrometheusMetrics> metrics = Lists.newArrayList();

        String[] lines = trimToEmpty(metricsString).split("\n");
        String currentHelp = "";
        String currentType = "";
        String currentSubType = "";
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (startsWith(line, "# HELP")) {
                // # HELP go_info Information about the Go environment.
                String[] helpParts = split(line, " ");
                if (nonNull(helpParts) && helpParts.length >= 3) {
                    currentHelp = join(helpParts, " ", Math.min(3, helpParts.length), helpParts.length);
                }
            } else if (startsWith(line, "# TYPE")) {
                // e.g: # TYPE go_gc_duration_seconds summary
                String[] typeParts = split(line, " ");
                if (nonNull(typeParts) && typeParts.length >= 3) {
                    currentType = typeParts[3];
                    currentSubType = typeParts[2];
                }
            } else if (!startsWith(line, "#")) {
                String name = "";
                Map<String, Object> tags = Maps.newHashMap();
                String value = "";

                int open = line.indexOf("{");
                int close = line.indexOf("}");
                // e.g1: node_cpu_seconds_total{cpu="4",mode="nice"} 2.05
                if (open > 0 && close > open) {
                    name = trimToEmpty(line.substring(0, open));
                    value = trimToEmpty(line.substring(close + 1));
                    String tagStrings = trimToEmpty(line.substring(open + 1, close));
                    String[] tagPairs = split(tagStrings, ",");
                    for (String tag : tagPairs) {
                        String[] kv = split(tag, "=");
                        if (nonNull(kv) && kv.length >= 2) {
                            tags.put(kv[0], replace(kv[1], "\"", ""));
                        }
                    }
                } else {
                    // e.g2: go_memstats_heap_objects 3954
                    String[] metricKV = split(line, " ");
                    name = trimToEmpty(metricKV[0]);
                    value = trimToEmpty(metricKV[1]);
                }
                // Add to metrics.
                metrics.add(PrometheusMetrics.builder()
                        .help(currentHelp)
                        .type(currentType)
                        .subType(currentSubType)
                        .name(name)
                        .value(value)
                        .tags(tags)
                        .build());
                // Reset
                currentHelp = "";
                currentType = "";
            }
        }

        return metrics;
    }

    @SuperBuilder
    @ToString
    public static class PrometheusMetrics {
        private String help;
        private String type;
        private String subType;
        private String name;
        private String value;
        private @Default Map<String, Object> tags = Maps.newHashMap();

        public @HostAccess.Export PrometheusMetrics() {
        }

        public @HostAccess.Export String getHelp() {
            return help;
        }

        public @HostAccess.Export void setHelp(String help) {
            this.help = help;
        }

        public @HostAccess.Export String getType() {
            return type;
        }

        public @HostAccess.Export void setType(String type) {
            this.type = type;
        }

        public @HostAccess.Export String getSubType() {
            return subType;
        }

        public @HostAccess.Export void setSubType(String subType) {
            this.subType = subType;
        }

        public @HostAccess.Export String getName() {
            return name;
        }

        public @HostAccess.Export void setName(String name) {
            this.name = name;
        }

        public @HostAccess.Export String getValue() {
            return value;
        }

        public @HostAccess.Export void setValue(String value) {
            this.value = value;
        }

        public @HostAccess.Export Map<String, Object> getTags() {
            return tags;
        }

        public @HostAccess.Export void setTags(Map<String, Object> tags) {
            this.tags = tags;
        }

    }

}
