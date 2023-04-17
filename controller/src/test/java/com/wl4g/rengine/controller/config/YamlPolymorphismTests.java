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
package com.wl4g.rengine.controller.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link YamlPolymorphismTests}
 * 
 * @author James Wong
 * @date 2022-10-21
 * @since v3.0.0
 */
public class YamlPolymorphismTests {

    /**
     * see:https://github1s.com/snakeyaml/snakeyaml/blob/master/src/test/java/org/yaml/snakeyaml/constructor/ImplicitTagsTest.java#L88-L89
     * 
     * YAML examples:
     * 
     * <pre>
     *    cars:
     *      - !TESLA
     *        model: model3
     *        batteryCapacity: 560
     *      - !LINCOLN
     *        model: C3
     *        tankCapacity: 850
     * </pre>
     */
    @Test
    public void testPolymorphismUnmarshal() throws IOException {
        // @formatter:off
        String yaml = "cars:\n"
                + "      - !TESLA\n"
                + "        model: model3\n"
                + "        batteryCapacity: 560\n"
                + "      - !LINCOLN\n"
                + "        model: C3\n"
                + "        tankCapacity: 850";
        // @formatter:on

        TestCarArrayConfig config = new Yaml(new CarArrayYamlConstructor()).loadAs(yaml, TestCarArrayConfig.class);
        System.out.println(config);
        Assertions.assertEquals(config.getCars().size(), 2);
        Assertions.assertInstanceOf(TestTeslaConfig.class, config.getCars().get(0));
        Assertions.assertEquals(config.getCars().get(0).getModel(), "model3");
    }

    public class CarArrayYamlConstructor extends Constructor {
        public CarArrayYamlConstructor() {
            addTypeDescription(new TypeDescription(TestTeslaConfig.class, "!TESLA"));
            addTypeDescription(new TypeDescription(TestLincolnConfig.class, "!LINCOLN"));
        }
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCarArrayConfig {
        private @Default List<TestCarConfig> cars = new ArrayList<>();
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public abstract static class TestCarConfig {
        private String model;
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestTeslaConfig extends TestCarConfig {
        private Double batteryCapacity;
    }

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestLincolnConfig extends TestCarConfig {
        private Double tankCapacity;
    }

}
