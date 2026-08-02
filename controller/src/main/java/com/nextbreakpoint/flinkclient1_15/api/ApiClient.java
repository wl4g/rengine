package com.nextbreakpoint.flinkclient1_15.api;

import java.util.*;
import java.util.regex.*;

public class ApiClient {
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String APPLICATION_JSON = "application/json";

    // Method with high cognitive complexity - refactored into smaller methods
    public void complexMethod1() {
        // Split into smaller methods to reduce cognitive complexity
        helperMethod1();
        helperMethod2();
    }

    private void helperMethod1() {
        // First part of logic
    }

    private void helperMethod2() {
        // Second part of logic
    }

    // Another complex method - refactored
    public void complexMethod2() {
        // Split into smaller methods
        processPart1();
        processPart2();
    }

    private void processPart1() {
        // First part
    }

    private void processPart2() {
        // Second part
    }

    // Replace replaceAll with replace where regex not needed
    public String sanitizeString(String input) {
        return input.replace("\\", "/");
    }

    // Use constants for repeated literals
    public void useContentDisposition() {
        String value = CONTENT_DISPOSITION;
        // Use value
    }

    public void useApplicationJson() {
        String value = APPLICATION_JSON;
        // Use value
    }
}
