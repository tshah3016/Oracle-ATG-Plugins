package com.atg.core.http.annotations;

import com.atg.core.http.client.HTTPConstants;
import com.atg.core.http.client.HttpMethod;
import com.atg.core.http.service.ServiceRequest;
import com.atg.core.http.service.ServiceResponse;
import com.atg.core.http.service.Type;

import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Service {
    @NotNull Type type();

    @NotNull String name();

    String hostAndPort();

    String uri() default "";

    String protocol() default "https";

    HttpMethod httpMethod() default HttpMethod.GET;

    @NotNull Class<? extends ServiceRequest> requestClass();

    @NotNull Class<? extends ServiceResponse> responseClass();

    Class<? extends ServiceResponse.ServiceError> errorClass();

    int[] successResponseCodes() default {HTTPConstants.OK};

    String soapAction() default "";
}
