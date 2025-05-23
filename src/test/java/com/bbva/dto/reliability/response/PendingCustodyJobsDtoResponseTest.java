package com.bbva.dto.reliability.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PendingCustodyJobsDtoResponseTest {

    @Test
    void testPendingCustodyJobsDtoResponse() {
        PendingCustodyJobsDtoResponse response = new PendingCustodyJobsDtoResponse();

        response.setJobName("JobCustody001");
        response.setJsonName("jobCustody001.json");
        response.setFrequency("Weekly");
        response.setJobType("Batch");
        response.setOriginType("Internal");
        response.setPhaseType("Development");

        assertEquals("JobCustody001", response.getJobName());
        assertEquals("jobCustody001.json", response.getJsonName());
        assertEquals("Weekly", response.getFrequency());
        assertEquals("Batch", response.getJobType());
        assertEquals("Internal", response.getOriginType());
        assertEquals("Development", response.getPhaseType());
    }
}
