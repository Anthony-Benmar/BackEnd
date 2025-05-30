package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.service.SingleBaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SingleBaseResourcesTest {
    private SingleBaseService baseunicaServiceMock;
    private SingleBaseResources baseunicaResources;

    @BeforeEach
    void setUp() throws Exception {
        baseunicaResources = new SingleBaseResources();
        baseunicaServiceMock = org.mockito.Mockito.mock(SingleBaseService.class);
        // Update the field name to match the actual field in SingleBaseResources
        java.lang.reflect.Field serviceField = SingleBaseResources.class.getDeclaredField("singleBaseService");
        serviceField.setAccessible(true);
        serviceField.set(baseunicaResources, baseunicaServiceMock);
    }

    @Test
    void testGetBaseUnicaWithSource() {
        // Simulamos la respuesta del servicio
        org.mockito.Mockito.when(baseunicaServiceMock.getBaseUnicaWithSource()).thenReturn(getResponseDto());

        // Llamamos al método del recurso
        IDataResult<List<SingleBaseResponseDTO>> response = baseunicaResources.getBaseUnicaWithSource();

        // Verificamos que la respuesta sea la esperada
        assertEquals(String.valueOf(javax.ws.rs.core.Response.Status.OK.getStatusCode()), response.status);
    }

    // Método para crear una respuesta simulada
    private IDataResult<List<SingleBaseResponseDTO>> getResponseDto() {
        return new SuccessDataResult<>(List.of());
    }
}
