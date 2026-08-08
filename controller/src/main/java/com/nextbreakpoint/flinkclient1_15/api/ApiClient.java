package com.nextbreakpoint.flinkclient1_15.api;

import java.util.regex.Pattern;

public class ApiClient {
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    // ... existing code ...

    public String sanitize(String input) {
        // Replace replaceAll with replace for literal replacement
        return input.replace("\\", "");
    }

    // ... existing code ...
}
