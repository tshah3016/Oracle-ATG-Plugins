package com.atg.core.http.service;

public interface ServiceRequest {

    interface RestServiceRequest extends ServiceRequest {
    }

    interface SoapServiceRequest extends ServiceRequest {
    }
}
