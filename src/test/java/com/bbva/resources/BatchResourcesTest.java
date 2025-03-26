package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.batch.request.InsertJobExecutionStatusRequest;
import com.bbva.service.BatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BatchResourcesTest {

    private BatchService batchServiceMock;
    private BatchResources batchResources;

    @BeforeEach
    void setUp() throws Exception {
        batchResources = new BatchResources();
        batchServiceMock = Mockito.mock(BatchService.class);
        Field serviceField = BatchResources.class.getDeclaredField("batchService");
        serviceField.setAccessible(true);
        serviceField.set(batchResources, batchServiceMock);
    }

    @Test
    void testGetLastJobExecutionStatusDate() throws Exception {
        when(batchServiceMock.getLastJobExecutionStatusDate()).thenReturn(getResponseDto());
        IDataResult<String> response = batchResources.getLastJobExecutionStatusDate();
        assertEquals(String.valueOf(Response.Status.OK.getStatusCode()), response.status);
    }

    @Test
    void testSaveJobExecutionStatus() {
        List<InsertJobExecutionStatusRequest> requestList = getInsertJobExecutionStatusRequestList();
        when(batchServiceMock.saveJobExecutionStatus(requestList))
                .thenReturn(new SuccessDataResult<>(null));
        IDataResult<Void> result = batchServiceMock.saveJobExecutionStatus(requestList);
        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("Succesfull", result.message);
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

    private IDataResult<String> getResponseDto() {
        return new SuccessDataResult<> ("");
    }

}
