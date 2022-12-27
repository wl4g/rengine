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
package com.wl4g.rengine.apiserver.mongo;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeArrayToList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.query.Criteria;

import com.google.common.collect.Lists;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.rengine.apiserver.admin.model.QueryBase;

/**
 * {@link QueryHolder}
 * 
 * @author James Wong
 * @version 2022-12-27
 * @since v1.0.0
 */
public abstract class QueryHolder {

    public static @NotNull Criteria baseCriteria(final @NotNull QueryBase<? extends BaseBean> model) {
        return baseCriteria(model, true, true);
    }

    public static @NotNull Criteria baseCriteria(
            final @NotNull QueryBase<? extends BaseBean> model,
            boolean and,
            boolean delFlagNormal) {
        notNullOf(model, "model");
        final List<Criteria> criterias = Lists.newArrayList();
        if (nonNull(model.getName())) {
            criterias.add(Criteria.where("name").regex(format("(%s)+", model.getName())));
        }
        if (nonNull(model.getEnable())) {
            criterias.add(Criteria.where("enable").is(model.getEnable() ? BaseBean.ENABLED : BaseBean.DISABLED));
        }
        if (nonNull(model.getLabels()) && !model.getLabels().isEmpty()) {
            criterias.add(Criteria.where("labels").in(model.getLabels()));
        }
        if (nonNull(model.getOrgCode())) {
            criterias.add(Criteria.where("orgCode").is(model.getOrgCode()));
        }
        if (delFlagNormal) {
            criterias.add(Criteria.where("delFlag").is(BaseBean.DEL_FLAG_NORMAL));
        } else {
            criterias.add(Criteria.where("delFlag").is(BaseBean.DEL_FLAG_DELETED));
        }
        return and ? new Criteria().andOperator(criterias) : new Criteria().orOperator(criterias);
    }

    public static @Nullable Criteria isCriteria(final @NotBlank String fieldName, final @NotNull Object fieldValue) {
        hasTextOf(fieldName, "fieldName");
        if (nonNull(fieldValue)) {
            return Criteria.where(fieldName).is(fieldValue);
        }
        return null;
    }

    public static @Nullable Criteria inCriteria(final @NotBlank String fieldName, final @NotNull Object fieldValue) {
        hasTextOf(fieldName, "fieldName");
        if (nonNull(fieldValue)) {
            return Criteria.where(fieldName).in(fieldValue);
        }
        return null;
    }

    public static Criteria andCriteria(Criteria... criteria) {
        return new Criteria().andOperator(safeArrayToList(criteria).stream().filter(c -> nonNull(c)).collect(toList()));
    }

}
