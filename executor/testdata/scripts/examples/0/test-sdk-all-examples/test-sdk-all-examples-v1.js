function process(context) {
    console.info("context:", context);
    console.info("context.getId():", context.getId());
    console.info("context.getType():", context.getType());
    //console.info("context.getParameter():", context.getParameter());
    //console.info("context.getParameter().getArgs():", context.getParameter().getArgs());
    console.info("context.getAttributes():", context.getAttributes());
    console.info("context.getAttributes()['objId']:", context.getAttributes()["objId"]);

    // for sdk case1:
//    const httpResult = testSdkForHttpClient(context);
//
//    // for sdk case2:
//    const processResult = testSdkForProcessClient(context);
//
//    // for sdk case3:
//    const sshResult = testSdkForSSHClient(context);
//
//    // for sdk case4:
//    const lockResult = testSdkForRedisLockClient(context);
//
//    // for sdk case5:
//    const mongoResult = testSdkForMongoSourceFacade(context);
//
//    // for sdk case6:
//    const jdbcResult = testSdkForJdbcSourceFacade(context);
//
//    // for sdk case7:
//    const redisResult = testSdkForRedisSourceFacade(context);
//
//    // for sdk case8:
//    const kafkaResult = testSdkForKafkaSourceFacade(context);
//
//    // for sdk case9:
//    const dingtalkResult = testSdkForDingtalkNotifier(context);

    // for sdk case10:
    //const emailResult = testSdkForEmailNotifier(context);

    // for sdk case11:
    const dateHolderResult = testSdkForDateHolder(context);

    // for sdk case12:
    const codingResult = testSdkForCoding(context);

    // for sdk case13:
    const hashingResult = testSdkForHashing(context);

    // for sdk case14:
    const aesResult = testSdkForAES(context);

    // for sdk case15:
    const rsaResult = testSdkForRSA(context);

    // for sdk case16:
    const randomHolderResult = testSdkForRandomHolder(context);

    // for sdk case17:
    const uuidResult = testSdkForUUID(context);

    // for sdk case18:
    //testSdkForExecutorTasks(context);

    // for sdk case19:
    const filesResult = testSdkForFiles(context);

    return new ScriptResult(true)
        //.addValue("httpResult", httpResult)
        //.addValue("processResult", processResult)
        //.addValue("sshResult", sshResult)
        //.addValue("lockResult", lockResult)
        //.addValue("mongoResult", mongoResult)
        //.addValue("jdbcResult", jdbcResult)
        //.addValue("redisResult", redisResult)
        //.addValue("kafkaResult", kafkaResult)
        //.addValue("dingtalkResult", dingtalkResult)
        //.addValue("emailResult", emailResult)
        .addValue("dateHolderResult", dateHolderResult)
        .addValue("codingResult", codingResult)
        .addValue("hashingResult", hashingResult)
        .addValue("aesResult", aesResult)
        .addValue("rsaResult", rsaResult)
        .addValue("randomHolderResult", randomHolderResult)
        .addValue("uuidResult", uuidResult)
        .addValue("filesResult", filesResult);
}

function testSdkForHttpClient(context) {
    // metnod1:
    try {
        const httpResult1 = new ScriptHttpClient().postForJson("http://httpbin.org/post", "");
        console.info("httpResult1:", httpResult1);
        console.info("httpResult1('/headers'):", httpResult1.at("/headers").toString());
        //return httpResult1;
    } catch(e) {
        console.error("ScriptHttpClient1 >>>", e);
        throw e;
    }

    // metnod2:
    try {
        const requestBody = {"name":"foo"};
        const headers = {"x-foo": "bar"};
        const httpClient = context.getDataService().getDefaultHttpClient();
        const httpResult2 = httpClient.exchange("http://httpbin.org/post", "POST", requestBody, headers);
        console.info("httpResult2:", httpResult2);
        return httpResult2;
    } catch(e) {
        console.error("ScriptHttpClient2 >>>", e);
        throw e;
    }
}

function testSdkForProcessClient(context) {
    try {
        //const sshService = new ScriptSSHClient();
        const processService = context.getDataService().getDefaultProcessClient();
        var processResult = processService.execute("ls -al /tmp/");
        console.info("processResult:", processResult);
        return processResult;
    } catch(e) {
        console.error("ScriptProcessClient >>>", e);
        throw e;
    }
}

function testSdkForSSHClient(context) {
    try {
        //const sshService = new ScriptSSHClient();
        const sshService = context.getDataService().getDefaultSSHClient();
        var sshResult = sshService.execute("localhost", 22, "prometheus", "123456", "ls -al /tmp/");
        console.info("sshResult:", sshResult);
        return sshResult;
    } catch(e) {
        console.error("ScriptSSHClient >>>", e);
        throw e;
    }
}

