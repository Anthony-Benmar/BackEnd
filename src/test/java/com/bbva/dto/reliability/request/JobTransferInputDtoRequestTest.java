package com.bbva.dto.reliability.request;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JobTransferInputDtoRequestTest {

    @Test
    void testJobTransferInputDtoRequest() {
        JobTransferInputDtoRequest request = new JobTransferInputDtoRequest();

        request.setJobTypeId(1);
        request.setJobName("Data Migration Job");
        request.setComponentName("MigrationComponent");
        request.setFrequencyId(2);
        request.setBitbucketUrl("https://bitbucket.org/project/repo");
        request.setInputPaths("/input/path1,/input/path2");
        request.setOutputPath("/output/path");
        request.setResponsible("john.doe@company.com");
        request.setComments("Initial migration job");
        request.setJobPhaseId(3);
        request.setOriginTypeId(4);
        request.setUseCaseId(101);
        request.setIsCritical("Yes");
        request.setDomainId(5);
        request.setPack("com.company.migration");
        request.setStatusId(1);

        assertEquals(1, request.getJobTypeId());
        assertEquals("Data Migration Job", request.getJobName());
        assertEquals("MigrationComponent", request.getComponentName());
        assertEquals(2, request.getFrequencyId());
        assertEquals("https://bitbucket.org/project/repo", request.getBitbucketUrl());
        assertEquals("/input/path1,/input/path2", request.getInputPaths());
        assertEquals("/output/path", request.getOutputPath());
        assertEquals("john.doe@company.com", request.getResponsible());
        assertEquals("Initial migration job", request.getComments());
        assertEquals(3, request.getJobPhaseId());
        assertEquals(4, request.getOriginTypeId());
        assertEquals(101, request.getUseCaseId());
        assertEquals("Yes", request.getIsCritical());
        assertEquals(5, request.getDomainId());
        assertEquals("com.company.migration", request.getPack());
        assertEquals(1, request.getStatusId());
    }
}
