# Rengine configuration for Client

## Quick start

### Example 1: order risk control evaluation

- The ways 1: [MyOrderServiceImpl.java](../../example/example-client-springboot/src/main/java/com/wl4g/rengine/example/client/service/impl/MyOrderServiceImpl.java)

```java
    @REvaluation(scenesCode = "${scenes_configs.createOrder}", bestEffort = true,
        paramsTemplate = "{{userId=#0.userId,goodId=#0.goodId,count=#1}}", failback = MyFailback.class)
    @Override
    public Map<String, String> create(CreateOrder order, Integer count) {
        log.info("Creating to order ...");
        // some order creating logical
        // ...
    }
```

- The ways 2:

```java
    private @Autowired RengineRiskHandler riskHandler;

    @Override
    public Map<String, String> create2(CreateOrder order, Integer count) {
        Map<String, String> args = new HashMap<>();
        args.put("userId", order.getUserId());
        args.put("goodId", order.getGoodId());
        args.put("count", valueOf(count));
        riskHandler.checkRiskFor(createOrderScenesCode, args);

        log.info("Creating2 to order ...");
        // some order creating logical
        // ...
    }
```

- [RengineRiskHandler.java](../../example/example-client-springboot/src/main/java/com/wl4g/rengine/example/client/risk/RengineRiskHandler.java)

```java
    private @Autowired RengineClient rengineClient;

    public void checkRiskFor(String scenesCode, Map<String, String> args) {
        log.info("Risk checking for : {} => {}", scenesCode, args);

        final var result = rengineClient.evaluate(scenesCode, true, args);
        log.info("Risk checked for result: {}, {} => {}", result, scenesCode, args);

        // Assertion risk evaluation result.
        if (result.getErrorCount() > 0) {
            throw new RengineException(format("Unable to operation, detected risk in your environment."));
        }
    }
```

- ***Notice:*** The params template expression to invoke the object get method is shallow, for example: `{{userId=#0.userId}}` is supported, `{{city=#0.address.city}}` is not supported.

## Configuration

- Refer to: [default-client.yaml.template](../../client/client-java/client-spring-boot-starter/src/main/resources/default-client.yaml.template)
