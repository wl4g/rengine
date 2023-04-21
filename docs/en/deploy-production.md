# Rengine for Deploy Production

> This article will introduce how to deploy Rengine to the production environment, where Rengine ApiServer, Controller, Executor, Jobs are deployed to the Kubernetes platform, and HBase is built using CDH6(including HBase and Kafka (or Flink) Cluster) refer to: [blogs.wl4g.com/archives/3368](https://blogs.wl4g.com/archives/3368)

## Deploy on Kubernetes

- [Deploy Rengine Stack (ApiServer, Executor, Controller, Job) to Kubernetes with helm.](../../tools/deploy/helm/rengine-stack/README.md)

- Deploy success

```bash
kubectl -n rengine get pods

NAME                                          READY   STATUS       RESTARTS   AGE
rengine-redis-cluster-0                       2/2     Running      0          5m19s
rengine-redis-cluster-1                       2/2     Running      0          5m19s
rengine-redis-cluster-2                       2/2     Running      0          5m19s
rengine-redis-cluster-3                       2/2     Running      0          5m19s
rengine-redis-cluster-4                       2/2     Running      0          5m19s
rengine-redis-cluster-5                       2/2     Running      0          5m19s
rengine-zookeeper-0                           1/1     Running      0          5m19s
rengine-zookeeper-1                           1/1     Running      0          5m19s
rengine-zookeeper-2                           1/1     Running      0          5m19s
rengine-mongodb-74bcdbd7b9-926v9              1/1     Running      0          5m19s
rengine-minio-566b978c86-x76qg                1/1     Running      0          5m19s
rengine-apiserver-baseline-5b6c9789d9-sxsf5   1/1     Running      0          5m19s
rengine-controller-baseline-78c568946-bhst6   1/1     Running      0          5m19s
rengine-executor-baseline-74f46464ff-qbtd9    1/1     Running      0          5m19s
rengine-ui-baseline-65d966dccc-j2sb8          1/1     Running      0          5m19s
rengine-init-minio                            1/1     Completed    0          5m19s
rengine-init-update-rootpassword              1/1     Completed    0          5m19s
```
