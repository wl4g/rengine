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
package com.wl4g.rengine.common.util;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Collections.singletonMap;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.infra.common.serialize.BsonUtils2;
import com.wl4g.infra.common.serialize.JacksonUtils;

/**
 * {@link BsonEntitySerializers}
 * 
 * @author James Wong
 * @version 2023-01-07
 * @since v1.0.0
 */
public abstract class BsonEntitySerializers {

    public static Document toDocument(final @Nullable BaseBean entity) {
        if (isNull(entity)) {
            return null;
        }
        return Document.parse(serialize(entity));
    }

    public static <T> T fromDocument(final @Nullable Document source, final @NotNull Class<T> clazz) {
        notNullOf(clazz, "clazz");
        if (isNull(source)) {
            return null;
        }
        return deserialize(source.toJson(BsonUtils2.DEFAULT_JSON_WRITER_SETTINGS), clazz);
    }

    public static String serialize(final @Nullable BaseBean entity) {
        if (isNull(entity)) {
            return null;
        }
        return toJSONString(DEFAULT_MODIFIER_MAPPER, entity, ID_TRANSFORM_SERIALIZE, IGNORE_PROPERTIES);
    }

    public static <T> T deserialize(final @Nullable String json, final @NotNull Class<T> clazz) {
        notNullOf(clazz, "clazz");
        if (isBlank(json)) {
            return null;
        }
        return parseJSON(DEFAULT_MODIFIER_MAPPER, json, clazz, ID_TRANSFORM_DESERIALIZE, IGNORE_PROPERTIES);
    }

    // Notice: When using a custom modifier, you should use an independent
    // objectMapper, because the same objectmapper instance will cache the
    // serializer of the target bean, which may cause the modifier to fail.
    public static final ObjectMapper DEFAULT_MODIFIER_MAPPER = JacksonUtils.newDefaultObjectMapper();

    public static final Map<String, String> ID_TRANSFORM_SERIALIZE = singletonMap("id", "_id");
    public static final Map<String, String> ID_TRANSFORM_DESERIALIZE = singletonMap("_id", "id");
    public static final String[] IGNORE_PROPERTIES = new String[] { "humanCreateDate", "humanUpdateDate" };

}
