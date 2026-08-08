package com.nextbreakpoint.flinkclient1_15.api;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

public class ApiClient {
    private RestTemplate restTemplate;
    private String basePath = "http://localhost:8081";

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String APPLICATION_JSON = "application/json";

    public ApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public ApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String expandPath(String path, Map<String, Object> pathParams) {
        String expandedPath = path;
        for (Map.Entry<String, Object> entry : pathParams.entrySet()) {
            expandedPath = expandedPath.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return expandedPath;
    }

    public List<MediaType> selectHeaderAccept(String[] accepts) {
        if (accepts == null || accepts.length == 0) {
            return null;
        }
        List<MediaType> result = new java.util.ArrayList<>();
        for (String accept : accepts) {
            result.add(MediaType.parseMediaType(accept));
        }
        return result;
    }

    public MediaType selectHeaderContentType(String[] contentTypes) {
        if (contentTypes == null || contentTypes.length == 0) {
            return MediaType.APPLICATION_JSON;
        }
        for (String contentType : contentTypes) {
            if (contentType.equals(APPLICATION_JSON)) {
                return MediaType.APPLICATION_JSON;
            }
        }
        return MediaType.parseMediaType(contentTypes[0]);
    }

    public <T> T invokeAPI(String path, HttpMethod method, MultiValueMap<String, String> queryParams, Object body, HttpHeaders headerParams, MultiValueMap<String, String> cookieParams, MultiValueMap<String, Object> formParams, List<MediaType> accept, MediaType contentType, String[] authNames, Class<T> returnType) throws RestClientException {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(basePath + path);
        if (queryParams != null) {
            builder.queryParams(queryParams);
        }

        HttpHeaders headers = new HttpHeaders();
        if (headerParams != null) {
            headers.putAll(headerParams);
        }
        if (contentType != null) {
            headers.setContentType(contentType);
        }
        if (accept != null && !accept.isEmpty()) {
            headers.setAccept(accept);
        }

        org.springframework.http.HttpEntity<Object> requestEntity;
        if (formParams != null && !formParams.isEmpty()) {
            MultiValueMap<String, Object> formBody = new LinkedMultiValueMap<>();
            formBody.putAll(formParams);
            requestEntity = new org.springframework.http.HttpEntity<>(formBody, headers);
        } else {
            requestEntity = new org.springframework.http.HttpEntity<>(body, headers);
        }

        ResponseEntity<T> response = restTemplate.exchange(builder.build().toUriString(), method, requestEntity, returnType);
        return response.getBody();
    }
}
