// Detect whether the APP process of the VM is healthy, otherwise and restart.
function process(context) {
    const vmHost = Assert.hasTextOf(context.getArgs()['vmHost'], "vmHost");
    const vmPort = Assert.hasTextOf(context.getArgs()['vmPort'], "vmPort");
    const vmUser = Assert.hasTextOf(context.getArgs()['vmUser'], "vmUser");
    const vmPassword = Assert.hasTextOf(context.getArgs()['vmPassword'], "vmPassword");
    const processFilter = Assert.hasTextOf(context.getArgs()['processFilter'], "processFilter");
    const restartCmd = Assert.hasTextOf(context.getArgs()['restartCmd'], "restartCmd");

    const logConnString = "ssh " + vmUser + " -p " + vmPort + "@" + vmHost
    console.info("Detecting VM process of ", logConnString, "processFilter:", processFilter, "restartCmd:", restartCmd);
    try {
        const sshClient = context.getDataService().getDefaultSSHClient();
        const detectCmd = "/bin/ps -ef | grep -v grep | grep " + processFilter;
        const detectedResult = sshClient.execute(vmHost, parseInt(vmPort + ""), vmUser, vmPassword, detectCmd);
        console.info("Detected VM process detectedResult of :", detectedResult);

        if (detectedResult != null && detectedResult.length() > 0) {
            return new ScriptResult(true)
                .addValue("vm_process_health_status_code", 0)
                .addValue("vm_process_health_status_result", detectedResult)
                .addValue("vm_process_health_status_desc", "healthy");
        }

        console.info("Dead, restarting for ", restartCmd)
        const restartResult = sshClient.execute(vmHost, parseInt(vmPort + ""), vmUser, vmPassword, restartCmd);
        console.info("Detected VM process restartResult of :", restartResult);

        return new ScriptResult(false)
            .addValue("vm_process_health_status_code", 1)
            .addValue("vm_process_health_status_result", restartResult)
            .addValue("vm_process_health_status_desc", "unhealthy");

    } catch (ex) {
        console.error("Unable to detecting VM process for :", logConnString, ", reason:", ex);
        return new ScriptResult(false)
            .addValue("vm_process_health_status_code", 2)
            .addValue("vm_process_health_status_result", ex)
            .addValue("vm_process_health_status_desc", "unhealthy");
    }

    return new ScriptResult(false)
        .addValue("vm_process_health_status_code", 3)
        .addValue("vm_process_health_status_desc", "unhealthy");
}