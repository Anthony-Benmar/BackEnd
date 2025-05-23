package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.ReliabilityDao;
import com.bbva.dto.reliability.request.InventoryInputsFilterDtoRequest;
import com.bbva.dto.reliability.request.InventoryJobUpdateDtoRequest;
import com.bbva.dto.reliability.response.ExecutionValidationDtoResponse;
import com.bbva.dto.reliability.response.InventoryInputsFilterDtoResponse;
import com.bbva.dto.reliability.response.PendingCustodyJobsDtoResponse;
import com.bbva.dto.reliability.response.ProjectCustodyInfoDtoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReliabilityServiceTest {
    private ReliabilityService reliabilityService;
    private ReliabilityDao reliabilityDaoMock;


    @BeforeEach
    void setUp() throws Exception {
        reliabilityService = new ReliabilityService();
        reliabilityDaoMock = mock(ReliabilityDao.class);
        var field = ReliabilityService.class.getDeclaredField("reliabilityDao");
        field.setAccessible(true);
        field.set(reliabilityService, reliabilityDaoMock);
    }

    @Test
    void testInventoryInputsFilterSuccess() {
        InventoryInputsFilterDtoRequest dto = new InventoryInputsFilterDtoRequest();
        InventoryInputsFilterDtoResponse mockResponse = new InventoryInputsFilterDtoResponse();

        when(reliabilityDaoMock.inventoryInputsFilter(dto)).thenReturn(mockResponse);

        IDataResult<InventoryInputsFilterDtoResponse> result = reliabilityService.inventoryInputsFilter(dto);

        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(mockResponse, result.data);
        verify(reliabilityDaoMock).inventoryInputsFilter(dto);
    }

    @Test
    void testUpdateInventoryJobStockSuccess() {
        InventoryJobUpdateDtoRequest dto = new InventoryJobUpdateDtoRequest();

        doNothing().when(reliabilityDaoMock).updateInventoryJobStock(dto);

        IDataResult<Void> result = reliabilityService.updateInventoryJobStock(dto);

        assertNotNull(result);
        assertTrue(result.success);
        assertNull(result.data);
        assertEquals("Job stock updated successfully", result.message);
        verify(reliabilityDaoMock).updateInventoryJobStock(dto);
    }

    @Test
    void testUpdateInventoryJobStockError() {
        InventoryJobUpdateDtoRequest dto = new InventoryJobUpdateDtoRequest();
        doThrow(new RuntimeException("Database error")).when(reliabilityDaoMock).updateInventoryJobStock(dto);

        IDataResult<Void> result = reliabilityService.updateInventoryJobStock(dto);

        assertNotNull(result);
        assertFalse(result.success);
        assertNull(result.data);
        assertEquals("500", result.status);
        assertEquals("Database error", result.message);
        verify(reliabilityDaoMock).updateInventoryJobStock(dto);
    }

    @Test
    void testGetPendingCustodyJobsSuccess() {
        String sdatoolId = "123";
        List<PendingCustodyJobsDtoResponse> mockList = List.of(new PendingCustodyJobsDtoResponse());

        when(reliabilityDaoMock.getPendingCustodyJobs(sdatoolId)).thenReturn(mockList);

        var result = reliabilityService.getPendingCustodyJobs(sdatoolId);

        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(mockList, result.data);
        verify(reliabilityDaoMock).getPendingCustodyJobs(sdatoolId);
    }

    @Test
    void testGetPendingCustodyJobsError() {
        String sdatoolId = "123";
        when(reliabilityDaoMock.getPendingCustodyJobs(sdatoolId)).thenThrow(new RuntimeException("Error fetching jobs"));

        IDataResult<List<PendingCustodyJobsDtoResponse>> result = reliabilityService.getPendingCustodyJobs(sdatoolId);

        assertNotNull(result);
        assertFalse(result.success);
        assertNull(result.data);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
        assertEquals("Error fetching jobs", result.message);
        verify(reliabilityDaoMock).getPendingCustodyJobs(sdatoolId);
    }

    @Test
    void testGetProjectCustodyInfoSuccess() {
        String sdatoolId = "123";
        List<ProjectCustodyInfoDtoResponse> mockList = List.of(new ProjectCustodyInfoDtoResponse());

        when(reliabilityDaoMock.getProjectCustodyInfo(sdatoolId)).thenReturn(mockList);

        var result = reliabilityService.getProjectCustodyInfo(sdatoolId);

        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(mockList, result.data);
        verify(reliabilityDaoMock).getProjectCustodyInfo(sdatoolId);
    }

    @Test
    void testGetExecutionValidationSuccess() {
        String jobName = "job1";
        ExecutionValidationDtoResponse mockResponse = new ExecutionValidationDtoResponse();

        when(reliabilityDaoMock.getExecutionValidation(jobName)).thenReturn(mockResponse);

        var result = reliabilityService.getExecutionValidation(jobName);

        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(mockResponse, result.data);
        verify(reliabilityDaoMock).getExecutionValidation(jobName);
    }
    @Test
    void testGetExecutionValidationError() {
        String jobName = "job1";
        when(reliabilityDaoMock.getExecutionValidation(jobName)).thenThrow(new RuntimeException("Error fetching validation"));

        IDataResult<ExecutionValidationDtoResponse> result = reliabilityService.getExecutionValidation(jobName);

        assertNotNull(result);
        assertFalse(result.success);
        assertNull(result.data);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
        assertEquals("Error fetching validation", result.message);
        verify(reliabilityDaoMock).getExecutionValidation(jobName);
    }
    @Test
    void testGetProjectCustodyInfoError() {
        String sdatoolId = "123";
        when(reliabilityDaoMock.getProjectCustodyInfo(sdatoolId)).thenThrow(new RuntimeException("Error fetching project info"));

        IDataResult<List<ProjectCustodyInfoDtoResponse>> result = reliabilityService.getProjectCustodyInfo(sdatoolId);

        assertNotNull(result);
        assertFalse(result.success);
        assertNull(result.data);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
        assertEquals("Error fetching project info", result.message);
        verify(reliabilityDaoMock).getProjectCustodyInfo(sdatoolId);
    }
}
