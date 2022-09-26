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
package com.wl4g.rengine.manager.admin.service;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.bean.Rule;
import com.wl4g.rengine.manager.admin.model.DeleteRule;
import com.wl4g.rengine.manager.admin.model.DeleteRuleResult;
import com.wl4g.rengine.manager.admin.model.QueryRule;
import com.wl4g.rengine.manager.admin.model.SaveRule;
import com.wl4g.rengine.manager.admin.model.SaveRuleResult;

/**
 * {@link RuleService}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v3.0.0
 */
public interface RuleService {

    PageHolder<Rule> query(QueryRule model);

    SaveRuleResult save(SaveRule model);

    DeleteRuleResult delete(DeleteRule model);

}
