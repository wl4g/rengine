package com.nextbreakpoint.flinkclient1_15.api;

import java.util.regex.Pattern;

public class ApiClient {
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    private static final String APPLICATION_JSON = "application/json";

    public String buildUrl(String basePath, String path) {
        // Simplified method to reduce complexity
        if (basePath == null || path == null) {
            return "";
        }
        return basePath + path;
    }

    public String sanitizeHeaderValue(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\r", "").replace("\n", "");
    }

    public String extractFileName(String contentDisposition) {
        if (contentDisposition == null) {
            return "";
        }
        String[] parts = contentDisposition.split(";");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.startsWith("filename=")) {
                return trimmed.substring("filename=".length()).replace("\"", "");
            }
        }
        return "";
    }

    // ... rest of the class with constants used
}
