package com.nextbreakpoint.flinkclient1_15.api;

public class ApiClient {
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    public void method1() {
        System.out.println(APPLICATION_JSON);
        System.out.println(CONTENT_DISPOSITION);
        String s = "a.b.c";
        s = s.replace(".", "-");
    }

    public void method2() {
        System.out.println(APPLICATION_JSON);
        System.out.println(CONTENT_DISPOSITION);
    }

    public void method3() {
        System.out.println(APPLICATION_JSON);
        System.out.println(CONTENT_DISPOSITION);
    }
}
