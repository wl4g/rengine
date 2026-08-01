package com.nextbreakpoint.flinkclient1_15.api;

import java.io.*;
import java.net.*;
import java.util.*;

public class ApiClient {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String APPLICATION_JSON = "application/json";

    // ... existing fields and methods ...

    private String buildUrl(String path, Map<String, Object> queryParams) {
        // ... existing logic ...
    }

    private String escapeString(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
