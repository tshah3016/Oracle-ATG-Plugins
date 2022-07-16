package com.atg.core.http;

import atg.nucleus.ServiceException;
import com.atg.core.http.annotations.Service;
import com.atg.core.http.service.DuplicateDefinitionException;
import com.atg.core.http.service.Type;
import com.atg.core.http.service.rest.impl.RestGatewayManager;
import com.atg.http.ApplePaymentManager;
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
class RestServiceGatewayTest {

    RestServiceGateway restGateway;

    @BeforeEach
    void setUp() {
        restGateway = new RestServiceGateway();
        restGateway.setDefaultConnectionTimeout(1000);
        restGateway.setDefaultMaxConnections(5);
        restGateway.setDefaultMaxConnectionsPerRoute(5);
        restGateway.setDefaultSocketTimeout(500);
    }

    @AfterEach
    void destroy() {
        restGateway = null;
    }

    @Test
    void Verify_RestGatewayManager_Created_When_GatewayAnnotation_Class_is_Present() throws ServiceException {
        RestServiceGateway spyingGateway = Mockito.spy(restGateway);
        spyingGateway.doStartService();
        assertEquals(spyingGateway.getClassesAnnotatedWithGatewayTypeAnnotation().size(),
                spyingGateway.getRegisteredGateways().size());
    }

    @Test
    void Verify_RestGatewayManager_Throws_DuplicateDefinitionException_When_Duplicate_Gateway_Name_Defined()
            throws ServiceException {
        RestServiceGateway spyingGateway = Mockito.spy(restGateway);
        Set<Class<?>> classes = new HashSet<>();
        classes.add(ApplePaymentManager.class);
        classes.add(DuplicateApplePaymentManager.class);
        Mockito.doReturn(classes).when(spyingGateway).getClassesAnnotatedWithGatewayTypeAnnotation();
        DuplicateDefinitionException exception =
                assertThrows(DuplicateDefinitionException.class, () -> spyingGateway.doStartService());
        assertNotEquals(spyingGateway.getClassesAnnotatedWithGatewayTypeAnnotation().size(),
                spyingGateway.getRegisteredGateways().size());
        assertEquals("AppleRestGateway", spyingGateway.getRegisteredGateways().keySet().stream().findFirst().get());
    }

    @Test
    void When_HttpClientCustomizerAnnotation_Is_Used_Consumer_Is_Added_To_RestGatewayManager() throws ServiceException {
        RestServiceGateway spyingGateway = Mockito.spy(restGateway);
        spyingGateway.doStartService();
        assertEquals(ApplePaymentManager.appleClientCustomizer,
                spyingGateway.getRegisteredGateways().get("AppleRestGateway").getClientCustomizer());
    }

    @Test
    void When_HttpClientCustomizerAnnotation_Is_Used_On_InvalidField_Consumer_Is_Not_Added_To_RestGatewayManager()
            throws ServiceException {
        RestServiceGateway spyingGateway = Mockito.spy(restGateway);
        Mockito.doReturn("duplicate.test").when(spyingGateway).getDefaultPackageToScan();
        spyingGateway.doStartService();
        //Because we are using reflection we still need to test the variable is not set
        assertNotEquals(new DuplicateApplePaymentManager().someCosumer,
                spyingGateway.getRegisteredGateways().get("AppleRestGateway").getClientCustomizer());
    }

    @Test
    void Verify_All_Services_Registered_ToGatewayManager_Are_OF_TYPE_REST() throws ServiceException {
        RestServiceGateway spyingGateway = Mockito.spy(restGateway);
        RestGatewayManager manager = Mockito.spy(RestGatewayManager.builder().gatewayName("AppleRestGateway")
                .defaultConnectionTimeout(100)
                .defaultMaxConnections(5)
                .defaultMaxConnectionsPerRoute(5)
                .defaultSocketTimeout(1000)
                .build());

        Mockito.doReturn(manager).when(spyingGateway).initializeGatewayManager(any());
        ArgumentCaptor<Set<Service>> ac = ArgumentCaptor.forClass(Set.class);
        spyingGateway.doStartService();
        Mockito.verify(manager).registerServices(ac.capture());
        ac.getValue().stream().forEach(service -> assertEquals(Type.REST, service.type()));
    }


}