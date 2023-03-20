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
package com.wl4g.rengine.service.mongo;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeArrayToList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import com.google.common.collect.Lists;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.rengine.service.model.BaseQuery;

/**
 * {@link QueryHolder}
 * 
 * @author James Wong
 * @version 2022-12-27
 * @since v1.0.0
 */
public abstract class QueryHolder {

    public static @NotNull Criteria baseCriteria(final @NotNull BaseQuery<? extends BaseBean> model) {
        return baseCriteria(model, true, true);
    }

    public static @NotNull Criteria baseCriteria(
            final @NotNull BaseQuery<? extends BaseBean> model,
            boolean and,
            boolean delFlagNormal) {
        notNullOf(model, "model");
        final List<Criteria> criterias = Lists.newArrayList();
        // if (nonNull(model.getId())) {
        // criterias.add(Criteria.where(DEFAULT_FIELD_ID_.is(model.getId()));
        // }
        if (nonNull(model.getNameEn())) {
            criterias.add(Criteria.where("nameEn").regex(format("(%s)+", model.getNameEn())));
        }
        if (nonNull(model.getNameZh())) {
            criterias.add(Criteria.where("nameZh").regex(format("(%s)+", model.getNameZh())));
        }
        if (nonNull(model.getEnable())) {
            criterias.add(Criteria.where("enable").is(model.getEnable() ? BaseBean.ENABLED : BaseBean.DISABLED));
        }
        if (nonNull(model.getLabels()) && !model.getLabels().isEmpty()) {
            criterias.add(Criteria.where("labels").in(model.getLabels()));
        }
        // if (nonNull(model.getOrgCode())) {
        // criterias.add(Criteria.where("orgCode").is(model.getOrgCode()));
        // }
        if (nonNull(model.getTenantId())) {
            criterias.add(Criteria.where("tenantId").is(model.getTenantId()));
        }
        if (delFlagNormal) {
            criterias.add(Criteria.where("delFlag").is(BaseBean.DEL_FLAG_NORMAL));
        } else {
            criterias.add(Criteria.where("delFlag").is(BaseBean.DEL_FLAG_DELETED));
        }
        return and ? new Criteria().andOperator(criterias) : new Criteria().orOperator(criterias);
    }

    public static @Nullable Criteria isIdCriteria(final @Nullable Object fieldValue) {
        if (nonNull(fieldValue)) {
            return Criteria.where(DEFAULT_FIELD_ID).is(fieldValue);
        }
        return null;
    }

    public static @Nullable <T> Criteria inIdsCriteria(final @Nullable List<T> fieldValues) {
        return inIdsCriteria(safeList(fieldValues).toArray());
    }

    public static @Nullable Criteria inIdsCriteria(final @Nullable Object... fieldValues) {
        return inCriteria(DEFAULT_FIELD_ID, fieldValues);
    }

    public static @Nullable Criteria gteUpdateDateCriteria(final @Nullable Object fieldValue) {
        if (nonNull(fieldValue)) {
            return Criteria.where(DEFAULT_FIELD_UPDATE_DATE).gte(fieldValue);
        }
        return null;
    }

    public static @Nullable Criteria lteUpdateDateCriteria(final @Nullable Object fieldValue) {
        if (nonNull(fieldValue)) {
            return Criteria.where(DEFAULT_FIELD_UPDATE_DATE).lte(fieldValue);
        }
        return null;
    }

    public static @Nullable Criteria isCriteria(final @NotBlank String fieldName, final @Nullable Object fieldValue) {
        hasTextOf(fieldName, "fieldName");
        if (nonNull(fieldValue)) {
            return Criteria.where(fieldName).is(fieldValue);
        }
        return null;
    }

    public static @Nullable Criteria gteCriteria(final @NotBlank String fieldName, final @Nullable Object fieldValue) {
        hasTextOf(fieldName, "fieldName");
        if (nonNull(fieldValue)) {
            return Criteria.where(fieldName).gte(fieldValue);
        }
        return null;
    }

    public static @Nullable Criteria lteCriteria(final @NotBlank String fieldName, final @Nullable Object fieldValue) {
        hasTextOf(fieldName, "fieldName");
        if (nonNull(fieldValue)) {
            return Criteria.where(fieldName).lte(fieldValue);
        }
        return null;
    }

    public static @Nullable Criteria modIdCriteria(final @Nullable Integer divisor, final @Nullable Integer remainder) {
        return modCriteria(DEFAULT_FIELD_ID, divisor, remainder);
    }

    public static @Nullable Criteria modCriteria(
            final @NotBlank String fieldName,
            final @Nullable Integer divisor,
            final @Nullable Integer remainder) {
        hasTextOf(fieldName, "fieldName");
        if (nonNull(divisor) && nonNull(remainder)) {
            return Criteria.where(fieldName).mod(divisor, remainder);
        }
        return null;
    }

    public static @Nullable <T> Criteria inCriteria(final @NotBlank String fieldName, final @Nullable List<T> fieldValues) {
        return inCriteria(fieldName, safeList(fieldValues).toArray());
    }

    public static @Nullable Criteria inCriteria(final @NotBlank String fieldName, final @Nullable Object... fieldValues) {
        hasTextOf(fieldName, "fieldName");
        // filter for null elements.
        final Object[] values = safeArrayToList(fieldValues).stream().filter(v -> nonNull(v)).toArray();
        if (values.length > 0) {
            return Criteria.where(fieldName).in(values);
        }
        return null;
    }

    public static Criteria andCriteria(Criteria... criteria) {
        return new Criteria().andOperator(safeArrayToList(criteria).stream().filter(c -> nonNull(c)).collect(toList()));
    }

    public static Criteria orCriteria(Criteria... criteria) {
        return new Criteria().orOperator(safeArrayToList(criteria).stream().filter(c -> nonNull(c)).collect(toList()));
    }

    public static Sort defaultSort() {
        return sort(Direction.DESC, "updateDate");
    }

    public static Sort descSort(final @NotBlank String... fieldNames) {
        return sort(Direction.DESC, fieldNames);
    }

    public static Sort sort(final @NotNull Direction direction, final @NotBlank String... fieldNames) {
        notNullOf(direction, "direction");
        notEmptyOf(fieldNames, "fieldNames");
        return Sort.by(direction, fieldNames);
    }

    public static UpdateDefinition logicalDelete() {
        return BasicUpdate.update(QueryHolder.DEFAULT_FIELD_DEL_FLAG, BaseBean.DEL_FLAG_DELETED);
    }

    public static final String DEFAULT_FIELD_ID = "_id";
    public static final String DEFAULT_FIELD_REVISION = "revision";
    public static final String DEFAULT_FIELD_UPDATE_DATE = "updateDate";
    public static final String DEFAULT_FIELD_DEL_FLAG = "delFlag";

}
