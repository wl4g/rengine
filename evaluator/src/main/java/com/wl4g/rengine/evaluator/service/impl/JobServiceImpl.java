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
package com.wl4g.rengine.evaluator.service.impl;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.bson.Document;

import com.wl4g.rengine.common.bean.Job;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.evaluator.service.JobService;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;

/**
 * {@link JobServiceImpl}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v3.0.0
 */
@ApplicationScoped
public class JobServiceImpl implements JobService {

    private @Inject ReactiveMongoClient mongoClient;

    // TODO hello world
    @Override
    public Uni<List<Job>> listAll() {
        return getCollection().find().map(doc -> {
            Job job = Job.builder().id(doc.getLong("id")).build();
            return job;
        }).collect().asList();
    }

    // TODO hello world
    @Override
    public Uni<Void> save(Job job) {
        Document document = new Document().append("id",
                job.getId())/* .append("labels", rule.getLabels()) */;
        return getCollection().insertOne(document).onItem().ignore().andContinueWithNull();
    }

    private ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase("fruit").getCollection(MongoCollectionDefinition.JOBS.getName());
    }

}
