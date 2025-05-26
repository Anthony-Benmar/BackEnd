package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.SingleBaseDao;
import com.bbva.dto.single_base.response.SingleBaseResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class SingleBaseServiceTest {
    @InjectMocks
    private SingleBaseService singleBaseService;
    @Mock
    private SingleBaseDao singleBaseDao;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testGetExceptionsWithSource() {
        List<SingleBaseResponseDTO> mockResponse = new ArrayList<>();
        mockResponse.add(new SingleBaseResponseDTO()); // Add mock data as needed
        when(singleBaseDao.getBaseUnicaWithSource()).thenReturn(mockResponse);
        IDataResult<List<SingleBaseResponseDTO>> result = singleBaseService.getBaseUnicaWithSource();

        // Assert
        assertEquals(SuccessDataResult.class, result.getClass());
    }
}
