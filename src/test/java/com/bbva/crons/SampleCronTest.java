package com.bbva.crons;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

class SampleCronTest {

    private final SampleCron sampleCron = new SampleCron();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void service() throws IOException {
        // Given
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(pw);

        // When
        try {
            sampleCron.service(request, response);
        } catch (Exception e) {
            fail("Exception: " + e.getMessage());
        }

        // Then
        String result = sw.getBuffer().toString();
        assertEquals("", result);
    }

    @Test
    void serviceThrowsException() throws IOException {
        // Given
        when(response.getWriter()).thenThrow(new IOException());

        // When + Then
        try {
            sampleCron.service(request, response);
        } catch (Exception e) {
            Assertions.assertThrows(IOException.class, () -> sampleCron.service(request, response));
        }
    }
}