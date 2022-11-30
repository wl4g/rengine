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
package com.wl4g.rengine.evaluator.service.impl;

import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.JOBS;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.bson.Document;

import com.wl4g.rengine.common.entity.Job;
import com.wl4g.rengine.evaluator.repository.MongoRepository;
import com.wl4g.rengine.evaluator.service.JobService;

import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link JobServiceImpl}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v1.0.0
 * @see https://mongodb.github.io/mongo-java-driver/3.4/builders/aggregation/#lookup
 */
@Slf4j
@Singleton
public class JobServiceImpl implements JobService {

    @Inject
    MongoRepository mongoRepository;

    @Override
    public Uni<List<Job>> listAll() {
        // TODO hello world
        return mongoRepository.getReactiveCollection(JOBS).find().map(doc -> {
            Job job = Job.builder().id(doc.getLong("id")).build();
            return job;
        }).collect().asList();
    }

    @Override
    public Uni<Void> save(Job job) {
        log.info("");
        // TODO hello world
        Document document = new Document().append("id",
                job.getId())/* .append("labels", rule.getLabels()) */;
        return mongoRepository.getReactiveCollection(JOBS).insertOne(document).onItem().ignore().andContinueWithNull();
    }

}
