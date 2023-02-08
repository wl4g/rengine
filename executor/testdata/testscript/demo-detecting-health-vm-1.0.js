function process(context) {
    console.info("Detecting VMs of contenxt:", context);
    
    const nodes = context.getAttributes()['nodes'] || [];
    console.info("nodes:", nodes);


    // Detect whether the SSH server of the VMs is healthy.
    const httpClient = context.getDataService().getDefaultHttpClient();
    for (var node : nodes) {
        const 
        const body = {"name":"foo"};
        const headers = {"x-foo": "bar"};
        const httpClient = context.getDataService().getDefaultHttpClient();
        const httpResult2 = httpClient.exchange("http://httpbin.org/post", "POST", body, headers);
    }


}