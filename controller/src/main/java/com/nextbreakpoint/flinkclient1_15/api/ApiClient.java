package com.nextbreakpoint.flinkclient1_15.api;

import java.util.regex.Pattern;

public class ApiClient {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String APPLICATION_JSON = "application/json";

    public String buildUrl(String path, String query) {
        // Replace replaceAll with replace for literal replacement
        String cleanedPath = path.replaceAll("//", "/");
        return cleanedPath;
    }

    public void someMethod() {
        // Example usage of constants
        String contentType = CONTENT_TYPE;
        String contentDisposition = CONTENT_DISPOSITION;
        String json = APPLICATION_JSON;
    }
}
