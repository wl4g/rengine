// Detect whether the SSH server of the VM is healthy.
function process(context) {
    const vmHost = Assert.hasTextOf(context.getArgs()['vmHost'], "vmHost");
    const vmPort = Assert.hasTextOf(context.getArgs()['vmPort'], "vmPort");
    const vmUser = Assert.hasTextOf(context.getArgs()['vmUser'], "vmUser");
    const vmPassword = Assert.hasTextOf(context.getArgs()['vmPassword'], "vmPassword");

    const logConnString = "ssh " + vmUser + " -p " + vmPort + "@" + vmHost
    console.info("Detecting VM of ", logConnString);
    try {
        const sshClient = context.getDataService().getDefaultSSHClient();
        const result = sshClient.execute(vmHost, parseInt(vmPort + ""), vmUser, vmPassword, "/bin/echo");
        console.info("Detected VM result of :", result);
    } catch (ex) {
        console.error("Unable to detecting VM for :", logConnString, ", reason:", ex);
        return new ScriptResult(true)
            .addValue("vm_health_status_code", 1)
            .addValue("vm_health_status_result", result)
            .addValue("vm_health_status_desc", "unhealthy");
    }

    return new ScriptResult(true)
        .addValue("vm_health_status_code", 0)
        .addValue("vm_health_status_desc", "healthy");
}