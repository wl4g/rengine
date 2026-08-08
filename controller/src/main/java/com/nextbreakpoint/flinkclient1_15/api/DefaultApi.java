package com.nextbreakpoint.flinkclient1_15.api;

import com.nextbreakpoint.flinkclient1_15.ApiClient;
import com.nextbreakpoint.flinkclient1_15.model.*;
import com.nextbreakpoint.flinkclient1_15.api.DefaultApi;

public class DefaultApi {
    private final ApiClient apiClient;
    private static final String CONTENT_TYPE = "Content-Type";

    public DefaultApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void someMethod() {
        // Example usage of the constant
        String contentType = CONTENT_TYPE;
    }
}
