package com.atg.core.http.client;

import com.atg.core.http.service.RequestHeaders;
import com.atg.core.http.service.ServiceRequest;
import com.atg.core.http.service.soap.SoapHeader;

import java.util.function.Consumer;

public interface HttpMethodProxyBuilder {

    HttpMethodProxyBuilder setURI(String uri);

    HttpMethodProxyBuilder addRequestHeaders(Consumer<RequestHeaders> requestHeaders);

    <T extends ServiceRequest> HttpMethodProxyBuilder createPayLoad(T request, SoapHeader soapHeader);

    HttpMethodProxy build();
}
