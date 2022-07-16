package com.atg.core.http.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Put on any static field of type Consumer&lt;HttpClientBuilder&gt; to allow for customization of the httpClient used by RestServiceGateway or SoapServiceGateway.
 * <p>
 * Example:
 * <pre>
 *     private static final Consumer&lt;HttpClientBuilder&gt; clientCustomizer =(httpClientBuilder -> httpClientBuilder.disableCookieManagement());
 * </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpClientCustomizer {
}
