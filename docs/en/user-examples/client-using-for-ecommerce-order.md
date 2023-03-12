# In an e-commerce scenario, an example of client configuration for risk rule detection when creating an order.

- Testing codes: [MyOrderServiceImpl.java#create](../../example/example-client-springboot/src/main/java/com/wl4g/rengine/example/client/service/impl/MyOrderServiceImpl.java)  

- Method 1 (**pseudocode**):

```bash
    @REvaluation(scenesCode = "${scenes_configs.createOrder}", bestEffort = true,
            paramsTemplate = "{{userId=#0.userId,goodId=#0.goodId,count=#1}}", failback = MyFailback.class,
            assertSpel = "#{riskScore > 50}", assertErrmsg = "Denied to operation, detected risk in your environment.")
    @Override
    public Map<String, String> create(CreateOrder order, Integer count) {
        log.info("Creating to order ...");

        // some order creating logical
        // ...
    }
```

- Method 2 (**pseudocode**):

```bash
    @Autowired
    private RengineClient rengineClient;

    @Value("${scenes_configs.createOrder}")
    private String scenesCode;

    @Override
    public Map<String, String> create(CreateOrder order, Integer count) {
        Map<String, Object> args = new HashMap<>();
        args.put("userId", order.getUserId());
        args.put("goodId", order.getGoodId());
        args.put("count", valueOf(count));

        checkRiskFor(args);

        log.info("Creating to order ...");
        // some order creating logical
        // ...
    }


    void checkRiskFor(Map<String, Object> args) {
        WorkflowExecuteResult result = rengineClient.execute(singletonList(scenesCode), true, args);

        // Assertion result.
        if (((Number) result.firstValueMap().getOrDefault("riskScore", 0d)).doubleValue() > 50d) {
            throw new RengineException(format("Denied to operation, detected risk in your environment."));
        } else {
            log.debug("Check passed.");
        }
    }
}
```

- Mocks accessing

```bash
curl -v -XPOST \
-H 'Content-Type: application/json' \
-d '{
 "userId": "u100102015",
 "goodId": "g20230101291234885",
 "address": "asgasdgasdg"
}' localhost:28004/order/create?count=2
```
