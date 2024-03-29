# Introduction

This chart bootstraps an Rengine all stack deployment on a Kubernetes cluster using the Helm package.

## 1. Features

- One-step support for istio-based dual-version (`baseline`/`upgrade`) canary (grayscale) deploy, percentage traffic load supported.

- Automatically calculate the number of Pods replicas based on the traffic percentage.

- The based on istio's traffic governance capabilities, it supports the necessary functions of distributed microservices such as `canary routing` , `request limiting`, `circuit breaker`, `fault injection`, and `request response filtering` etc.

- Automatically add the response header `x-app-version`, the standard microservice interface is friendly to gray service diagnosis.

## 2. Prerequisites

+ Kubernetes 1.21+
+ Helm 3.9+
+ Istio 1.12+ (Optional and recommends)

## 3. Getting the Chart

+ helm charts global [values.yaml](./values.yaml)

+ From source

```bash
git clone https://github.com/wl4g/rengine.git
cd rengine/tools/deploy/helm/rengine-stack
```

+ Or from chart repos

```bash
helm repo add rengine https://registry.wl4g.io/repository/helm-release
```

> If you want to install an unstable version, you need to add `--devel` when you execute the `helm install` command.
> If you only want to test or simulate running, add the options `--dry-run --debug`

## 4. Initial deploy

+ Initial deploy with baseline only

```bash
helm -n rengine upgrade -i --create-namespace rengine tools/deploy/helm/rengine-stack --set="\
apiserver.image.baselineTag=1.0.0,\
controller.image.baselineTag=1.0.0,\
executor.image.baselineTag=1.0.0
```

## 5. Upgrade deploy with canary

+ 5.1 Inject istio labels for namespace

```bash
kubectl label ns rengine istio-injection=enabled --overwrite
```

+ 5.2 Upgrade deploy with canary. (weighted by traffic)

```bash
helm -n rengine upgrade -i --create-namespace rengine tools/deploy/helm/rengine-stack --set="\
apiserver.image.baselineTag=1.0.0,\
apiserver.image.upgradeTag=1.0.1,\
controller.image.baselineTag=1.0.0,\
controller.image.upgradeTag=1.0.1,\
executor.image.baselineTag=1.0.0,\
executor.image.upgradeTag=1.0.1,\
apiserver.governance.istio.ingress.http.canary.baseline.weight=80,\
apiserver.governance.istio.ingress.http.canary.upgrade.weight=20,\
controller.governance.istio.ingress.http.canary.baseline.weight=80,\
controller.governance.istio.ingress.http.canary.upgrade.weight=20,\
executor.governance.istio.ingress.http.canary.baseline.weight=80,\
executor.governance.istio.ingress.http.canary.upgrade.weight=20"
```

+ 5.3 After confirming that the upgrade is successful, use the new version as the benchmark, remove the old version, and switch all traffic to the new version

```bash
helm -n rengine upgrade --install --create-namespace rengine rengine-stack --set="\
apiserver.image.baselineTag=1.0.1,\
apiserver.image.upgradeTag=,\
controller.image.baselineTag=1.0.1,\
controller.image.upgradeTag=,\
executor.image.baselineTag=1.0.1,\
executor.image.upgradeTag=,\
apiserver.governance.istio.ingress.http.canary.baseline.weight=100,\
apiserver.governance.istio.ingress.http.canary.upgrade.weight=0,\
controller.governance.istio.ingress.http.canary.baseline.weight=100,\
controller.governance.istio.ingress.http.canary.upgrade.weight=0,\
executor.governance.istio.ingress.http.canary.baseline.weight=100,\
executor.governance.istio.ingress.http.canary.upgrade.weight=0"
```

## 6. Rebuild dependents

- ***Notice:*** The following dependent third-party component charts are generated based on generic templates.
In fact, Rengine's required dependencies are only a subset of them, which are enabled on demand, automatic deployment
of all third-party dependent components is disabled by default.

```bash
helm dependency build

helm dependency update

helm dependency list
NAME               VERSION     REPOSITORY                                  STATUS
apiserver          ~1.0.0      file://charts/apiserver                     ok
controller         ~1.0.0      file://charts/controller                    ok
executor           ~1.0.0      file://charts/executor                      ok
redis-cluster      ~17.0.x     https://charts.bitnami.com/bitnami          ok    
mongodb            ~12.1.27    https://charts.bitnami.com/bitnami          ok    
minio              ~11.7.13    https://charts.bitnami.com/bitnami          ok
kafka              ~18.0.3     https://charts.bitnami.com/bitnami          ok    
jaeger             ~0.57.1     https://jaegertracing.github.io/helm-charts ok    
jaeger-operator    ~2.33.0     https://jaegertracing.github.io/helm-charts ok    
...
```

