package com.atg.core.http.service.impl;

import com.atg.core.http.annotations.Service;
import com.atg.core.http.annotations.util.AnnotationUtils;
import com.atg.core.http.client.HttpClientProxy;
import com.atg.core.http.client.impl.HttpClientInitializer;
import com.atg.core.http.service.DuplicateDefinitionException;
import com.atg.core.http.service.ServiceExecutor;
import com.atg.core.http.service.ServiceResponse;
import com.atg.core.http.service.Type;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public abstract class WebserviceGatewayManager<SD extends ServiceDefinition, R extends ServiceContext, S extends ServiceResponse>
        implements ServiceExecutor<R, S> {

    private final int defaultMaxConnections, defaultMaxConnectionsPerRoute, defaultConnectionTimeout,
            defaultSocketTimeout;

    @Getter
    @Setter
    private Type gatewayType;
    @Getter
    @Setter
    private String gatewayName;
    @Getter
    private Consumer<HttpClientBuilder> clientCustomizer;
    @Getter
    private Map<String, SD> services;
    @Getter
    private HttpClientProxy httpClient;

    public WebserviceGatewayManager(Type gatewayType, String gatewayName, int defaultMaxConnections,
                                    int defaultMaxConnectionsPerRoute,
                                    int defaultConnectionTimeout, int defaultSocketTimeout) {
        this.gatewayType = gatewayType;
        this.gatewayName = gatewayName;
        this.defaultMaxConnections = defaultMaxConnections;
        this.defaultMaxConnectionsPerRoute = defaultMaxConnectionsPerRoute;
        this.defaultConnectionTimeout = defaultConnectionTimeout;
        this.defaultSocketTimeout = defaultSocketTimeout;

    }

    protected Consumer<HttpClientBuilder> configureDefaultClient() {
        //Maybe make the defaults configurable in a properties file?
        return (httpClientBuilder -> {
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
            connectionManager.setDefaultMaxPerRoute(defaultMaxConnectionsPerRoute);
            connectionManager.setMaxTotal(defaultMaxConnections);
            RequestConfig.Builder connectionConfig = RequestConfig.custom();
            connectionConfig.setConnectTimeout(defaultConnectionTimeout);
            connectionConfig.setSocketTimeout(defaultSocketTimeout);
            httpClientBuilder.setConnectionManager(connectionManager);
            httpClientBuilder.setDefaultRequestConfig(connectionConfig.build());
        });
    }

    public void registerServices(Set<Service> services) {
        Collection<String> duplicateServiceNames =
                AnnotationUtils.getDuplicateAnnotationsByCriteria(services, service -> service.name());
        if (!duplicateServiceNames.isEmpty()) {
            //Replace Sysout with Logger
            duplicateServiceNames.stream()
                    .forEach(duplicate -> System.out.println("Duplicate ServiceName found " + duplicate));
            throw new DuplicateDefinitionException("Error during Service Registration. Duplicate Services defined");
        }
        this.services = services.stream()
                .collect(Collectors.toMap(service -> service.name(), this::defineService));
        initializeServices();
    }

    protected abstract SD defineService(Service service);

    private void initializeServices() {
        if (clientCustomizer == null) {
            clientCustomizer = configureDefaultClient();
        }
        httpClient = HttpClientInitializer.buildClosableHttpClient(this);
    }

    public void addHttpClientCustomizer(Consumer<HttpClientBuilder> modifyHttpClient) {
        this.clientCustomizer = modifyHttpClient;
    }

    public ServiceDefinition getService(String serviceName) {
        return services.get(serviceName);
    }

    public boolean isServiceRegistered(String serviceName) {
        return getServices().containsKey(serviceName);
    }


}
