package com.atg.core.http.service.soap.impl;

import com.atg.core.http.GatewayRequestContext;
import com.atg.core.http.service.PathVariableSubstitutor;
import com.atg.core.http.service.RequestHeaders;
import com.atg.core.http.service.ServiceExecutor;
import com.atg.core.http.service.ServiceRequest;
import com.atg.core.http.service.ServiceResponse;
import com.atg.core.http.service.impl.ServiceContext;
import lombok.Builder;
import lombok.Getter;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Getter
public class SoapServiceContext extends ServiceContext {
    private static final BiFunction<SoapServiceContext, ServiceExecutor<SoapServiceContext, ServiceResponse.SoapServiceResponse>, Optional<ServiceResponse.SoapServiceResponse>>
            executor = ((serviceContext, serviceExecutor) -> serviceExecutor.executeService(serviceContext));
    private final ServiceRequest.SoapServiceRequest withRequest;
    private final com.atg.core.http.service.soap.SoapHeader withSoapHeader;

    @Builder(builderMethodName = "internalBuilder")
    public SoapServiceContext(
            GatewayRequestContext gatewayRequestContext, String withURI,
            Consumer<PathVariableSubstitutor> uriPathSubstitutions,
            Consumer<RequestHeaders> requestHeaders,
            BiConsumer<HttpRequestBase, HttpClientContext> customizeRequest,
            BiFunction<org.apache.http.client.methods.CloseableHttpResponse, ServiceContext, ServiceResponse> customizeResponse,
            ServiceRequest.SoapServiceRequest withRequest,
            com.atg.core.http.service.soap.SoapHeader withSoapHeader) {
        super(gatewayRequestContext, withURI, uriPathSubstitutions, requestHeaders, customizeRequest,
                customizeResponse);
        this.withRequest = withRequest;
        this.withSoapHeader = withSoapHeader;
    }

    public static SoapServiceContextBuilder builder(GatewayRequestContext gatewayContext) {
        return internalBuilder().gatewayRequestContext(gatewayContext);
    }

    public Optional<ServiceResponse.SoapServiceResponse> executeRequest() {
        return executor.apply(this, this.getGatewayContext().getGateway());
    }

    public static class SoapServiceContextBuilder {

        private SoapServiceContextBuilder gatewayRequestContext(GatewayRequestContext requestContext) {
            this.gatewayRequestContext = requestContext;
            return this;
        }

        public Optional<ServiceResponse.SoapServiceResponse> executeRequest() {
            return new SoapServiceContext(this.gatewayRequestContext, this.withURI, this.uriPathSubstitutions,
                    this.requestHeaders, this.customizeRequest, this.customizeResponse, this.withRequest,
                    this.withSoapHeader)
                    .executeRequest();
        }
    }
}
