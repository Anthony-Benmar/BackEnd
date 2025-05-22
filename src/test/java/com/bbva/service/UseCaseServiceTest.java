package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.UseCaseReliabilityDao;
import com.bbva.dto.use_case.request.UpdateOrInsertUseCaseDtoRequest;
import com.bbva.dto.use_case.request.UseCaseInputsFilterDtoRequest;
import com.bbva.dto.use_case.response.UpdateOrInsertDtoResponse;
import com.bbva.dto.use_case.response.UseCaseInputsFilterDtoResponse;
import com.bbva.entities.use_case.UseCaseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UseCaseServiceTest {

    private UseCaseService useCaseService;
    private UseCaseReliabilityDao useCaseReliabilityDaoMock;

    @BeforeEach
    void setUp() throws Exception {
        useCaseService = new UseCaseService();
        useCaseReliabilityDaoMock = mock(UseCaseReliabilityDao.class);

        // Inyectar el mock de UseCaseReliabilityDao
        var field = UseCaseService.class.getDeclaredField("useCaseReliabilityDao");
        field.setAccessible(true);
        field.set(useCaseService, useCaseReliabilityDaoMock);
    }

    @Test
    void testListUseCasesSuccess() {
        List<UseCaseEntity> mockList = List.of(new UseCaseEntity());
        when(useCaseReliabilityDaoMock.listAllUseCases()).thenReturn(mockList);

        IDataResult<List<UseCaseEntity>> result = useCaseService.listUseCases();

        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(mockList, result.data);
        verify(useCaseReliabilityDaoMock).listAllUseCases();
    }
    @Test
    void testUpdateOrInsertUseCaseSuccess() {
        UpdateOrInsertDtoResponse mockResponse = new UpdateOrInsertDtoResponse();
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        dto.setUseCaseId(1); // Ensure the DTO is properly initialized
        dto.setUseCaseName("Test Use Case");
        dto.setUseCaseDescription("Test Description");
        dto.setDomainId(1);
        dto.setDeliveredPiId(1);
        dto.setCritical(1);
        dto.setIsRegulatory(1);
        dto.setUseCaseScope(1);
        dto.setOperativeModel(1);

        when(useCaseReliabilityDaoMock.updateOrInsertUseCase(any(UpdateOrInsertUseCaseDtoRequest.class))).thenReturn(mockResponse);

        IDataResult<UpdateOrInsertDtoResponse> result = useCaseService.updateOrInsertUseCase(dto);

        assertNotNull(result);
        assertTrue(result.success);
        verify(useCaseReliabilityDaoMock).updateOrInsertUseCase(dto);
    }
    @Test
    void testUpdateOrInsertUseCaseValidationError() {
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();

        IDataResult<UpdateOrInsertDtoResponse> result = useCaseService.updateOrInsertUseCase(dto);

        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("UseCaseName must not be null or empty", result.message);
        verify(useCaseReliabilityDaoMock, never()).updateOrInsertUseCase(dto);
    }
    @Test
    void testUpdateOrInsertUseCaseException() {
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        dto.setUseCaseId(1);
        dto.setUseCaseName("Test Use Case");
        dto.setUseCaseDescription("Test Description");
        dto.setDomainId(1);
        dto.setDeliveredPiId(1);
        dto.setCritical(1);
        dto.setIsRegulatory(1);
        dto.setUseCaseScope(1);
        dto.setOperativeModel(1);
        when(useCaseReliabilityDaoMock.updateOrInsertUseCase(dto)).thenThrow(new RuntimeException("Database error"));

        IDataResult<UpdateOrInsertDtoResponse> result = useCaseService.updateOrInsertUseCase(dto);

        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("Database error", result.message);

        verify(useCaseReliabilityDaoMock).updateOrInsertUseCase(dto);
    }

    @Test
    void testGetFilteredUseCasesSuccess() {
        UseCaseInputsFilterDtoRequest dto = new UseCaseInputsFilterDtoRequest();
        UseCaseInputsFilterDtoResponse mockResponse = new UseCaseInputsFilterDtoResponse();
        when(useCaseReliabilityDaoMock.getFilteredUseCases(dto)).thenReturn(mockResponse);

        IDataResult<UseCaseInputsFilterDtoResponse> result = useCaseService.getFilteredUseCases(dto);

        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(mockResponse, result.data);
        verify(useCaseReliabilityDaoMock).getFilteredUseCases(dto);
    }

    @Test
    void testGetFilteredUseCasesException() {
        UseCaseInputsFilterDtoRequest dto = new UseCaseInputsFilterDtoRequest();
        when(useCaseReliabilityDaoMock.getFilteredUseCases(dto)).thenThrow(new RuntimeException("Database error"));

        IDataResult<UseCaseInputsFilterDtoResponse> result = useCaseService.getFilteredUseCases(dto);

        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("Database error", result.message);
        verify(useCaseReliabilityDaoMock).getFilteredUseCases(dto);
    }
}
