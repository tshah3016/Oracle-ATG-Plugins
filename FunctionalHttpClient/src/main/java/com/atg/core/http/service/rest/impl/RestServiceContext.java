package com.atg.core.http.service.rest.impl;

import com.atg.core.http.GatewayRequestContext;
import com.atg.core.http.service.PathVariableSubstitutor;
import com.atg.core.http.service.RequestHeaders;
import com.atg.core.http.service.ServiceExecutor;
import com.atg.core.http.service.ServiceRequest;
import com.atg.core.http.service.ServiceResponse;
import com.atg.core.http.service.impl.ServiceContext;
import lombok.Builder;
import lombok.Getter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Getter
public class RestServiceContext extends ServiceContext {
    private static final BiFunction<RestServiceContext, ServiceExecutor<RestServiceContext, ServiceResponse.RestServiceResponse>, Optional<ServiceResponse.RestServiceResponse>>
            executor = ((serviceContext, serviceExecutor) -> serviceExecutor.executeService(serviceContext));
    private final ServiceRequest.RestServiceRequest withRequestPayload;

    @Builder(builderMethodName = "internalBuilder")
    public RestServiceContext(
            GatewayRequestContext gatewayRequestContext, String withURI,
            Consumer<PathVariableSubstitutor> uriPathSubstitutions,
            Consumer<RequestHeaders> requestHeaders,
            BiConsumer<HttpRequestBase, HttpClientContext> customizeRequest,
            BiFunction<CloseableHttpResponse, ServiceContext, ServiceResponse> customizeResponse,
            ServiceRequest.RestServiceRequest withRequestPayload) {
        super(gatewayRequestContext, withURI, uriPathSubstitutions, requestHeaders, customizeRequest,
                customizeResponse);
        this.withRequestPayload = withRequestPayload;
    }

    public static RestServiceContextBuilder builder(GatewayRequestContext gatewayContext) {
        return internalBuilder().gatewayRequestContext(gatewayContext);
    }

    public Optional<ServiceResponse.RestServiceResponse> executeRequest() {
        return executor.apply(this, this.getGatewayContext().getGateway());
    }


    public static class RestServiceContextBuilder {

        private RestServiceContextBuilder gatewayRequestContext(GatewayRequestContext requestContext) {
            this.gatewayRequestContext = requestContext;
            return this;
        }

        //never invoke this
        private RestServiceContext build() {
            return null;
        }

        public Optional<ServiceResponse.RestServiceResponse> executeRequest() {
            return new RestServiceContext(this.gatewayRequestContext, this.withURI, this.uriPathSubstitutions,
                    this.requestHeaders, this.customizeRequest, this.customizeResponse, this.withRequestPayload)
                    .executeRequest();
        }
    }
}

