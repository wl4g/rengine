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
    const response1 = context.getDefaultHttpClient().getAsText("http://httpbin.org/get");
    console.info("response1:", response1);
    console.info("response1('/headers'):", response1.at("/headers"));

    // for case3:
    const response2 = new ScriptHttpClient().postAsJson("http://httpbin.org/post", "");
    console.info("response2:", response2);
    console.info("response2('/headers'):", response2.at("/headers").toString());

    // return response2;
    // return "ok...";
    return new ScriptResult(true).withValue(response2).addAttribute("key11111", "value1111");
}
