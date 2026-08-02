package com.nextbreakpoint.flinkclient1_15.api;

import com.nextbreakpoint.flinkclient1_15.ApiClient;
import com.nextbreakpoint.flinkclient1_15.model.*;
import com.nextbreakpoint.flinkclient1_15.api.DefaultApi;

import java.util.*;

public class DefaultApi {
    private static final String CONTENT_TYPE = "Content-Type";
    private final ApiClient apiClient;

    public DefaultApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // ... (all methods using CONTENT_TYPE constant instead of literal)
}
