package com.bbva.dto.reliability.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InventoryInputsDtoResponseTest {
    @Test
    void testInventoryInputsDtoResponse() {
        InventoryInputsDtoResponse response = new InventoryInputsDtoResponse();

        String[] inputPathsArray = {"/input/path1", "/input/path2"};

        response.setDomainName("Data Platform");
        response.setUseCase("ETL for Sales");
        response.setJobName("Sales ETL Job");
        response.setComponentName("IngestorComponent");
        response.setJobType("Batch");
        response.setIsCritical("Yes");
        response.setFrequency("Daily");
        response.setInputPaths("/input/path1;/input/path2");
        response.setInputPathsArray(inputPathsArray);
        response.setOutputPath("/output/data");
        response.setJobPhase("Execution");
        response.setDomainId(1);
        response.setUseCaseId(101);
        response.setFrequencyId(3);
        response.setJobTypeId(2);
        response.setBitBucketUrl("https://bitbucket.org/project/repo");
        response.setPack("DataPack1");

        assertEquals("Data Platform", response.getDomainName());
        assertEquals("ETL for Sales", response.getUseCase());
        assertEquals("Sales ETL Job", response.getJobName());
        assertEquals("IngestorComponent", response.getComponentName());
        assertEquals("Batch", response.getJobType());
        assertEquals("Yes", response.getIsCritical());
        assertEquals("Daily", response.getFrequency());
        assertEquals("/input/path1;/input/path2", response.getInputPaths());
        assertArrayEquals(inputPathsArray, response.getInputPathsArray());
        assertEquals("/output/data", response.getOutputPath());
        assertEquals("Execution", response.getJobPhase());
        assertEquals(1, response.getDomainId());
        assertEquals(101, response.getUseCaseId());
        assertEquals(3, response.getFrequencyId());
        assertEquals(2, response.getJobTypeId());
        assertEquals("https://bitbucket.org/project/repo", response.getBitBucketUrl());
        assertEquals("DataPack1", response.getPack());
    }
}
