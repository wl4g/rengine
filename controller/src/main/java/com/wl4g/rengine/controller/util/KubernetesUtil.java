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
package com.wl4g.rengine.controller.util;

import static org.apache.commons.lang3.StringUtils.isBlank;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;
import lombok.CustomLog;

/**
 * {@link KubernetesUtil}
 * 
 * @author James Wong
 * @date 2023-04-13
 * @since v1.0.0
 * @see https://github.com/fabric8io/kubernetes-client/blob/v6.5.1/kubernetes-examples/src/main/java/io/fabric8/kubernetes/examples/CreatePod.java
 */
@CustomLog
public class KubernetesUtil {

    public static Pod createPod(Pod pod, String oauthToken, String namespace, String[] args) {
        Config config = new ConfigBuilder().withOauthToken(oauthToken).build();

        try (KubernetesClient client = new DefaultKubernetesClient(config)) {
            namespace = isBlank(namespace) ? client.getNamespace() : namespace;

            log.info("Creating pod in namespace {}", namespace);
            NonNamespaceOperation<Pod, PodList, PodResource<Pod>> podOps = client.pods().inNamespace(namespace);

            Pod result = podOps.create(pod);
            log.info("Created pod {}", result.getMetadata().getName());

            return result;
        }
    }

}
