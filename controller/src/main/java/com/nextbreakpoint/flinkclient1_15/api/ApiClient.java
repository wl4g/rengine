package com.nextbreakpoint.flinkclient1_15.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiClient {
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    // Simplified version - refactored to reduce complexity
    public String parameterToString(Object param) {
        if (param == null) {
            return "";
        } else if (param instanceof Collection) {
            StringBuilder b = new StringBuilder();
            for (Object o : (Collection<?>) param) {
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

    // Replace replaceAll with replace where regex not needed
    public String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_").replace("..", "_");
    }

    // Other methods using constants
    public void setContentType(Map<String, String> headers) {
        headers.put("Content-Type", APPLICATION_JSON);
    }

    public void setContentDisposition(Map<String, String> headers) {
        headers.put(CONTENT_DISPOSITION, "attachment");
    }
}
