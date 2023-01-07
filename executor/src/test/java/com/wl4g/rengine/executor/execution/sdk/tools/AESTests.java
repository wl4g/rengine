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

    // --- AES/CBC ---

    @Test
    public void testCbcNoPaddingGenerateKeyAndEncryptAndDecrypt() {
        final AES aes = new AES();

        final String base64Iv = new CodecSource("1234567890abcdef").toBase64();
        System.out.println("base64Iv: " + base64Iv);

        final String base64Key = aes.generateKeyToBase64();
        System.out.println("base64Key: " + base64Key);

        // 由于使用 NoPadding 模式, 因此加密数据字节长度必须为 16 的倍数
        final String plaintext = "abcdefghijklmnopqrstuvwxyz123456";
        System.out.println("plaintext: " + plaintext);

        final String ciphertext = aes.encryptCbcNoPaddingToBase64(base64Key, base64Iv, plaintext);
        final String plaintext2 = aes.decryptCbcNoPaddingFromBase64(base64Key, base64Iv, ciphertext);

        System.out.println("ciphertext: " + ciphertext);
        System.out.println("plaintext2: " + plaintext2);

        assert eqIgnCase(plaintext, plaintext2);
    }

    @Test
    public void testCbcPkcs7GenerateKeyAndEncryptAndDecrypt() {
        final AES aes = new AES();

        final String base64Iv = new CodecSource("1234567890abcdef").toBase64();
        System.out.println("base64Iv: " + base64Iv);

        final String base64Key = aes.generateKeyToBase64();
        System.out.println("base64Key: " + base64Key);

        // 由于使用了 (PKCS7)Padding 模式, 因此加密数据字节长度不足时会自动填充为 16 的倍数
        final String plaintext = "abcdefghijklmnopqrstuvwxyz";
        System.out.println("plaintext: " + plaintext);

        final String ciphertext = aes.encryptCbcPkcs7ToBase64(base64Key, base64Iv, plaintext);
        final String plaintext2 = aes.decryptCbcPkcs7FromBase64(base64Key, base64Iv, ciphertext);

        System.out.println("ciphertext: " + ciphertext);
        System.out.println("plaintext2: " + plaintext2);

        assert eqIgnCase(plaintext, plaintext2);
    }

    // --- AES/ECB ---

    @Test
    public void testEcbNoPaddingGenerateKeyAndEncryptAndDecrypt() {
        final AES aes = new AES();

        final String base64Key = aes.generateKeyToBase64();
        System.out.println("base64Key: " + base64Key);

        // 由于使用 NoPadding 模式, 因此加密数据字节长度必须为 16 的倍数
        final String plaintext = "abcdefghijklmnopqrstuvwxyz123456";
        System.out.println("plaintext: " + plaintext);

        // ECB 模式不支持 IV
        final String ciphertext = aes.encryptEcbNoPaddingToBase64(base64Key, plaintext);
        final String plaintext2 = aes.decryptEcbNoPaddingFromBase64(base64Key, ciphertext);

        System.out.println("ciphertext: " + ciphertext);
        System.out.println("plaintext2: " + plaintext2);

        assert eqIgnCase(plaintext, plaintext2);
    }

    @Test
    public void testEcbPkcs7GenerateKeyAndEncryptAndDecrypt() {
        final AES aes = new AES();

        final String base64Key = aes.generateKeyToBase64();
        System.out.println("base64Key: " + base64Key);

        // 由于使用了 (PKCS7)Padding 模式, 因此加密数据字节长度不足时会自动填充为 16 的倍数
        final String plaintext = "abcdefghijklmnopqrstuvwxyz";
        System.out.println("plaintext: " + plaintext);

        // ECB 模式不支持 IV
        final String ciphertext = aes.encryptEcbPkcs7ToBase64(base64Key, plaintext);
        final String plaintext2 = aes.decryptEcbPkcs7FromBase64(base64Key, ciphertext);

        System.out.println("ciphertext: " + ciphertext);
        System.out.println("plaintext2: " + plaintext2);

        assert eqIgnCase(plaintext, plaintext2);
    }

}
