# Functional Http Client

ATG does not have any OOTB api to easily invoke REST/SOAP services. The functional http client provides an convenient and a consistent way to define services and then provides a functional api of invoking the services

Two types of services can be defined

* Rest Service
* Soap Service

To define a service, a class has to be tagged as one of

* @RestGateway
* @SoapGateway

## Defining Services

A service is defined by using the method level annotation @Service

The following attributes can be defined for a service

1. Type of service - A service can be of type REST or SOAP
2. Name of the service - The service name should be unique for all services defined in the gateway
3. Host And Port - Host and port of the service can be defined in the format \<hostName\>\:\<port\>
4. Http Method - The type of Http method to be used for invoking the service. All Http method types are supported
5. Request Class - The class name of the request object. For Rest services, the class must extend RestServiceRequest and for Soap services the class must extend SoapServiceRequest
6. Response Class - The class name of the response object that will be returned. For Rest services, the class must extend RestServiceResponse and for Soap services the class must extend SoapServiceResponse
7. Error Response Class - The class name of the error response object that will be returned. For Rest services, the class must extend RestServiceError and for Soap services the class must extend SoapServiceError
8. Success Response codes - An array of Http Response codes that can be defined to be a success response code for the service. By default HttpStatus.OK is considered a success
9. Soap Action - This attribute is only applicable for Soap services and will be ignored for Rest services.

**Example of defining a Rest Service**

```java
@RestGateway(name="CybersourceRestGateway")
public class CybersourcePaymentManager extends GenericService {

@Service(type= Type.REST,name="cybsAuthorizeService",hostAndPort = "api.cybersource.com",
httpMethod = HttpMethod.POST,requestClass = CyberSourceAuthRequest.class,
responseClass = CybersourceAuthResponse.class,errorClass = CybersourceAuthResponse.class)
public CybersourceAuthResponse authorizeCreditCard(CyberSourceAuthRequest request){

}
```

**Example of Defining a SOAP Service**

```java
@SoapGateway(name="CybersourceSoapGateway")
public class CybersourcePaymentManager extends GenericService {
  
    @Service(type= Type.SOAP,name="cybsAuthorizeSoapService",hostAndPort = "api.soap.cybersource.com",
            httpMethod = HttpMethod.POST,requestClass = CyberSourceAuthRequest.class,
            responseClass = CybersourceAuthResponse.class,errorClass = CybersourceAuthResponse.class,soapAction="SOAP HEADER:")
    public CybersourceAuthResponse authorizeCreditCard(CyberSourceAuthRequest request){

    }
}
```

## Customizing the HttpClient

Every Rest/Soap Gateway during initialization gets initialized with a new instance of the httpClient that will be used by all the services defined for that gateway. The http client during initialization can be customized to suit specific needs of the gateway.

The customization of the http client can be done by creating a static member variable of type Consumer\<HttpClientBuilder\> and annotating the member variable with the annotation HttpClientCustomizer

```java
@RestGateway(name="CybersourceRestGateway")
public class CybersourcePaymentManager extends GenericService {
    @HttpClientCustomizer
    private static final Consumer<HttpClientBuilder> customizeClient=(httpClientBuilder -> httpClientBuilder.setMaxConnTotal(10));
}
```

**Default HttpClient configuration**

If the client customizer is not defined the http client is initialized with the following defaults

1. DefaultMaxConnections: DefaultMaxConnections set for the RestServiceGateway/SoapServiceGateway
2. DefaultMaxConnectionsPerRoute: DefaultMaxConnectionsPerRoute set for the RestServiceGateway/SoapServiceGateway
3. SocketTimeout: DefaultSocketTimeout set for the RestServiceGateway/SoapServiceGateway
4. ConnectionTimeout: DefaultConnectionTimeout set for the RestServiceGateway/SoapServiceGateway

## Invoking the service

RestServiceGateway and SoapServiceGateway classes provide a functional api to easily invoke any service from any class without having to reference any components in the classes that need to invoke the services. Also, the classes where the services are defined can be completely separated from the classes that invoke the service. This also means that how a particular service is invoked can be completely different in two different methods.


### Basic Request Execution Example

```java
public CybersourceAuthResponse authorizeCreditCard(CybersourceAuthRequest request){
        return RestServiceGateway.use("CybersourceRestGateway")
                .createRestRequestFor("cybsAuthorizeService")
                .withRequestPayload(request)
                .withURI("/api/auth")
                .executeRequest().get();
    }
```


### Request Execution with Dynamic Path Variables and Custom Headers

URIPathSubstitutions method allows a consumer to be defined to add variable values that will be substituted at runtime during request execution.

RequestHeaders method allows a consumer to add custom headers to the request

```java
public ServiceResponse.RestServiceResponse authorizeCreditCard(ServiceRequest.RestServiceRequest request){
        return RestServiceGateway.use("CybersourceRestGateway")
                .createRestRequestFor("cybsAuthorizeService")
                .withRequestPayload(request)
                .withURI("/api/auth/{requestId}")
                .uriPathSubstitutions(substitutor -> substitutor.addVariableValues("requestId","12345"))
                .requestHeaders(headers->headers.addRequestHeader("Basic Auth:","asdfsdfeerwe")
                        .addRequestHeader("Content-Type","application/xml"))   
                .executeRequest().get();
    }
```

### Customizing the Request

customizeRequest method allows a consumer to be defined that can use the httpRequestBase and the RequestContext object to define a consumer that can modify the httpRequestBase object and set the values of the http request just before the request is made.

```java
public ServiceResponse.RestServiceResponse authorizeCreditCard(ServiceRequest.RestServiceRequest request){
        return RestServiceGateway.use("CybersourceRestGateway")
                .createRestRequestFor("cybsAuthorizeService")
                .withRequestPayload(request)
                .withURI("/api/auth")
                .customizeRequest((httpRequest,context)->httpRequest.setURI(URI.create("http://newURI")))
                .executeRequest().get();
    }
```

### Customizing the Response

customizeResponse method allows a consumer to be defined that can use the HttpResponseResult object to define a consumer to modify the response from the service call and customize the logic of how the response should be parsed

If the customizeResponse method is not used, the default logic of parsing the response into the corresponding objects will be used.

```java
public ServiceResponse.RestServiceResponse authorizeCreditCard(ServiceRequest.RestServiceRequest request){
        return RestServiceGateway.use("CybersourceRestGateway")
                .createRestRequestFor("cybsAuthorizeService")
                .withRequestPayload(request)
                .withURI("/api/auth")
                .customizeResponse((httpResponse,serviceContext)-> {
                    try {
                        return (ServiceResponse) new ObjectMapper().readValue(httpResponse.getEntity().getContent(),serviceContext.getGatewayContext().getService()
                                .getResponseClass());
                    } catch (IOException e) {
                       serviceContext.error("error parsing response");
                    }
                    return null;
                })
                .executeRequest().get();
    }
```
