package com.bbva.resources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class HealthResourceTest {

    private final HealthResource healthResource = new HealthResource();

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void hello() {
        // Given
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        // When
        Response response = healthResource.health(request);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("{\"status\":\"OK\"}", response.getEntity());
    }
}