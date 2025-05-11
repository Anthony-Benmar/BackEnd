package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.ExceptionDao;
import com.bbva.dto.exception.response.ExceptionEntityResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ExceptionServiceTest {
    @Mock
    private ExceptionDao exceptionDao;

    @InjectMocks
    private ExceptionService exceptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetExceptionsWithSource() {
        // Arrange
        List<ExceptionEntityResponseDTO> mockResponse = new ArrayList<>();
        mockResponse.add(new ExceptionEntityResponseDTO()); // Add mock data as needed
        when(exceptionDao.getExceptionsWithSource()).thenReturn(mockResponse);

        // Act
        IDataResult<List<ExceptionEntityResponseDTO>> result = exceptionService.getExceptionsWithSource();

        // Assert
        assertEquals(SuccessDataResult.class, result.getClass());
    }
}
