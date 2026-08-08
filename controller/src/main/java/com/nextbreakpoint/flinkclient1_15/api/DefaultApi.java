package com.nextbreakpoint.flinkclient1_15.api;

import com.nextbreakpoint.flinkclient1_15.ApiClient;

import com.nextbreakpoint.flinkclient1_15.model.Error;
import com.nextbreakpoint.flinkclient1_15.model.JarUploadResponse;
import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DefaultApi {
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

    /**
     * Upload a jar to the cluster
     * 
     *
     * @param jarfile (required) The jar file to upload
     * @return JarUploadResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public JarUploadResponse uploadJar(MultipartFile jarfile) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'jarfile' is set
        if (jarfile == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'jarfile' when calling uploadJar");
        }
        
        String path = apiClient.expandPath("/jars/upload", new HashMap<>());
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        if (jarfile != null) {
            try {
                Path tempFile = Files.createTempFile("upload", jarfile.getOriginalFilename());
                Files.copy(jarfile.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
                formParams.add("jarfile", new FileSystemResource(tempFile.toFile()));
            } catch (Exception e) {
                throw new RestClientException("Error processing file upload", e);
            }
        }
        
        final String[] localVarAccepts = { "application/json" };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { "multipart/form-data" };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, cookieParams, formParams, localVarAccept, localVarContentType, localVarAuthNames, JarUploadResponse.class);
    }
}
