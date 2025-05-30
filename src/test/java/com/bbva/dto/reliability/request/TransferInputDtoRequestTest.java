package com.bbva.dto.reliability.request;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TransferInputDtoRequestTest {

    @Test
    void testTransferInputDtoRequest() {
        TransferInputDtoRequest request = new TransferInputDtoRequest();

        JobTransferInputDtoRequest job1 = new JobTransferInputDtoRequest();
        job1.setJobName("ETL Job 1");
        job1.setJobTypeId(1);

        JobTransferInputDtoRequest job2 = new JobTransferInputDtoRequest();
        job2.setJobName("Data Migration");
        job2.setJobTypeId(2);

        List<JobTransferInputDtoRequest> jobList = Arrays.asList(job1, job2);

        request.setPack("com.company.transfer");
        request.setDomainId(5);
        request.setProductOwnerUserId(1001);
        request.setUseCaseId(201);
        request.setProjectId(301);
        request.setCreatorUserId(1002);
        request.setPdfLink("http://documents/transfer.pdf");
        request.setJobCount(2);
        request.setTransferInputDtoRequests(jobList);

        assertEquals("com.company.transfer", request.getPack());
        assertEquals(5, request.getDomainId());
        assertEquals(1001, request.getProductOwnerUserId());
        assertEquals(201, request.getUseCaseId());
        assertEquals(301, request.getProjectId());
        assertEquals(1002, request.getCreatorUserId());
        assertEquals("http://documents/transfer.pdf", request.getPdfLink());
        assertEquals(2, request.getJobCount());

        assertNotNull(request.getTransferInputDtoRequests());
        assertEquals(2, request.getTransferInputDtoRequests().size());
        assertEquals("ETL Job 1", request.getTransferInputDtoRequests().get(0).getJobName());
        assertEquals(1, request.getTransferInputDtoRequests().get(0).getJobTypeId());
        assertEquals("Data Migration", request.getTransferInputDtoRequests().get(1).getJobName());
    }
}
