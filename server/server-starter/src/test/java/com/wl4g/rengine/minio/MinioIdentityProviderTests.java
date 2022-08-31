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
package com.wl4g.rengine.minio;

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import io.minio.credentials.Credentials;
import io.minio.credentials.Jwt;
import io.minio.credentials.WebIdentityProvider;
import io.minio.http.HttpUtils;
import okhttp3.OkHttpClient;

/**
 * {@link MinioIdentityProviderTests}
 * 
 * @author James Wong
 * @version 2022-08-30
 * @since v3.0.0
 * @see More examples see to:
 *      {@link com.wl4g.infra.common.minio.MinioClientTests}
 */
public class MinioIdentityProviderTests {

    static OkHttpClient defaultHttpClient = HttpUtils.newDefaultHttpClient(15_000, 15_000, 15_000);

    static Supplier<Jwt> jwtSuppiler = () -> new Jwt("access_token1111", 7200);
    static String stsEndpoint = "http://localhost:9000";
    static String accessKey = "test_tenant1001_user1";
    static String secretKey = "12345678";
    static int durationSeconds = 5 * 60;
    static String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::test_tenant1001/*\"]}]}";
    static String region = "us-east-1";
    static String roleSessionName = "anySession";
    static String roleArn = "arn:aws:s3:::test_tenant1001/*";
    static String externalId = "rengineApp";

    // see:https://github.com/minio/minio/blob/8.4.3/docs/sts/web-identity.md
    // see:https://github.com/minio/minio/blob/master/docs/sts/keycloak.md
    @Test
    public void testGetSTSWithWebIdentity() {
        WebIdentityProvider provider = new WebIdentityProvider(jwtSuppiler, stsEndpoint, durationSeconds, policy, roleArn,
                roleSessionName, defaultHttpClient);

        Credentials credentials = provider.fetch();
        System.out.println(toJSONString(credentials));
    }

}
