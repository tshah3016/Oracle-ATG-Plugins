package com.atg.core.http.service;

public class ServiceNotRegisteredException extends RuntimeException {
    public ServiceNotRegisteredException(String format) {
        super(format);
    }
}