## 7. Uninstalling the Chart

To uninstall/delete the `rengine` deployment:

```bash
helm -n rengine del rengine
```

## 8. Configurable

The following table lists the configurable parameters of the SpringBoot APP(Rengine) chart and their default values.

| Parameter  | Description | Default Value |
| ---        |  ---        | ---           |
| `<app>.enabled` | SpringBoot APP image name | true |
| `<app>.image.repository` | SpringBoot APP image name | `wl4g/&lt;app&gt;` |
| `<app>.image.baselineTag` | SpringBoot APP Image baseline tag name | `latest` |
| `<app>.image.upgradeTag` | SpringBoot APP Image upgrade tag name | `nil` |
| `<app>.image.pullPolicy`  | The image pull policy  | `IfNotPresent` |
| `<app>.image.pullSecrets`  | The image pull secrets  | `{hub-docker-secret, cr-aliyun-secret, ccr-tencentyun-secret, cr-nexus3-secret}` |
| `<app>.envFromSecret` | The name pull a secret in the same kubernetes namespace which contains values that will be added to the environment | nil |
| `<app>.autoscaling.enabled` | Autoscaling enabled status. | `true` |
| `<app>.autoscaling.replicaCount` | The total number of replicas for this application. (ie: baseline deployment pods + upgrade deployment pods) | 1 |
| `<app>.updateStrategy.type` | Pods update strategy. | `RollingUpdate` |
| `<app>.podAnnotations` | pod annotations | `{prometheus.io/scrape=true, prometheus.io/path=/actuator/prometheus, prometheus.io/port=10108}` |
| `<app>.persistence.enabled` | Enable APP persistence using PVC | `false` |
| `<app>.persistence.storageClass` | Storage class of backing PVC |`nil` (uses alpha storage class annotation)|
| `<app>.persistence.existingClaim` | SpringBoot APP data Persistent Volume existing claim name, evaluated as a template |""|
| `<app>.persistence.accessMode` | PVC Access Mode for APP volume | `ReadWriteOnce` |
| `<app>.persistence.size` | PVC Storage Request for APP volume | `20Mi` |
| `<app>.resources.enabled` | Enable resource requests/limits | `true` |
| `<app>.resources.requests.cpu` | CPU resource requests/limits | `100m` |
| `<app>.resources.requests.memory` | Memory resource requests/limits | `256Mi` |
| `<app>.resources.limits.cpu` | CPU resource requests/limits | `900m` |
| `<app>.resources.limits.memory` | Memory resource requests/limits | `1024Mi` |
| `<app>.initContainers` | Containers that run before the creation of APP containers. They can contain utilities or setup scripts. |`{}`|
| `<app>.podSecurityContext.enabled` | Pod security context enabled | `true` |
| `<app>.podSecurityContext.fsGroup` | Pod security fs group | `1000` |
| `<app>.podSecurityContext.fsGroupChangePolicy` | Enable pod security group policy | `Always` |
| `<app>.podSecurityContext.runAsUser` | Enable pod as uid |1000|
| `<app>.podSecurityContext.supplementalGroups` | Enable pod security supplemental groups | `[]` |
| `<app>.containerSecurityContext.enabled` | Enable container security context | `true` |
| `<app>.containerSecurityContext.runAsNonRoot` | Run container as root | `true` |
| `<app>.containerSecurityContext.runAsUser` | Run container as uid | `1000` |
| `<app>.nodeSelector` | Node labels for pod assignment |`{}`|
| `<app>.tolerations` | Toleration labels for pod assignment |`[]`|
| `<app>.affinity` | Map of node/pod affinities |`{}`|
| `<app>.envConfigs` | SpringBoot APP startup environments. | JAVA_OPTS="-Djava.awt.headless=true"</br>APP_ACTIVE="pro"</br>SPRING_SERVER_PORT="8080" |
| `<app>.agentConfig` | SpringBoot APP startup javaagent configuration.(Usually no configuration is required) |`{}`|
| `<app>.appConfigs.items`  | for example Rengine manager configurations. see to: [github.com/wl4g/rengine/tree/master/manager/src/main/resources/](https://github.com/wl4g/rengine/tree/master/manager/src/main/resources/)|`{}`|
| `<app>.service.provider`  | Kubernetes Service provider. | ClusterIP |
| `<app>.service.apiPortPort`  | Port for api. |18080|
| `<app>.service.prometheusPortPort`  | Port for prometheus. |10108|
| `<app>.service.nodePorts.api`  | Kubernetes node port for api. |  nil  |
| `<app>.service.nodePorts.prometheus`  | Kubernetes node port for prometheus. |  nil  |
| `<app>.service.loadBalancerIP`  | loadBalancerIP for Service |  nil |
| `<app>.service.loadBalancerSourceRanges` |  Address(es) that are allowed when service is LoadBalancer | [] |
| `<app>.service.externalIPs` |   ExternalIPs for the service | [] |
| `<app>.service.annotations` |   Service annotations | `{}` (evaluated as a template)|
| `<app>.governance.provider` | Service governance provider.(`Ingress`/`Istio`) | `Istio` |
| `<app>.governance.ingress.<name>.enabled` | Enable app governance with legacy ingress | false |
| `<app>.governance.ingress.<name>.ingressClassName` | Set the legacy ingress class for APP api |  nginx  |
| `<app>.governance.ingress.<name>.path` | Ingress app path |  / |
| `<app>.governance.ingress.<name>.customHosts` | Ingress app hosts | e.g: &lt;app&gt;.APP.svc.cluster.local |
| `<app>.governance.ingress.<name>.tls` | Ingress app tls | [] |
| `<app>.governance.ingress.<name>.annotations` | Ingress annotations for APP management | {} |
| `<app>.governance.istio.ingress.domain` | Istio ingress top domain | wl4g.io |
| `<app>.governance.istio.ingress.customHosts` | Istio ingress hosts | e.g: some-example.com |
| `<app>.governance.istio.ingress.http.primaryHttpServiceExposeName` | - | `api` |
| `<app>.governance.istio.ingress.http.canary.uriPrefix` | - | `/` |
| `<app>.governance.istio.ingress.http.canary.cookieRegex` | - | `^(.*?;)?(email=[^;]*@wl4g.io)(;.*)?$` |
| `<app>.governance.istio.ingress.http.canary.baseline.loadBalancer` | - | ROUND_ROBIN |
| `<app>.governance.istio.ingress.http.canary.baseline.weight` | - | 80 |
| `<app>.governance.istio.ingress.http.canary.upgrade.loadBalancer` | - | ROUND_ROBIN |
| `<app>.governance.istio.ingress.http.canary.upgrade.weight` | - | 20 |
| `<app>.governance.istio.ingress.http.scheme` | - | http |
| `<app>.governance.istio.ingress.http.tls.mode` | - | SIMPLE |
| `<app>.governance.istio.ingress.http.fault.delay.percentage.value` | - | 0.1 |
| `<app>.governance.istio.ingress.http.fault.delay.fixedDelay` | - | 5s |
| `<app>.governance.istio.ingress.http.fault.abort.percentage.value` | - | 0.1 |
| `<app>.governance.istio.ingress.http.fault.abort.fixedDelay` | - | 5s |
| `<app>.governance.istio.ingress.http.fault.abort.httpStatus` | - | 400 |
| `<app>.governance.istio.ingress.http.retries.attempts` | - | 5 |
| `<app>.governance.istio.ingress.http.retries.perTryTimeout` | - | 30s |
| `<app>.governance.istio.ingress.http.outlierDetection.consecutive5xxErrors` | - | 7 |
| `<app>.governance.istio.ingress.http.outlierDetection.interval` | - | 5m |
| `<app>.governance.istio.ingress.http.outlierDetection.baseEjectionTime` | - | 15m |
| `<app>.governance.istio.ingress.tcp.enabled` | Enable tcp istio ingress | `false` |
| `<app>.governance.istio.ingress.tcp.frontPort` | Enable tcp istio ingress | `1883` |
| `<app>.governance.istio.ingress.tcp.backendPort` | Enable tcp istio ingress | `1883` |
| `<app>.governance.istio.egress[].name` | External service name | `example-wx-payment` |
| `<app>.governance.istio.egress[].serviceAccount` | External service serviceAccount. | `nil` |
| `<app>.governance.istio.egress[].labels` | External service entries labels. | `classify=external-service, version=v1` |
| `<app>.governance.istio.egress[].instancePorts[].protocol` | External service entries instance ports protocol. | `TCP` |
| `<app>.governance.istio.egress[].instancePorts[].targetPort` | External service entries instance ports protocol. | `443` |
| `<app>.governance.istio.egress[].instanceAddresses[].ip` | External service entries instance IP. | `nil` |
| `<app>.governance.istio.egress[].instanceAddresses[].hosts[]` | External service entries instance hosts. | `[]` |
| `<app>.governance.istio.egress[].location` | Mesh egess service entries location. | `MESH_EXTERNAL` |
| `<app>.governance.istio.egress[].resolution` | Mesh egess service entries resolution. | `DNS` |
| --- (Optional) Global Dependents Components. --- | | |
| `global.commonConfigs.preStartScript` | Container pre-start hook scripts. | `nil` |
| `global.commonConfigs.envConfigs` | Container start environments. | `{}` |
| `global.commonConfigs.agentConfigs.mountPath` | Agent agent config mount path. | `/opt/apps/ecm/{app.name}/{app.name}-package/{app.name}-current/ext-lib/` |
| `global.commonConfigs.agentConfigs.items` | Agent agent configs. | `{}` |
| `global.commonConfigs.appConfigs.mountPath` | Application config mount path. | `/opt/apps/ecm/{app.name}/{app.name}-package/{app.name}-current/conf/` |
| `global.commonConfigs.appConfigs.items` | Application configs. | `{}` |
| `global.componentServices.otlp.internal.enabled` | Enable internal service. | `false` |
| `global.componentServices.otlp.external.enabled` | Enable external service. | `false` |
| `global.componentServices.otlp.external.namespace` | External service namespace. | `nil` |
| `global.componentServices.otlp.external.serviceAccount` | External service serviceAccount. | `nil` |
| `global.componentServices.otlp.external.labels` | External service entries labels. | `classify=external-service, version=v1` |
| `global.componentServices.otlp.external.instancePorts[].protocol` | External service entries instance ports protocol. | `TCP` |
| `global.componentServices.otlp.external.instancePorts[].targetPort` | External service entries instance ports protocol. | `4318` |
| `global.componentServices.otlp.external.instanceAddresses[].ip` | External service entries instance IP. | `nil` |
| `global.componentServices.otlp.external.instanceAddresses[].hosts[]` | External service entries instance hosts. | `[]` |
| `global.componentServices.otlp.external.location` | Mesh egess service entries location. | `MESH_EXTERNAL` |
| `global.componentServices.otlp.external.resolution` | Mesh egess service entries resolution. | `NONE` |
| `global.componentServices.jaeger.internal.enabled` | Enable internal service. | `false` |
| `global.componentServices.jaeger.external.enabled` | Enable external service. | `false` |
| `global.componentServices.jaeger.external.namespace` | External service namespace. | `nil` |
| `global.componentServices.jaeger.external.serviceAccount` | External service serviceAccount. | `nil` |
| `global.componentServices.jaeger.external.labels` | External service entries labels. | `classify=external-service, version=v1` |
| `global.componentServices.jaeger.external.instancePorts[].protocol` | External service entries instance ports protocol. | `TCP` |
| `global.componentServices.jaeger.external.instancePorts[].targetPort` | External service entries instance ports protocol. | `14268` |
| `global.componentServices.jaeger.external.instanceAddresses[].ip` | External service entries instance IP. | `nil` |
| `global.componentServices.jaeger.external.instanceAddresses[].hosts[]` | External service entries instance hosts. | `[]` |
| `global.componentServices.jaeger.external.location` | Mesh egess service entries location. | `MESH_EXTERNAL` |
| `global.componentServices.jaeger.external.resolution` | Mesh egess service entries resolution. | `NONE` |
| `global.componentServices.zookeeper.internal.enabled` | Enable internal service. | `false` |
| `global.componentServices.zookeeper.external.enabled` | Enable external service. | `false` |
| `global.componentServices.zookeeper.external.namespace` | External service namespace. | `nil` |
| `global.componentServices.zookeeper.external.serviceAccount` | External service serviceAccount. | `nil` |
| `global.componentServices.zookeeper.external.labels` | External service entries labels. | `classify=external-service, version=v1` |
| `global.componentServices.zookeeper.external.instancePorts[].protocol` | External service entries instance ports protocol. | `TCP` |
| `global.componentServices.zookeeper.external.instancePorts[].targetPort` | External service entries instance ports protocol. | `2181` |
| `global.componentServices.zookeeper.external.instanceAddresses[].ip` | External service entries instance IP. | `nil` |
| `global.componentServices.zookeeper.external.instanceAddresses[].hosts[]` | External service entries instance hosts. | `[]` |
| `global.componentServices.zookeeper.external.location` | Mesh egess service entries location. | `MESH_EXTERNAL` |
| `global.componentServices.zookeeper.external.resolution` | Mesh egess service entries resolution. | `NONE` |
| `global.componentServices.kafka.internal.enabled` | Enable internal service. | `false` |
| `global.componentServices.kafka.external.enabled` | Enable external service. | `false` |
| `global.componentServices.kafka.external.namespace` | External service namespace. | `nil` |
| `global.componentServices.kafka.external.serviceAccount` | External service serviceAccount. | `nil` |
| `global.componentServices.kafka.external.labels` | External service entries labels. | `classify=external-service, version=v1` |
| `global.componentServices.kafka.external.instancePorts[].protocol` | External service entries instance ports protocol. | `TCP` |
| `global.componentServices.kafka.external.instancePorts[].targetPort` | External service entries instance ports protocol. | `9092` |
| `global.componentServices.kafka.external.instanceAddresses[].ip` | External service entries instance IP. | `nil` |
| `global.componentServices.kafka.external.instanceAddresses[].hosts[]` | External service entries instance hosts. | `[]` |
| `global.componentServices.kafka.external.location` | Mesh egess service entries location. | `MESH_EXTERNAL` |
| `global.componentServices.kafka.external.resolution` | Mesh egess service entries resolution. | `NONE` |
| `global.componentServices.mysql.internal.enabled` | Enable internal service. | `false` |
| `global.componentServices.mysql.external.enabled` | Enable external service. | `false` |
| `global.componentServices.mysql.external.namespace` | External service namespace. | `nil` |
| `global.componentServices.mysql.external.serviceAccount` | External service serviceAccount. | `nil` |
| `global.componentServices.mysql.external.labels` | External service entries labels. | `classify=external-service, version=v1` |
| `global.componentServices.mysql.external.instancePorts[].protocol` | External service entries instance ports protocol. | `TCP` |
| `global.componentServices.mysql.external.instancePorts[].targetPort` | External service entries instance ports protocol. | `3306` |
| `global.componentServices.mysql.external.instanceAddresses[].ip` | External service entries instance IP. | `nil` |
| `global.componentServices.mysql.external.instanceAddresses[].hosts[]` | External service entries instance hosts. | `[]` |
| `global.componentServices.mysql.external.location` | Mesh egess service entries location. | `MESH_EXTERNAL` |
| `global.componentServices.mysql.external.resolution` | Mesh egess service entries resolution. | `NONE` |
| `global.componentServices.redis.internal.enabled` | Enable internal service. | `false` |
| `global.componentServices.redis.external.enabled` | Enable external service. | `false` |
| `global.componentServices.redis.external.namespace` | External service namespace. | `nil` |
| `global.componentServices.redis.external.serviceAccount` | External service serviceAccount. | `nil` |
| `global.componentServices.redis.external.labels` | External service entries labels. | `classify=external-service, version=v1` |
| `global.componentServices.redis.external.instancePorts[].protocol` | External service entries instance ports protocol. | `TCP` |
| `global.componentServices.redis.external.instancePorts[].targetPort` | External service entries instance ports protocol. | `6379` |
| `global.componentServices.redis.external.instanceAddresses[].ip` | External service entries instance IP. | `nil` |
| `global.componentServices.redis.external.instanceAddresses[].hosts[]` | External service entries instance hosts. | `[]` |
| `global.componentServices.redis.external.location` | Mesh egess service entries location. | `MESH_EXTERNAL` |
| `global.componentServices.redis.external.resolution` | Mesh egess service entries resolution. | `NONE` |
| `global.componentServices.minio.internal.enabled` | Enable internal service. | `false` |
| `global.componentServices.minio.external.enabled` | Enable external service. | `false` |
| `global.componentServices.minio.external.namespace` | External service namespace. | `nil` |
| `global.componentServices.minio.external.serviceAccount` | External service serviceAccount. | `nil` |
| `global.componentServices.minio.external.labels` | External service entries labels. | `classify=external-service, version=v1` |
| `global.componentServices.minio.external.instancePorts[].protocol` | External service entries instance ports protocol. | `TCP` |
| `global.componentServices.minio.external.instancePorts[].targetPort` | External service entries instance ports protocol. | `9000,9090` |
| `global.componentServices.minio.external.instanceAddresses[].ip` | External service entries instance IP. | `nil` |
| `global.componentServices.minio.external.instanceAddresses[].hosts[]` | External service entries instance hosts. | `[]` |
| `global.componentServices.minio.external.location` | Mesh egess service entries location. | `MESH_EXTERNAL` |
| `global.componentServices.minio.external.resolution` | Mesh egess service entries resolution. | `NONE` |
| `global.componentServices.mongodb.internal.enabled` | Enable internal service. | `false` |
| `global.componentServices.mongodb.external.enabled` | Enable external service. | `false` |
| `global.componentServices.mongodb.external.namespace` | External service namespace. | `nil` |
| `global.componentServices.mongodb.external.serviceAccount` | External service serviceAccount. | `nil` |
| `global.componentServices.mongodb.external.labels` | External service entries labels. | `classify=external-service, version=v1` |
| `global.componentServices.mongodb.external.instancePorts[].protocol` | External service entries instance ports protocol. | `TCP` |
| `global.componentServices.mongodb.external.instancePorts[].targetPort` | External service entries instance ports protocol. | `27017` |
| `global.componentServices.mongodb.external.instanceAddresses[].ip` | External service entries instance IP. | `nil` |
| `global.componentServices.mongodb.external.instanceAddresses[].hosts[]` | External service entries instance hosts. | `[]` |
| `global.componentServices.mongodb.external.location` | Mesh egess service entries location. | `MESH_EXTERNAL` |
| `global.componentServices.mongodb.external.resolution` | Mesh egess service entries resolution. | `NONE` |
| `global.componentServices.hmaster.internal.enabled` | Enable internal service. | `false` |
| `global.componentServices.hmaster.external.enabled` | Enable external service. | `false` |
| `global.componentServices.hmaster.external.namespace` | External service namespace. | `nil` |
| `global.componentServices.hmaster.external.serviceAccount` | External service serviceAccount. | `nil` |
| `global.componentServices.hmaster.external.labels` | External service entries labels. | `classify=external-service, version=v1` |
| `global.componentServices.hmaster.external.instancePorts[].protocol` | External service entries instance ports protocol. | `TCP` |
| `global.componentServices.hmaster.external.instancePorts[].targetPort` | External service entries instance ports protocol. | `16000,16010` |
| `global.componentServices.hmaster.external.instanceAddresses[].ip` | External service entries instance IP. | `nil` |
| `global.componentServices.hmaster.external.instanceAddresses[].hosts[]` | External service entries instance hosts. | `[]` |
| `global.componentServices.hmaster.external.location` | Mesh egess service entries location. | `MESH_EXTERNAL` |
| `global.componentServices.hmaster.external.resolution` | Mesh egess service entries resolution. | `NONE` |
| `global.componentServices.hregionserver.internal.enabled` | Enable internal service. | `false` |
| `global.componentServices.hregionserver.external.enabled` | Enable external service. | `false` |
| `global.componentServices.hregionserver.external.namespace` | External service namespace. | `nil` |
| `global.componentServices.hregionserver.external.serviceAccount` | External service serviceAccount. | `nil` |
| `global.componentServices.hregionserver.external.labels` | External service entries labels. | `classify=external-service, version=v1` |
| `global.componentServices.hregionserver.external.instancePorts[].protocol` | External service entries instance ports protocol. | `TCP` |
| `global.componentServices.hregionserver.external.instancePorts[].targetPort` | External service entries instance ports protocol. | `16020,16030` |
| `global.componentServices.hregionserver.external.instanceAddresses[].ip` | External service entries instance IP. | `nil` |
| `global.componentServices.hregionserver.external.instanceAddresses[].hosts[]` | External service entries instance hosts. | `[]` |
| `global.componentServices.hregionserver.external.location` | Mesh egess service entries location. | `MESH_EXTERNAL` |
| `global.componentServices.hregionserver.external.resolution` | Mesh egess service entries resolution. | `NONE` |
| `global.componentServices.namenode.internal.enabled` | Enable internal service. | `false` |
| `global.componentServices.namenode.external.enabled` | Enable external service. | `false` |
| `global.componentServices.namenode.external.namespace` | External service namespace. | `nil` |
| `global.componentServices.namenode.external.serviceAccount` | External service serviceAccount. | `nil` |
| `global.componentServices.namenode.external.labels` | External service entries labels. | `classify=external-service, version=v1` |
| `global.componentServices.namenode.external.instancePorts[].protocol` | External service entries instance ports protocol. | `TCP` |
| `global.componentServices.namenode.external.instancePorts[].targetPort` | External service entries instance ports protocol. | `9870,8020,8088` |
| `global.componentServices.namenode.external.instanceAddresses[].ip` | External service entries instance IP. | `nil` |
| `global.componentServices.namenode.external.instanceAddresses[].hosts[]` | External service entries instance hosts. | `[]` |
| `global.componentServices.namenode.external.location` | Mesh egess service entries location. | `MESH_EXTERNAL` |
| `global.componentServices.namenode.external.resolution` | Mesh egess service entries resolution. | `NONE` |
| `global.componentServices.nodemanager.internal.enabled` | Enable internal service. | `false` |
| `global.componentServices.nodemanager.external.enabled` | Enable external service. | `false` |
| `global.componentServices.nodemanager.external.namespace` | External service namespace. | `nil` |
| `global.componentServices.nodemanager.external.serviceAccount` | External service serviceAccount. | `nil` |
| `global.componentServices.nodemanager.external.labels` | External service entries labels. | `classify=external-service, version=v1` |
| `global.componentServices.nodemanager.external.instancePorts[].protocol` | External service entries instance ports protocol. | `TCP` |
| `global.componentServices.nodemanager.external.instancePorts[].targetPort` | External service entries instance ports protocol. | `8040,8041,7337,8042,13562` |
| `global.componentServices.nodemanager.external.instanceAddresses[].ip` | External service entries instance IP. | `nil` |
| `global.componentServices.nodemanager.external.instanceAddresses[].hosts[]` | External service entries instance hosts. | `[]` |
| `global.componentServices.nodemanager.external.location` | Mesh egess service entries location. | `MESH_EXTERNAL` |
| `global.componentServices.nodemanager.external.resolution` | Mesh egess service entries resolution. | `NONE` |
| `global.componentServices.datanode.internal.enabled` | Enable internal service. | `false` |
| `global.componentServices.datanode.external.enabled` | Enable external service. | `false` |
| `global.componentServices.datanode.external.namespace` | External service namespace. | `nil` |
| `global.componentServices.datanode.external.serviceAccount` | External service serviceAccount. | `nil` |
| `global.componentServices.datanode.external.labels` | External service entries labels. | `classify=external-service, version=v1` |
| `global.componentServices.datanode.external.instancePorts[].protocol` | External service entries instance ports protocol. | `TCP` |
| `global.componentServices.datanode.external.instancePorts[].targetPort` | External service entries instance ports protocol. | `9867,9864,9866,32828` |
| `global.componentServices.datanode.external.instanceAddresses[].ip` | External service entries instance IP. | `nil` |
| `global.componentServices.datanode.external.instanceAddresses[].hosts[]` | External service entries instance hosts. | `[]` |
| `global.componentServices.datanode.external.location` | Mesh egess service entries location. | `MESH_EXTERNAL` |
| `global.componentServices.datanode.external.resolution` | Mesh egess service entries resolution. | `NONE` |

## 9. FAQ

### How to troubleshoot Pods that are missing os tools

- Use ephemeral containers to debug running or crashed Pods: [kubernetes.io/docs/tasks/debug-application-cluster/debug-running-pod](https://kubernetes.io/docs/tasks/debug-application-cluster/debug-running-pod/)

- Parent charts override the property values of child charts see:
[github.com/whmzsu/helm-doc-zh-cn/blob/master/chart_template_guide/subcharts_and_globals-zh_cn.md](https://github.com/whmzsu/helm-doc-zh-cn/blob/master/chart_template_guide/subcharts_and_globals-zh_cn.md)
