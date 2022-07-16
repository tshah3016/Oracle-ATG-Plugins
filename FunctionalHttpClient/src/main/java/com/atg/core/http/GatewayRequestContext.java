package com.atg.core.http;

import atg.nucleus.logging.VariableArgumentApplicationLogging;
import com.atg.core.http.service.ServiceExecutor;
import com.atg.core.http.service.ServiceNotRegisteredException;
import com.atg.core.http.service.ServiceResponse;
import com.atg.core.http.service.Type;
import com.atg.core.http.service.impl.ServiceContext;
import com.atg.core.http.service.impl.ServiceDefinition;
import com.atg.core.http.service.rest.RestRequestContext;
import com.atg.core.http.service.rest.impl.RestGatewayManager;
import com.atg.core.http.service.rest.impl.RestServiceContext;
import com.atg.core.http.service.soap.SoapRequestContext;
import com.atg.core.http.service.soap.impl.SoapGatewayManager;
import com.atg.core.http.service.soap.impl.SoapServiceContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
public abstract class GatewayRequestContext<R extends ServiceContext, S extends ServiceResponse> {

    private static final String LOG_PREFIX = "ServiceGateway -> %s, Service Name -> %s -";
    private static final String INFO_PREFIX = "INFO: ";
    private static final String DEBUG_PREFIX = "DEBUG: ";
    private static final String ERROR_PREFIX = "ERROR: ";


    @NotNull
    @Getter
    protected final ServiceExecutor<R, S> gateway;
    @NotNull
    @Getter
    private final VariableArgumentApplicationLogging logger;
    @Getter
    protected String serviceName;

    public static RestRequestContext createRestGatewayContext(RestGatewayManager gateway,
                                                              VariableArgumentApplicationLogging logger) {
        return new RestGatewayContext(gateway, logger);
    }

    public static SoapRequestContext createSoapGatewayContext(SoapGatewayManager gateway,
                                                              VariableArgumentApplicationLogging logger) {
        return new SoapGatewayContext(gateway, logger);
    }

    public ServiceDefinition getService() {
        return gateway.getService(serviceName);
    }

    public Type getServiceType() {
        return gateway.getGatewayType();
    }

    public void info(String text, Object... o) {
        logger.vlogInfo(INFO_PREFIX +
                String.format(LOG_PREFIX, gateway.getGatewayName(), serviceName) +
                text, o);
    }

    public void debug(String text, Object... o) {
        logger.vlogDebug(DEBUG_PREFIX +
                String.format(LOG_PREFIX, gateway.getGatewayName(), serviceName) +
                text, o);
    }

    public void error(String text, Object... o) {
        logger.vlogError(ERROR_PREFIX +
                String.format(LOG_PREFIX, gateway.getGatewayName(), serviceName) +
                text, o);
    }

    public static class RestGatewayContext
            extends GatewayRequestContext<RestServiceContext, ServiceResponse.RestServiceResponse>
            implements RestRequestContext {

        public RestGatewayContext(RestGatewayManager gateway, VariableArgumentApplicationLogging logger) {
            super(gateway, logger);
        }


        public RestServiceContext.RestServiceContextBuilder createRestRequestFor(String serviceName) {
            this.serviceName = serviceName;
            if (!gateway.isServiceRegistered(serviceName)) {
                throw new ServiceNotRegisteredException(
                        "Service with name " + serviceName + " is not registered to the RestServiceGateway");
            }
            return RestServiceContext.builder(this);
        }
    }

    public static class SoapGatewayContext
            extends GatewayRequestContext<SoapServiceContext, ServiceResponse.SoapServiceResponse>
            implements SoapRequestContext {

        public SoapGatewayContext(SoapGatewayManager gateway,
                                  VariableArgumentApplicationLogging logger) {
            super(gateway, logger);
        }

        public SoapServiceContext.SoapServiceContextBuilder createSoapRequestFor(String serviceName) {
            this.serviceName = serviceName;
            if (!gateway.isServiceRegistered(serviceName)) {
                throw new ServiceNotRegisteredException(
                        "Service with name " + serviceName + " is not registered to the RestServiceGateway");
            }
            return SoapServiceContext.builder(this);
        }
    }


}
