package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.batch.response.EfectivityEntityResponseDTO;
import com.bbva.service.EfectivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EfectivityResourcesTest {
    private EfectivityService efectivityServiceMock;
    private EfectivityResources efectivityResources;

    @BeforeEach
    void setUp() throws Exception {
        efectivityResources = new EfectivityResources();
        efectivityServiceMock = org.mockito.Mockito.mock(EfectivityService.class);
        java.lang.reflect.Field serviceField = EfectivityResources.class.getDeclaredField("efectivityService");
        serviceField.setAccessible(true);
        serviceField.set(efectivityResources, efectivityServiceMock);
    }
    @Test
    void testGetEfectivityResourcesTest() {
        String tableName = "testTable";
        org.mockito.Mockito.when(efectivityServiceMock.getEfectivityWithSource(tableName)).thenReturn(getResponseDto());
        IDataResult<List<EfectivityEntityResponseDTO>> response = efectivityResources.exceptionService(tableName);
        assertEquals(String.valueOf(javax.ws.rs.core.Response.Status.OK.getStatusCode()), response.status);
    }
    private IDataResult<List<EfectivityEntityResponseDTO>> getResponseDto() {
        return new SuccessDataResult<>(List.of());
    }
}
