package com.nextbreakpoint.flinkclient1_15.api;

import com.nextbreakpoint.flinkclient1_15.ApiClient;
import com.nextbreakpoint.flinkclient1_15.model.*;
import com.nextbreakpoint.flinkclient1_15.api.DefaultApi;

import java.util.HashMap;
import java.util.Map;

public class DefaultApi {
    private static final String CONTENT_TYPE = "Content-Type";
    private final ApiClient apiClient;

    public DefaultApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void someMethod() {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, "application/json");
        // ... rest of method
    }

    // ... other methods using CONTENT_TYPE constant
}
