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
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.service.minio;

import static com.wl4g.infra.common.minio.S3Policy.Action.CreateBucketAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.GetBucketLocationAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.GetBucketPolicyStatusAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.GetObjectAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.GetObjectLegalHoldAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.ListAllMyBucketsAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.ListBucketAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.ListBucketMultipartUploadsAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.ListMultipartUploadPartsAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.PutObjectAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.PutObjectLegalHoldAction;
import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.bouncycastle.crypto.InvalidCipherTextException;

import com.wl4g.infra.common.cli.CommandLineTool;
import com.wl4g.infra.common.cli.CommandLineTool.CommandLineFacade;
import com.wl4g.infra.common.minio.S3Policy;
import com.wl4g.infra.common.minio.S3Policy.EffectType;
import com.wl4g.infra.common.minio.S3Policy.Statement;
import com.wl4g.infra.common.minio.v8_4.MinioAdminClient;
import com.wl4g.infra.common.minio.v8_4.UserInfo.Status;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.http.HttpUtils;
import okhttp3.OkHttpClient;

/**
 * {@link MinioAdminTool}
 * 
 * @author James Wong
 * @version 2022-09-16
 * @since Copy of
 *        https://github.com/wl4g/infra/blob/master/common/src/test/java/com/wl4g/infra/common/minio/MinioAdminTool.java
 */
public final class RengineMinioPolicyTool {

    private static final OkHttpClient defaultHttpClient = HttpUtils.newDefaultHttpClient(3000L, 60_000L, 60_000L);

    public static final String DEFAULT_ENDPOINT = "http://localhost:9000";
    public static final String DEFAULT_REGION = "us-east-1";

    public static final String DEFAULT_ADMIN_ACCESSKEY = "minioadmin";
    public static final String DEFAULT_ADMIN_SECRETKEY = "minioadmin";

    public static final String DEFAULT_ACCESSKEY = "rengine";
    public static final String DEFAULT_SECRETKEY = "12345678";
    public static final String DEFAULT_BUCKET = DEFAULT_ACCESSKEY;

    public static final String DEFAULT_POLICY_NAME = DEFAULT_ACCESSKEY + "_readwrite";
    public static final List<S3Policy.Action> DEFAULT_POLICY_ACTIONS = asList(CreateBucketAction, GetBucketLocationAction,
            GetBucketPolicyStatusAction, ListBucketAction, ListAllMyBucketsAction, ListBucketMultipartUploadsAction,
            ListMultipartUploadPartsAction, PutObjectAction, PutObjectLegalHoldAction, GetObjectAction, GetObjectLegalHoldAction);

    public static void main(String[] args) throws Exception {
        try {
            CommandLineFacade line = CommandLineTool.builder()
                    .option("e", "endpoint", DEFAULT_ENDPOINT, "MinIO server endpoint.")
                    .option("r", "region", DEFAULT_REGION, "MinIO server region.")
                    .option("A", "adminAccessKey", DEFAULT_ADMIN_ACCESSKEY, "Admin access key.")
                    .option("S", "adminSecretKey", DEFAULT_ADMIN_SECRETKEY, "Admin secret key.")
                    .option("a", "accessKey", DEFAULT_ACCESSKEY, "Tenant access key.")
                    .option("s", "secretKey", DEFAULT_SECRETKEY, "Tenant secret key.")
                    .option("b", "tenantBucket", DEFAULT_BUCKET, "Tenant bucket.")
                    .option("n", "policyName", DEFAULT_POLICY_NAME, "Tenant policy name.")
                    .option("j", "policyJson", null,
                            format("Tenant policy json. Defaults to access to <tenant bucket> only actions of : %s.",
                                    DEFAULT_POLICY_ACTIONS))
                    .build(args);
            String endpoint = line.get("e");
            String region = line.get("r");
            String adminAccessKey = line.get("A");
            String adminSecretKey = line.get("S");
            String accessKey = line.get("a");
            String secretKey = line.get("s");
            String bucket = line.get("b");
            String policyName = line.get("n");
            String policyJson = line.get("j");

            // Use default policy
            if (isBlank(policyJson)) {
                policyJson = S3Policy.builder()
                        .version(S3Policy.DEFAULT_POLICY_VERSION)
                        .statement(singletonList(Statement.builder()
                                .effect(EffectType.Allow)
                                // see:https://docs.aws.amazon.com/dict-authorization/latest/reference/list_amazons3.html#amazons3-actions-as-permissions
                                .action(asList(CreateBucketAction, GetBucketLocationAction, GetBucketPolicyStatusAction,
                                        ListBucketAction, ListAllMyBucketsAction, ListBucketMultipartUploadsAction,
                                        ListMultipartUploadPartsAction, PutObjectAction, PutObjectLegalHoldAction,
                                        GetObjectAction, GetObjectLegalHoldAction))
                                .resource(singletonList("arn:aws:s3:::" + bucket + "/*"))
                                .build()))
                        .build()
                        .toString();
            }

            out.println("Using configuration arguments:");
            out.println("---------------------------------------");
            out.println("         endpoint: " + endpoint);
            out.println("           region: " + region);
            out.println("   adminAccessKey: " + adminAccessKey);
            out.println("   adminSecretKey: " + adminSecretKey);
            out.println("  accessKey: " + accessKey);
            out.println("  secretKey: " + secretKey);
            out.println("     tenantBucket: " + bucket);
            out.println("       policyName: " + policyName);
            out.println("       policyJson: " + policyJson);
            out.println("---------------------------------------");
            out.println("\nCall to MinIO Server ...\n");

            createTenantPolicy(endpoint, region, adminAccessKey, adminSecretKey, policyName, policyJson);
            createTenantUser(endpoint, region, adminAccessKey, adminSecretKey, accessKey, secretKey, policyName);
            assignTenantPolicyToUser(endpoint, region, adminAccessKey, adminSecretKey, accessKey, secretKey, policyName);
            createTenantBucket(endpoint, region, accessKey, secretKey, bucket);

        } finally {
            defaultHttpClient.dispatcher().executorService().shutdown();
        }
    }

