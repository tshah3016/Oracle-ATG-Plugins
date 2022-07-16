package com.atg.core.http.service.soap.impl;

import com.atg.core.http.annotations.Service;
import com.atg.core.http.client.HttpMethod;
import com.atg.core.http.service.DuplicateDefinitionException;
import com.atg.core.http.service.Type;
import com.atg.core.http.service.impl.ServiceDefinition;
import com.atg.http.ApplePayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class SoapGatewayManagerTest {
    @InjectMocks
    SoapGatewayManager soapGatewayManager = new SoapGatewayManager("TestGateway", 5, 5, 1000, 1000);
    @Mock
    Service attributeDefinitionTestService;

    @Mock
    Service service1;

    @Mock
    Service service2;

    @Test
    public void Verify_All_Service_Attributes_Are_Set_when_service_is_defined() {
        int[] successCodes = {200, 201, 204};
        String uri = "/abc/{param1}";
        HttpMethod get = HttpMethod.GET;
        String host = "www.abc.com";
        String name = "TestService";
        String protocol = "https";
        String soapHeader = "SoapAction";
        Type rest = Type.REST;
        Class<ApplePayload> requestPayload = ApplePayload.class;
        Class<ApplePayload> response = ApplePayload.class;
        Class<ApplePayload> error = ApplePayload.class;
        //Given
        Mockito.when(attributeDefinitionTestService.hostAndPort()).thenReturn(host);
        Mockito.when(attributeDefinitionTestService.name()).thenReturn(name);
        Mockito.when(attributeDefinitionTestService.protocol()).thenReturn(protocol);
        Mockito.when(attributeDefinitionTestService.type()).thenReturn(rest);
        Mockito.when(attributeDefinitionTestService.successResponseCodes()).thenReturn(successCodes);
        Mockito.when(attributeDefinitionTestService.uri()).thenReturn(uri);
        Mockito.when(attributeDefinitionTestService.soapAction()).thenReturn(soapHeader);
        doReturn(requestPayload).when(attributeDefinitionTestService).requestClass();
        doReturn(response).when(attributeDefinitionTestService).responseClass();
        doReturn(error).when(attributeDefinitionTestService).errorClass();
        //when
        ServiceDefinition.SoapServiceDefinition definition =
                soapGatewayManager.defineService(attributeDefinitionTestService);
        //then
        assertEquals(successCodes, definition.getSuccessResponseCodes());
        assertEquals(uri, definition.getUri());
        assertNotEquals(get, definition.getHttpMethod());
        assertEquals(HttpMethod.POST, definition.getHttpMethod());
        assertEquals(host, definition.getHostAndPort());
        assertEquals(name, definition.getName());
        assertEquals(protocol, definition.getProtocol());
        assertEquals(rest, definition.getType());
        assertEquals(soapHeader, definition.getSoapAction());
        assertEquals(requestPayload, definition.getRequestClass());
        assertEquals(response, definition.getResponseClass());
        assertEquals(error, definition.getErrorClass());
    }

    @Test
    public void verify_services_are_registered_to_gateway_manager_when_no_duplicate_services_present() {
        String name = "ValidService1";
        String name1 = "ValidService2";
        //Given
        Mockito.when(service1.name()).thenReturn(name);

        Mockito.when(service2.name()).thenReturn(name1);

        Set<Service> inputServices = new HashSet<>();
        inputServices.add(service1);
        inputServices.add(service2);

        //when
        soapGatewayManager.registerServices(inputServices);

        //then
        assertEquals(inputServices.size(), soapGatewayManager.getServices().size());
        assertTrue(soapGatewayManager.getServices().keySet()
                .containsAll(inputServices.stream().map(service -> service.name()).collect(Collectors.toList())));
        assertNotNull(soapGatewayManager.getHttpClient());
    }

    @Test
    public void verify_DuplicateDefinitionException_is_thrown_when_duplicate_services_present() {
        String name = "ValidService1";
        //Given
        Mockito.when(service1.name()).thenReturn(name);

        Mockito.when(service2.name()).thenReturn(name);

        Set<Service> inputServices = new HashSet<>();
        inputServices.add(service1);
        inputServices.add(service2);

        //when then
        assertThrows(DuplicateDefinitionException.class, () -> soapGatewayManager.registerServices(inputServices));
        assertNull(soapGatewayManager.getHttpClient());
    }
}