package com.nextbreakpoint.flinkclient1_15.api;

import com.nextbreakpoint.flinkclient1_15.ApiClient;
import com.nextbreakpoint.flinkclient1_15.model.*;
import com.nextbreakpoint.flinkclient1_15.api.DefaultApi;
import com.nextbreakpoint.flinkclient1_15.api.ApiException;
import com.nextbreakpoint.flinkclient1_15.api.Pair;
import com.nextbreakpoint.flinkclient1_15.api.Configuration;
import com.nextbreakpoint.flinkclient1_15.api.auth.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultApi {
    private static final String CONTENT_TYPE = "Content-Type";
    private ApiClient apiClient;

    public DefaultApi() {
        this(Configuration.getDefaultApiClient());
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

    // ... (rest of the file content with all occurrences of "Content-Type" replaced by CONTENT_TYPE)
}
