package com.atg.core.http;

import atg.nucleus.Nucleus;
import com.atg.core.http.annotations.RestGateway;
import com.atg.core.http.service.Type;
import com.atg.core.http.service.rest.RestRequestContext;
import com.atg.core.http.service.rest.impl.RestGatewayManager;
import com.google.common.base.Preconditions;

public final class RestServiceGateway extends AbstractWebserviceGateway<RestGateway, RestGatewayManager> {

    public static final String PATH = "/com/atg/core/http/RestServiceGateway";
    private static RestServiceGateway INSTANCE;

    public static RestRequestContext use(String gatewayName) {
        RestGatewayManager restGatewayManager =
                Preconditions.checkNotNull(getInstance().getRegisteredGateways().get(gatewayName),
                        "RestServiceGateway with the name " + gatewayName +
                                " is not registered. Please Ensure the class is placed in the default scan package " +
                                getInstance().getDefaultPackageToScan());
        return GatewayRequestContext.createRestGatewayContext(restGatewayManager, getInstance());
    }

    protected static RestServiceGateway getInstance() {
        if (INSTANCE == null) {
            INSTANCE = (RestServiceGateway) Nucleus.getGlobalNucleus().resolveName(PATH);
        }
        return INSTANCE;
    }

    @Override
    protected Class<RestGateway> gatewayTypeAnnotationClass() {
        return RestGateway.class;
    }

    @Override
    protected RestGatewayManager initializeGatewayManager(RestGateway annotation) {
        return RestGatewayManager.builder()
                .gatewayName(annotation.name())
                .defaultMaxConnections(getDefaultMaxConnections())
                .defaultMaxConnectionsPerRoute(getDefaultMaxConnectionsPerRoute())
                .defaultConnectionTimeout(getDefaultConnectionTimeout())
                .defaultSocketTimeout(getDefaultSocketTimeout())
                .build();
    }

    @Override
    public Type gatewayType() {
        return Type.REST;
    }
}
