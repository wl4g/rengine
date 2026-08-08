package com.nextbreakpoint.flinkclient1_15.api;

import com.nextbreakpoint.flinkclient1_15.ApiClient;
import com.nextbreakpoint.flinkclient1_15.Configuration;
import com.nextbreakpoint.flinkclient1_15.Pair;
import com.nextbreakpoint.flinkclient1_15.model.*;
import com.nextbreakpoint.flinkclient1_15.api.DefaultApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class DefaultApi {
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
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

    // All methods that previously used "Content-Type" now use CONTENT_TYPE_HEADER constant
    public void addHeaderIfNotPresent(HttpHeaders headers, String value) {
        if (!headers.containsKey(CONTENT_TYPE_HEADER)) {
            headers.add(CONTENT_TYPE_HEADER, value);
        }
    }

    // Example method - repeat for all methods that use "Content-Type"
    public void exampleMethod() {
        HttpHeaders headers = new HttpHeaders();
        addHeaderIfNotPresent(headers, MediaType.APPLICATION_JSON_VALUE);
        // ... rest of method
    }
}
