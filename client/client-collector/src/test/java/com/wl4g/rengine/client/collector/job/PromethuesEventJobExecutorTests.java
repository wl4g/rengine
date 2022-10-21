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
package com.wl4g.rengine.client.collector.job;

import org.junit.jupiter.api.Test;

/**
 * {@link PromethuesEventJobExecutorTests}
 * 
 * @author James Wong
 * @version 2022-10-22
 * @since v3.0.0
 */
public class PromethuesEventJobExecutorTests {

    @Test
    public void testParseMetrics() {
        String fromResult = "# HELP go_gc_duration_seconds A summary of the GC invocation durations.\n"
                + "# TYPE go_gc_duration_seconds summary\n" + "go_gc_duration_seconds{quantile=\"0\"} 4.1449e-05\n"
                + "go_gc_duration_seconds{quantile=\"0.25\"} 5.3531e-05\n"
                + "go_gc_duration_seconds{quantile=\"0.5\"} 0.000144173\n"
                + "go_gc_duration_seconds{quantile=\"0.75\"} 0.000376855\n"
                + "go_gc_duration_seconds{quantile=\"1\"} 0.005432199\n" + "go_gc_duration_seconds_sum 0.009511142\n"
                + "go_gc_duration_seconds_count 11\n" + "# HELP go_goroutines Number of goroutines that currently exist.\n"
                + "# TYPE go_goroutines gauge\n" + "go_goroutines 6";
        String result = PrometheusEventJobExecutor.parseMetrics(fromResult);
        System.out.println(result);
    }

}
