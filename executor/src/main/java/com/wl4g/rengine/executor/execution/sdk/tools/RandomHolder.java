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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.graalvm.polyglot.HostAccess;

/**
 * {@link RandomHolder}
 * 
 * @author James Wong
 * @version 2023-01-29
 * @since v1.0.0
 */
public class RandomHolder {
    public static final RandomHolder DEFAULT = new RandomHolder();

    private RandomHolder() {
    }

    // --------- Random for Number ---------.

    public @HostAccess.Export boolean nextBoolean() {
        return RandomUtils.nextBoolean();
    }

    public @HostAccess.Export int nextInt(final int startInclusive, final int endExclusive) {
        return RandomUtils.nextInt(startInclusive, endExclusive);
    }

    public @HostAccess.Export int nextInt() {
        return RandomUtils.nextInt();
    }

    public @HostAccess.Export long nextLong(final long startInclusive, final long endExclusive) {
        return RandomUtils.nextLong(startInclusive, endExclusive);
    }

    public @HostAccess.Export long nextLong() {
        return RandomUtils.nextLong();
    }

    public @HostAccess.Export double nextDouble(final double startInclusive, final double endExclusive) {
        return RandomUtils.nextDouble(startInclusive, endExclusive);
    }

    public @HostAccess.Export double nextDouble() {
        return RandomUtils.nextDouble();
    }

    public @HostAccess.Export float nextFloat(final float startInclusive, final float endExclusive) {
        return RandomUtils.nextFloat(startInclusive, endExclusive);
    }

    public @HostAccess.Export float nextFloat() {
        return RandomUtils.nextFloat();
    }

    // --------- Random for String ---------.

    public @HostAccess.Export String random(final int count) {
        return RandomStringUtils.random(count);
    }

    public @HostAccess.Export String randomAscii(final int count) {
        return RandomStringUtils.randomAscii(count);
    }

    public @HostAccess.Export String randomAscii(final int minLengthInclusive, final int maxLengthExclusive) {
        return RandomStringUtils.randomAscii(minLengthInclusive, maxLengthExclusive);
    }

    public @HostAccess.Export String randomAlphabetic(final int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    public @HostAccess.Export String randomAlphabetic(final int minLengthInclusive, final int maxLengthExclusive) {
        return RandomStringUtils.randomAlphabetic(minLengthInclusive, maxLengthExclusive);
    }

    public @HostAccess.Export String randomAlphanumeric(final int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    public @HostAccess.Export String randomAlphanumeric(final int minLengthInclusive, final int maxLengthExclusive) {
        return RandomStringUtils.randomAlphabetic(minLengthInclusive, maxLengthExclusive);
    }

    public @HostAccess.Export String randomGraph(final int count) {
        return RandomStringUtils.randomGraph(count);
    }

    public @HostAccess.Export String randomGraph(final int minLengthInclusive, final int maxLengthExclusive) {
        return RandomStringUtils.randomGraph(minLengthInclusive, maxLengthExclusive);
    }

    public @HostAccess.Export String randomNumeric(final int count) {
        return RandomStringUtils.randomNumeric(count);
    }

    public @HostAccess.Export String randomNumeric(final int minLengthInclusive, final int maxLengthExclusive) {
        return RandomStringUtils.randomNumeric(minLengthInclusive, maxLengthExclusive);
    }

    public @HostAccess.Export String randomPrint(final int count) {
        return RandomStringUtils.randomPrint(count);
    }

    public @HostAccess.Export String randomPrint(final int minLengthInclusive, final int maxLengthExclusive) {
        return RandomStringUtils.randomPrint(minLengthInclusive, maxLengthExclusive);
    }

    public @HostAccess.Export String random(final int count, final boolean letters, final boolean numbers) {
        return RandomStringUtils.random(count, letters, numbers);
    }

    public @HostAccess.Export String random(
            final int count,
            final int start,
            final int end,
            final boolean letters,
            final boolean numbers) {
        return RandomStringUtils.random(count, start, end, letters, numbers);
    }

    public @HostAccess.Export String random(
            final int count,
            final int start,
            final int end,
            final boolean letters,
            final boolean numbers,
            final char... chars) {
        return RandomStringUtils.random(count, start, end, letters, numbers, chars);
    }

    public @HostAccess.Export String random(final int count, final String chars) {
        return RandomStringUtils.random(count, chars);
    }

    public @HostAccess.Export String random(final int count, final char... chars) {
        return RandomStringUtils.random(count, chars);
    }

}