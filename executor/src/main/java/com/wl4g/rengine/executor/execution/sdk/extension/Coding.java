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
package com.wl4g.rengine.executor.execution.sdk.extension;

import static com.google.common.base.Charsets.UTF_8;
import static java.util.Objects.isNull;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.graalvm.polyglot.HostAccess;

import com.wl4g.infra.common.codec.Base58;

/**
 * {@link Coding}
 * 
 * @author James Wong
 * @version 2022-12-25
 * @since v1.0.0
 */
public class Coding {

    public static @HostAccess.Export String toBase58(String str) {
        if (isNull(str)) {
            return null;
        }
        return Base58.encodeBase58(str);
    }

    public static @HostAccess.Export String fromBase58(String str) {
        if (isNull(str)) {
            return null;
        }
        return new String(Base58.decodeBase58(str), UTF_8);
    }

    public static @HostAccess.Export String toBase64(String str) {
        if (isNull(str)) {
            return null;
        }
        return Base64.encodeBase64String(str.getBytes(UTF_8));
    }

    public static @HostAccess.Export String fromBase64(String str) {
        if (isNull(str)) {
            return null;
        }
        return new String(Base64.decodeBase64(str.getBytes(UTF_8)), UTF_8);
    }

    public static @HostAccess.Export String toHex(String str) {
        if (isNull(str)) {
            return null;
        }
        return Hex.encodeHexString(str.getBytes(UTF_8));
    }

    public static @HostAccess.Export String fromHex(String str) throws DecoderException {
        if (isNull(str)) {
            return null;
        }
        return new String(Hex.decodeHex(str), UTF_8);
    }

}
