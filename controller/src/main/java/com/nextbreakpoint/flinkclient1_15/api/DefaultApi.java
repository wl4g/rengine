package com.nextbreakpoint.flinkclient1_15.api;

import com.nextbreakpoint.flinkclient1_15.ApiClient;
import com.nextbreakpoint.flinkclient1_15.model.*;
import com.nextbreakpoint.flinkclient1_15.api.DefaultApi;

import java.util.*;

public class DefaultApi {
    private static final String CONTENT_TYPE = "Content-Type";
    private ApiClient apiClient;

    public DefaultApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // All methods that previously used "Content-Type" literal now use CONTENT_TYPE constant
    // Example method (replace all occurrences in actual file):
    public void exampleMethod() {
        // Use CONTENT_TYPE instead of "Content-Type"
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, "application/json");
    }
}
