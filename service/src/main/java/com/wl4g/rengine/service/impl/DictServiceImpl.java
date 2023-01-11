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
package com.wl4g.rengine.service.impl;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.StringUtils2.eqIgnCase;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.descSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.Dict;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.service.DictService;
import com.wl4g.rengine.service.config.RengineServiceProperties;
import com.wl4g.rengine.service.model.DeleteDict;
import com.wl4g.rengine.service.model.DeleteDictResult;
import com.wl4g.rengine.service.model.QueryDict;
import com.wl4g.rengine.service.model.SaveDict;
import com.wl4g.rengine.service.model.SaveDictResult;

import lombok.CustomLog;

/**
 * {@link DictServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 * @see {@link com.wl4g.rengine.executor.service.impl.ReactiveDictServiceImpl}
 */
@Service
@CustomLog
public class DictServiceImpl implements DictService {

    @Autowired
    RengineServiceProperties config;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    HashOperations<String, String, String> hashOperations;

    @PostConstruct
    public void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public PageHolder<Dict> query(QueryDict model) {
        List<Dict> dicts = null;

        // The first priority get single from cache.
        if (nonNull(model.getType()) && !isBlank(model.getKey())) {
            final var dict = parseJSON(hashOperations.get(config.getService().getDictCachedPrefix(),
                    Dict.buildCacheHashKey(model.getType().name(), model.getKey())), Dict.class);
            dicts = singletonList(dict);
        }
        // The second priority get all from cache, and filter.
        else if (isNull(model.getType()) || isBlank(model.getKey())) {
            final var allDictJsonMap = hashOperations.entries(config.getService().getDictCachedPrefix());
            dicts = safeMap(allDictJsonMap).entrySet()
                    .parallelStream()
                    .map(e -> parseJSON(e.getValue(), Dict.class))
                    // A query condition with a value needs to be actually
                    // equal to hit, and a query condition with a null value
                    // hit by default.
                    .filter(dict -> {
                        boolean hitId = isNull(model.getDictId());
                        if (nonNull(model.getDictId())) {
                            hitId = eqIgnCase(model.getDictId(), dict.getId());
                        }
                        boolean hitOrgCode = isBlank(model.getOrgCode());
                        if (!isBlank(model.getOrgCode())) {
                            hitOrgCode = eqIgnCase(model.getOrgCode(), dict.getOrgCode());
                        }
                        boolean hitLabels = CollectionUtils2.isEmpty(model.getLabels());
                        if (!CollectionUtils2.isEmpty(model.getLabels())) {
                            hitLabels = safeList(model.getLabels()).stream()
                                    .allMatch(l -> safeList(dict.getLabels()).contains(l));
                        }
                        boolean hitEnable = isNull(model.getEnable());
                        if (nonNull(model.getEnable())) {
                            hitEnable = (model.getEnable() ? BaseBean.ENABLED : BaseBean.DISABLED) == dict.getEnable();
                        }
                        boolean hitType = isNull(model.getType());
                        if (nonNull(model.getType())) {
                            hitType = eqIgnCase(model.getType(), dict.getType());
                        }
                        boolean hitKey = isBlank(model.getKey());
                        if (!isBlank(model.getKey())) {
                            hitKey = eqIgnCase(model.getKey(), dict.getKey());
                        }
                        boolean hitValue = isBlank(model.getValue());
                        if (!isBlank(model.getValue())) {
                            hitValue = eqIgnCase(model.getValue(), dict.getValue());
                        }
                        return hitId && hitOrgCode && hitLabels && hitEnable && hitType && hitKey && hitValue;
                    })
                    .collect(toList());
        }

        // The third priority get from DB, and save to cache.
        long total = 0;
        if (CollectionUtils2.isEmpty(dicts)) {
            final Query query = new Query(andCriteria(baseCriteria(model), isCriteria("type", model.getType().name()),
                    isCriteria("key", model.getKey()), isCriteria("value", model.getValue())))
                            .with(PageRequest.of(model.getPageNum(), model.getPageSize(), descSort("sort", "updateDate")));
            dicts = mongoTemplate.find(query, Dict.class, MongoCollectionDefinition.SYS_DICTS.getName());
            total = mongoTemplate.count(query, MongoCollectionDefinition.SYS_DICTS.getName());

            // Save to cache.
            safeList(dicts).parallelStream().forEach(dict -> {
                hashOperations.put(config.getService().getDictCachedPrefix(),
                        Dict.buildCacheHashKey(dict.getType().name(), dict.getKey()), toJSONString(dict));
            });
        }

        // Sets cached expire.
        setDictCachedExpire();

        return new PageHolder<Dict>(model.getPageNum(), model.getPageSize()).withTotal(total).withRecords(dicts);
    }

    @Override
    public SaveDictResult save(SaveDict model) {
        Dict dicts = model;
        notNullOf(dicts, "dicts");

        if (isNull(dicts.getId())) {
            dicts.setId(IdGenUtils.nextLong());
            dicts.preInsert();
        } else {
            dicts.preUpdate();
        }

        final Dict saved = mongoTemplate.insert(dicts, MongoCollectionDefinition.SYS_DICTS.getName());

        // Save to cached.
        if (nonNull(saved.getId()) && saved.getId() > 0) {
            hashOperations.put(config.getService().getDictCachedPrefix(),
                    Dict.buildCacheHashKey(saved.getType().name(), saved.getKey()), toJSONString(saved));
            // Sets cached expire.
            setDictCachedExpire();
        }

        return SaveDictResult.builder().id(saved.getId()).build();
    }

    @Override
    public DeleteDictResult delete(DeleteDict model) {
        final Query idQuery = new Query(isIdCriteria(model.getId()));

        // Gets pre deletion dict for cache.
        final Dict preDelete = mongoTemplate.findOne(idQuery, Dict.class);
        if (nonNull(preDelete)) {
            // Remove the DB.
            final DeleteResult result = mongoTemplate.remove(idQuery, MongoCollectionDefinition.SYS_DICTS.getName());

            // Remove the cached.
            if (result.getDeletedCount() > 0) {
                Long deleteResult = hashOperations.delete(Dict.buildCacheHashKey(preDelete.getType().name(), preDelete.getKey()));
                if (nonNull(deleteResult) && deleteResult > 0) {
                    log.warn("Failed to remove cache for dict : {}, {}", preDelete.getType(), preDelete.getKey());
                }
            }
            return DeleteDictResult.builder().deletedCount(result.getDeletedCount()).build();
        }
        log.warn("Cannot delete non-existent dict for : {}", model.getId());

        return DeleteDictResult.builder().deletedCount(0L).build();
    }

    private Boolean setDictCachedExpire() {
        return redisTemplate.expire(config.getService().getDictCachedPrefix(), config.getService().getDictCachedExpire(),
                TimeUnit.MILLISECONDS);
    }

}
