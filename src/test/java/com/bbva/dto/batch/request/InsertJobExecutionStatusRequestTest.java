package com.bbva.dto.batch.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsertJobExecutionStatusRequestTest {

    @Test
    void testGettersAndSetters() {
        InsertJobExecutionStatusRequest request = new InsertJobExecutionStatusRequest();
        request.setJobName("JobName1");
        request.setSchedtable("SchedTable1");
        request.setApplication("Application1");
        request.setSubApplication("SubApp1");
        request.setRunAs("RunAs1");
        request.setOrderId("ORD123");
        request.setOdate("2025-03-27");
        request.setStartTime("10:00");
        request.setEndTime("10:30");
        request.setRunTime("30 mins");
        request.setRunCounter("1");
        request.setEndedStatus("SUCCESS");
        request.setHost("Host1");
        request.setCputime("10s");

        assertEquals("JobName1", request.getJobName());
        assertEquals("SchedTable1", request.getSchedtable());
        assertEquals("Application1", request.getApplication());
        assertEquals("SubApp1", request.getSubApplication());
        assertEquals("RunAs1", request.getRunAs());
        assertEquals("ORD123", request.getOrderId());
        assertEquals("2025-03-27", request.getOdate());
        assertEquals("10:00", request.getStartTime());
        assertEquals("10:30", request.getEndTime());
        assertEquals("30 mins", request.getRunTime());
        assertEquals("1", request.getRunCounter());
        assertEquals("SUCCESS", request.getEndedStatus());
        assertEquals("Host1", request.getHost());
        assertEquals("10s", request.getCputime());
    }
}
