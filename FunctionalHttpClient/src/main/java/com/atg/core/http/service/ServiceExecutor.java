package com.atg.core.http.service;


import com.atg.core.http.service.impl.ServiceContext;
import com.atg.core.http.service.impl.ServiceDefinition;

import java.util.Optional;

public interface ServiceExecutor<R extends ServiceContext, S extends ServiceResponse> {
    String getGatewayName();

    Optional<S> executeService(R requestContext);

    ServiceDefinition getService(String serviceName);

    boolean isServiceRegistered(String serviceName);

    Type getGatewayType();
}
