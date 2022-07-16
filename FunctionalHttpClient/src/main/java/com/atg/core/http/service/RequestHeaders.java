package com.atg.core.http.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> requestHeaderMap;

    public RequestHeaders() {
        requestHeaderMap = new HashMap<>();
    }

    public RequestHeaders addRequestHeader(String key, String value) {
        requestHeaderMap.put(key, value);
        return this;
    }

    public Map<String, String> getAllRequestHeaders() {
        return Collections.unmodifiableMap(requestHeaderMap);
    }
}
