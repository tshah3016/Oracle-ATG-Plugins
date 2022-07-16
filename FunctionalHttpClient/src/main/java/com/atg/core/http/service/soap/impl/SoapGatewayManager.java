package com.atg.core.http.service.soap.impl;

import com.atg.core.http.annotations.Service;
import com.atg.core.http.client.HttpMethod;
import com.atg.core.http.client.HttpMethodProxy;
import com.atg.core.http.client.RequestUtils;
import com.atg.core.http.service.ServiceRequest;
import com.atg.core.http.service.ServiceResponse;
import com.atg.core.http.service.Type;
import com.atg.core.http.service.impl.ServiceDefinition;
import com.atg.core.http.service.impl.ServiceDefinition.SoapServiceDefinition;
import com.atg.core.http.service.impl.WebserviceGatewayManager;
import lombok.Builder;

import java.util.Optional;

public class SoapGatewayManager
        extends
        WebserviceGatewayManager<SoapServiceDefinition, SoapServiceContext, ServiceResponse.SoapServiceResponse> {

    @Builder
    public SoapGatewayManager(String gatewayName, int defaultMaxConnections, int defaultMaxConnectionsPerRoute,
                              int defaultConnectionTimeout, int defaultSocketTimeout) {
        super(Type.SOAP, gatewayName, defaultMaxConnections, defaultMaxConnectionsPerRoute,
                defaultConnectionTimeout, defaultSocketTimeout);
    }

    @Override
    public Optional<ServiceResponse.SoapServiceResponse> executeService(SoapServiceContext requestContext) {
        ServiceDefinition service = getServices().get(requestContext.getGatewayContext().getServiceName());
        HttpMethodProxy method = getHttpClient().newRequest(service)
                .setURI(RequestUtils.buildUri(requestContext, service))
                .addRequestHeaders(requestContext.getRequestHeaders())
                .createPayLoad(requestContext.getWithRequest(), requestContext.getWithSoapHeader())
                .build();
        return getHttpClient().executeRequest(method, requestContext);
    }

    @Override
    protected SoapServiceDefinition defineService(Service service) {
        return SoapServiceDefinition.builder()
                .name(service.name())
                .type(service.type())
                .protocol(service.protocol())
                .uri(service.uri())
                .hostAndPort(service.hostAndPort())
                .httpMethod(HttpMethod.POST)
                .soapAction(service.soapAction())
                .successResponseCodes(service.successResponseCodes())
                .requestClass((Class<ServiceRequest.SoapServiceRequest>) service.requestClass())
                .responseClass((Class<ServiceResponse.SoapServiceResponse>) service.responseClass())
                .errorClass((Class<ServiceResponse.SoapServiceError>) service.errorClass())
                .build();
    }
}
