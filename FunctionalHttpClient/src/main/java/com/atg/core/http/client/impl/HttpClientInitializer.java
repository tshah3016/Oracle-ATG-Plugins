package com.atg.core.http.client.impl;

import com.atg.core.http.client.HttpClientProxy;
import com.atg.core.http.service.impl.WebserviceGatewayManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

public class HttpClientInitializer {

    public static <T extends WebserviceGatewayManager> HttpClientProxy buildClosableHttpClient(
            T webServiceConfigurationManager) {

        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        webServiceConfigurationManager.getClientCustomizer().accept(httpClientBuilder);

        return new CloseableHttpClient(httpClientBuilder.build());

    }
}
