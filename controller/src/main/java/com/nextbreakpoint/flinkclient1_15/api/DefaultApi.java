package com.nextbreakpoint.flinkclient1_15.api;

import com.nextbreakpoint.flinkclient1_15.ApiClient;
import com.nextbreakpoint.flinkclient1_15.model.*;
import com.nextbreakpoint.flinkclient1_15.api.DefaultApi;

import java.util.*;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class DefaultApi {
    private final ApiClient apiClient;
    private static final String CONTENT_TYPE = "Content-Type";

    public DefaultApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    // ... (rest of the class with all methods, replacing "Content-Type" with CONTENT_TYPE)
}
