package com.atg.core.http.client;

import org.apache.http.impl.client.HttpClientBuilder;

import java.util.function.Consumer;

public interface ClientCustomizer {

    Consumer<HttpClientBuilder> modifyHttpClient();
}
