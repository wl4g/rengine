package com.nextbreakpoint.flinkclient1_15.api;

import com.nextbreakpoint.flinkclient1_15.ApiClient;
import com.nextbreakpoint.flinkclient1_15.model.*;
import com.nextbreakpoint.flinkclient1_15.api.DefaultApi;

import java.util.*;

public class DefaultApi {
    private static final String CONTENT_TYPE = "Content-Type";
    private ApiClient apiClient;

    public DefaultApi() {
        this(new ApiClient());
    }

    public DefaultApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // All methods that use "Content-Type" should use CONTENT_TYPE constant
    // Example method:
    public void someMethod() {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, "application/json");
        // ... rest of method
    }
}
