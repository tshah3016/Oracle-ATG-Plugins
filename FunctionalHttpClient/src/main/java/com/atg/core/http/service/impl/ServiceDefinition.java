package com.atg.core.http.service.impl;

import com.atg.core.http.client.HttpMethod;
import com.atg.core.http.service.ServiceRequest;
import com.atg.core.http.service.ServiceResponse;
import com.atg.core.http.service.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static com.atg.core.http.service.ServiceRequest.RestServiceRequest;
import static com.atg.core.http.service.ServiceRequest.SoapServiceRequest;
import static com.atg.core.http.service.ServiceResponse.RestServiceError;
import static com.atg.core.http.service.ServiceResponse.RestServiceResponse;
import static com.atg.core.http.service.ServiceResponse.SoapServiceError;
import static com.atg.core.http.service.ServiceResponse.SoapServiceResponse;

@AllArgsConstructor
@Getter
public abstract class ServiceDefinition<REQ extends ServiceRequest, RES extends ServiceResponse, ERR extends ServiceResponse.ServiceError> {
    private final String name;
    private final Type type;
    private final String uri;
    private final String protocol;
    private final String hostAndPort;
    private final HttpMethod httpMethod;
    private final Class<REQ> requestClass;
    private final Class<RES> responseClass;
    private final Class<ERR> errorClass;
    private final int[] successResponseCodes;

    @Getter
    public static class RestServiceDefinition
            extends ServiceDefinition<RestServiceRequest, RestServiceResponse, RestServiceError> {
        @Builder
        public RestServiceDefinition(String name, Type type, String uri, String protocol, String hostAndPort,
                                     HttpMethod httpMethod,
                                     Class<RestServiceRequest> requestClass,
                                     Class<RestServiceResponse> responseClass,
                                     Class<RestServiceError> errorClass, String soapAction,
                                     int[] successResponseCodes) {
            super(name, type, uri, protocol, hostAndPort, httpMethod, requestClass, responseClass, errorClass,
                    successResponseCodes);
        }
    }


    @Getter
    public static class SoapServiceDefinition
            extends ServiceDefinition<SoapServiceRequest, SoapServiceResponse, SoapServiceError> {
        private final String soapAction;

        @Builder
        public SoapServiceDefinition(String name, Type type, String uri, String protocol, String hostAndPort,
                                     HttpMethod httpMethod,
                                     Class<SoapServiceRequest> requestClass,
                                     Class<SoapServiceResponse> responseClass,
                                     Class<SoapServiceError> errorClass, String soapAction,
                                     int[] successResponseCodes) {
            super(name, type, uri, protocol, hostAndPort, httpMethod, requestClass, responseClass, errorClass,
                    successResponseCodes);
            this.soapAction = soapAction;
        }
    }
}
