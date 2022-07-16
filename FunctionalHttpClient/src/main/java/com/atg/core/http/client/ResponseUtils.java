package com.atg.core.http.client;

import com.atg.core.http.service.ServiceResponse;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import java.io.InputStream;

public class ResponseUtils {
    public static ServiceResponse parseRestResponse(Class<? extends ServiceResponse> aClass, HttpEntity entity,
                                                    Header[] allHeaders)
            throws JAXBException, SOAPException, IOException {
        String type = RequestUtils.getContentTypeFromHeaders(allHeaders);
        ServiceResponse serviceResponse = null;
        switch (type) {
            case "xml":
                serviceResponse = (ServiceResponse) getUnMarshaller(aClass).unmarshal(entity.getContent());
                break;
            default:
                serviceResponse = RequestUtils.mapper.readValue(entity.getContent(), aClass);
                break;

        }
        return serviceResponse;
    }

    private static Unmarshaller getUnMarshaller(Class<? extends ServiceResponse> classy)
            throws JAXBException, SOAPException {
        JAXBContext jaxbContext = JAXBContext.newInstance(classy);
        return jaxbContext.createUnmarshaller();
    }

    public static ServiceResponse parseSoapResponse(Class<? extends ServiceResponse> aClass, HttpEntity entity,
                                                    boolean success) throws IOException, SOAPException, JAXBException {
        ServiceResponse soapResponse = null;
        try (InputStream is = entity.getContent()) {
            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage(null, is);
            if (soapMessage != null && soapMessage.getSOAPBody() != null) {
                // In case of error, we expect to have a fault node
                if (!success) {
                    if (soapMessage.getSOAPBody().getFault() != null) {
                        Node faultNode = soapMessage.getSOAPBody().getFault();
                        soapResponse = (ServiceResponse) getUnMarshaller(aClass).unmarshal(faultNode);
                    }
                } else {
                    soapResponse = (ServiceResponse) getUnMarshaller(aClass).unmarshal(
                            soapMessage.getSOAPBody().extractContentAsDocument());
                }
            }
        }
        return soapResponse;
    }

}
