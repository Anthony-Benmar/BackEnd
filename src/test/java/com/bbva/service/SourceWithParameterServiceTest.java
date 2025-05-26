package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.SourceWithParameterDao;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDTO;
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
        List<SourceWithParameterDTO> mockResponse = new ArrayList<>();
        mockResponse.add(new SourceWithParameterDTO()); // Add mock data as needed
        when(sourceWithParameterDao.getSourceWithParameter()).thenReturn(mockResponse);
        // Act
        IDataResult<List<SourceWithParameterDTO>> result = sourceWithParameterService.getSourceWithParameter();

        // Assert
        assertEquals(SuccessDataResult.class, result.getClass());
    }
}
