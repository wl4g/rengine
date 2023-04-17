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
import static java.util.Objects.isNull;

import java.security.spec.KeySpec;

import javax.validation.constraints.NotBlank;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.infra.common.codec.CodecSource;
import com.wl4g.infra.common.crypto.asymmetric.RSACryptor;
import com.wl4g.infra.common.crypto.asymmetric.spec.RSAKeyPairSpec;

/**
 * {@link RSA}
 * 
 * @author James Wong
 * @date 2022-12-25
 * @since v1.0.0
 */
public class RSA {
    public static final RSA DEFAULT = new RSA();

    private RSA() {
    }

    public @HostAccess.Export String encryptToBase64(
            final boolean isPrivateKey,
            final @NotBlank String publicOrPrivateBase64Key,
            final String plaintext) {
        hasTextOf(publicOrPrivateBase64Key, "publicOrPrivateBase64Key");
        if (isNull(plaintext)) {
            return null;
        }
        final RSACryptor rsa = new RSACryptor();
        KeySpec key = null;
        if (isPrivateKey) {
            key = rsa.generateKeySpec(CodecSource.fromBase64(publicOrPrivateBase64Key).getBytes());
        } else {
            key = rsa.generatePubKeySpec(CodecSource.fromBase64(publicOrPrivateBase64Key).getBytes());
        }
        return rsa.encrypt(key, new CodecSource(plaintext)).toBase64();
    }

    public @HostAccess.Export String decryptFromBase64(
            final boolean isPrivateKey,
            final @NotBlank String base64Key,
            String base64Ciphertext) {
        hasTextOf(base64Key, "base64PublicKey");
        if (isNull(base64Ciphertext)) {
            return null;
        }
        final RSACryptor rsa = new RSACryptor();
        KeySpec key = null;
        if (isPrivateKey) {
            key = rsa.generateKeySpec(CodecSource.fromBase64(base64Key).getBytes());
        } else {
            key = rsa.generatePubKeySpec(CodecSource.fromBase64(base64Key).getBytes());
        }
        return rsa.decrypt(key, CodecSource.fromBase64(base64Ciphertext)).toString();
    }

    public @HostAccess.Export RsaKeyPair generateKeyToBase64() {
        final RSAKeyPairSpec keyPair = (RSAKeyPairSpec) new RSACryptor().generateKeyPair();
        return new RsaKeyPair(keyPair.getPubBase64String(), keyPair.getBase64String());
    }

    public static class RsaKeyPair {
        private final String publicKey;
        private final String privateKey;

        public RsaKeyPair(String publicKey, String privateKey) {
            this.publicKey = hasTextOf(publicKey, "publicKey");
            this.privateKey = hasTextOf(privateKey, "privateKey");
        }

        public @HostAccess.Export String getPublicKey() {
            return publicKey;
        }

        public @HostAccess.Export String getPrivateKey() {
            return privateKey;
        }

        @Override
        public @HostAccess.Export String toString() {
            return "RsaKeyPair [publicKey=" + publicKey + ", privateKey=" + privateKey + "]";
        }

    }

}
