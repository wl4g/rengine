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

import static com.google.common.base.Charsets.UTF_8;
import static java.util.Objects.isNull;

import org.apache.commons.codec.binary.Base64;
import org.graalvm.polyglot.HostAccess;

/**
 * {@link Hashing}
 * 
 * @author James Wong
 * @date 2022-12-25
 * @since v1.0.0
 */
@SuppressWarnings("deprecation")
public class Hashing {
    public static final Hashing DEFAULT = new Hashing();

    private @HostAccess.Export Hashing() {
    }

    public @HostAccess.Export String md5(String str) {
        if (isNull(str)) {
            return null;
        }
        return Base64.encodeBase64String(com.google.common.hash.Hashing.md5().hashBytes(str.getBytes(UTF_8)).asBytes());
    }

    public @HostAccess.Export String sha1(String str) {
        if (isNull(str)) {
            return null;
        }
        return Base64.encodeBase64String(com.google.common.hash.Hashing.sha1().hashBytes(str.getBytes(UTF_8)).asBytes());
    }

    public @HostAccess.Export String sha256(String str) {
        if (isNull(str)) {
            return null;
        }
        return Base64.encodeBase64String(com.google.common.hash.Hashing.sha256().hashBytes(str.getBytes(UTF_8)).asBytes());
    }

    public @HostAccess.Export String sha384(String str) {
        if (isNull(str)) {
            return null;
        }
        return Base64.encodeBase64String(com.google.common.hash.Hashing.sha384().hashBytes(str.getBytes(UTF_8)).asBytes());
    }

    public @HostAccess.Export String sha512(String str) {
        if (isNull(str)) {
            return null;
        }
        return Base64.encodeBase64String(com.google.common.hash.Hashing.sha512().hashBytes(str.getBytes(UTF_8)).asBytes());
    }

    public @HostAccess.Export String hmacSha1(String key, String str) {
        if (isNull(str)) {
            return null;
        }
        return Base64.encodeBase64String(
                com.google.common.hash.Hashing.hmacSha1(key.getBytes(UTF_8)).hashBytes(str.getBytes(UTF_8)).asBytes());
    }

    public @HostAccess.Export String hmacSha256(String key, String str) {
        if (isNull(str)) {
            return null;
        }
        return Base64.encodeBase64String(
                com.google.common.hash.Hashing.hmacSha256(key.getBytes(UTF_8)).hashBytes(str.getBytes(UTF_8)).asBytes());
    }

    public @HostAccess.Export String hmacSha512(String key, String str) {
        if (isNull(str)) {
            return null;
        }
        return Base64.encodeBase64String(
                com.google.common.hash.Hashing.hmacSha512(key.getBytes(UTF_8)).hashBytes(str.getBytes(UTF_8)).asBytes());
    }

}
