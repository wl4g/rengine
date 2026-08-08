package com.wl4g.rengine.service.deploy;

public class RengineRootPasswordTool {
    private static final String SEPARATOR = "---------------------------------------";

    public void printPasswordInfo() {
        System.out.println(SEPARATOR);
        System.out.println("Root password information");
        System.out.println(SEPARATOR);
    }

    public void printPasswordReset() {
        System.out.println(SEPARATOR);
        System.out.println("Password reset completed");
        System.out.println(SEPARATOR);
    }

    public void printPasswordError() {
        System.out.println(SEPARATOR);
        System.out.println("Error occurred");
        System.out.println(SEPARATOR);
    }

    public void printPasswordSuccess() {
        System.out.println(SEPARATOR);
        System.out.println("Success");
        System.out.println(SEPARATOR);
    }
}
