package com.atg.core.http.client;

public class RequestExecutionException extends RuntimeException {

    public RequestExecutionException(String message) {
        super(message);
    }

    public RequestExecutionException(Throwable e) {
        super(e);
    }
}
