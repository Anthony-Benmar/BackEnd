package com.bbva.dto.reliability.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InventoryJobUpdateDtoRequestTest {
    @Test
    void testInventoryJobUpdateDtoRequest() {
        InventoryJobUpdateDtoRequest request = new InventoryJobUpdateDtoRequest();

        request.setJobName("Daily ETL Job");
        request.setComponentName("IngestionComponent");
        request.setFrequencyId(3);
        request.setBitBucketUrl("https://www.google.com");
        request.setInputPaths("/data/input/");
        request.setOutputPath("/data/output/");
        request.setJobTypeId(2);
        request.setUseCaseId(101);
        request.setIsCritical("Yes");
        request.setDomainId(5);

        assertEquals("Daily ETL Job", request.getJobName());
        assertEquals("IngestionComponent", request.getComponentName());
        assertEquals(3, request.getFrequencyId());
        assertEquals("https://www.google.com", request.getBitBucketUrl());
        assertEquals("/data/input/", request.getInputPaths());
        assertEquals("/data/output/", request.getOutputPath());
        assertEquals(2, request.getJobTypeId());
        assertEquals(101, request.getUseCaseId());
        assertEquals("Yes", request.getIsCritical());
        assertEquals(5, request.getDomainId());
    }
}
