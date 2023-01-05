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
package com.wl4g.rengine.executor.execution.sdk.tools;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.FastTimeClock.currentTimeMillis;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.infra.common.lang.DateUtils2;

/**
 * {@link DateHolder}
 * 
 * @author James Wong
 * @version 2022-12-25
 * @since v1.0.0
 */
public class DateHolder {
    private static final DateHolder DEFAULT = new DateHolder();

    public static @HostAccess.Export DateHolder getInstance() {
        return DEFAULT;
    }

    public @HostAccess.Export DateHolder() {
    }

    public @HostAccess.Export long currentMillis() {
        return currentTimeMillis();
    }

    public @HostAccess.Export long currentNanoTime() {
        return System.nanoTime();
    }

    public @HostAccess.Export Date getDate() {
        return new Date();
    }

    public @HostAccess.Export Date parseDate(final String dateString, final String pattern) throws ParseException {
        if (isBlank(dateString) || isNull(pattern)) {
            return null;
        }
        return DateUtils2.parseDate(dateString, pattern);
    }

    public @HostAccess.Export String formatDate(String pattern) {
        return formatDate(new Date(), pattern);
    }

    public @HostAccess.Export String formatDate(Date date, String pattern) {
        if (isNull(date) || isNull(pattern)) {
            return null;
        }
        return DateUtils2.formatDate(date, pattern);
    }

    public @HostAccess.Export Date getDateOf(int calendarField, int amount, String pattern) {
        if (isNull(pattern)) {
            return null;
        }
        hasTextOf(pattern, "pattern");
        final Calendar c = Calendar.getInstance();
        c.add(calendarField, amount);
        return c.getTime();
    }

    public @HostAccess.Export String getFormatDateOf(int calendarField, int amount, String pattern) {
        if (isNull(pattern)) {
            return null;
        }
        return DateUtils2.getDateOf(calendarField, amount, pattern);
    }

    public @HostAccess.Export Double getDistanceOf(Date before, Date after, String pattern) {
        if (isNull(before) || isNull(after) || isNull(pattern)) {
            return null;
        }
        return DateUtils2.getDistanceOf(before, after, pattern);
    }

}
