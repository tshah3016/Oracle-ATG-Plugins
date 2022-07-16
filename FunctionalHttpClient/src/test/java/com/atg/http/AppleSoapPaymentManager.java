package com.atg.http;

import atg.nucleus.GenericService;
import com.atg.core.http.SoapServiceGateway;
import com.atg.core.http.annotations.HttpClientCustomizer;
import com.atg.core.http.annotations.Service;
import com.atg.core.http.annotations.SoapGateway;
import com.atg.core.http.client.HttpMethod;
import com.atg.core.http.service.ServiceResponse;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Optional;
import java.util.function.Consumer;

import static com.atg.core.http.service.Type.SOAP;

@SoapGateway(name = "AppleSoapGateway")
public class AppleSoapPaymentManager extends GenericService {
    @HttpClientCustomizer
    public static final Consumer<HttpClientBuilder> appleClientCustomizer =
            (httpClientBuilder -> httpClientBuilder.disableCookieManagement());

    @Service(name = "debit", type = SOAP, hostAndPort = "applepay.com",
            httpMethod = HttpMethod.POST,
            requestClass = ApplePayload.class,
            responseClass = ApplePayload.class,
            errorClass = ApplePayload.class)
    public void debitApplePay(ApplePayload payload) {
        Optional<ServiceResponse.SoapServiceResponse> r = SoapServiceGateway.use("AppleAppleRestGatewayGateway")
                .createSoapRequestFor("debitPay")
                .withURI("/apple/pay/debit/{accountId}")
                .requestHeaders(requestHeaders -> requestHeaders
                        .addRequestHeader("AUTH", "Authorization:12345")
                        .addRequestHeader("Header 2", "Value3"))
                .withRequest(payload)
                .uriPathSubstitutions((pathSubstitutor) -> {
                    pathSubstitutor.addVariableValues("accountId", "123456")
                            .addVariableValues("value2", "333333");
                })
                .customizeRequest((httpRequest, context) -> {
                    httpRequest.setHeader("Something", "SomeValue");
                    context.setAttribute("SomeParam", "SomeParamValue");
                })
                .customizeResponse((response, serviceContext) -> {
                    response.getEntity().toString();
                    return payload;
                })
                .executeRequest();
    }
}
