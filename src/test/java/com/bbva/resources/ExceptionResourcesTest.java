package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.batch.response.ExceptionEntityResponseDTO;
import com.bbva.service.ExceptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ExceptionResourcesTest {

    private ExceptionService exceptionServiceMock;
    private ExceptionResources exceptionResources;

    @BeforeEach
    void setUp() throws Exception {
        exceptionResources = new ExceptionResources();
        exceptionServiceMock = Mockito.mock(ExceptionService.class);
        Field serviceField = ExceptionResources.class.getDeclaredField("exceptionService");
        serviceField.setAccessible(true);
        serviceField.set(exceptionResources, exceptionServiceMock);
    }

    @Test
    void testGetLastJobExecutionStatusDate() {
        when(exceptionServiceMock.getExceptionsWithSource()).thenReturn(getResponseDto());
        IDataResult<List<ExceptionEntityResponseDTO>> response = exceptionResources.exceptionService();
        assertEquals(String.valueOf(Response.Status.OK.getStatusCode()), response.status);
    }

    private IDataResult<List<ExceptionEntityResponseDTO>> getResponseDto() {
        return new SuccessDataResult<>(List.of());
    }
}
