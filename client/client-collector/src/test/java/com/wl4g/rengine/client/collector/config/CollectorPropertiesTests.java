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
package com.wl4g.rengine.client.collector.config;

import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * {@link CollectorPropertiesTests}
 * 
 * @author James Wong
 * @version 2022-10-21
 * @since v3.0.0
 */
public class CollectorPropertiesTests {

    @Test
    public void testGetDefaultTimeZone() throws IOException {
        System.out.println(TimeZone.getDefault().getID());
        System.out.println(TimeZone.getDefault().toString());
        System.out.println(TimeZone.getDefault().getDisplayName());
        System.out.println(TimeZone.getDefault().getDisplayName(Locale.US));
        System.out.println(TimeZone.getDefault().getDSTSavings());
        System.out.println(TimeZone.getDefault().toZoneId().getId());
        System.out.println(TimeZone.getDefault().toZoneId().toString());
        System.out.println(TimeZone.getDefault().toZoneId().normalized());
    }

    @Test
    public void testScrapeJobPropertiesPolymorphismUnmarshal() throws Exception {
        // @formatter:off
        String yaml = "scrapeJobConfigs:\n"
//                + "  - !SIMPLE_HTTP\n"
                + "  - !PROMETHEUS\n"
                + "    name: http-job\n"
                + "    description: The job that scrapes events remote over HTTP.\n"
                + "    staticParams:\n"
                + "      - name: instance-1\n"
                + "        url: http://localhost:8080/event\n"
                + "        method: GET\n"
                + "        headers:\n"
                + "          Content-Type: \"text/html,X-Foo=Bar\"\n"
                + "        body: \"\"\n"
                + "        connectTimeoutMs: 3000\n"
                + "        readTimeoutMs: 5000";
        // @formatter:on

        CollectorProperties config = new Yaml(new CollectorYamlConstructor()).loadAs(yaml, CollectorProperties.class);
        System.out.println(config);
    }

}