    private static void createTenantPolicy(
            String endpoint,
            String region,
            String adminAccessKey,
            String adminSecretKey,
            String tenantPolicyName,
            String tenantPolicyJson)
            throws InvalidKeyException, NoSuchAlgorithmException, InvalidCipherTextException, IOException {
        MinioAdminClient adminCilent = MinioAdminClient.builder()
                .endpoint(endpoint)
                .region(region)
                .credentials(adminAccessKey, adminSecretKey)
                .httpClient(defaultHttpClient)
                .build();
        adminCilent.addCannedPolicy(tenantPolicyName, tenantPolicyJson);
        out.println(" Created Policy: " + tenantPolicyName + " => " + tenantPolicyJson);

    }

    private static void createTenantUser(
            String endpoint,
            String region,
            String adminAccessKey,
            String adminSecretKey,
            String accessKey,
            String secretKey,
            String tenantPolicyName)
            throws InvalidKeyException, NoSuchAlgorithmException, InvalidCipherTextException, IOException {
        MinioAdminClient adminCilent = MinioAdminClient.builder()
                .endpoint(endpoint)
                .region(region)
                .credentials(adminAccessKey, adminSecretKey)
                .httpClient(defaultHttpClient)
                .build();
        // Won't automatically assign policies? ? ?
        adminCilent.addUser(accessKey, Status.ENABLED, secretKey, tenantPolicyName, emptyList());
        out.println("   Created User: " + accessKey);
    }

    private static void assignTenantPolicyToUser(
            String endpoint,
            String region,
            String adminAccessKey,
            String adminSecretKey,
            String accessKey,
            String secretKey,
            String tenantPolicyName)
            throws InvalidKeyException, NoSuchAlgorithmException, InvalidCipherTextException, IOException {
        MinioAdminClient adminCilent = MinioAdminClient.builder()
                .endpoint(endpoint)
                .region(region)
                .credentials(adminAccessKey, adminSecretKey)
                .httpClient(defaultHttpClient)
                .build();
        adminCilent.setPolicy(accessKey, false, tenantPolicyName);
        out.println("Assigned Policy: " + tenantPolicyName + " => " + accessKey);
    }

    private static void createTenantBucket(
            String endpoint,
            String region,
            String accessKey,
            String secretKey,
            String tenantBucket) throws Exception {

        MinioClient client = MinioClient.builder()
                .endpoint(endpoint)
                .region(region)
                .credentials(accessKey, secretKey)
                .httpClient(defaultHttpClient)
                .build();

        if (!client.bucketExists(BucketExistsArgs.builder().bucket(tenantBucket).build())) {
            client.makeBucket(MakeBucketArgs.builder().bucket(tenantBucket).build());
            out.println(" Created Bucket: " + tenantBucket);
        } else {
            out.println(" Already Bucket: " + tenantBucket);
        }
    }

}
