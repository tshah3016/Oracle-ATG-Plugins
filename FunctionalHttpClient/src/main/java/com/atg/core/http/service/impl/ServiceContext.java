package com.atg.core.http.service.impl;

import com.atg.core.http.GatewayRequestContext;
import com.atg.core.http.service.PathVariableSubstitutor;
import com.atg.core.http.service.RequestHeaders;
import com.atg.core.http.service.ServiceResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@AllArgsConstructor
public abstract class ServiceContext {

    @Getter
    @NotNull
    private final GatewayRequestContext gatewayContext;
    @Getter
    private final String withURI;
    @Getter
    private final Consumer<PathVariableSubstitutor> uriPathSubstitutions;
    @Getter
    private final Consumer<RequestHeaders> requestHeaders;
    @Getter
    private final java.util.function.BiConsumer<org.apache.http.client.methods.HttpRequestBase, org.apache.http.client.protocol.HttpClientContext>
            customizeRequest;
    @Getter
    private final BiFunction<org.apache.http.client.methods.CloseableHttpResponse, ServiceContext, ServiceResponse>
            customizeResponse;

    public abstract <RES extends ServiceResponse> Optional<RES> executeRequest();

    public void info(String text, Object... o) {
        gatewayContext.info(text, o);
    }

    public void debug(String text, Object... o) {
        gatewayContext.debug(text, o);
    }

    public void error(String text, Object... o) {
        gatewayContext.error(text, o);
    }
}


