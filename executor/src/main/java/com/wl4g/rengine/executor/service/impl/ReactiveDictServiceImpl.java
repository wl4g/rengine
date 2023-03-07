/**
 * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
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
package com.wl4g.rengine.executor.service.impl;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.StringUtils2.eqIgnCase;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.common.collect.Lists;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.sys.Dict;
import com.wl4g.rengine.common.entity.sys.Dict.DictType;
import com.wl4g.rengine.common.util.BsonEntitySerializers;
import com.wl4g.rengine.executor.repository.MongoRepository;
import com.wl4g.rengine.executor.service.DictService;
import com.wl4g.rengine.executor.service.ServiceConfig;

import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.hash.ReactiveHashCommands;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.CustomLog;

/**
 * {@link ReactiveDictServiceImpl}
 * 
 * @author James Wong
 * @version 2023-01-14
 * @since v1.0.0
 * @see {@link com.wl4g.rengine.ReactiveDictServiceImpl.impl.DictServiceImpl}
 */
@CustomLog
@Singleton
public class ReactiveDictServiceImpl implements DictService {

    @Inject
    ServiceConfig serviceConfig;

    @Inject
    MongoRepository mongoRepository;

    @Inject
    ReactiveRedisDataSource reactiveRedisDS;

    ReactiveHashCommands<String, String, String> reactiveHashCommands;

    ReactiveKeyCommands<String> reactiveKeyCommands;

    @PostConstruct
    void init() {
        this.reactiveHashCommands = reactiveRedisDS.hash(String.class);
        this.reactiveKeyCommands = reactiveRedisDS.key(String.class);
    }

    @Override
    public Uni<List<Dict>> findDicts(Long dictId, DictType type, String key, String value) {
        log.debug("Finding dicts by dictId: {}, type: {}, key: {}, value: {}", dictId, type, key, value);

        // The first priority get all from cache, and filter.
        if (nonNull(type) && !isBlank(key)) {
            return reactiveHashCommands.hget(serviceConfig.dictCachedPrefix(), Dict.buildCacheHashKey(type.name(), key))
                    .chain(dictJson -> {
                        if (!isBlank(dictJson)) {
                            return Uni.createFrom()
                                    .item(() -> singletonList(parseJSON(dictJson, Dict.class)))
                                    .chain(_dicts -> setDictCachedExpire().map(res -> _dicts));
                        }
                        // If cache doesn't, then fetch from DB.
                        return findDictsFromDB(dictId, type, key, value).chain(dicts -> saveDictsToCache(dicts));
                    });
        }

        //
        // [Notice]: Since quarkus-redis 2.12 is only fully compatible with at
        // least Redis 7, the hgetall method will report an error:
        // java.lang.UnsupportedOperationException: This type doesn't hold a Map
        // type, the problem see:
        // https://github.com/quarkusio/quarkus/issues/28837
        // see:https://github.com/quarkusio/quarkus/blob/2.12.2.Final/extensions/redis-client/runtime/src/main/java/io/quarkus/redis/runtime/datasource/Marshaller.java#L101
        //
        // Fix compatibility with Redis 5
        // see:https://github.com/quarkusio/quarkus/pull/28854
        //
        return reactiveHashCommands.hgetall(serviceConfig.dictCachedPrefix()).map(allDictJsonMap -> {
            return safeMap(allDictJsonMap).entrySet()
                    .parallelStream()
                    .map(e -> parseJSON(e.getValue(), Dict.class))
                    // A query condition with a value needs to be
                    // actually equal to hit, and a query condition with
                    // a null value hit by default.
                    .filter(dict -> {
                        boolean hitValue = isBlank(value);
                        if (!isBlank(value)) {
                            hitValue = eqIgnCase(value, dict.getValue());
                        }
                        return hitValue;
                    })
                    .collect(toList());
            // see:https://quarkus.io/guides/mutiny-primer#shortcuts
            // uni.flatMap(x → uni2)
            // Equivalent-to: uni.chain(x → uni2)
            // Equivalent-to: uni.onItem().transformToUni(x → uni2)
        }).chain(dicts -> {
            if (CollectionUtils2.isEmpty(dicts)) {
                return findDictsFromDB(dictId, type, key, value).chain(queryDicts -> saveDictsToCache(queryDicts));
            }
            return Uni.createFrom().item(() -> dicts).chain(_dicts -> setDictCachedExpire().map(res -> _dicts));
        });
    }

    private Uni<List<Dict>> findDictsFromDB(Long dictId, DictType type, String key, String value) {
        // Common show projection.
        final Bson enableFilter = Aggregates.match(Filters.eq("enable", BaseBean.ENABLED));
        final Bson delFlagFilter = Aggregates.match(Filters.eq("delFlag", BaseBean.DEL_FLAG_NORMAL));
        final Bson project = Aggregates.project(Projections.fields(Projections.exclude("_class", "delFlag")));
        final List<Bson> aggregates = Lists.newArrayList();
        if (nonNull(dictId)) {
            aggregates.add(Aggregates.match(Filters.eq("_id", dictId)));
        }
        if (nonNull(type)) {
            aggregates.add(Aggregates.match(Filters.eq("type", type)));
        }
        if (!isBlank(key)) {
            aggregates.add(Aggregates.match(Filters.eq("key", key)));
        }
        if (!isBlank(value)) {
            aggregates.add(Aggregates.match(Filters.eq("value", value)));
        }
        aggregates.add(enableFilter);
        aggregates.add(delFlagFilter);
        aggregates.add(project);

        // Solution-1:
        // @formatter:off
        //final MongoCollection<Document> collection = mongoRepository.getCollection(MongoCollectionDefinition.SYS_DICTS);
        //final MongoCursor<Dict> cursor = collection.aggregate(aggregates)
        //        .batchSize(engineConfig.maxQueryBatch())
        //        .map(dictDoc -> BsonEntitySerializers.fromDocument(dictDoc, Dict.class))
        //        .iterator();
        //try {
        //    return IteratorUtils.toList(cursor);
        //} finally {
        //    cursor.close();
        //}
        // @formatter:on

        // Solution-2(reactive):
        final ReactiveMongoCollection<Document> collection = mongoRepository
                .getReactiveCollection(MongoCollectionDefinition.SYS_DICTS);
        final Multi<Dict> dictMulti = collection.aggregate(aggregates)
                .map(dictDoc -> BsonEntitySerializers.fromDocument(dictDoc, Dict.class));
        return dictMulti.collect().asList();
    }

    private Uni<? extends List<Dict>> saveDictsToCache(List<Dict> dicts) {
        if (CollectionUtils2.isEmpty(dicts)) {
            return Uni.createFrom().nullItem();
        }

        // [Notice]: Should use Multi for better performance
        // when elements are larger than 25?
        // see:https://stackoverflow.com/questions/67495287/uni-combine-all-unis-v-s-multi-onitem-transformtomultiandconcatenate
        final List<Uni<Boolean>> allSavedUni = safeList(dicts).parallelStream().map(dict -> {
            return reactiveHashCommands.hset(serviceConfig.dictCachedPrefix(),
                    Dict.buildCacheHashKey(dict.getType().name(), dict.getKey()), toJSONString(dict));
        }).collect(toList());

        return Uni.combine()
                .all()
                .unis(allSavedUni)
                .combinedWith(saveds -> dicts)
                .chain(_dicts -> setDictCachedExpire().map(res -> _dicts));
    }

    private Uni<Boolean> setDictCachedExpire() {
        return reactiveKeyCommands.pexpire(serviceConfig.dictCachedPrefix(), serviceConfig.dictCachedExpire());
    }

}
