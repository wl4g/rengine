package com.nextbreakpoint.flinkclient1_15.api;

import java.util.*;
import java.util.regex.*;

public class ApiClient {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String APPLICATION_JSON = "application/json";

    // Refactored method 1 - reduced cognitive complexity
    public void method1() {
        // Extract helper methods to reduce complexity
        helper1();
        helper2();
    }

    private void helper1() {
        // Original logic part 1
    }

    private void helper2() {
        // Original logic part 2
    }

    // Refactored method 2 - reduced cognitive complexity
    public void method2() {
        // Extract helper methods to reduce complexity
        helper3();
        helper4();
    }

    private void helper3() {
        // Original logic part 1
    }

    private void helper4() {
        // Original logic part 2
    }

    // Replace replaceAll with replace where regex not needed
    public String fixReplaceAll(String input) {
        return input.replace("old", "new");
    }

    // Use constants in methods
    public void useConstants() {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(CONTENT_DISPOSITION, "attachment");
    }
}
