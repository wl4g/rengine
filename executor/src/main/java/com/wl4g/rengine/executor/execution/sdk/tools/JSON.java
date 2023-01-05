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

import static com.wl4g.infra.common.serialize.JacksonUtils.parseArrayMapObject;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseMapObject;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.isNull;

import org.graalvm.polyglot.HostAccess;

/**
 * {@link JSON}
 * 
 * @author James Wong
 * @version 2022-12-25
 * @since v1.0.0
 */
public class JSON {
    private static final JSON DEFAULT = new JSON();

    public static @HostAccess.Export JSON getInstance() {
        return DEFAULT;
    }

    public @HostAccess.Export JSON() {
    }

    public @HostAccess.Export Object parse(String json) {
        if (isNull(json)) {
            return null;
        }
        if (json.startsWith("[") && json.endsWith("]")) {
            return parseArrayMapObject(null, json);
        }
        return parseMapObject(json);
    }

    public @HostAccess.Export String stringify(Object obj) {
        return stringify(obj, false);
    }

    public @HostAccess.Export String stringify(Object obj, boolean isPretty) {
        return toJSONString(obj, isPretty);
    }

}
