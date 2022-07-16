package com.atg.core.http;

import atg.nucleus.ServiceException;
import com.atg.core.http.annotations.Service;
import com.atg.core.http.service.DuplicateDefinitionException;
import com.atg.core.http.service.Type;
import com.atg.core.http.service.soap.impl.SoapGatewayManager;
import com.atg.http.AppleSoapPaymentManager;
import duplicate.test.DuplicateApplePaymentManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SoapServiceGatewayTest {

    SoapServiceGateway soapGateway;

    @BeforeEach
    void setUp() {
        soapGateway = new SoapServiceGateway();
        soapGateway.setDefaultConnectionTimeout(1000);
        soapGateway.setDefaultMaxConnections(5);
        soapGateway.setDefaultMaxConnectionsPerRoute(5);
        soapGateway.setDefaultSocketTimeout(500);
    }

    @AfterEach
    void destroy() {
        soapGateway = null;
    }

    @Test
    void VerifyRestGatewayManagerCreatedWhenGatewayAnnotationClassPresent() throws ServiceException {
        SoapServiceGateway spyingGateway = Mockito.spy(soapGateway);
        spyingGateway.doStartService();
        assertEquals(spyingGateway.getClassesAnnotatedWithGatewayTypeAnnotation().size(),
                spyingGateway.getRegisteredGateways().size());
        assertEquals("AppleSoapGateway", spyingGateway.getRegisteredGateways().keySet().stream().findFirst().get());
    }

    @Test
    void VerifyRestGatewayManagerThrowsDuplicateDefinitionExceptionWhenDuplicateGatewayNameDefined()
            throws ServiceException {
        SoapServiceGateway spyingGateway = Mockito.spy(soapGateway);
        Set<Class<?>> classes = new HashSet<>();
        classes.add(AppleSoapPaymentManager.class);
        classes.add(DuplicateApplePaymentManager.class);
        Mockito.doReturn(classes).when(spyingGateway).getClassesAnnotatedWithGatewayTypeAnnotation();
        DuplicateDefinitionException exception =
                assertThrows(DuplicateDefinitionException.class, () -> spyingGateway.doStartService());
        assertNotEquals(spyingGateway.getClassesAnnotatedWithGatewayTypeAnnotation().size(),
                spyingGateway.getRegisteredGateways().size());
        assertEquals("AppleSoapGateway", spyingGateway.getRegisteredGateways().keySet().stream().findFirst().get());
    }

    @Test
    void WhenHttpClientCustomizerAnnotationIsUsedConsumerIsAddedToRestGatewayManager() throws ServiceException {
        SoapServiceGateway spyingGateway = Mockito.spy(soapGateway);
        spyingGateway.doStartService();
        assertEquals(AppleSoapPaymentManager.appleClientCustomizer,
                spyingGateway.getRegisteredGateways().get("AppleSoapGateway").getClientCustomizer());
    }

    @Test
    void WhenHttpClientCustomizerAnnotationIsUsedOnInvalidFieldConsumerIsNotAddedToRestGatewayManager()
            throws ServiceException {
        SoapServiceGateway spyingGateway = Mockito.spy(soapGateway);
        Mockito.doReturn("duplicate.test").when(spyingGateway).getDefaultPackageToScan();
        spyingGateway.doStartService();
        //Because we are using reflection we still need to test the variable is not set
        assertNotEquals(new DuplicateApplePaymentManager().someCosumer,
                spyingGateway.getRegisteredGateways().get("AppleSoapGateway").getClientCustomizer());
    }

    @Test
    void Verify_All_Services_Registered_ToGatewayManager_Are_Of_Type_Soap() throws ServiceException {
        SoapServiceGateway spyingGateway = Mockito.spy(soapGateway);
        SoapGatewayManager manager = Mockito.spy(SoapGatewayManager.builder().gatewayName("AppleSoapGateway")
                .defaultConnectionTimeout(100)
                .defaultMaxConnections(5)
                .defaultMaxConnectionsPerRoute(5)
                .defaultSocketTimeout(1000)
                .build());

        Mockito.doReturn(manager).when(spyingGateway).initializeGatewayManager(any());
        ArgumentCaptor<Set<Service>> ac = ArgumentCaptor.forClass(Set.class);
        spyingGateway.doStartService();
        Mockito.verify(manager).registerServices(ac.capture());
        ac.getValue().stream().forEach(service -> assertEquals(Type.SOAP, service.type()));
    }


}