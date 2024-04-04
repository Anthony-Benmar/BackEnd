package com.bbva.common;

public final class HttpStatusCodes {
    private HttpStatusCodes() {
    }

    public static final String HTTP_OK = "200";
    public static final String HTTP_CREATED = "201";
    public static final String HTTP_ACCEPTED = "202";
    public static final String HTTP_INTERNAL_SERVER_ERROR = "500";

    public static String getStatusText(String statusCode) {
        switch (statusCode) {
            case HTTP_OK:
                return "OK";
            case HTTP_CREATED:
                return "Created";
            case HTTP_ACCEPTED:
                return "Accepted";
            case HTTP_INTERNAL_SERVER_ERROR:
                return "Internal server error";
            default:
                return "Unknown Status Code";
        }
    }

}

