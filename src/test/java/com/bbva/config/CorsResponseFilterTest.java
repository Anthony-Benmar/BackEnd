package com.bbva.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CorsResponseFilterTest {

    private CorsResponseFilter filter;

    @BeforeEach
    void setUp() {
        filter = new CorsResponseFilter();
    }

    @Test
    void testFilter_addsCorsHeaders_whenRequestHeadersAreNull() throws IOException {
        ContainerRequestContext request = mock(ContainerRequestContext.class);
        ContainerResponseContext response = mock(ContainerResponseContext.class);

        MultivaluedMap<String, Object> responseHeaders = new MultivaluedHashMap<>();

        when(request.getHeaderString("Access-Control-Request-Headers")).thenReturn(null);
        when(response.getHeaders()).thenReturn(responseHeaders);

        filter.filter(request, response);

        assertEquals("*", responseHeaders.getFirst("Access-Control-Allow-Origin"));
        assertEquals(CorsResponseFilter.ALLOWED_METHODS, responseHeaders.getFirst("Access-Control-Allow-Methods"));
        assertEquals(CorsResponseFilter.MAX_AGE, responseHeaders.getFirst("Access-Control-Max-Age"));
        assertTrue(((String) responseHeaders.getFirst("Access-Control-Allow-Headers")).contains("Content-Type"));
        assertEquals(CorsResponseFilter.DEFAULT_EXPOSED_HEADERS, responseHeaders.getFirst("Access-Control-Expose-Headers"));
        assertEquals("true", responseHeaders.getFirst("Access-Control-Allow-Credentials"));
    }

    @Test
    void testFilter_addsRequestedHeaders() throws IOException {
        ContainerRequestContext request = mock(ContainerRequestContext.class);
        ContainerResponseContext response = mock(ContainerResponseContext.class);

        MultivaluedMap<String, Object> responseHeaders = new MultivaluedHashMap<>();
        String customHeaders = "Custom-Header";

        when(request.getHeaderString("Access-Control-Request-Headers")).thenReturn(customHeaders);
        when(response.getHeaders()).thenReturn(responseHeaders);

        filter.filter(request, response);

        String allowHeaders = (String) responseHeaders.getFirst("Access-Control-Allow-Headers");
        assertTrue(allowHeaders.contains(customHeaders));
        assertTrue(allowHeaders.contains("Content-Type"));
    }
}
