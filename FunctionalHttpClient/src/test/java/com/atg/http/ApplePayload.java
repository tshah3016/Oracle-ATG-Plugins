package com.atg.http;

import com.atg.core.http.service.ServiceRequest;
import com.atg.core.http.service.ServiceResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApplePayload implements
        ServiceRequest.RestServiceRequest, ServiceResponse.RestServiceResponse, ServiceRequest.SoapServiceRequest,
        ServiceResponse.SoapServiceResponse, ServiceResponse.RestServiceError, ServiceResponse.SoapServiceError {
    private final String orderId;
    private final double orderAmount;

}
