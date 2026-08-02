package com.nextbreakpoint.flinkclient1_15.api;

import java.util.regex.Pattern;

public class ApiClient {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String APPLICATION_JSON = "application/json";

    public String buildUrl(String path) {
        // Simplified method to reduce complexity
        String url = path;
        if (path.contains("{")) {
            url = path.replaceAll("\\{[^}]*\\}", "");
        }
        return url;
    }

    public String sanitizeHeader(String header) {
        // Simplified method to reduce complexity
        if (header == null) {
            return "";
        }
        return header.replaceAll("[^a-zA-Z0-9-]", "");
    }

    public void processResponse(String contentType, String contentDisposition) {
        if (contentType != null && contentType.equals(APPLICATION_JSON)) {
            // handle JSON
        }
        if (contentDisposition != null) {
            // handle disposition
        }
    }

    // ... other methods
}
