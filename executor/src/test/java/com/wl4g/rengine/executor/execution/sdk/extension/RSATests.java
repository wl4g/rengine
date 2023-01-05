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

import com.wl4g.rengine.executor.execution.sdk.tools.RSA;
import com.wl4g.rengine.executor.execution.sdk.tools.RSA.RsaKeyPair;

/**
 * {@link RSATests}
 * 
 * @author James Wong
 * @version 2022-12-26
 * @since v1.0.0
 */
public class RSATests {

    @Test
    public void testGenerateKeyAndPublicKeyEncryptAndPrivateDecrypt() {
        final RSA rsa = new RSA();

        final RsaKeyPair base64Key = rsa.generateKeyToBase64();
        System.out.println("base64Key: " + base64Key);

        final String plaintext = "abcdefghijklmnopqrstuvwxyz";
        System.out.println("plaintext: " + plaintext);

        final String ciphertext = rsa.encryptToBase64(false, base64Key.getPublicKey(), plaintext);
        final String plaintext2 = rsa.decryptFromBase64(true, base64Key.getPrivateKey(), ciphertext);

        System.out.println("ciphertext: " + ciphertext);
        System.out.println("plaintext2: " + plaintext2);

        assert eqIgnCase(plaintext, plaintext2);
    }

}
