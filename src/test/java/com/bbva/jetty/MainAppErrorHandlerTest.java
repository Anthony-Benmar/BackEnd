package com.bbva.jetty;

import org.eclipse.jetty.server.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MainAppErrorHandlerTest {

    @Mock
    Request request;

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    HttpServletResponse httpServletResponse;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle() throws IOException {
        // Given
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(httpServletResponse.getWriter()).thenReturn(pw);

        // When
        try {
            MainAppErrorHandler mainAppErrorHandler = new MainAppErrorHandler();
            mainAppErrorHandler.handle("/test", request, httpServletRequest, httpServletResponse);
        } catch (IOException e) {
            fail("Exception: " + e.getMessage());
        }

        // Then
        String result = sw.getBuffer().toString();
        Assertions.assertEquals("{\"status\":0}", result);
    }
}