package com.atg.core.http.service;

public interface ServiceResponse {
    interface ServiceError extends ServiceResponse {
    }

    interface RestServiceResponse extends ServiceResponse {
    }

    interface SoapServiceResponse extends ServiceResponse {
    }

    interface RestServiceError extends RestServiceResponse, ServiceError {
    }

    interface SoapServiceError extends SoapServiceResponse, ServiceError {
    }
}
