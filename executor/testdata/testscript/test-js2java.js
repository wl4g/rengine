function process(context) {
    // for case1:
    console.info("context:", context);
    console.info("context.getId():", context.getId());
    console.info("context.getType():", context.getType());
    //console.info("context.getParameter():", context.getParameter());
    //console.info("context.getParameter().getArgs():", context.getParameter().getArgs());
    console.info("context.getAttributes():", context.getAttributes());
    console.info("context.getAttributes()['objId']:", context.getAttributes()["objId"]);

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

    // for case9:
    //testForExecutorTasks(context);

    // for case10:
    testForAESEncryptions(context);

    // for case11:
    testForRSAEncryptions(context);

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
        //const sshService = new ScriptSSHClient();
        const sshService = context.getDataService().getDefaultSSHClient();
        var sshResult = sshService.execute("localhost", 22, "prometheus", "123456", "ls -al /tmp/");
        console.info("sshResult:", sshResult);
        return sshResult;
    } catch(e) {
        console.error(">>>", e);
    }
}

function testForKafkaPublish(context) {
    try {
        const topic = "test_topic";
        const kafkaService = context.getDataService().getKafkaService("default");
        kafkaService.publish(topic, {"foo":"bar"});
        const kafkaResult = "none";
        console.info("kafkaResult: " + kafkaResult);
        return kafkaResult;
    } catch(e) {
        console.error(">>>", e);
    }
}

function testForExecutorTasks(context) {
    if (context.getId() != 11) { return; }
    try {
        var executor = context.getExecutor();
        var futures = [];
        for (var i = 0; i <= 5; i++) {
            var f = executor.submit(() => {
                var i =1;
                console.info("Test task " + i + " running ...");
                return "result for task "+i;
            });
            futures.push(f);
        }
        for (var i = 0; i <= futures.length; i++) {
            console.info("++++result " + i + " is : " + futures[i].get());
        }
    } catch(e) {
        console.error(">>>", e);
    }
}

function testForAESEncryptions(context) {
    var base64Iv = new Coding().toBase64("1234567890abcdef");
    console.info("base64Iv: " + base64Iv);

    var aes = new AES();
    var base64Key = aes.generateKeyToBase64();
    console.info("base64Key: " + base64Key);

    var plaintext = "abcdefghijklmnopqrstuvwxyz";
    console.info("plaintext: " + plaintext);

    var ciphertext = aes.encrypt256CbcPkcs7ToBase64(base64Key, base64Iv, plaintext);
    var plaintext2 = aes.decrypt256CbcPkcs7FromBase64(base64Key, base64Iv, ciphertext);

    console.info("ciphertext: " + ciphertext);
    console.info("plaintext2: " + plaintext2);
}

function testForRSAEncryptions(context) {
    var rsa = new RSA();
    var base64Key = rsa.generateKeyToBase64();
    console.info("base64Key: " + base64Key);

    var plaintext = "abcdefghijklmnopqrstuvwxyz";
    console.info("plaintext: " + plaintext);

    var ciphertext = rsa.encryptToBase64(false, base64Key.getPublicKey(), plaintext);
    var plaintext2 = rsa.decryptFromBase64(true, base64Key.getPrivateKey(), ciphertext);

    console.info("ciphertext: " + ciphertext);
    console.info("plaintext2: " + plaintext2);
}

