package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.baseunica.response.BaseunicaResponseDTO;
import com.bbva.service.BaseunicaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseunicaResourcesTest {
    private BaseunicaService baseunicaServiceMock;
    private BaseunicaResources baseunicaResources;

    @BeforeEach
    void setUp() throws Exception {
        baseunicaResources = new BaseunicaResources();
        baseunicaServiceMock = org.mockito.Mockito.mock(BaseunicaService.class);
        // Usamos reflexión para inyectar el servicio simulado en el recurso
        java.lang.reflect.Field serviceField = BaseunicaResources.class.getDeclaredField("baseunicaService");
        serviceField.setAccessible(true);
        serviceField.set(baseunicaResources, baseunicaServiceMock);
    }

    @Test
    void testGetBaseUnicaWithSource() {
        String tableName = "testTable";
        // Simulamos la respuesta del servicio
        org.mockito.Mockito.when(baseunicaServiceMock.getBaseUnicaWithSource(tableName)).thenReturn(getResponseDto());

        // Llamamos al método del recurso
        IDataResult<List<BaseunicaResponseDTO>> response = baseunicaResources.getBaseUnicaWithSource(tableName);

        // Verificamos que la respuesta sea la esperada
        assertEquals(String.valueOf(javax.ws.rs.core.Response.Status.OK.getStatusCode()), response.status);
    }

    // Método para crear una respuesta simulada
    private IDataResult<List<BaseunicaResponseDTO>> getResponseDto() {
        return new SuccessDataResult<>(List.of());
    }
}
