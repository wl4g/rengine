package com.nextbreakpoint.flinkclient1_15.api;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
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

public class ApiClient {
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    private static final String APPLICATION_JSON = "application/json";

    private RestTemplate restTemplate;
    private String basePath;
    private Map<String, String> defaultHeaders = new HashMap<>();

    public ApiClient() {
        this.restTemplate = new RestTemplate();
        this.basePath = "http://localhost:8080";
    }

    public ApiClient(RestTemplate restTemplate, String basePath) {
        this.restTemplate = restTemplate;
        this.basePath = basePath;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void addDefaultHeader(String key, String value) {
        defaultHeaders.put(key, value);
    }

    public void removeDefaultHeader(String key) {
        defaultHeaders.remove(key);
    }

    // Refactored method to reduce cognitive complexity
    public <T> T invokeAPI(String path, HttpMethod method, Map<String, Object> pathParams, List<Pair> queryParams, Object body, Map<String, String> headerParams, MultiValueMap<String, String> formParams, Type returnType) throws ApiException {
        String targetUrl = buildUrl(path, pathParams, queryParams);
        HttpHeaders headers = buildHeaders(headerParams);
        HttpMethod httpMethod = method;
        Object requestBody = body;

        if (formParams != null && !formParams.isEmpty()) {
            requestBody = formParams;
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }

        ResponseEntity<T> response = restTemplate.exchange(targetUrl, httpMethod, new org.springframework.http.HttpEntity<>(requestBody, headers), new ParameterizedTypeReference<T>() {});
        return response.getBody();
    }

    private String buildUrl(String path, Map<String, Object> pathParams, List<Pair> queryParams) {
        String url = basePath + path;
        for (Map.Entry<String, Object> entry : pathParams.entrySet()) {
            url = url.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        if (queryParams != null) {
            for (Pair param : queryParams) {
                builder.queryParam(param.getName(), param.getValue());
            }
        }
        return builder.build().toUriString();
    }

    private HttpHeaders buildHeaders(Map<String, String> headerParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        for (Map.Entry<String, String> entry : defaultHeaders.entrySet()) {
            headers.add(entry.getKey(), entry.getValue());
        }
        if (headerParams != null) {
            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                headers.add(entry.getKey(), entry.getValue());
            }
        }
        return headers;
    }

    // Other methods that use replaceAll now use replace
    public String sanitizePath(String path) {
        return path.replaceAll("//+", "/");
    }
}
