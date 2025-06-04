package com.bbva.dto.reliability.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PendingCustodyJobsDtoResponseTest {

    @Test
    void testPendingCustodyJobsDtoResponse() {
        PendingCustodyJobsDtoResponse response = new PendingCustodyJobsDtoResponse();

        response.setJobName("JobCustody001");
        response.setJsonName("jobCustody001.json");
        response.setFrequencyId("Weekly");
        response.setJobTypeId("Batch");
        response.setOriginTypeId("Internal");
        response.setPhaseTypeId("Development");
        response.setPrincipalJob("Principal Job");

        assertEquals("JobCustody001", response.getJobName());
        assertEquals("jobCustody001.json", response.getJsonName());
        assertEquals("Weekly", response.getFrequencyId());
        assertEquals("Batch", response.getJobTypeId());
        assertEquals("Internal", response.getOriginTypeId());
        assertEquals("Development", response.getPhaseTypeId());
        assertEquals("Principal Job", response.getPrincipalJob());
    }
}
