// Detect whether the SSH server of the VM is healthy.
function process(context) {
    const args = context.getParameter().getArgs();
    const vmHost = Assert.hasTextOf(args['vmHost'], "vmHost");
    const vmPort = Assert.hasTextOf(args['vmPort'], "vmPort");
    const vmUser = Assert.hasTextOf(args['vmUser'], "vmUser");
    const vmPassword = Assert.hasTextOf(args['vmPassword'], "vmPassword");

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