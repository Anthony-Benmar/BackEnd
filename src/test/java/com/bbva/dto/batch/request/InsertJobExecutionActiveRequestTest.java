package com.bbva.dto.batch.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsertJobExecutionActiveRequestTest {

    @Test
    void testGettersAndSetters() {
        InsertJobExecutionActiveRequest request = new InsertJobExecutionActiveRequest();
        request.setOrderId("ORD001");
        request.setJobName("JobName1");
        request.setSchedtable("SchedTable1");
        request.setApplication("Application1");
        request.setSubApplication("SubApp1");
        request.setOdate("2025-03-27");
        request.setStartTime("08:00");
        request.setEndTime("08:30");
        request.setHost("Host1");
        request.setRunAs("RunAs1");
        request.setStatus("SUCCESS");

        assertEquals("ORD001", request.getOrderId());
        assertEquals("JobName1", request.getJobName());
        assertEquals("SchedTable1", request.getSchedtable());
        assertEquals("Application1", request.getApplication());
        assertEquals("SubApp1", request.getSubApplication());
        assertEquals("2025-03-27", request.getOdate());
        assertEquals("08:00", request.getStartTime());
        assertEquals("08:30", request.getEndTime());
        assertEquals("Host1", request.getHost());
        assertEquals("RunAs1", request.getRunAs());
        assertEquals("SUCCESS", request.getStatus());
    }
}
