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
package com.wl4g;

import com.wl4g.infra.common.arthas.ArthasAttacher;

import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import io.quarkus.runtime.ApplicationLifecycleManager;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * {@link RengineColletor}
 * 
 * @author James Wong
 * @version 2022-10-16
 * @since v1.0.0
 */
@QuarkusMain
public class RengineScheduler implements QuarkusApplication {

    public static void main(String[] args) {
        // After opening and running, an error will be reported ?
        // NoClassDefFoundError:org/apache/shardingsphere/elasticjob/api/JobConfiguration
        ArthasAttacher.attachIfNecessary("rengine-scheduler", QuarkusClassLoader.class);
        System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.JBossLogFactory");
        Quarkus.run(RengineScheduler.class, args);
    }

    @Override
    public int run(String... args) throws Exception {
        Quarkus.waitForExit();
        return ApplicationLifecycleManager.getExitCode();
    }

}
