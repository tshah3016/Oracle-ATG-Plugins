package com.atg.core.http.service;

import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

public class PathVariableMatcher implements PathVariableSubstitutor {
    private final Map<String, String> variableMap;

    public PathVariableMatcher() {
        this.variableMap = new HashMap<>();
    }

    public PathVariableMatcher addVariableValues(String variableName, String variableValue) {
        variableMap.put(variableName, variableValue);
        return this;
    }

    public String substituteUrlPathVariables(String url) {
        StringSubstitutor sub = new StringSubstitutor(variableMap, "{", "}");
        return sub.replace(url);
    }
}