function testSdkForRedisLockClient(context) {
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
        console.error("ScriptRedisLockClient >>>", e);
        throw e;
    }
}

function testSdkForMongoSourceFacade(context) {
    try {
        const mongoQuery = [
            { $match: { "eventType": "ecommerce_trade_gift" } },
            { $project: { "delFlag": 0 } }
        ];
        const mongoFacade = context.getDataService().obtainMongoDSFacade("default");
        console.info("mongoFacade: " + mongoFacade);
        var mongoResult = mongoFacade.findList("t_aggregates", mongoQuery);
        console.info("mongoResult: " + mongoResult);
        return mongoResult;
    } catch(e) {
        console.error("MongoSourceFacade >>>", e);
        throw e;
    }
}

function testSdkForRedisSourceFacade(context) {
    try {
        const redisFacade = context.getDataService().obtainRedisDSFacade("default");
        console.info("redisFacade: " + redisFacade);
        redisFacade.set("key111", "value111");
        var redisResult = redisFacade.get("key111");
        console.info("redisResult: " + redisResult);
        return redisResult;
    } catch(e) {
        console.error("RedisSourceFacade >>>", e);
        throw e;
    }
}

function testSdkForJdbcSourceFacade(context) {
    try {
        var sql = "select * from user where user='root'";
        const jdbcFacade = context.getDataService().obtainJdbcDSFacade("default");
        console.info("jdbcFacade: " + jdbcFacade);
        var jdbcResult = jdbcFacade.findList(sql, []);
        console.info("jdbcResult: " + jdbcResult);
        return jdbcResult;
    } catch(e) {
        console.error("JdbcSourceFacade >>>", e);
        throw e;
    }
}

function testSdkForKafkaSourceFacade(context) {
    try {
        const topic = "test_topic";
        const kafkaFacade = context.getDataService().obtainKafkaDSFacade("default");
        kafkaFacade.publish(topic, {"foo":"bar"});
        const kafkaResult = "none";
        console.info("kafkaResult: " + kafkaResult);
        return kafkaResult;
    } catch(e) {
        console.error("KafkaSourceFacade >>>", e);
        throw e;
    }
}

function testSdkForDingtalkNotifier(context) {
    try {
        console.info("dingtalkNotifier ...");
        const dingtalkNotifier = context.getDataService().obtainNotifier("DINGTALK");
        console.info("dingtalkNotifier: " + dingtalkNotifier);

        const openConversationId = Assert.hasTextOf(context.getAttributes()["openConversationId"], "attributes.openConversationId");
        const robotCode = Assert.hasTextOf(context.getAttributes()["robotCode"], "attributes.robotCode");
        const parameter = {
            "msgParam": "{\"title\":\"(故障演练)异常告警\",\"text\":\"- 告警时间: 2023-01-01 01:01:01\n- 持续时间: 10m\n- 应用服务: mqttcollect\n- 集群环境: production\n- 节点 IP: 10.0.0.112\n- 节点 CPU(10s): 200%\n- 节点 Free Mem(5m): 10%\n- 节点 InNet(1m): 1234mbps\n- 节点 OutNet(1m): 1234mbps\n- 节点 IOPS(1m): 512/1501\n- 节点 Free Disks: 99GB/250GB\n- 诊断信息: <font color='#ff0000' size=3>send_kafka_fail_rate > 30%</font>\n- **[更多指标](http://grafana.example.com/123)**\",\"buttonTitle1\":\"Restart Now\",\"buttonUrl1\":\"https://qq.com\",\"buttonTitle2\":\"Cancel\",\"buttonUrl2\":\"https://qq.com\"}",
            "msgKey": "sampleActionCard6",
            "openConversationId": openConversationId,
            "robotCode": robotCode
        };
        const notifierResult = dingtalkNotifier.send(parameter);
        console.info("dingtalkNotifierResult: " + notifierResult);
        return notifierResult;
    } catch(e) {
        console.error("DingtalkNotifier >>>", e);
        throw e;
    }
}

function testSdkForEmailNotifier(context) {
    try {
        console.info("emailNotifier ...");
        const emailNotifier = context.getDataService().obtainNotifier("EMAIL");
        console.info("emailNotifier: " + emailNotifier);

        const parameter = {
            "subject": "Testing Sender",
            "to": ["983708408@qq.com"],
            //"replyTo": [],
            //"cc": [],
            //"bcc": [],
            "msg": "This testing <b>MIME<b> message!!!</br><font color=red>It's is red font.</font>"
        };
        const emailResult = emailNotifier.send(parameter);
        console.info("emailNotifierResult: " + emailResult);
        return emailResult;
    } catch(e) {
        console.error("EmailNotifier >>>", e);
        throw e;
    }
}

