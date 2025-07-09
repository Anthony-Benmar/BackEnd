package com.bbva.config;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

@Provider
public class CorsResponseFilter implements ContainerResponseFilter {
    public static final String ALLOWED_METHODS = "OPTIONS, GET, POST, PUT, DELETE, HEAD";
    public final static int MAX_AGE = 42 * 60 * 60;
    public final static String DEFAULT_ALLOWED_HEADERS = "*, X-Requested-With," +
            "Content-Type, Accept, Authorization,CSRF-Token, X-Requested-By, Authorization, Content-Length," +
            "Host, User-Agent, Accept-Encoding, Connection";
    public final static String DEFAULT_EXPOSED_HEADERS = "location,info,Content-Disposition";


    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {
        MultivaluedMap<String,Object> headers = responseContext.getHeaders();
        headers.putSingle("Access-Control-Allow-Origin", "*");
        headers.putSingle("Access-Control-Allow-Headers", getRequestedAllowedHeaders(requestContext));
        headers.putSingle("Access-Control-Expose-Headers", getRequestedExposedHeaders(requestContext));
        headers.putSingle("Access-Control-Allow-Credentials", "true");
        headers.putSingle("Access-Control-Allow-Methods", ALLOWED_METHODS);
        headers.putSingle("Access-Control-Max-Age", String.valueOf(MAX_AGE));
    }

    String getRequestedAllowedHeaders(ContainerRequestContext responseContext) {
        List<String> headers = responseContext.getHeaders().get("Access-Control-Allow-Headers");
        return createHeaderList(headers, DEFAULT_ALLOWED_HEADERS);
    }

    String getRequestedExposedHeaders(ContainerRequestContext responseContext) {
        List<String> headers = responseContext.getHeaders().get("Access-Control-Expose-Headers");
        return createHeaderList(headers, DEFAULT_EXPOSED_HEADERS);
    }

    String createHeaderList(List<String> headers, String defaultHeaders) {
        if (headers == null || headers.isEmpty()) {
            return defaultHeaders;
        }
        StringBuilder retVal = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            String header = (String) headers.get(i);
            retVal.append(header);
            retVal.append(',');
        }
        retVal.append(defaultHeaders);
        return retVal.toString();
    }
}