// Usually this is used for debugging scenarios where printing.
function process(context) {

    console.info("==== The current context disassembled print: =====");
    console.info(" context                                 :", context);
    console.info(" context.getId()                         :", context.getId());
    console.info(" context.getType()                       :", context.getType());

    console.info(" context.getParameter().getRequestTime() :", context.getParameter().getRequestTime());
    console.info(" context.getParameter().getClientId()    :", context.getParameter().getClientId());
    console.info(" context.getParameter().getTraceId()     :", context.getParameter().getTraceId());
    console.info(" context.getParameter().isTrace()        :", context.getParameter().isTrace());
    console.info(" context.getParameter().getScenesCode()  :", context.getParameter().getScenesCode());
    console.info(" context.getParameter().getWorkflowId()  :", context.getParameter().getWorkflowId());
    console.info(" context.getParameter().getArgs()        :", JSON.stringify(context.getParameter().getArgs()));

    console.info(" context.getAttributes()                 :", JSON.stringify(context.getAttributes()));

    return new ScriptResult(true);
}