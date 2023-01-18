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

import org.graalvm.polyglot.HostAccess;

/**
 * {@link UUID}
 * 
 * @author James Wong
 * @version 2022-12-25
 * @since v1.0.0
 */
public class UUID {
    private static final UUID DEFAULT = new UUID();

    public static @HostAccess.Export UUID getInstance() {
        return DEFAULT;
    }

    public @HostAccess.Export UUID() {
    }

    public @HostAccess.Export java.util.UUID randomUUID() {
        return java.util.UUID.randomUUID();
    }

}
