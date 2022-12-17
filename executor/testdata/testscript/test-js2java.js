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
    const httpResult1 = testForHttpRequest1(context);

    // for case3:
    const httpResult2 = testForHttpRequest2(context);

    // for case4:
    const mongoResult = testForMongoQuery(context);

    // for case5:
    const redisResult = testForRedisOperation(context);

    // for case6:
    const jdbcResult = testForJdbcSql(context);

    // for case7:
    const sshResult = testForSshExec(context);

    // for case8:
    const kafkaResult = testForKafkaPublish(context);

    return new ScriptResult(true)
        .addValue("httpResult1", httpResult1)
        .addValue("httpResult2", httpResult2)
        .addValue("mongoResult", mongoResult)
        .addValue("redisResult", redisResult)
        .addValue("jdbcResult", jdbcResult)
        .addValue("sshResult", sshResult)
        .addValue("kafkaResult", kafkaResult);
}

function testForHttpRequest1(context) {
    try {
        const httpResult1 = new ScriptHttpClient().postForJson("http://httpbin.org/post", "");
        console.info("httpResult1:", httpResult1);
        console.info("httpResult1('/headers'):", httpResult1.at("/headers").toString());
        return httpResult1;
    } catch(e) {
        console.error(">>>", e);
    }
}

function testForHttpRequest2(context) {
    try {
        const httpResult2 = context.getDataService().getDefaultHttpClient().getForText("http://httpbin.org/get");
        console.info("httpResult2:", httpResult2);
        console.info("httpResult2('/headers'):", httpResult2.at("/headers"));
        return httpResult2;
    } catch(e) {
        console.error(">>>", e);
    }
}

function testForMongoQuery(context) {
    try {
        const mongoQuery = [
            { $match: { "eventType": "ecommerce_trade_gift" } },
            { $project: { "delFlag": 0 } }
        ];
        const mongoService = context.getDataService().getMongoService("default");
        console.info("mongoService: " + mongoService);
        var mongoResult = mongoService.findList("aggregates", mongoQuery);
        console.info("mongoResult: " + mongoResult);
        return mongoResult;
    } catch(e) {
        console.error(">>>", e);
    }
}

function testForRedisOperation(context) {
    try {
        const redisService = context.getDataService().getRedisService("default");
        console.info("redisService: " + redisService);
        redisService.set("key111", "value111");
        var redisResult = redisService.get("key111");
        console.info("redisResult: " + redisResult);
        return redisResult;
    } catch(e) {
        console.error(">>>", e);
    }
}

function testForJdbcSql(context) {
    try {
        var sql = "select * from user where user='root'";
        const jdbcService = context.getDataService().getJDBCService("default");
        console.info("jdbcService: " + jdbcService);
        var jdbcResult = jdbcService.findList(sql, []);
        console.info("jdbcResult: " + jdbcResult);
        return jdbcResult;
    } catch(e) {
        console.error(">>>", e);
    }
}

function testForSshExec(context) {
    try {
        const sshService = context.getDataService().getDefaultSSHClient();
        var sshResult = sshService.execute("localhost", 22, "prometheus", "123456", "ls -al /tmp/");
        console.info("sshResult:", sshResult);
        return sshResult;
    } catch(e) {
        console.error(">>>", e);
    }
}

// docker exec -it kafka1 /opt/bitnami/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test_topic
function testForKafkaPublish(context) {
    try {
        const topic = "test_topic";
        const kafkaService = context.getDataService().getKafkaService("default");
        kafkaService.publish(topic, {"foo1":"bar1"});
        const kafkaResult = "none";
        console.info("kafkaResult: " + kafkaResult);
        return kafkaResult;
    } catch(e) {
        console.error(">>>", e);
    }
}