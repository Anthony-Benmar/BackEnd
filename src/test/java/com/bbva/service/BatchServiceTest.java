package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.BatchDao;
import com.bbva.dto.batch.request.InsertJobExecutionActiveRequest;
import com.bbva.dto.batch.request.InsertJobExecutionStatusRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
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
    void testGetLastJobExecutionStatusDate() throws Exception {
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

    private List<InsertJobExecutionStatusRequest> getInsertJobExecutionStatusRequestList(){
        InsertJobExecutionStatusRequest request1 = new InsertJobExecutionStatusRequest();
        request1.setJobName("JobA");
        request1.setSchedtable("ScheduleTable1");
        request1.setApplication("ApplicationX");
        request1.setSubApplication("SubApplicationY");
        request1.setRunAs("User1");
        request1.setOrderId("ORD123");
        request1.setOdate("2025-03-26");
        request1.setStartTime("14:00");
        request1.setEndTime("14:30");
        request1.setRunTime("30");
        request1.setRunCounter("1");
        request1.setEndedStatus("SUCCESS");
        request1.setHost("HostA");
        request1.setCputime("5");

        InsertJobExecutionStatusRequest request2 = new InsertJobExecutionStatusRequest();
        request2.setJobName("JobB");
        request2.setSchedtable("ScheduleTable2");
        request2.setApplication("ApplicationY");
        request2.setSubApplication("SubApplicationZ");
        request2.setRunAs("User2");
        request2.setOrderId("ORD456");
        request2.setOdate("2025-03-26");
        request2.setStartTime("15:00");
        request2.setEndTime("15:45");
        request2.setRunTime("45");
        request2.setRunCounter("2");
        request2.setEndedStatus("FAILED");
        request2.setHost("HostB");
        request2.setCputime("10");

        return List.of(request1,
                request2);
    }

    private List<InsertJobExecutionActiveRequest> getInsertJobExcecutionActiveRequestList(){
        InsertJobExecutionActiveRequest request1 = new InsertJobExecutionActiveRequest();
        request1.setOrderId("ORD001");
        request1.setJobName("DataProcessingJob");
        request1.setSchedtable("DailySchedule");
        request1.setApplication("DataWarehouse");
        request1.setSubApplication("ETLPipeline");
        request1.setOdate("2025-03-26");
        request1.setStartTime("10:00");
        request1.setEndTime("10:30");
        request1.setHost("ServerA");
        request1.setRunAs("batchUser");
        request1.setStatus("SUCCESS");

        InsertJobExecutionActiveRequest request2 = new InsertJobExecutionActiveRequest();
        request2.setOrderId("ORD002");
        request2.setJobName("CleanupJob");
        request2.setSchedtable("WeeklyCleanup");
        request2.setApplication("DataPlatform");
        request2.setSubApplication("CleanupModule");
        request2.setOdate("2025-03-26");
        request2.setStartTime("01:00");
        request2.setEndTime("01:45");
        request2.setHost("ServerB");
        request2.setRunAs("adminUser");
        request2.setStatus("FAILED");

        return List.of(request1,request2);
    }

    private String getLastJobExecutionStatusDate(){
        return "2024-10-17 14:35:44";
    }

    private IDataResult<String> getResponseDto() {
        return new SuccessDataResult<>("");
    }
}
