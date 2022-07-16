package com.atg.core.http.service.soap;

import com.atg.core.http.service.soap.impl.SoapServiceContext;

public interface SoapRequestContext {
    SoapServiceContext.SoapServiceContextBuilder createSoapRequestFor(String serviceName);
}
