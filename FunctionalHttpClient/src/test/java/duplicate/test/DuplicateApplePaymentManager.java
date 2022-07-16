package duplicate.test;

import atg.nucleus.GenericService;
import com.atg.core.http.RestServiceGateway;
import com.atg.core.http.annotations.HttpClientCustomizer;
import com.atg.core.http.annotations.RestGateway;
import com.atg.core.http.annotations.Service;
import com.atg.core.http.annotations.SoapGateway;
import com.atg.core.http.client.HttpMethod;
import com.atg.core.http.service.ServiceResponse;
import com.atg.http.ApplePayload;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Optional;
import java.util.function.Consumer;

import static com.atg.core.http.service.Type.SOAP;

@RestGateway(name = "AppleRestGateway")
@SoapGateway(name = "AppleSoapGateway")
public class DuplicateApplePaymentManager extends GenericService {
    @HttpClientCustomizer
    public final Consumer<HttpClientBuilder> someCosumer =
            (someString -> someString.toString());

    @Service(name = "debit", type = SOAP, hostAndPort = "applepay.com",
            httpMethod = HttpMethod.POST,
            requestClass = ApplePayload.class,
            responseClass = ApplePayload.class,
            errorClass = ApplePayload.class)
    public void debitApplePay(ApplePayload payload) {
        Optional<ServiceResponse.RestServiceResponse> r = RestServiceGateway.use("AppleAppleRestGatewayGateway")
                .createRestRequestFor("debitPay")
                .withURI("/apple/pay/debit/{accountId}")
                .requestHeaders(requestHeaders -> requestHeaders
                        .addRequestHeader("AUTH", "Authorization:12345")
                        .addRequestHeader("Header 2", "Value3"))
                .withRequestPayload(payload)
                .uriPathSubstitutions((pathSubstitutor) -> {
                    pathSubstitutor.addVariableValues("accountId", "123456")
                            .addVariableValues("value2", "333333");
                })
                .customizeRequest((httpRequest, context) -> {
                    httpRequest.setHeader("Something", "SomeValue");
                    context.setAttribute("SomeParam", "SomeParamValue");
                })
                .customizeResponse((response, serviceContext) -> {
                    response.getEntity().toString();
                    return payload;
                })
                .executeRequest();
    }
}
