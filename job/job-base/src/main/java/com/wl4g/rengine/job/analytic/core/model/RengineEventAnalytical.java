/*
 * Copyright 2017 ~ 2025 the original authors James Wong.
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
package com.wl4g.rengine.job.analytic.core.model;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import javax.validation.constraints.NotNull;

import com.wl4g.rengine.common.event.RengineEvent;

/**
 * {@link RengineEventAnalytical}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-06-08 v3.0.0
 * @since v1.0.0
 */
public class RengineEventAnalytical extends RengineEvent {
    private static final long serialVersionUID = 7396808707170188284L;

    public RengineEventAnalytical(@NotNull RengineEvent event) {
        super(((RengineEvent) notNullOf(event, "event")).getType(), event.getObservedTime(), (EventSource) event.getSource(),
                event.getBody(), event.getAttributes());
    }

    // /**
    // * Sorted field list.
    // */
    // public static final List<Field> ORDERED_FIELDS = unmodifiableList(
    // findAllDeclaredFields(RengineEventAnalytical.class,
    // true).stream().filter(f -> {
    // // Check if it is a valid field that needs to be persisted.
    // int m = f.getModifiers();
    // Class<?> t = f.getType();
    // return !isTransient(m) && !isStatic(m) && !isFinal(m) && !isAbstract(m)
    // && !isNative(m) && (t.isPrimitive()
    // || t.isEnum() || String.class.isAssignableFrom(t) ||
    // Number.class.isAssignableFrom(t));
    // }).map(f -> {
    // makeAccessible(f);
    // return f;
    // }).collect(toList()));
}
