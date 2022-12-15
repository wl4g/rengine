function process(context) {
    // for case1:
    console.info("context:", context);
    console.info("context.id:", context.id);
    console.info("context.getId():", context.getId());
    console.info("context.getType():", context.getType());
    console.info("context.getArgs():", context.getArgs()[0]);
    console.info("context.getAttributes():", context.getAttributes());
    console.info("context.getAttributes()['objId']:", context.getAttributes()["objId"]);
    console.info("context.getEvent():", context.getEvent());
    console.info("context.getEvent().getType():", context.getEvent().getType());
    console.info("context.getEvent().getObservedTime():", context.getEvent().getObservedTime());
    console.info("context.getEvent().getBody():", context.getEvent().getBody());
    console.info("context.getEvent().getAttributes():", context.getEvent().getAttributes());
    console.info("context.getEvent().getSource():", context.getEvent().getSource());
    console.info("context.getEvent().getSource().getTime():", context.getEvent().getSource().getTime());
    console.info("context.getEvent().getSource().getPrincipals():", context.getEvent().getSource().getPrincipals());
    console.info("context.getEvent().getSource().getLocation():", context.getEvent().getSource().getLocation());
    console.info("context.getEvent().getSource().getLocation().getIpAddress():", context.getEvent().getSource().getLocation().getIpAddress());
    console.info("context.getEvent().getSource().getLocation().getZipcode():", context.getEvent().getSource().getLocation().getZipcode());

    // for case2:
    const response1 = new ScriptHttpClient().postForJson("http://httpbin.org/post", "");
    console.info("http response1:", response1);
    console.info("http response1('/headers'):", response1.at("/headers").toString());

    // for case3:
    const response2 = context.getDataService().getDefaultHttpClient().getForText("http://httpbin.org/get");
    console.info("http response2:", response2);
    console.info("http response2('/headers'):", response2.at("/headers"));

    // for case4:
    var mongoQuery = [
        { $match: { "eventType": "ecommerce_trade_gift" } },
        { $project: { "delFlag": 0 } }
    ];
    var mongoResult = context.getDataService().getMongoService("default").findList("aggregates", mongoQuery);
    console.info("mongo result: " + mongoResult);

    // for case5:
    var sql = "select * from user where user='root'";
    var jdbcResult = context.getDataService().getJDBCService("default").findList(sql, []);
    console.info("jdbc result: " + jdbcResult);

    // for case6:
    var redisService = context.getDataService().getRedisClusterService("default");
    redisService.set("key111", "value111");
    var redisResult = redisService.get("key111");
    console.info("redis cluster result: " + redisResult);

    return new ScriptResult(true)
                .addValue("response1", response1)
                .addValue("response2", response2)
                .addValue("mongoResult", mongoResult)
                .addValue("jdbcResult", jdbcResult)
                .addValue("redisResult", redisResult);
}
