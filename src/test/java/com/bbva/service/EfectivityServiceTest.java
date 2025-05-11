package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.EfectivityDao;
import com.bbva.dao.ExceptionDao;
import com.bbva.dto.efectivity.response.EfectivityEntityResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

public class EfectivityServiceTest {
    @Mock
    private EfectivityDao efectivityDao;

    @InjectMocks
    private EfectivityService efectivityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEfectivityWithSource() {
        // Arrange
        String tableName = "test_table";
        List<EfectivityEntityResponseDTO> mockResponse = List.of(new EfectivityEntityResponseDTO());
        Mockito.when(efectivityDao.getEfectivityWithSource(tableName)).thenReturn(mockResponse);

        // Act
        IDataResult<List<EfectivityEntityResponseDTO>> result = efectivityService.getEfectivityWithSource(tableName);

        // Assert
        Assertions.assertNotNull(result);
    }
}
