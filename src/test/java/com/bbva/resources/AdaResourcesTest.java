package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.ada.request.AdaJobExecutionFilterRequestDTO;
import com.bbva.dto.ada.response.AdaJobExecutionFilterData;
import com.bbva.dto.ada.response.AdaJobExecutionFilterResponseDTO;
import com.bbva.service.AdaService;
import com.bbva.util.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AdaResourcesTest {

    private AdaResources adaResources;
    private AdaService adaServiceMock;
    private Helper helperMock;

    @BeforeEach
    void setUp() {
        adaResources = new AdaResources();
        adaServiceMock = mock(AdaService.class);
        helperMock = mock(Helper.class);

        try {
            // Reemplazar la instancia de AdaService con el mock
            var adaServiceField = AdaResources.class.getDeclaredField("adaService");
            adaServiceField.setAccessible(true);
            adaServiceField.set(adaResources, adaServiceMock);

            // Reemplazar la instancia de Helper con el mock
            var helperField = AdaResources.class.getDeclaredField("helper");
            helperField.setAccessible(true);
            helperField.set(adaResources, helperMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFilter() {
        // Configuración del DTO esperado
        AdaJobExecutionFilterRequestDTO dto = new AdaJobExecutionFilterRequestDTO();
        dto.setRecords_amount(3);
        dto.setPage(1);

        // Configuración de datos simulados
        AdaJobExecutionFilterData adaJobExecutionFilterData = new AdaJobExecutionFilterData();
        adaJobExecutionFilterData.setRecordsCount(3);

        List<AdaJobExecutionFilterData> mockList = List.of(
                adaJobExecutionFilterData,
                adaJobExecutionFilterData,
                adaJobExecutionFilterData
        );

        AdaJobExecutionFilterResponseDTO mockResponse = new AdaJobExecutionFilterResponseDTO();
        mockResponse.setData(mockList);
        mockResponse.setCount(3);
        mockResponse.setPages_amount(1);

        IDataResult<AdaJobExecutionFilterResponseDTO> mockResult = new SuccessDataResult<>(mockResponse);

        // Configuración del mock para Helper
        when(helperMock.parseIntegerOrDefault("3", 10)).thenReturn(3);
        when(helperMock.parseIntegerOrDefault("1", 1)).thenReturn(1);

        // Configuración del mock para AdaService
        when(adaServiceMock.filter(dto)).thenReturn(mockResult);

        // Ejecución del método
        IDataResult<AdaJobExecutionFilterResponseDTO> result = adaResources.filter(
                "3", "1", null, null, null, null, null, null, null, null
        );

        // Verificaciones
        assertNotNull(result); // Verifica que el resultado no sea null
        assertEquals(mockResult, result); // Compara el resultado con el mock esperado
        verify(helperMock).parseIntegerOrDefault("3", 10); // Verifica que el helper fue llamado correctamente
        verify(helperMock).parseIntegerOrDefault("1", 1); // Verifica que el helper fue llamado correctamente
        verify(adaServiceMock).filter(dto); // Verifica que el mock fue llamado correctamente
    }
}