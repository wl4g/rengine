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
package com.wl4g.rengine.manager.admin.service.impl;

import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;
import static java.lang.String.format;
import static java.util.Objects.isNull;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.Workflow;
import com.wl4g.rengine.common.util.IdGenUtil;
import com.wl4g.rengine.manager.admin.model.DeleteWorkflow;
import com.wl4g.rengine.manager.admin.model.DeleteWorkflowResult;
import com.wl4g.rengine.manager.admin.model.QueryWorkflow;
import com.wl4g.rengine.manager.admin.model.SaveWorkflow;
import com.wl4g.rengine.manager.admin.model.SaveWorkflowResult;
import com.wl4g.rengine.manager.admin.service.WorkflowService;

/**
 * {@link WorkflowServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class WorkflowServiceImpl implements WorkflowService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public PageHolder<Workflow> query(QueryWorkflow model) {
        Query query = new Query(new Criteria().orOperator(Criteria.where("_id").is(model.getWorkflowId()),
                Criteria.where("scenesId").is(model.getScenesId()),
                Criteria.where("name").regex(format("(%s)+", model.getName())), Criteria.where("enable").is(model.getEnable()),
                Criteria.where("orgCode").is(model.getOrgCode()), Criteria.where("labels").in(model.getLabels())));
        query.with(PageRequest.of(model.getPageNum(), model.getPageSize(), Sort.by(Direction.DESC, "updateDate")));

        List<Workflow> workflows = mongoTemplate.find(query, Workflow.class, MongoCollectionDefinition.WORKFLOWS.getName());

        Collections.sort(workflows, (o1, o2) -> safeLongToInt(o2.getUpdateDate().getTime() - o1.getUpdateDate().getTime()));

        // QueryWorkflowResult.builder()
        // .workflows(safeList(workflows).stream()
        // .map(p -> Workflow.builder()
        // .id(p.getId())
        // .name(p.getName())
        // .labels(p.getLabels())
        // .enable(p.getEnable())
        // .remark(p.getRemark())
        // .updateBy(p.getUpdateBy())
        // .updateDate(p.getUpdateDate())
        // .createBy(p.getCreateBy())
        // .createDate(p.getCreateDate())
        // .build())
        // .collect(toList()))
        // .build();

        return new PageHolder<Workflow>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.WORKFLOWS.getName()))
                .withRecords(workflows);
    }

    @Override
    public SaveWorkflowResult save(SaveWorkflow model) {
        Workflow workflow = Workflow.builder()
                .id(model.getId())
                .name(model.getName())
                .orgCode(model.getOrgCode())
                .labels(model.getLabels())
                .ruleIds(model.getRuleIds())
                .enable(model.getEnable())
                .remark(model.getRemark())
                .build();

        if (isNull(workflow.getId())) {
            workflow.setId(IdGenUtil.nextLong());
            workflow.preInsert();
        } else {
            workflow.preUpdate();
        }

        Workflow saved = mongoTemplate.insert(workflow, MongoCollectionDefinition.WORKFLOWS.getName());
        return SaveWorkflowResult.builder().id(saved.getId()).build();
    }

    @Override
    public DeleteWorkflowResult delete(DeleteWorkflow model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.WORKFLOWS.getName());
        return DeleteWorkflowResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
