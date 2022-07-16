package com.atg.http;

import atg.nucleus.GenericService;
import com.atg.core.http.annotations.HttpClientCustomizer;
import com.atg.core.http.annotations.RestGateway;
import com.atg.core.http.annotations.Service;
import com.atg.core.http.annotations.SoapGateway;
import com.atg.core.http.client.HttpMethod;
import com.atg.core.http.service.Type;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.function.Consumer;

@RestGateway(name = "AppleRestGateway")
@SoapGateway(name = "SomeSoapGateway")
public class ApplePaymentManager extends GenericService {
    @HttpClientCustomizer
    public static final Consumer<HttpClientBuilder> appleClientCustomizer =
            (httpClientBuilder -> httpClientBuilder.disableCookieManagement());

    @Service(name = "debitRest", type = Type.REST, hostAndPort = "applepay.com",
            httpMethod = HttpMethod.POST,
            requestClass = ApplePayload.class,
            responseClass = ApplePayload.class,
            errorClass = ApplePayload.class)
    public void debitApplePay(ApplePayload payload) {

    }

    @Service(name = "debitSoap", type = Type.SOAP, hostAndPort = "applepay.com", uri = "/apple/pay/debit",
            httpMethod = HttpMethod.POST,
            requestClass = ApplePayload.class,
            responseClass = ApplePayload.class,
            errorClass = ApplePayload.class)
    public void debitApplePayBySoap(ApplePayload payload) {

    }
}
