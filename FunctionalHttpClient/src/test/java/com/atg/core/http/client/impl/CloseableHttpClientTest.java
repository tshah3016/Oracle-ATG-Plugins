package com.atg.core.http.client.impl;

import com.atg.core.http.client.RequestExecutionException;
import com.atg.core.http.service.ServiceResponse;
import com.atg.core.http.service.impl.ServiceContext;
import lombok.SneakyThrows;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.function.BiConsumer;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloseableHttpClientTest {

    @Mock
    private org.apache.http.impl.client.CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse response;
    @InjectMocks
    private CloseableHttpClient client;
    @Mock
    CloseableHttpMethod proxy;
    @Mock
    ServiceContext context;
    @Mock
    StatusLine statusLine;

    @Mock
    private ServiceResponse serviceResponse;
    @Mock
    BiConsumer<HttpRequestBase, HttpClientContext> customizer;

    @SneakyThrows
    @Test
    void verify_customize_request_Consumer_is_invoked_when_execute_request_is_called() {
        //Given
        client=Mockito.spy(client);
        doNothing().when(context).info(any(),any());
        Mockito.when(context.getCustomizeRequest()).thenReturn(customizer);
        Mockito.when(httpClient.execute(any(),(HttpClientContext)any())).thenReturn(response);
        Mockito.when(response.getStatusLine()).thenReturn(statusLine);
        Mockito.when(statusLine.getStatusCode()).thenReturn(200);
        Mockito.doReturn(serviceResponse).when(client).parseResponse(any(),any());
        //When
        client.executeRequest(proxy,context);
        //Then
       Mockito.verify(customizer,times(1)).accept(any(),any());

    }

    @SneakyThrows
    @Test
    void verify_RequestExecution_Exception_is_thrown_when_http_client_throws_exception() {
        //Given
        client=Mockito.spy(client);
        Mockito.when(httpClient.execute(any(),(HttpClientContext)any())).thenThrow(IOException.class);

        //When
        assertThrows(RequestExecutionException.class,()->client.executeRequest(proxy,context));

        //Then
        Mockito.verify(client,never()).parseResponse(any(),any());
        Mockito.verify(proxy,times(1)).close();
    }

    @Test
    void populateResponseForServiceType() {
    }
}