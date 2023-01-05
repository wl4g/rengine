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
package com.wl4g.rengine.executor.execution.sdk.extension;

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import java.util.List;

import org.junit.Test;

import com.wl4g.rengine.executor.execution.sdk.tools.PrometheusParser;
import com.wl4g.rengine.executor.execution.sdk.tools.PrometheusParser.PrometheusMetrics;

/**
 * {@link PrometheusParserTests}
 * 
 * @author James Wong
 * @version 2023-01-06
 * @since v1.0.0
 */
public class PrometheusParserTests {

    @Test
    public void testParsePrometheusMetrics() {
        // @formatter:off
        final String metricsString = ""
                + "# HELP jmx_scrape_duration_seconds Time this JMX scrape took, in seconds.\n"
                + "# TYPE jmx_scrape_duration_seconds gauge\n"
                + "jmx_scrape_duration_seconds 4.354308628\n"
                + "# HELP jmx_scrape_error Non-zero if this scrape failed.\n"
                + "# TYPE jmx_scrape_error gauge\n"
                + "jmx_scrape_error 0.0\n"
                + "# HELP jmx_scrape_cached_beans Number of beans with their matching rule cached\n"
                + "# TYPE jmx_scrape_cached_beans gauge\n"
                + "jmx_scrape_cached_beans 0.0\n"
                + "# HELP jmx_config_reload_failure_created Number of times configuration have failed to be reloaded.\n"
                + "# TYPE jmx_config_reload_failure_created gauge\n"
                + "jmx_config_reload_failure_created 1.672820502529E9\n"
                + "# HELP jmx_config_reload_success_created Number of times configuration have successfully been reloaded.\n"
                + "# TYPE jmx_config_reload_success_created gauge\n"
                + "jmx_config_reload_success_created 1.672820502524E9\n"
                + "# HELP jvm_memory_pool_allocated_bytes_created Total bytes allocated in a given JVM memory pool. Only updated after GC, not continuously.\n"
                + "# TYPE jvm_memory_pool_allocated_bytes_created gauge\n"
                + "jvm_memory_pool_allocated_bytes_created{pool=\"G1 Old Gen\",} 1.672820506681E9\n"
                + "jvm_memory_pool_allocated_bytes_created{pool=\"Code Cache\",} 1.672820506685E9\n"
                + "jvm_memory_pool_allocated_bytes_created{pool=\"G1 Eden Space\",} 1.672820506685E9\n"
                + "jvm_memory_pool_allocated_bytes_created{pool=\"G1 Survivor Space\",} 1.672820506685E9\n"
                + "jvm_memory_pool_allocated_bytes_created{pool=\"Compressed Class Space\",} 1.672820506685E9\n"
                + "jvm_memory_pool_allocated_bytes_created{pool=\"Metaspace\",} 1.672820506685E9";
        // @formatter:on

        final List<PrometheusMetrics> parsed = PrometheusParser.getInstance().parse(metricsString);
        final String metricsJson = toJSONString(parsed, true);
        System.out.println(metricsJson);
    }

}
