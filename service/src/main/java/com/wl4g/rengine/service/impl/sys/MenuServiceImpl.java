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
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_MENUS;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.util.Objects.isNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.function.TreeConvertor;
import com.wl4g.rengine.common.entity.sys.Menu;
import com.wl4g.rengine.service.MenuService;
import com.wl4g.rengine.service.impl.BasicServiceImpl;
import com.wl4g.rengine.service.model.sys.MenuDelete;
import com.wl4g.rengine.service.model.sys.MenuDeleteResult;
import com.wl4g.rengine.service.model.sys.MenuQuery;
import com.wl4g.rengine.service.model.sys.MenuSave;
import com.wl4g.rengine.service.model.sys.MenuSaveResult;
import com.wl4g.rengine.service.security.user.AuthenticationService;

/**
 * {@link MenuServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class MenuServiceImpl extends BasicServiceImpl implements MenuService {

    @Autowired
    AuthenticationService authenticationService;

    @Override
    public PageHolder<Menu> query(MenuQuery model) {
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getMenuId())))
                .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<Menu> menus = mongoTemplate.find(query, Menu.class, SYS_MENUS.getName());

        return new PageHolder<Menu>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, SYS_MENUS.getName()))
                .withRecords(menus);
    }

    @Override
    public List<Menu> loadMenuTree() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        notNullOf(authentication, "authentication");

        final List<Menu> flatMenus = authenticationService.loadMenusByUsername(authentication.getName());

        final TreeConvertor<Long, Menu> convertor = new TreeConvertor<>(Menu.DEFAULT_ROOT_PARENT_ID, (id1, id2) -> {
            // Prevent wrapping type (Long) from can't using '=='.
            return id1.compareTo(id2) == 0;
        });
        return convertor.formatToChildren(flatMenus, false);
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

        Menu saved = mongoTemplate.save(menu, SYS_MENUS.getName());
        return MenuSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public MenuDeleteResult delete(MenuDelete model) {
        return MenuDeleteResult.builder().deletedCount(doDeleteGracefully(model, SYS_MENUS)).build();
    }

}
