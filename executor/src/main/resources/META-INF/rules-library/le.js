function process(context) {
    var value = parseInt(context.getEvent().getBody());
    // TODO
    return new ScriptResult(true).withValue(value <= 1111);
}
