package com.atg.core.http.client.impl;

import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.nucleus.logging.ClassLoggingFactory;
import com.atg.core.http.client.HttpMethod;
import com.atg.core.http.client.HttpMethodProxy;
import com.atg.core.http.client.RequestExecutionException;
import com.atg.core.http.client.RequestUtils;
import com.atg.core.http.service.ServiceRequest;
import com.atg.core.http.service.soap.SoapHeader;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class CloseableHttpMethod implements HttpMethodProxy {

    private static final ApplicationLoggingImpl mLogger =
            (ApplicationLoggingImpl) ClassLoggingFactory.getFactory().getLoggerForClass(
                    CloseableHttpClient.class);

    private HttpRequestBase httpMethod;
    private boolean hasBody;

    public CloseableHttpMethod(HttpMethod method) {
        setHttpMethod(method);
    }

    public HttpRequestBase getHttpMethod() {
        return httpMethod;
    }

    @Override
    public void setHttpMethod(HttpMethod method) {
        switch (method) {
            case GET:
                this.httpMethod = new HttpGet();
                hasBody = false;
                break;
            case POST:
                this.httpMethod = new HttpPost();
                hasBody = true;
                break;
            case PUT:
                this.httpMethod = new HttpPut();
                hasBody = true;
                break;
            case PATCH:
                this.httpMethod = new HttpPatch();
                hasBody = true;
                break;
            case DELETE:
                this.httpMethod = new HttpDelete();
                hasBody = false;
                break;
            case OPTIONS:
                this.httpMethod = new HttpOptions();
                hasBody = false;
                break;
            case TRACE:
                this.httpMethod = new HttpTrace();
                hasBody = false;
                break;
            default:
                throw new RequestExecutionException("Invalid HttpMethod");
        }

    }

    @Override
    public void setURI(String uri) {
        httpMethod.setURI(URI.create(uri));
    }

    @Override
    public void addRequestHeaders(Map<String, String> requestHeaders) {
        requestHeaders.forEach((key, value) -> httpMethod.addHeader(key, value));
    }

    @Override
    public void createPayLoad(ServiceRequest request, SoapHeader soapHeader) {
        if (request instanceof ServiceRequest.SoapServiceRequest) {
            ((HttpEntityEnclosingRequest) httpMethod).setEntity(
                    RequestUtils.createSoapEnvelope((ServiceRequest.SoapServiceRequest) request, soapHeader));
        } else if (request instanceof ServiceRequest.RestServiceRequest) {
            if (hasBody) {
                ((HttpEntityEnclosingRequest) httpMethod).setEntity(
                        RequestUtils.createRequestBody((ServiceRequest.RestServiceRequest) request,
                                httpMethod.getAllHeaders()));
            } else {
                List<NameValuePair> paramList =
                        RequestUtils.createParamsList((ServiceRequest.RestServiceRequest) request);
                URI uri;
                try {
                    uri = new URIBuilder(httpMethod.getURI()).addParameters(paramList).build();
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException("Invalid uri for parameters " + paramList.toString());
                }
                httpMethod.setURI(uri);
            }

        }

    }

    @Override
    public void close() throws Exception {
        httpMethod.releaseConnection();
    }
}
