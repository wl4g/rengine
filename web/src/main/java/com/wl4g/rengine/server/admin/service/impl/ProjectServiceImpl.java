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
package com.wl4g.rengine.server.admin.service.impl;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.wl4g.rengine.common.bean.mongo.Project;
import com.wl4g.rengine.server.admin.model.AddProject;
import com.wl4g.rengine.server.admin.model.AddProjectResult;
import com.wl4g.rengine.server.admin.model.DeleteProject;
import com.wl4g.rengine.server.admin.model.DeleteProjectResult;
import com.wl4g.rengine.server.admin.model.QueryProject;
import com.wl4g.rengine.server.admin.model.QueryProjectResult;
import com.wl4g.rengine.server.admin.service.ProjectService;
import com.wl4g.rengine.server.constants.RengineWebConstants.MongoCollectionDefinition;
import com.wl4g.rengine.server.util.IdGenUtil;

/**
 * {@link ProjectServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v3.0.0
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public QueryProjectResult query(QueryProject model) {
        // TODO use pagination

        Criteria criteria = new Criteria().orOperator(Criteria.where("name").is(model.getName()),
                Criteria.where("_id").is(model.getProjectId()), Criteria.where("labels").in(model.getLabels()),
                Criteria.where("owner").is(model.getOwner()));

        List<Project> projects = mongoTemplate.find(new Query(criteria), Project.class,
                MongoCollectionDefinition.PROJECTS.getName());

        Collections.sort(projects, (o1, o2) -> safeLongToInt(o2.getUpdateDate().getTime() - o1.getUpdateDate().getTime()));

        return QueryProjectResult.builder()
                .projects(safeList(projects).stream()
                        .map(p -> Project.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .owner(p.getOwner())
                                .labels(p.getLabels())
                                .enabled(p.getEnabled())
                                .remark(p.getRemark())
                                .updateBy(p.getUpdateBy())
                                .updateDate(p.getUpdateDate())
                                .build())
                        .collect(toList()))
                .build();
    }

    @Override
    public AddProjectResult save(AddProject model) {
        Project project = Project.builder()
                .id(IdGenUtil.next())
                .name(model.getName())
                .owner(model.getOwner())
                .labels(model.getLabels())
                .enabled(model.getEnabled())
                .remark(model.getRemark())
                .updateBy("admin")
                .updateDate(new Date())
                .build();
        Project saved = mongoTemplate.insert(project, MongoCollectionDefinition.PROJECTS.getName());
        return AddProjectResult.builder().id(saved.getId()).build();
    }

    @Override
    public DeleteProjectResult delete(DeleteProject model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.PROJECTS.getName());
        return DeleteProjectResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
