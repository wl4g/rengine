# Rengine Roadmap for 2022 (rough plan)

Please refer the following sections for Rengine release plan of H1 2022, new release will be cut on monthly basis.

Please let us know if you have urgent needs which are not presented in the plan.

## 0.1.8

- Configurable scenes, workflows, rules, library, testset in management UI.

- Supported to Flink CEP(Real-time compute) or Flink SQL(Offline compute)

- Integrate Arthas (proxy/tunnel) with Rengine ApiServer for easy troubleshooting by sysadmins

- Support docker-compose one-click deployment demo environment (including all: `apiserver, executor, controller, mongo, redis, mysql, kafka, flink, hbase`)

- Improve the execution graph of the workflow graal.js engine (only for non-flink services)

- Complete client-core, client-eventbus support basic calls, such as the first best practice is to access iot-collector to clean and filter.

## 0.1.9

- Add built-in core metrics (prometheus) and write official metrics alert rules and grafana view rules.

- Write more rule examples for different scenarios, such as:  
  - a. login and registration risk control detection (for example, the number of login registrations with the same IP within 10m exceeds the limit),  
  - b. Iot collection and cleaning (set different invalid data filter conditions for different device models + installation information),  
  - c. Scrape prometheus metrics calculation alarm (n consecutive over-threshold within 10m), etc.

- Improve Rengine ApiServer configuration management, friendly to user configuration rules, support online simulation of test-data sets

## 1.0.0

- Supports one-click deployment to Kubernetes production clusters through helm, including `apiserver, executor, controller, `  
`mongo, redis, mysql, kafka, flink`, but excluding HBase (such as CDH/HDP deployment is recommended)

- Improve the butt tracking data to the jaeger dashboard, and fully display the call relationship diagram.

- Add the rengine-operator module to make it auto management to the cloud-native environment?

- Improve the Rengine Executor(quarkus native) runtime debugging toolchain and documentation, including memory snapshot dumps, real-time thread status dumps

