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

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static java.util.Objects.isNull;

import javax.validation.constraints.NotBlank;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.infra.common.codec.CodecSource;
import com.wl4g.infra.common.crypto.symmetric.AES256CBCPKCS7;

/**
 * {@link AES}
 * 
 * @author James Wong
 * @version 2022-12-25
 * @since v1.0.0
 */
public class AES {

    public @HostAccess.Export AES() {
    }

    public @HostAccess.Export String encrypt256CbcPkcs7ToBase64(
            final @NotBlank String base64Key,
            final @NotBlank String base64Iv,
            final String plaintext) {
        hasTextOf(base64Key, "base64Key");
        if (isNull(plaintext)) {
            return null;
        }
        return new AES256CBCPKCS7()
                .encrypt(CodecSource.fromBase64(base64Key).getBytes(), CodecSource.fromBase64(base64Iv).getBytes(),
                        new CodecSource(plaintext))
                .toBase64();
    }

    public @HostAccess.Export String decrypt256CbcPkcs7FromBase64(
            final @NotBlank String base64Key,
            final @NotBlank String base64Iv,
            final String base64Ciphertext) {
        hasTextOf(base64Key, "base64Key");
        if (isNull(base64Ciphertext)) {
            return null;
        }
        return new AES256CBCPKCS7()
                .decrypt(CodecSource.fromBase64(base64Key).getBytes(), CodecSource.fromBase64(base64Iv).getBytes(),
                        CodecSource.fromBase64(base64Ciphertext))
                .toString();
    }

    public @HostAccess.Export String generateKeyToBase64() {
        return new AES256CBCPKCS7().generateKey().toBase64();
    }

}
