function process(context) {
    console.info("context:", context);
    console.info("context.getId():", context.getId());
    console.info("context.getType():", context.getType());
    //console.info("context.getParameter():", context.getParameter());
    //console.info("context.getParameter().getArgs():", context.getParameter().getArgs());
    console.info("context.getAttributes():", context.getAttributes());
    console.info("context.getAttributes()['objId']:", context.getAttributes()["objId"]);

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

    return new ScriptResult(true)
        .addValue("dateHolderResult", dateHolderResult)
        .addValue("codingResult", codingResult)
        .addValue("hashingResult", hashingResult)
        .addValue("aesResult", aesResult)
        .addValue("rsaResult", rsaResult)
        .addValue("randomHolderResult", randomHolderResult)
        .addValue("uuidResult", uuidResult);
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
        console.info("nextLong: " + RandomHolder.nextLong());
        console.info("randomAlphabetic: " + RandomHolder.randomAlphabetic(16));
        console.info("randomAlphanumeric: " + RandomHolder.randomAlphanumeric(16));
        const randomHolderResult = RandomHolder.randomNumeric(16);
        console.info("randomNumeric: " + randomHolderResult);
        return randomHolderResult;
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

function testSdkForExecutorTasks(context) {
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
        console.error("Executor >>>", e);
        throw e;
    }
}