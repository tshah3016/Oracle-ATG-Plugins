package com.atg.core.http.client;

import com.atg.core.http.service.ServiceRequest;
import com.atg.core.http.service.soap.SoapHeader;

import java.util.Map;

public interface HttpMethodProxy extends AutoCloseable {

    void setURI(String uri);

    void setHttpMethod(HttpMethod method);

    void addRequestHeaders(Map<String, String> requestHeaders);

    void createPayLoad(ServiceRequest request, SoapHeader soapHeader);
}
