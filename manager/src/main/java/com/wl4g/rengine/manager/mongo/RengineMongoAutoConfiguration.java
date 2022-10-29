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
package com.wl4g.rengine.manager.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoAction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteConcernResolver;

import com.mongodb.WriteConcern;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;

/**
 * {@link RengineWriteConcernResolver}
 * 
 * @author James Wong
 * @version 2022-09-10
 * @since v1.0.0
 */
@Configuration
public class RengineMongoAutoConfiguration {

    @Bean
    @ConditionalOnClass(MongoTemplate.class)
    public WriteConcernResolver rengineWriteConcernResolver(MongoTemplate mongoTemplate) {
        return new RengineWriteConcernResolver(mongoTemplate);
    }

    public static class RengineWriteConcernResolver implements WriteConcernResolver {

        public RengineWriteConcernResolver(MongoTemplate mongoTemplate) {
            mongoTemplate.setWriteConcernResolver(this);
        }

        @Override
        public WriteConcern resolve(MongoAction action) {
            if (MongoCollectionDefinition.of(action.getCollectionName()).isWriteConcernSafe()) {
                return WriteConcern.MAJORITY;
            }
            return action.getDefaultWriteConcern();
        }

    }

}