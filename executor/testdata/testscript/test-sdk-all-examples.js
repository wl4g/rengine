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
    //const httpResult1 = testForHttpRequest1(context);

    // for case3:
    //const httpResult2 = testForHttpRequest2(context);

    // for case4:
    const lockResult = testForRedisLockClient(context);

    // for case5:
    const sshResult = testForSshClient(context);

    // for case6:
    const processResult = testForProcessClient(context);

    // for case7:
    const mongoResult = testForMongoSourceFacade(context);

    // for case8:
    const redisResult = testForRedisSourceFacade(context);

    // for case9:
    const jdbcResult = testForJdbcSourceFacade(context);

    // for case10:
    const kafkaResult = testForKafkaSourceFacade(context);

    // for case11:
    testForExecutorTasks(context);

    // for case12:
    const dateHolderResult = testForDateHolder(context);

    // for case13:
    const codingResult = testForCoding(context);

    // for case14:
    const hashingResult = testForHashing(context);

    // for case15:
    const aesResult = testForAES(context);

    // for case16:
    const rsaResult = testForRSA(context);

    return new ScriptResult(true)
        //.addValue("httpResult1", httpResult1)
        //.addValue("httpResult2", httpResult2)
        .addValue("lockResult", lockResult)
        .addValue("sshResult", sshResult)
        .addValue("processResult", processResult)
        .addValue("mongoResult", mongoResult)
        .addValue("redisResult", redisResult)
        .addValue("jdbcResult", jdbcResult)
        .addValue("kafkaResult", kafkaResult)
        .addValue("dateHolderResult", dateHolderResult)
        .addValue("codingResult", codingResult)
        .addValue("hashingResult", hashingResult)
        .addValue("aesResult", aesResult)
        .addValue("rsaResult", rsaResult);
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
        const requestBody = {"name":"foo"};
        const headers = {"x-foo": "bar"};
        const httpClient = context.getDataService().getDefaultHttpClient();
        const httpResult2 = httpClient.exchange("http://httpbin.org/post", "POST", requestBody, headers);
        console.info("httpResult2:", httpResult2);
        return httpResult2;
    } catch(e) {
        console.error(">>>", e);
    }
}

function testForRedisLockClient(context) {
    try {
        const redisLockService = context.getDataService().getDefaultRedisLockClient();
        console.info("redisLockService: " + redisLockService);
        var redisLock = redisLockService.getLock("testLock");
        if (redisLock.tryLock()) {
            console.info("Got lock for : " + redisLock);
        } else {
            console.info("No got lock for : " + redisLock);
        }
        console.info("redisLock: " + redisLock);
        return redisLock;
    } catch(e) {
        console.error(">>>", e);
    }
}

function testForSshClient(context) {
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

function testForProcessClient(context) {
    try {
        //const sshService = new ScriptSSHClient();
        const processService = context.getDataService().getDefaultProcessClient();
        var processResult = processService.execute("ls -al /tmp/");
        console.info("processResult:", processResult);
        return processResult;
    } catch(e) {
        console.error(">>>", e);
    }
}

function testForMongoSourceFacade(context) {
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

function testForRedisSourceFacade(context) {
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

function testForJdbcSourceFacade(context) {
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

function testForKafkaSourceFacade(context) {
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

function testForDateHolder(context) {
    console.info("currentMillis: " + DateHolder.currentMillis());
    console.info("currentNanoTime: " + DateHolder.currentNanoTime());
    var date = DateHolder.getDate();
    console.info("getDate: " + date);
    console.info("formatDate: " + DateHolder.formatDate(date, "yyyy-MM-dd"));
    console.info("getDateOf: " + DateHolder.getDateOf(5, 10, "yyyy-MM-dd"));
    return date;
}

function testForCoding(context) {
    var base64 = Coding.toBase64("1234567890abcdef");
    console.info("base64: " + base64);
    console.info("fromBase64: " + Coding.fromBase64(base64));

    var base58 = Coding.toBase58("1234567890abcdef");
    console.info("base58: " + base58);
    console.info("fromBase58: " + Coding.fromBase64(base58));

    var hex = Coding.toHex("1234567890abcdef");
    console.info("hex: " + hex);
    console.info("fromHex: " + Coding.fromHex(hex));
    return hex;
}

function testForHashing(context) {
    console.info("md5: " + Hashing.md5("1234567890abcdef"));
    console.info("sha1: " + Hashing.sha1("1234567890abcdef"));
    console.info("sha256: " + Hashing.sha256("1234567890abcdef"));
    console.info("sha384: " + Hashing.sha384("1234567890abcdef"));
    console.info("sha512: " + Hashing.sha512("1234567890abcdef"));
    console.info("hmacSha1: " + Hashing.hmacSha1("abc", "1234567890abcdef"));
    console.info("hmacSha256: " + Hashing.hmacSha256("abc", "1234567890abcdef"));
    var hmacSha512 = Hashing.hmacSha512("abc", "1234567890abcdef");
    console.info("hmacSha512: " + hmacSha512);
    return hmacSha512;
}

function testForAES(context) {
    var base64Iv = Coding.toBase64("1234567890abcdef");
    console.info("base64Iv: " + base64Iv);

    var base64Key = AES.generateKeyToBase64();
    console.info("base64Key: " + base64Key);

    var plaintext = "abcdefghijklmnopqrstuvwxyz";
    console.info("plaintext: " + plaintext);

    var ciphertext = AES.encryptCbcPkcs7ToBase64(base64Key, base64Iv, plaintext);
    var plaintext2 = AES.decryptCbcPkcs7FromBase64(base64Key, base64Iv, ciphertext);

    console.info("ciphertext: " + ciphertext);
    console.info("plaintext2: " + plaintext2);
    return plaintext2;
}

function testForRSA(context) {
    var base64Key = RSA.generateKeyToBase64();
    console.info("base64Key: " + base64Key);

    var plaintext = "abcdefghijklmnopqrstuvwxyz";
    console.info("plaintext: " + plaintext);

    var ciphertext = RSA.encryptToBase64(false, base64Key.getPublicKey(), plaintext);
    var plaintext2 = RSA.decryptFromBase64(true, base64Key.getPrivateKey(), ciphertext);

    console.info("ciphertext: " + ciphertext);
    console.info("plaintext2: " + plaintext2);
    return plaintext2;
}

