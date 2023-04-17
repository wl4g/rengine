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

import static java.lang.String.format;
import static java.lang.System.out;
import static org.apache.commons.lang3.StringUtils.replaceChars;

import java.util.Set;

import com.wl4g.infra.common.cli.CommandLineTool;
import com.wl4g.infra.common.cli.CommandLineTool.CommandLineFacade;
import com.wl4g.infra.common.resource.StreamResource;
import com.wl4g.infra.common.resource.resolver.ClassPathResourcePatternResolver;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
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
public final class RengineMinioUploadTool {

    private static final OkHttpClient defaultHttpClient = HttpUtils.newDefaultHttpClient(3000L, 60_000L, 60_000L);

    public static final String DEFAULT_ENDPOINT = "http://localhost:9000";
    public static final String DEFAULT_REGION = "us-east-1";
    public static final String DEFAULT_ACCESSKEY = "rengine";
    public static final String DEFAULT_SECRETKEY = "12345678";
    public static final String DEFAULT_BUCKET = "rengine";
    public static final String DEFAULT_LOCATION_PATTERNS = "classpath*:/example/rulescript/0/*/*.*";
    public static final String DEFAULT_FROM_LOCATION_PATH = "/example/rulescript/";
    public static final String DEFAULT_PREFIX = "libjs";

    public static void main(String[] args) throws Exception {
        try {
            CommandLineFacade line = CommandLineTool.builder()
                    .option("e", "endpoint", DEFAULT_ENDPOINT, "MinIO server endpoint.")
                    .option("r", "region", DEFAULT_REGION, "MinIO server region.")
                    .option("a", "accessKey", DEFAULT_ACCESSKEY, "access key.")
                    .option("s", "secretKey", DEFAULT_SECRETKEY, "secret key.")
                    .option("b", "bucket", DEFAULT_BUCKET, "bucket.")
                    .option("L", "locationPatterns", DEFAULT_LOCATION_PATTERNS, "upload location patterns.")
                    .option("p", "fromLocationPath", DEFAULT_FROM_LOCATION_PATH, "upload from location base path.")
                    .option("P", "prefix", DEFAULT_PREFIX, "upload target object prefix.")
                    .build(args);
            String endpoint = line.get("e");
            String region = line.get("r");
            String accessKey = line.get("a");
            String secretKey = line.get("s");
            String bucket = line.get("b");
            String locationPatterns = line.get("L");
            String fromLocationPath = line.get("p");
            String prefix = line.get("P");

            out.println("Using configuration arguments:");
            out.println("---------------------------------------");
            out.println("         endpoint: " + endpoint);
            out.println("           region: " + region);
            out.println("        accessKey: " + accessKey);
            out.println("        secretKey: " + secretKey);
            out.println("           bucket: " + bucket);
            out.println(" locationPatterns: " + locationPatterns);
            out.println(" fromLocationPath: " + fromLocationPath);
            out.println("           prefix: " + prefix);
            out.println("---------------------------------------");
            out.println("\nCall to MinIO Server ...\n");

            uploadDir(endpoint, region, accessKey, secretKey, bucket, locationPatterns, fromLocationPath, prefix);

        } finally {
            defaultHttpClient.dispatcher().executorService().shutdown();
        }
    }

    private static void uploadDir(
            String endpoint,
            String region,
            String accessKey,
            String secretKey,
            String bucket,
            String locationPatterns,
            String fromLocationPath,
            String prefix) throws Exception {

        MinioClient client = MinioClient.builder()
                .endpoint(endpoint)
                .region(region)
                .credentials(accessKey, secretKey)
                .httpClient(defaultHttpClient)
                .build();

        ClassPathResourcePatternResolver resolver = new ClassPathResourcePatternResolver();
        Set<StreamResource> resources = resolver.getResources(locationPatterns);

        for (StreamResource resource : resources) {
            String fromPath = resource.getURI().toString();
            out.println(format("Uploading from %s => ", resource, fromPath));
            String objectPath = prefix + "/" + fromPath.substring(fromPath.indexOf(fromLocationPath) + fromLocationPath.length());
            objectPath = replaceChars(objectPath, "//", "/");
            ObjectWriteResponse response = client.putObject(PutObjectArgs.builder()
                    .region(region)
                    .bucket(bucket)
                    .stream(resource.getInputStream(), resource.contentLength(), 5242880)
                    .object(objectPath)
                    .build());
            out.println(format("Uploaded etag: %s to %s from %s", response.etag(), objectPath, resource));
        }

        out.println(format("Successful uploads total of %s", resources.size()));
    }

}
