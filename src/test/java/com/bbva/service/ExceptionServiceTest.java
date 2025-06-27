package com.bbva.service;


import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.ExceptionBaseDao;
import com.bbva.dto.exception_base.request.ExceptionBasePaginationDtoRequest;
import com.bbva.dto.exception_base.request.ExceptionBaseReadOnlyDtoRequest;
import com.bbva.dto.exception_base.response.ExceptionBaseDataDtoResponse;
import com.bbva.dto.exception_base.response.ExceptionBasePaginatedResponseDTO;
import com.bbva.dto.exception_base.response.ExceptionBaseReadOnlyDtoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExceptionServiceTest {
    private ExceptionBaseDao exceptionBaseDao;
    private ExceptionBaseService exceptionBaseService;

    @BeforeEach
    void setUp() {
        exceptionBaseDao = mock(ExceptionBaseDao.class);
        exceptionBaseService = new ExceptionBaseService(exceptionBaseDao);
    }

    @Test
    void testGetExceptionsWithSource_returnsPaginatedResponse() {
        ExceptionBasePaginationDtoRequest request = new ExceptionBasePaginationDtoRequest();
        ExceptionBaseDataDtoResponse exception = new ExceptionBaseDataDtoResponse();
        exception.setId("1");

        when(exceptionBaseDao.getExceptionsWithSource(request)).thenReturn(List.of(exception));
        when(exceptionBaseDao.getExceptionsTotalCount(request)).thenReturn(1);

        IDataResult<ExceptionBasePaginatedResponseDTO> result = exceptionBaseService.getExceptionsWithSource(request);
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(1, result.data.getTotalCount());
        assertEquals("1", result.data.getData().get(0).getId());
    }

    @Test
    void testReadOnly_whenDataExists_returnsMappedResponse() {
        ExceptionBaseReadOnlyDtoRequest request = new ExceptionBaseReadOnlyDtoRequest();
        request.setId("123");

        ExceptionBaseDataDtoResponse dbResponse = new ExceptionBaseDataDtoResponse();
        dbResponse.setId("123");
        dbResponse.setTdsDescription("desc");

        when(exceptionBaseDao.getExceptionById("123")).thenReturn(dbResponse);

        IDataResult<ExceptionBaseReadOnlyDtoResponse> result = exceptionBaseService.readOnly(request);
        assertNotNull(result.data);
        assertEquals("123", result.data.getId());
        assertEquals("desc", result.data.getTdsDescription());
    }

    @Test
    void testReadOnly_whenDataNull_returnsEmptyResponse() {
        ExceptionBaseReadOnlyDtoRequest request = new ExceptionBaseReadOnlyDtoRequest();
        request.setId("999");

        when(exceptionBaseDao.getExceptionById("999")).thenReturn(null);

        IDataResult<ExceptionBaseReadOnlyDtoResponse> result = exceptionBaseService.readOnly(request);
        assertNotNull(result.data);
        assertNull(result.data.getId());  // vacía pero no null
    }

    @Test
    void testGetDistinctRequestingProjects_returnsList() {
        when(exceptionBaseDao.getDistinctRequestingProjects()).thenReturn(List.of("PROJ1", "PROJ2"));

        List<String> result = exceptionBaseService.getDistinctRequestingProjects();
        assertEquals(2, result.size());
        assertTrue(result.contains("PROJ1"));
    }

    @Test
    void testGetDistinctApprovalResponsibles_returnsList() {
        when(exceptionBaseDao.getDistinctApprovalResponsibles()).thenReturn(Collections.singletonList("Alice"));
        List<String> result = exceptionBaseService.getDistinctApprovalResponsibles();
        assertEquals(1, result.size());
    }

    @Test
    void testGetDistinctRegistrationDates_returnsList() {
        when(exceptionBaseDao.getDistinctRegistrationDates()).thenReturn(List.of("2024-01-01"));
        List<String> result = exceptionBaseService.getDistinctRegistrationDates();
        assertEquals(1, result.size());
    }

    @Test
    void testGetDistinctQuarterYearSprints_returnsList() {
        when(exceptionBaseDao.getDistinctQuarterYearSprints()).thenReturn(List.of("Q1-2024", "Q2-2024"));
        List<String> result = exceptionBaseService.getDistinctQuarterYearSprints();
        assertEquals(2, result.size());
    }
}
