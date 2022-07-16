package com.atg.core.http.service.rest;


import com.atg.core.http.service.rest.impl.RestServiceContext;

public interface RestRequestContext {
    RestServiceContext.RestServiceContextBuilder createRestRequestFor(String serviceName);
}
