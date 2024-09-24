package com.bbva.resources.filters;

import com.google.cloud.ServiceOptions;
import com.google.cloud.logging.TraceLoggingEnhancer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TraceLoggingFilterTest {

    private final TraceLoggingFilter traceFilter = new TraceLoggingFilter();

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletResponse response;

    @Mock
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doFilter() throws ServletException, IOException {
        //Given
        String traceId = "abc123/abc";
        String projectId = "dummy-project";
        String traceFormated = String.format("projects/%s/traces/%s", projectId, traceId.split("/")[0]);

        //when
        when(request.getHeader("x-cloud-trace-context")).thenReturn(traceId);

        try (MockedStatic<TraceLoggingEnhancer> traceLoggingEnhancerMockedStatic = mockStatic(TraceLoggingEnhancer.class);
             MockedStatic<ServiceOptions> serviceOptionsMockedStatic = mockStatic(ServiceOptions.class)) {
            serviceOptionsMockedStatic.when(() -> ServiceOptions.getDefaultProjectId()).thenReturn(projectId);

            traceFilter.doFilter(request, response, chain);

            traceLoggingEnhancerMockedStatic.verify(() -> TraceLoggingEnhancer.setCurrentTraceId(traceFormated), times(1));
            verify(chain, times(1)).doFilter(request, response);
        }
    }

    @Test
    void doFilterWithGetHeaderException() throws ServletException, IOException {
        //when
        when(request.getHeader("x-cloud-trace-context")).thenThrow(RuntimeException.class);

        traceFilter.doFilter(request, response, chain);

        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterWithNextFilterException() throws ServletException, IOException {
        //Given
        String traceId = "abc123/abc";
        String projectId = "dummy-project";
        String traceFormated = String.format("projects/%s/traces/%s", projectId, traceId.split("/")[0]);

        //when
        when(request.getHeader("x-cloud-trace-context")).thenReturn(traceId);

        try (MockedStatic<TraceLoggingEnhancer> traceLoggingEnhancerMockedStatic = mockStatic(TraceLoggingEnhancer.class);
             MockedStatic<ServiceOptions> serviceOptionsMockedStatic = mockStatic(ServiceOptions.class)) {
            serviceOptionsMockedStatic.when(() -> ServiceOptions.getDefaultProjectId()).thenReturn(projectId);
            doThrow(RuntimeException.class).when(chain).doFilter(request, response);

            assertThrows(RuntimeException.class,() -> traceFilter.doFilter(request, response, chain));

            traceLoggingEnhancerMockedStatic.verify(() -> TraceLoggingEnhancer.setCurrentTraceId(traceFormated), times(1));
        }
    }
}