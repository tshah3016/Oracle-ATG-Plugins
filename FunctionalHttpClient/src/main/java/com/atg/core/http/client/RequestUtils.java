package com.atg.core.http.client;

import com.atg.core.http.service.PathVariableMatcher;
import com.atg.core.http.service.ServiceRequest;
import com.atg.core.http.service.impl.ServiceContext;
import com.atg.core.http.service.impl.ServiceDefinition;
import com.atg.core.http.service.soap.SoapHeader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestUtils {

    public static final ObjectMapper mapper = new ObjectMapper();

    public static StringEntity createSoapEnvelope(ServiceRequest.SoapServiceRequest soapRequest,
                                                  SoapHeader soapHeader) {
        String soap = null;
        try {
            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            SOAPHeader soapHead = envelope.getHeader();
            // Add Header XML Content. The header is optionnal and can be null
            if (soapHeader != null) {
                getMarshaller(soapHeader).marshal(soapHeader, soapHead);
            }
            // Add Body XML content
            SOAPBody soapBody = envelope.getBody();
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            getMarshaller(soapRequest).marshal(soapRequest, document);
            soapBody.addDocument(document);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            soapMessage.writeTo(outputStream);
            soap = outputStream.toString();
        } catch (SOAPException | JAXBException | ParserConfigurationException | IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return new StringEntity(soap, ContentType.create("text/xml", HTTPConstants.UTF8));

    }

    private static Marshaller getMarshaller(Object request) throws JAXBException, SOAPException {
        JAXBContext jaxbContext = JAXBContext.newInstance(request.getClass());
        return jaxbContext.createMarshaller();
    }

    @SneakyThrows
    public static HttpEntity createRequestBody(ServiceRequest.RestServiceRequest request, Header[] headers) {
        String type = getContentTypeFromHeaders(headers);
        HttpEntity entity = null;
        switch (type) {
            case "urlencoded":
                List<NameValuePair> params = createParamsList(request);
                entity = new UrlEncodedFormEntity(params, HTTPConstants.UTF8);
                break;
            case "json":
                String payload = mapper.writeValueAsString(request);
                entity = new StringEntity(payload,
                        ContentType.create(HTTPConstants.JSON_CONTENT_TYPE, HTTPConstants.UTF8));
                break;
            case "xml":
                StringWriter writer = new StringWriter();
                getMarshaller(request).marshal(request, writer);
                entity = new StringEntity(writer.toString(),
                        ContentType.create(HTTPConstants.XML_CONTENT_TYPE, HTTPConstants.UTF8));
                break;
        }
        if (entity == null) {
            throw new IllegalArgumentException("Unable to parse Request to Body");
        }
        return entity;
    }

    public static String getContentTypeFromHeaders(Header[] headers) {
        return Arrays.stream(headers)
                .filter(header -> header.getName().equals(HTTPConstants.CONTENT_TYPE_HEADER_NAME))
                .findFirst()
                .map(header -> {
                    if (header.getValue().contains("json")) {
                        return "json";
                    }
                    if (header.getValue().contains("xml")) {
                        return "xml";
                    } else {
                        return "urlencoded";
                    }
                })
                .orElse("urlencoded");
    }

    public static List<NameValuePair> createParamsList(ServiceRequest.RestServiceRequest request) {
        return Stream.of(request)
                .map(restServiceRequest -> (Map<String, String>) mapper.convertValue(restServiceRequest,
                        new TypeReference<Map<String, String>>() {
                        }))
                .flatMap(map -> map.entrySet().stream())
                .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

    }

    public static String buildUri(ServiceContext requestContext, ServiceDefinition service) {
        String uri = service.getUri();
        if (requestContext.getWithURI() != null) {
            uri = requestContext.getWithURI();
        }
        if (requestContext.getUriPathSubstitutions() != null) {
            PathVariableMatcher pvs = new PathVariableMatcher();
            requestContext.getUriPathSubstitutions().accept(pvs);
            uri = pvs.substituteUrlPathVariables(uri);
        }
        if (URI.create(uri).isAbsolute()) {
            return uri;
        }
        return getStringBuilder().append(service.getProtocol())
                .append(HTTPConstants.PROTOCOL_SEP)
                .append(service.getHostAndPort())
                .append(uri).toString();
    }

    private static StringBuilder getStringBuilder() {
        return new StringBuilder();
    }
}
