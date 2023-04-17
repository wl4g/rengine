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
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.collector.config;

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import com.wl4g.infra.context.utils.expression.SpelExpressions;

/**
 * {@link CollectorPropertiesTests}
 * 
 * @author James Wong
 * @date 2022-10-21
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
               // + "  - !SIMPLE_HTTP\n"
                + "  - !PROMETHEUS\n"
                + "    name: http-job\n"
                + "    description: The job that scrapes events remote over HTTP.\n"
                + "    jobParams:\n"
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
        System.out.println(toJSONString(config, true));
    }

    @Test
    public void testSpelExpression() throws Exception {
        //@formatter:off
        //String expression = "#{T(com.wl4g.infra.common.lang.DateUtils2).getDateOf(T(com.wl4g.infra.common.lang.DateUtils2).addDays(new java.util.Date(),-1),\"yyyy-MM-dd\")}";
        //@formatter:on
        String expression = "#{T(com.wl4g.infra.common.lang.DateUtils2).getDateOf(5, -1, \"yyyy-MM-dd\")}";
        Object result = SpelExpressions.create().resolve(expression);
        System.out.println(result);
    }

}
