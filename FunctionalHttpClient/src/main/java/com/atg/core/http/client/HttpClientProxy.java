package com.atg.core.http.client;

import com.atg.core.http.service.ServiceResponse;
import com.atg.core.http.service.impl.ServiceContext;
import com.atg.core.http.service.impl.ServiceDefinition;

import java.util.Optional;

public interface HttpClientProxy {

    HttpMethodProxyBuilder newRequest(ServiceDefinition service);

    <S extends ServiceResponse> Optional<S> executeRequest(HttpMethodProxy method, ServiceContext requestContext);
}
