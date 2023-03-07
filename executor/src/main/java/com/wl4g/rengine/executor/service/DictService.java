/*
 * Copyright 2017 ~ 2025 the original author or authors. James Wong  <jameswong1376@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.executor.service;

import java.util.List;

import javax.annotation.Nullable;

import com.wl4g.rengine.common.entity.sys.Dict;
import com.wl4g.rengine.common.entity.sys.Dict.DictType;

import io.smallrye.mutiny.Uni;

/**
 * {@link DictService}
 * 
 * @author James Wong
 * @version 2023-01-14
 * @since v1.0.0
 */
public interface DictService {

    default Uni<List<Dict>> findDicts(@Nullable DictType type, @Nullable String key) {
        return findDicts(null, type, key, null);
    }

    Uni<List<Dict>> findDicts(@Nullable Long dictId, @Nullable DictType type, @Nullable String key, @Nullable String value);

}
