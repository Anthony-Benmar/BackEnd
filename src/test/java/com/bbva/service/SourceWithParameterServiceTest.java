package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.SourceWithParameterDao;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterPaginatedResponseDTO;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class SourceWithParameterServiceTest {
    @Mock
    private SourceWithParameterDao sourceWithParameterDao;

    @InjectMocks
    private SourceWithParameterService sourceWithParameterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testGetExceptionsWithSource() {
        List<SourceWithParameterDataDtoResponse> mockResponse = new ArrayList<>();
        mockResponse.add(new SourceWithParameterDataDtoResponse()); // Add mock data as needed
        when(sourceWithParameterDao.getSourceWithParameter(null)).thenReturn(mockResponse);
        when(sourceWithParameterDao.getSourceWithParameterTotalCount(null)).thenReturn(mockResponse.size());
        // Act
        IDataResult<SourceWithParameterPaginatedResponseDTO> result = sourceWithParameterService.getSourceWithParameter(null);

        // Assert
        assertEquals(SuccessDataResult.class, result.getClass());
    }
}
