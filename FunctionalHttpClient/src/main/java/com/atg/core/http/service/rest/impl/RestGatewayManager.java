package com.atg.core.http.service.rest.impl;

import com.atg.core.http.annotations.Service;
import com.atg.core.http.client.HttpMethodProxy;
import com.atg.core.http.client.RequestUtils;
import com.atg.core.http.service.ServiceRequest;
import com.atg.core.http.service.ServiceResponse;
import com.atg.core.http.service.Type;
import com.atg.core.http.service.impl.ServiceDefinition;
import com.atg.core.http.service.impl.ServiceDefinition.RestServiceDefinition;
import com.atg.core.http.service.impl.WebserviceGatewayManager;
import lombok.Builder;

import java.util.Optional;

public class RestGatewayManager extends
        WebserviceGatewayManager<RestServiceDefinition, RestServiceContext, ServiceResponse.RestServiceResponse> {


    @Builder
    public RestGatewayManager(String gatewayName, int defaultMaxConnections, int defaultMaxConnectionsPerRoute,
                              int defaultConnectionTimeout, int defaultSocketTimeout) {
        super(Type.REST, gatewayName, defaultMaxConnections, defaultMaxConnectionsPerRoute,
                defaultConnectionTimeout, defaultSocketTimeout);
    }

    @Override
    protected RestServiceDefinition defineService(Service service) {
        return RestServiceDefinition.builder()
                .name(service.name())
                .type(service.type())
                .protocol(service.protocol())
                .uri(service.uri())
                .hostAndPort(service.hostAndPort())
                .httpMethod(service.httpMethod())
                .successResponseCodes(service.successResponseCodes())
                .requestClass((Class<ServiceRequest.RestServiceRequest>) service.requestClass())
                .responseClass((Class<ServiceResponse.RestServiceResponse>) service.responseClass())
                .errorClass((Class<ServiceResponse.RestServiceError>) service.errorClass())
                .build();
    }

    @Override
    public Optional<ServiceResponse.RestServiceResponse> executeService(RestServiceContext requestContext) {
        ServiceDefinition service = getServices().get(requestContext.getGatewayContext().getServiceName());
        HttpMethodProxy method = getHttpClient().newRequest(service)
                .setURI(RequestUtils.buildUri(requestContext, service))
                .addRequestHeaders(requestContext.getRequestHeaders())
                .createPayLoad(requestContext.getWithRequestPayload(), null)
                .build();
        return getHttpClient().executeRequest(method, requestContext);
    }
}
