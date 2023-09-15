package com.bbva.jetty;

public class MainAppRuntimeException extends RuntimeException {

    public MainAppRuntimeException(String message) {
        super(message);
    }

    public MainAppRuntimeException(Throwable throwable) {
        super(throwable);
    }
}