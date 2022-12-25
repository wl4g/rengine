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

import static com.wl4g.infra.common.lang.StringUtils2.eqIgnCase;

import org.junit.Test;

import com.wl4g.infra.common.codec.CodecSource;

/**
 * {@link AESTests}
 * 
 * @author James Wong
 * @version 2022-12-26
 * @since v1.0.0
 */
public class AESTests {

    @Test
    public void test256CbcPkcs7GenerateKeyAndEncryptAndDecrypt() {
        final String base64Iv = new CodecSource("1234567890abcdef").toBase64();
        System.out.println("base64Iv: " + base64Iv);

        final String base64Key = AES.generateKeyToBase64();
        System.out.println("base64Key: " + base64Key);

        final String plaintext = "abcdefghijklmnopqrstuvwxyz";
        System.out.println("plaintext: " + plaintext);

        final String ciphertext = AES.encrypt256CbcPkcs7ToBase64(base64Key, base64Iv, plaintext);
        final String plaintext2 = AES.decrypt256CbcPkcs7FromBase64(base64Key, base64Iv, ciphertext);

        System.out.println("ciphertext: " + ciphertext);
        System.out.println("plaintext2: " + plaintext2);

        assert eqIgnCase(plaintext, plaintext2);
    }

}
