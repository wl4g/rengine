/*
 * Flink REST Client
 * ...
 */
package com.nextbreakpoint.flinkclient1_15.api;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

public class ApiClient {
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    // ... rest of the class ...

    public String serialize(Object obj) throws ApiException {
        try {
            if (obj != null) {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(obj);
                return json;
            }
            return null;
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }

    // ... other methods ...

    private String buildUrl(String path, List<Pair> queryParams) throws ApiException {
        // ...
    }

    private String parameterToString(Object param) {
        if (param == null) {
            return "";
        } else if (param instanceof Date) {
            return formatDate((Date) param);
        } else if (param instanceof Collection) {
            StringBuilder b = new StringBuilder();
            for (Object o : (Collection) param) {
                if (b.length() > 0) {
                    b.append(",");
                }
                b.append(String.valueOf(o));
            }
            return b.toString();
        } else {
            return String.valueOf(param);
        }
    }

    private String formatDate(Date date) {
        // ...
    }

    private boolean isJsonMime(String mime) {
        return APPLICATION_JSON.equals(mime) || mime != null && mime.startsWith(APPLICATION_JSON + ";");
    }

    private HttpEntity buildEntity(Object body, String contentType) throws ApiException {
        if (body instanceof byte[]) {
            return new ByteArrayEntity((byte[]) body);
        }
        String json = serialize(body);
        StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        entity.setContentType(contentType != null ? contentType : APPLICATION_JSON);
        return entity;
    }

    private String getContentDisposition(HttpEntity entity) {
        Header header = entity.getContentType();
        if (header != null) {
            String value = header.getValue();
            if (value != null && value.contains(CONTENT_DISPOSITION)) {
                return value.substring(value.indexOf(CONTENT_DISPOSITION) + CONTENT_DISPOSITION.length() + 1);
            }
        }
        return null;
    }

    // ... rest of the file ...
}
