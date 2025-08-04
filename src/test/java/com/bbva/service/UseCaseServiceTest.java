package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.UseCaseReliabilityDao;
import com.bbva.dto.use_case.response.UseCaseInputsDtoResponse;
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
    @Test
    void testLstUseCasesException() {
        when(useCaseReliabilityDaoMock.listAllUseCases()).thenThrow(new RuntimeException("Database error"));

        IDataResult<List<UseCaseEntity>> result = useCaseService.listUseCases();

        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("Database error", result.message);
        verify(useCaseReliabilityDaoMock).listAllUseCases();
    }
    @Test
    void testValidateRequest_UseCaseIdRequiredForUpdate_MockedIsInsertOperation() {
        UseCaseService useCaseServiceSpy = spy(new UseCaseService());
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        dto.setUseCaseId(0);

        doReturn(false).when(useCaseServiceSpy).isInsertOperation(dto);

        IDataResult<?> result = useCaseServiceSpy.validateRequest(dto);

        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("UseCaseId is required for update.", result.message);
    }

    @Test
    void testValidateRequest_UseCaseNameMustNotBeNullOrEmpty() {
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        dto.setUseCaseName(null); // Invalid name

        IDataResult<?> result = useCaseService.validateRequest(dto);

        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("UseCaseName must not be null or empty", result.message);
    }

    @Test
    void testValidateRequest_UseCaseDescriptionMustNotBeNullOrEmpty() {
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        dto.setUseCaseId(1);
        dto.setUseCaseName("Test Use Case");
        dto.setDomainId(1);
        dto.setAction("Test Action");
        dto.setUserId("Test User");
        dto.setDeliveredPiId(1);
        dto.setCritical(1);
        dto.setIsRegulatory(1);
        dto.setUseCaseScope(1);
        dto.setOperativeModel(1);
        dto.setUseCaseDescription(""); // Invalid description

        IDataResult<?> result = useCaseService.validateRequest(dto);

        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("UseCaseDescription must not be null or empty", result.message);
    }

    @Test
    void testValidateRequest_DomainIdMustNotBeNullOrZero() {
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        dto.setUseCaseId(1);
        dto.setUseCaseName("Test Use Case");
        dto.setUseCaseDescription("Test Description");
        dto.setAction("Test Action");
        dto.setUserId("Test User");
        dto.setDeliveredPiId(1);
        dto.setCritical(1);
        dto.setIsRegulatory(1);
        dto.setUseCaseScope(1);
        dto.setOperativeModel(1);
        dto.setDomainId(0);

        IDataResult<?> result = useCaseService.validateRequest(dto);

        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("DomainId must not be null or 0", result.message);
    }

    @Test
    void testValidateRequest_DeliveredPiIdMustNotBeNullOrZero() {
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        dto.setUseCaseId(1);
        dto.setUseCaseName("Test Use Case");
        dto.setUseCaseDescription("Test Description");
        dto.setDomainId(1);
        dto.setAction("Test Action");
        dto.setUserId("Test User");
        dto.setCritical(1);
        dto.setIsRegulatory(1);
        dto.setUseCaseScope(1);
        dto.setOperativeModel(1);
        dto.setDeliveredPiId(0); // Invalid delivered PI ID

        IDataResult<?> result = useCaseService.validateRequest(dto);

        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("DeliveredPiId must not be null or 0", result.message);
    }

    @Test
    void testValidateRequest_CriticalMustNotBeNullOrZero() {
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        dto.setUseCaseId(1);
        dto.setUseCaseName("Test Use Case");
        dto.setUseCaseDescription("Test Description");
        dto.setDomainId(1);
        dto.setAction("Test Action");
        dto.setUserId("Test User");
        dto.setDeliveredPiId(1);
        dto.setIsRegulatory(1);
        dto.setUseCaseScope(1);
        dto.setOperativeModel(1);

        dto.setCritical(0);

        IDataResult<?> result = useCaseService.validateRequest(dto);

        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("Critical must not be null or 0", result.message);
    }

    @Test
    void testValidateRequest_RegulatoryMustNotBeNull() {
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        dto.setUseCaseId(1);
        dto.setUseCaseName("Test Use Case");
        dto.setUseCaseDescription("Test Description");
        dto.setDomainId(1);
        dto.setAction("Test Action");
        dto.setUserId("Test User");
        dto.setDeliveredPiId(1);
        dto.setCritical(1);
        dto.setUseCaseScope(1);
        dto.setOperativeModel(1);

        dto.setIsRegulatory(null);

        IDataResult<?> result = useCaseService.validateRequest(dto);

        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("Regulatory must not be null", result.message);
    }

    @Test
    void testValidateRequest_UseCaseScopeMustNotBeNullOrZero() {
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        dto.setUseCaseId(1);
        dto.setUseCaseName("Test Use Case");
        dto.setUseCaseDescription("Test Description");
        dto.setDomainId(1);
        dto.setAction("Test Action");
        dto.setUserId("Test User");
        dto.setDeliveredPiId(1);
        dto.setCritical(1);
        dto.setIsRegulatory(1);
        dto.setOperativeModel(1);
        dto.setUseCaseScope(0);

        IDataResult<?> result = useCaseService.validateRequest(dto);

        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("UseCaseScope must not be null or 0", result.message);
    }

    @Test
    void testValidateRequest_OperativeModelMustNotBeNull() {
        UpdateOrInsertUseCaseDtoRequest dto = new UpdateOrInsertUseCaseDtoRequest();
        dto.setUseCaseId(1);
        dto.setUseCaseName("Test Use Case");
        dto.setUseCaseDescription("Test Description");
        dto.setDomainId(1);
        dto.setAction("Test Action");
        dto.setUserId("Test User");
        dto.setDeliveredPiId(1);
        dto.setCritical(1);
        dto.setIsRegulatory(1);
        dto.setUseCaseScope(1);
        dto.setOperativeModel(null);

        IDataResult<?> result = useCaseService.validateRequest(dto);

        assertNotNull(result);
        assertFalse(result.success);
        assertEquals("OperativeModel must not be null", result.message);
    }

    @Test
    void testGenerateDocumentUseCases() {
        UseCaseInputsFilterDtoRequest dto = new UseCaseInputsFilterDtoRequest();

        UseCaseInputsDtoResponse useCaseMock = new UseCaseInputsDtoResponse();
        useCaseMock.setDomainName("Dominio 1");
        useCaseMock.setUseCaseName("Caso de Uso 1");
        useCaseMock.setUseCaseDescription("Descripción 1");
        useCaseMock.setProjectCount(2);
        useCaseMock.setProjects("Proyecto A, Proyecto B");
        useCaseMock.setPiLargeName("Q3 2025");
        useCaseMock.setCriticalDesc("Alta");
        useCaseMock.setIsRegulatory(1);
        useCaseMock.setUseCaseScopeDesc("Global");
        useCaseMock.setOperativeModel(1);

        when(useCaseReliabilityDaoMock.listAllFilteredUseCases(dto)).thenReturn(List.of(useCaseMock));

        byte[] result = useCaseService.generateDocumentUseCases(dto);

        assertNotNull(result);
        assertTrue(result.length > 0);

        assertEquals((byte)0x50, result[0]);
        assertEquals((byte)0x4B, result[1]);
    }
}
