# Developer's case quide for underlying principles of complex workflow graph based on e-commerce recharge trade and send gift.

> The following is a real e-commerce/telephone recharge gift case, and its recharge rules are complex and changeable.

- The business flow as follows:

- ![workflow-theory-for-ecommerce-trade](../../shots/best-cases/workflow-theory-for-ecommerce-trade.png)

- The principle core codes for implementing the underlying execution graph is as follows (**pseudocode**):

```bash
    // Ignore some codes ...
    // ...

    // 1. Create workflow graph.
    WorkflowGraph workflow = new WorkflowGraph(1001010L, nodes, collections);

    // 2. Create execution parameter.
    ExecutionGraphParameter parameter = ExecutionGraphParameter.builder()
            .requestTime(currentTimeMillis())
            .traceId(UUID.randomUUID().toString())
            .trace(true)
            .scenesCode("s1234567890")
            .workflowId("wf1234567890")
            .args(singletonMap("deviceId", "12345678"))
            .build();

    // 3. Create execution context.
    ExecutionGraphContext context = new ExecutionGraphContext(parameter, ctx -> { // 模拟(PROCESS/RELATION/RUN)类型的node执行script,也只有这几种类型才需要执行script
            final String nodeId = ctx.getCurrentNode().getId(); // 当前执行script的节点ID
            final String nodeType = ((BaseOperator<?>) ctx.getCurrentNode()).getType(); // 当前执行script的节点Type
            out.println(format("current nodeId: %s@%s, lastResult : %s", nodeId, nodeType, toJSONString(ctx.getLastResult())));
            // 1. 在之后支持执行script的节点的规则代码中, 可使用 ctx.getLastResult() 获取前一个节点的返回值.
            // 2. 当前节点返回值会覆盖上一个节点的返回值.
            return new ExecutionGraphResult(ReturnState.TRUE, singletonMap("foo" + nodeId, "bar" + nodeId));
        });

    // 4. Invoking execution
    ExecutionGraph<?> graph = ExecutionGraph.from(workflow);
    ExecutionGraphResult result = graph.apply(context);

    // 5. Result
    out.println("-------------------------------------------------------");
    out.println("                           Final result : " + toJSONString(result));
    out.println("-------------------------------------------------------");
    out.println("Executed tracing info : \n" + context.asTraceText(true));
```

- see: [ExecutionGraphTests.java#testECommerceTradeWorkflow](../../common/src/test/java/com/wl4g/rengine/common/graph/ExecutionGraphTests.java)
