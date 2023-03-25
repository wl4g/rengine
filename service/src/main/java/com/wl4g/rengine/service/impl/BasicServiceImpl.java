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

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.service.mongo.QueryHolder.logicalDelete;

import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.service.config.RengineServiceProperties;
import com.wl4g.rengine.service.minio.MinioClientManager;
import com.wl4g.rengine.service.model.BaseDelete;
import com.wl4g.rengine.service.mongo.GlobalMongoSequenceService;

/**
 * {@link BasicServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public abstract class BasicServiceImpl {

    protected @Autowired Validator validator;

    protected @Autowired RengineServiceProperties config;

    protected @Autowired MongoTemplate mongoTemplate;

    protected @Autowired RedisTemplate<String, String> redisTemplate;

    protected @Autowired GlobalMongoSequenceService mongoSequenceService;

    protected @Autowired(required = false) MinioClientManager minioManager;

    protected long doDeleteGracefully(@NotNull BaseDelete model, @NotNull MongoCollectionDefinition collection) {
        notNullOf(model, "delete");
        notNullOf(collection, "collection");

        // for physical deletion.
        if (model.getForce()) {
            // 'id' is a keyword, it will be automatically converted to '_id'
            final DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                    collection.getName());
            return result.getDeletedCount();
        }

        // for logicistal delete.
        final UpdateResult result = mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(model.getId())), logicalDelete(),
                collection.getName());
        return result.getModifiedCount();
    }

}
