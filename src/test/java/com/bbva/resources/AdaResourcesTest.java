package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.AdaDao;
import com.bbva.dto.ada.request.AdaJobExecutionFilterRequestDTO;
import com.bbva.dto.ada.response.AdaJobExecutionFilterResponseDTO;
import com.bbva.service.AdaService;
import com.bbva.util.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AdaResourcesTest {

    private AdaResources adaResources;
    private AdaService adaServiceMock;
    private Helper helperMock;

    @BeforeEach
    void setUp() {
        adaServiceMock = mock(AdaService.class);
        helperMock = mock(Helper.class);
        adaResources = new AdaResources();
    }

    @Test
    void testFilter() {
        AdaJobExecutionFilterResponseDTO mockResponse = new AdaJobExecutionFilterResponseDTO();
        mockResponse.setCount(3);
        mockResponse.setPages_amount(1);

        when(adaServiceMock.filter(any(AdaJobExecutionFilterRequestDTO.class)))
                .thenReturn(mock(IDataResult.class));

        IDataResult<AdaJobExecutionFilterResponseDTO> result = adaResources.filter(
                "3", "1", null, null, null, null, null, null, null, null
        );
        
        assertNotNull(result, "El resultado no debería ser null");
    }
}