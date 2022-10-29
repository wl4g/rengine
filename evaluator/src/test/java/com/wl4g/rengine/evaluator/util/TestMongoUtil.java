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
package com.wl4g.rengine.evaluator.util;

import static com.wl4g.infra.common.reflect.ReflectionUtils2.findField;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.setField;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoDriverInformation;
import com.mongodb.client.MongoClient;
import com.mongodb.client.internal.MongoClientImpl;
import com.wl4g.rengine.evaluator.repository.MongoRepository;

/**
 * {@link TestMongoUtil}
 * 
 * @author James Wong
 * @version 2022-10-10
 * @since v1.0.0
 */
public abstract class TestMongoUtil {

    public static MongoRepository createMongoRepository() {
        MongoClient mongoClient = new MongoClientImpl(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017/rengine"))
                .build(), MongoDriverInformation.builder().build());

        MongoRepository mongoRepository = new MongoRepository();
        setField(findField(MongoRepository.class, null, MongoClient.class), mongoRepository, mongoClient, true);

        return mongoRepository;
    }

}
