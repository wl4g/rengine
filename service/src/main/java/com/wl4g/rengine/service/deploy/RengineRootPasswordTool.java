package com.wl4g.rengine.service.deploy;

import com.wl4g.rengine.service.deploy.RengineRootPasswordTool;

public class RengineRootPasswordTool {
    private static final String SEPARATOR_LINE = "---------------------------------------";

    public static void printSeparator() {
        System.out.println(SEPARATOR_LINE);
    }

    public static void printHeader() {
        System.out.println(SEPARATOR_LINE);
        System.out.println("Rengine Root Password Tool");
        System.out.println(SEPARATOR_LINE);
    }

    public static void printFooter() {
        System.out.println(SEPARATOR_LINE);
    }
}
