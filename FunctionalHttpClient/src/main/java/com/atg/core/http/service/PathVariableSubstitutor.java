package com.atg.core.http.service;

public interface PathVariableSubstitutor {
    PathVariableMatcher addVariableValues(String variableName, String variableValue);
}
