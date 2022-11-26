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
package com.wl4g.rengine.collector.config;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;

import com.wl4g.rengine.collector.job.CollectJobExecutor.EventJobType;

/**
 * {@link CollectorYamlConstructor}
 * 
 * @author James Wong
 * @version 2022-10-27
 * @since v3.0.0
 */
public class CollectorYamlConstructor extends Constructor {

    public CollectorYamlConstructor() {
        configure(this);
    }

    public static void configure(BaseConstructor constructor) {
        notNullOf(constructor, "constructor");
        for (EventJobType type : EventJobType.values()) {
            // TODO Notice: For example, PrometheusCollectJobExecutor inherits the
            // SimpleHttpCollectJobExecutor and is temporarily treated as the
            // latter class.
            constructor.addTypeDescription(new TypeDescription(type.getJobConfigClass(), "!".concat(type.name())));
        }
    }

}
