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
package com.wl4g.rengine.executor.repository;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;

/**
 * {@link MongoSourceFacade}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v1.0.0
 */
@Singleton
public class MongoRepository {

    @Inject
    MongoClient mongoClient;

    @Inject
    ReactiveMongoClient reactiveMongoClient;

    public MongoCollection<Document> getCollection(MongoCollectionDefinition collection) {
        return mongoClient.getDatabase(RengineConstants.DEFAULT_MONGODB_DATABASE).getCollection(collection.getName());
    }

    public ReactiveMongoCollection<Document> getReactiveCollection(MongoCollectionDefinition collection) {
        return reactiveMongoClient.getDatabase(RengineConstants.DEFAULT_MONGODB_DATABASE).getCollection(collection.getName());
    }

}
