package com.atg.core.http;

import atg.nucleus.Nucleus;
import com.atg.core.http.annotations.SoapGateway;
import com.atg.core.http.service.Type;
import com.atg.core.http.service.soap.SoapRequestContext;
import com.atg.core.http.service.soap.impl.SoapGatewayManager;
import com.google.common.base.Preconditions;

public final class SoapServiceGateway extends AbstractWebserviceGateway<SoapGateway, SoapGatewayManager> {

    public static final String PATH = "/com/atg/core/http/SoapServiceGateway";
    private static SoapServiceGateway INSTANCE;

    public static SoapRequestContext use(String gatewayName) {
        SoapGatewayManager soapGatewayManager =
                Preconditions.checkNotNull(getInstance().getRegisteredGateways().get(gatewayName),
                        "SoapServiceGateway with the name " + gatewayName +
                                " is not registered. Please Ensure the class is placed in the default scan package " +
                                getInstance().getDefaultPackageToScan());
        return GatewayRequestContext.createSoapGatewayContext(soapGatewayManager, getInstance());
    }

    private static SoapServiceGateway getInstance() {
        if (INSTANCE == null) {
            INSTANCE = (SoapServiceGateway) Nucleus.getGlobalNucleus().resolveName(PATH);
        }
        return INSTANCE;
    }


    @Override
    protected Class<SoapGateway> gatewayTypeAnnotationClass() {
        return SoapGateway.class;
    }

    @Override
    protected SoapGatewayManager initializeGatewayManager(SoapGateway annotation) {
        return SoapGatewayManager.builder()
                .gatewayName(annotation.name())
                .defaultMaxConnections(getDefaultMaxConnections())
                .defaultMaxConnectionsPerRoute(getDefaultMaxConnectionsPerRoute())
                .defaultConnectionTimeout(getDefaultConnectionTimeout())
                .defaultSocketTimeout(getDefaultSocketTimeout())
                .build();
    }

    @Override
    public Type gatewayType() {
        return Type.SOAP;
    }
}
