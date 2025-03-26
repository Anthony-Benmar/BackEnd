package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.DataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.BatchDao;
import com.bbva.dto.batch.request.*;
import com.bbva.dto.batch.response.*;
import com.bbva.entities.InsertEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BatchServiceTest {

    private BatchService batchService;
    private BatchDao batchDaoMock;

    @BeforeEach
    void setUp() throws Exception {
        batchService = new BatchService();
        batchDaoMock = Mockito.mock(BatchDao.class);
        Field serviceField = BatchService.class.getDeclaredField("batchDao");
        serviceField.setAccessible(true);
        serviceField.set(batchService, batchDaoMock);
    }

    @Test
    void testGetLastJobExecutionStatusDate() {
        when(batchDaoMock.getLastJobExecutionStatusDate()).thenReturn(getLastJobExecutionStatusDate());
        IDataResult<String> result = batchService.getLastJobExecutionStatusDate();
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("Succesfull", result.message);
    }

    @Test
    void testSaveJobExecutionStatus() {
        List<InsertJobExecutionStatusRequest> requestList = getInsertJobExecutionStatusRequestList();
        doNothing().when(batchDaoMock).saveJobExecutionStatus(requestList);
        batchService.saveJobExecutionStatus(requestList);
        verify(batchDaoMock, times(1)).saveJobExecutionStatus(requestList);
    }

    @Test
    void testSaveJobExecutionStatusError() {
        List<InsertJobExecutionStatusRequest> requestList = getInsertJobExecutionStatusRequestList();
        doThrow(new RuntimeException("Database error")).when(batchDaoMock).saveJobExecutionStatus(requestList);
        IDataResult<Void> result = batchService.saveJobExecutionStatus(requestList);
        assertEquals("No se pudo realizar el registro: Database error", result.message);
        assertEquals("500", result.status);
    }

    @Test
    void testSaveJobExecutionActive() {
        List<InsertJobExecutionActiveRequest> requestList = getInsertJobExcecutionActiveRequestList();
        doNothing().when(batchDaoMock).saveJobExecutionActive(requestList);
        batchService.saveJobExecutionActive(requestList);
        verify(batchDaoMock, times(1)).saveJobExecutionActive(requestList);
    }

    @Test
    void testSaveJobExecutionActiveError() {
        List<InsertJobExecutionActiveRequest> requestList = getInsertJobExcecutionActiveRequestList();
        doThrow(new RuntimeException("Database error")).when(batchDaoMock).saveJobExecutionActive(requestList);
        IDataResult<Void> result = batchService.saveJobExecutionActive(requestList);
        assertEquals("No se pudo realizar el registro: Database error", result.message);
        assertEquals("500", result.status);
    }

    @Test
    void testFilter() {
        JobExecutionFilterRequestDTO request = new JobExecutionFilterRequestDTO();
        when(batchDaoMock.filter(request)).thenReturn(getFilterResponseDto());
        IDataResult<JobExecutionFilterResponseDTO> result = batchService.filter(request);
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("Succesfull", result.message);
    }

    @Test
    void testInsertReliabilityIncidence() {
        InsertReliabilityIncidenceDTO request = new InsertReliabilityIncidenceDTO();
        when(batchDaoMock.insertReliabilityIncidence(request)).thenReturn(getResponseInsertReliabilityIncidence());
        IDataResult<InsertEntity> result = batchService.insertReliabilityIncidence(request);
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("Job guardado correctamente", result.message);
    }

    @Test
    void testInsertAJIFJobExecution() {
        List<InsertAJIFJobExecutionRequest> request = new ArrayList<>();
        when(batchDaoMock.insertAJIFJobExecutionRequest(request)).thenReturn(getResponseInsertAJIFJobExecution());
        IDataResult<InsertAJIFJobExecutionResponseDTO> result = batchService.insertAJIFJobExecution(request);
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("Succesfull", result.message);
    }

    @Test
    void testGetStatusJobExecution() {
        String jobName = "";
        Integer quantity = 1;
        when(batchDaoMock.getStatusJobExecution(jobName,quantity)).thenReturn(getResponseGetStatusJobExecution());
        IDataResult<List<StatusJobExecutionDTO>> result = batchService.getStatusJobExecution(jobName,quantity);
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("Succesfull", result.message);
    }

    @Test
    void testGetJobExecutionById() {
        String folder = "";
        String orderId = "";
        String jobName = "";
        Integer runCounter = 1;
        when(batchDaoMock.getJobExecutionById(folder,orderId,jobName,runCounter)).thenReturn(getResponseGetJobExecutionById());
        IDataResult<JobExecutionByIdDTO> result = batchService.getJobExecutionById(folder,orderId,jobName,runCounter);
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("Succesfull", result.message);
    }

    @Test
    void testGetJobExecutionByIdError() {
        String folder = "";
        String orderId = "";
        String jobName = "";
        IDataResult<JobExecutionByIdDTO> result = batchService.getJobExecutionById(folder,orderId,jobName, null);
        assertNotNull(result);
        assertEquals("500", result.status);
        assertEquals("El campo runCounter es requerido", result.message);
    }

    @Test
    void testFilterIssueAction() {
        BatchIssuesActionFilterDtoRequest request = new BatchIssuesActionFilterDtoRequest();
        when(batchDaoMock.filterIssueAction(request)).thenReturn(getResponseFilterIssueAction());
        IDataResult<BatchIssuesActionFilterDtoResponse> result = batchService.filterIssueAction(request);
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("Succesfull", result.message);
    }

    private List<StatusJobExecutionDTO> getResponseGetStatusJobExecution() {
        return List.of(new StatusJobExecutionDTO(), new StatusJobExecutionDTO());
    }

    private List<InsertJobExecutionStatusRequest> getInsertJobExecutionStatusRequestList(){
        return List.of(new InsertJobExecutionStatusRequest(),
                new InsertJobExecutionStatusRequest());
    }

    private List<InsertJobExecutionActiveRequest> getInsertJobExcecutionActiveRequestList(){
        return List.of(new InsertJobExecutionActiveRequest(),new InsertJobExecutionActiveRequest());
    }

    private String getLastJobExecutionStatusDate(){
        return "2024-10-17 14:35:44";
    }

    private JobExecutionFilterResponseDTO getFilterResponseDto() {
        return new JobExecutionFilterResponseDTO();
    }

    private InsertEntity getResponseInsertReliabilityIncidence(){
        return new InsertEntity();
    }

    private DataResult<InsertAJIFJobExecutionResponseDTO> getResponseInsertAJIFJobExecution() {
        return new SuccessDataResult<>(new InsertAJIFJobExecutionResponseDTO());
    }

    private JobExecutionByIdDTO getResponseGetJobExecutionById() {
        return new JobExecutionByIdDTO();
    }

    private BatchIssuesActionFilterDtoResponse getResponseFilterIssueAction() {
        return new BatchIssuesActionFilterDtoResponse();
    }
}
