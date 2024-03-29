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
package com.wl4g.rengine.service;

import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.Rule.RuleEngine;
import com.wl4g.rengine.common.entity.RuleScript;
import com.wl4g.rengine.common.model.RuleScriptExecuteRequest;
import com.wl4g.rengine.common.model.RuleScriptExecuteResult;
import com.wl4g.rengine.service.model.RuleScriptDelete;
import com.wl4g.rengine.service.model.RuleScriptDeleteResult;
import com.wl4g.rengine.service.model.RuleScriptQuery;
import com.wl4g.rengine.service.model.RuleScriptSave;
import com.wl4g.rengine.service.model.RuleScriptSaveResult;
import com.wl4g.rengine.service.util.RuleScriptParser.ScriptASTInfo;

/**
 * {@link RuleScriptScriptService}
 * 
 * @author James Wong
 * @date 2022-08-29
 * @since v1.0.0
 */
public interface RuleScriptService {

    PageHolder<RuleScript> query(@NotNull RuleScriptQuery model);

    ScriptASTInfo parse(@NotNull RuleEngine engine, @NotNull Long scriptId);

    RuleScriptSaveResult save(@NotNull RuleScriptSave model);

    RuleScriptDeleteResult delete(@NotNull RuleScriptDelete model);

    RespBase<RuleScriptExecuteResult> execute(@NotNull RuleScriptExecuteRequest model);

}
