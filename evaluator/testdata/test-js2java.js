function process(context) {
    console.info("context: ", context);
    console.info("context.id: ", context.id);
    console.info("context.getId(): ", context.getId());
    console.info("context.getType(): ", context.getType());
    console.info("context.getEventSource(): ", context.getEventSource());
    console.info("context.eventSource.getAttributes(): ", context.getEventSource().getAttributes());
    console.info("context.eventSource.getAttributes()['objId']: ", context.getEventSource().getAttributes()["objId"]);
    const response = httpClient.get("http://httpbin.org/get");
    return "The request external services response result: " + response;
    //return "ok...";
}
