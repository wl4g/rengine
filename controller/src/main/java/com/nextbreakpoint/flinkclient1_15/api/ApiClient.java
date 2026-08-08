package com.nextbreakpoint.flinkclient1_15.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiClient {
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";

    // ... existing fields and methods ...

    public String selectHeaderContentType(String[] contentTypes) {
        if (contentTypes.length == 0) {
            return APPLICATION_JSON;
        }
        for (String contentType : contentTypes) {
            if (contentType.equals(APPLICATION_JSON)) {
                return contentType;
            }
        }
        return contentTypes[0];
    }

    public String selectHeaderAccept(String[] accepts) {
        if (accepts.length == 0) {
            return null;
        }
        for (String accept : accepts) {
            if (accept.equals(APPLICATION_JSON)) {
                return accept;
            }
        }
        return String.join(",", accepts);
    }

    public String parameterToString(Object param) {
        if (param == null) {
            return "";
        } else if (param instanceof Collection<?>) {
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

    public String escapeString(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // ... rest of the class ...
}
