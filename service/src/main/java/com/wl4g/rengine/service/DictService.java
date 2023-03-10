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
package com.wl4g.rengine.service;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.entity.sys.Dict;
import com.wl4g.rengine.common.entity.sys.Dict.DictType;
import com.wl4g.rengine.service.model.DictDelete;
import com.wl4g.rengine.service.model.DictDeleteResult;
import com.wl4g.rengine.service.model.DictQuery;
import com.wl4g.rengine.service.model.DictSave;
import com.wl4g.rengine.service.model.DictSaveResult;

/**
 * {@link DictService}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
public interface DictService {

    default Map<String, Object> loadInitDicts(DictQuery model) {
        final var dicts = safeList(query(DictQuery.builder()
                .type(model.getType())
                .key(model.getKey())
                .value(model.getValue())
                .name(model.getName())
                .pageSize(Integer.MAX_VALUE)
                .build()).getRecords());

        final Map<String, Object> result = new HashMap<>(dicts.size());
        final Map<String, List<Dict>> dictList = new HashMap<>(dicts.size());
        final Map<String, Map<String, Dict>> dictMap = new HashMap<>(dicts.size());
        for (Dict dict : dicts) {
            DictType type = dict.getType();
            // To dictionaries list
            List<Dict> list = dictList.getOrDefault(type.name(), new ArrayList<>());
            list.add(dict);
            dictList.put(type.name(), list);

            // To dictionaries map
            Map<String, Dict> map = dictMap.getOrDefault(type.name(), new HashMap<>());
            map.put(dict.getValue(), dict);
            dictMap.put(type.name(), map);
        }
        result.put("dictList", dictList);
        result.put("dictMap", dictMap);

        return result;
    }

    default List<Dict> findDicts(@Nullable DictType type, @Nullable String key, @Nullable String value) {
        return query(DictQuery.builder().type(type).key(key).value(value).pageNum(1).pageSize(Integer.MAX_VALUE).build())
                .getRecords();
    }

    PageHolder<Dict> query(@NotNull DictQuery model);

    DictSaveResult save(@NotNull DictSave model);

    DictDeleteResult delete(@NotNull DictDelete model);

}
