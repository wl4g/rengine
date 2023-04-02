// Usually this is used for debugging scenarios where printing.
def process(context):

    print("========== The current context anatomy print: ==========")
    print(" context                                 :", context)
    print(" context.getId()                         :", context.getId())
    print(" context.getType()                       :", context.getType())

    print(" context.getParameter().getRequestTime() :", context.getParameter().getRequestTime())
    print(" context.getParameter().getClientId()    :", context.getParameter().getClientId())
    print(" context.getParameter().getTraceId()     :", context.getParameter().getTraceId())
    print(" context.getParameter().isTrace()        :", context.getParameter().isTrace())
    print(" context.getParameter().getWorkflowId()  :", context.getParameter().getWorkflowId())
    print(" context.getParameter().getArgs()        :", JSON.stringify(context.getParameter().getArgs()))

    print(" context.getAttributes()                 :", JSON.stringify(context.getAttributes()))

    return ScriptResult(true).addValue("finishedTime", DateHolder.currentMillis())
