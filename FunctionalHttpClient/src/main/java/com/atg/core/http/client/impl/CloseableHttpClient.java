package com.atg.core.http.client.impl;

import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.nucleus.logging.ClassLoggingFactory;
import com.atg.core.http.client.HTTPConstants;
import com.atg.core.http.client.HttpClientProxy;
import com.atg.core.http.client.HttpMethod;
import com.atg.core.http.client.HttpMethodProxy;
import com.atg.core.http.client.HttpMethodProxyBuilder;
import com.atg.core.http.client.RequestExecutionException;
import com.atg.core.http.client.ResponseUtils;
import com.atg.core.http.service.RequestHeaders;
import com.atg.core.http.service.ServiceRequest;
import com.atg.core.http.service.ServiceResponse;
import com.atg.core.http.service.impl.ServiceContext;
import com.atg.core.http.service.impl.ServiceDefinition;
import com.atg.core.http.service.soap.SoapHeader;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.util.EntityUtils;

import javax.xml.bind.JAXBException;
import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class CloseableHttpClient implements HttpClientProxy {

    private static final ApplicationLoggingImpl mLogger =
            (ApplicationLoggingImpl) ClassLoggingFactory.getFactory().getLoggerForClass(
                    CloseableHttpClient.class);

    private final org.apache.http.impl.client.CloseableHttpClient httpClient;

    public CloseableHttpClient(org.apache.http.impl.client.CloseableHttpClient build) {
        httpClient = build;
    }

    public HttpMethodProxyBuilder newRequest(ServiceDefinition service) {
        return new CloseableHttpMethodProxyBuilder(service.getHttpMethod());
    }


    @Override
    public <T extends ServiceResponse> Optional<T> executeRequest(HttpMethodProxy method,
                                                                  ServiceContext requestContext) {
        T response = null;
        CloseableHttpResponse result = null;
        try (HttpMethodProxy closableMethod = method) {
            final long startTime = System.nanoTime();
            requestContext.info("Tracking Service call start time {0}", startTime);
            HttpRequestBase httpMethod = ((CloseableHttpMethod) method).getHttpMethod();

            HttpClientContext context = HttpClientContext.create();

            if (requestContext.getCustomizeRequest() != null) {
                requestContext.getCustomizeRequest().accept(httpMethod, context);
            }

            result = httpClient.execute(httpMethod, context);
            response = (T) parseResponse(result, requestContext);

            final long endTime = System.nanoTime();
            requestContext.info("Tracking Service call end time {0},with status {1}, Call Duration : {2} ms",
                    endTime, result.getStatusLine().getStatusCode(), endTime - startTime);

        } catch (Exception e) {
            requestContext.error(e.getMessage());
            throw new RequestExecutionException(e);
        }
        return Optional.of(response);
    }

    protected ServiceResponse parseResponse(CloseableHttpResponse result, ServiceContext requestContext)
            throws IOException, InstantiationException, IllegalAccessException {
        boolean hasBody = result.getEntity() != null;
        requestContext.debug("Response Body : {0}",
                hasBody ? EntityUtils.toString(result.getEntity(), HTTPConstants.UTF8) : "Null response body");
        if (requestContext.getCustomizeResponse() != null) {
            return requestContext.getCustomizeResponse().apply(result, requestContext);
        }
        //Check if the response is in our list of success responses
        boolean isSuccess = IntStream.of(requestContext.getGatewayContext().getService().getSuccessResponseCodes())
                .anyMatch(i -> i == result.getStatusLine().getStatusCode());


        return populateResponseForServiceType(result, requestContext, isSuccess);
    }

    protected <R extends ServiceContext, S extends ServiceResponse> S populateResponseForServiceType(
            CloseableHttpResponse result, R requestContext, boolean success) {
        S response;
        try {
            response = (S) (success ? requestContext.getGatewayContext().getService().getResponseClass().newInstance()
                    : requestContext.getGatewayContext().getService().getErrorClass().newInstance());
            switch (requestContext.getGatewayContext().getServiceType()) {
                case REST:
                    //If response has body
                    if (result.getEntity() != null) {
                        response = (S) ResponseUtils.parseRestResponse(response.getClass(), result.getEntity(),
                                result.getAllHeaders());
                    }
                    break;
                case SOAP:
                    if (result.getEntity() != null) {
                        response =
                                (S) ResponseUtils.parseSoapResponse(response.getClass(), result.getEntity(), success);
                    }
                    break;
            }
        } catch (IOException | InstantiationException | IllegalAccessException | JAXBException | SOAPException e) {
            throw new RequestExecutionException("Error parsing response " + e.getMessage());
        }
        return response;
    }

    private static class CloseableHttpMethodProxyBuilder implements HttpMethodProxyBuilder {

        private final HttpMethod httpMethod;
        private String uri;
        private Consumer<RequestHeaders> requestHeaders;
        private ServiceRequest request;
        private SoapHeader soapHeader;

        public CloseableHttpMethodProxyBuilder(HttpMethod method) {
            this.httpMethod = method;
        }

        @Override
        public HttpMethodProxyBuilder setURI(String uri) {
            this.uri = uri;
            return this;
        }

        @Override
        public HttpMethodProxyBuilder addRequestHeaders(Consumer<RequestHeaders> requestHeaders) {
            this.requestHeaders = requestHeaders;
            return this;
        }

        @Override
        public HttpMethodProxyBuilder createPayLoad(ServiceRequest request, SoapHeader soapHeader) {
            this.request = request;
            this.soapHeader = soapHeader;
            return this;
        }

        @Override
        public HttpMethodProxy build() {
            CloseableHttpMethod httpMethod = new CloseableHttpMethod(this.httpMethod);
            if (this.requestHeaders != null) {
                RequestHeaders headers = new RequestHeaders();
                this.requestHeaders.accept(headers);
                httpMethod.addRequestHeaders(headers.getAllRequestHeaders());
            }
            httpMethod.setURI(this.uri);
            httpMethod.createPayLoad(this.request, this.soapHeader);
            return httpMethod;
        }
    }

}
