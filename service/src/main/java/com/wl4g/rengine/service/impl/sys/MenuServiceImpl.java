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
package com.wl4g.rengine.service.impl.sys;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.util.Objects.isNull;

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
import com.wl4g.rengine.common.entity.sys.Menu;
import com.wl4g.rengine.service.MenuService;
import com.wl4g.rengine.service.model.sys.MenuDelete;
import com.wl4g.rengine.service.model.sys.MenuDeleteResult;
import com.wl4g.rengine.service.model.sys.MenuQuery;
import com.wl4g.rengine.service.model.sys.MenuSave;
import com.wl4g.rengine.service.model.sys.MenuSaveResult;

/**
 * {@link MenuServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class MenuServiceImpl implements MenuService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public PageHolder<Menu> query(MenuQuery model) {
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getMenuId())))
                .with(PageRequest.of(model.getPageNum(), model.getPageSize(), Sort.by(Direction.DESC, "updateDate")));

        final List<Menu> menus = mongoTemplate.find(query, Menu.class, MongoCollectionDefinition.SYS_MENUS.getName());

        return new PageHolder<Menu>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.SYS_MENUS.getName()))
                .withRecords(menus);
    }

    @Override
    public MenuSaveResult save(MenuSave model) {
        Menu menu = model;
        notNullOf(menu, "menu");

        if (isNull(menu.getId())) {
            menu.preInsert();
        } else {
            menu.preUpdate();
        }

        Menu saved = mongoTemplate.save(menu, MongoCollectionDefinition.SYS_MENUS.getName());
        return MenuSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public MenuDeleteResult delete(MenuDelete model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.SYS_MENUS.getName());
        return MenuDeleteResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
