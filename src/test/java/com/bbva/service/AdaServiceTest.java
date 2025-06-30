package com.bbva.service;

import com.bbva.dao.AdaDao;
import com.bbva.dto.ada.request.AdaJobExecutionFilterRequestDTO;
import com.bbva.dto.ada.response.AdaJobExecutionFilterResponseDTO;
import com.bbva.core.abstracts.IDataResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdaServiceTest {

    private AdaService adaService;
    private AdaDao adaDaoMock;

    @BeforeEach
    void setUp() {
        adaDaoMock = mock(AdaDao.class);
        adaService = new AdaService() {
            {
                try {
                    var field = AdaService.class.getDeclaredField("adaDao");
                    field.setAccessible(true);
                    field.set(this, adaDaoMock);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void testFilter() {
        AdaJobExecutionFilterRequestDTO dto = new AdaJobExecutionFilterRequestDTO();
        dto.setRecords_amount(3);
        dto.setPage(1);

        AdaJobExecutionFilterResponseDTO mockResponse = new AdaJobExecutionFilterResponseDTO();
        mockResponse.setCount(3);
        mockResponse.setPagesAmount(1);

        when(adaDaoMock.filter(dto)).thenReturn(mockResponse);
        IDataResult<AdaJobExecutionFilterResponseDTO> result = adaService.filter(dto);

        assertEquals(mockResponse, result.data);
        verify(adaDaoMock).filter(dto);
    }
}