function testSdkForDateHolder(context) {
    try {
        console.info("currentMillis: " + DateHolder.currentMillis());
        console.info("currentNanoTime: " + DateHolder.currentNanoTime());
        var date = DateHolder.getDate();
        console.info("getDate: " + date);
        console.info("formatDate: " + DateHolder.formatDate(date, "yyyy-MM-dd"));
        console.info("getDateOf: " + DateHolder.getDateOf(5, 10, "yyyy-MM-dd"));
        return date;
    } catch(e) {
        console.error("DateHolder >>>", e);
        throw e;
    }
}

function testSdkForCoding(context) {
    try {
        var base64 = Coding.toBase64("1234567890abcdef");
        console.info("base64: " + base64);
        console.info("fromBase64: " + Coding.fromBase64(base64));

        var base58 = Coding.toBase58("1234567890abcdef");
        console.info("base58: " + base58);
        console.info("fromBase58: " + Coding.fromBase64(base58));

        var hex = Coding.toHex("1234567890abcdef");
        console.info("hex: " + hex);
        console.info("fromHex: " + Coding.fromHex(hex));
        return base58;
    } catch(e) {
        console.error("Coding >>>", e);
        throw e;
    }
}

function testSdkForHashing(context) {
    try {
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
    } catch(e) {
        console.error("Hashing >>>", e);
        throw e;
    }
}

function testSdkForAES(context) {
    try {
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
    } catch(e) {
        console.error("AES >>>", e);
        throw e;
    }
}

function testSdkForRSA(context) {
    try {
        var base64Key = RSA.generateKeyToBase64();
        console.info("base64Key: " + base64Key);

        var plaintext = "abcdefghijklmnopqrstuvwxyz";
        console.info("plaintext: " + plaintext);

        var ciphertext = RSA.encryptToBase64(false, base64Key.getPublicKey(), plaintext);
        var plaintext2 = RSA.decryptFromBase64(true, base64Key.getPrivateKey(), ciphertext);

        console.info("ciphertext: " + ciphertext);
        console.info("plaintext2: " + plaintext2);
        return plaintext2;
    } catch(e) {
        console.error("RSA >>>", e);
        throw e;
    }
}

function testSdkForRandomHolder(context) {
    try {
        console.info("nextBoolean: " + RandomHolder.nextBoolean());
        console.info("nextInt: " + RandomHolder.nextInt());
        console.info("nextInt(10, 20): " + RandomHolder.nextInt(10, 20));
        console.info("nextFloat: " + RandomHolder.nextFloat());
        console.info("nextFloat(10, 20): " + RandomHolder.nextFloat(10.0, 20.0));
        console.info("nextDouble: " + RandomHolder.nextDouble());
        console.info("nextDouble(10, 20): " + RandomHolder.nextDouble(10.0, 20.0));
        console.info("nextLong: " + RandomHolder.nextLong());
        console.info("nextLong(10, 20): " + RandomHolder.nextLong(10, 20));
        console.info("randomAscii: " + RandomHolder.randomAscii(16));
        console.info("randomAlphabetic: " + RandomHolder.randomAlphabetic(16));
        console.info("randomAlphanumeric: " + RandomHolder.randomAlphanumeric(16));
        console.info("randomGraph: " + RandomHolder.randomGraph(16));
        console.info("randomNumeric: " + RandomHolder.randomNumeric(16));
        console.info("randomPrint: " + RandomHolder.randomPrint(16));
        return RandomHolder.randomNumeric(16);
    } catch(e) {
        console.error("RandomHolder >>>", e);
        throw e;
    }
}

function testSdkForUUID(context) {
    try {
        const uuidResult = UUID.randomUUID();
        console.info("randomUUID: " + uuidResult);
        return uuidResult;
    } catch(e) {
        console.error("UUID >>>", e);
        throw e;
    }
}

function testSdkForFiles(context) {
    try {
        Files.writeFromString("/1.txt", "abcdefghijklmnopqrstyvwxyz");
        Files.mkdirs("/dir1");
        const listResult = Files.listFiles("/");
        console.info("listResult: " + listResult);
        var result = Files.readToString("/1.txt");
        console.info(result);
        return result;
    } catch(e) {
        console.error("Files >>>", e);
        throw e;
    }
}
