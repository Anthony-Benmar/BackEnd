package com.bbva.dto.reliability.request;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransferInputDtoRequestTest {

    @Test
    void testTransferInputDtoRequest_withEmailAndJobs() {
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
        request.setProductOwnerEmail("po@bbva.com");
        request.setUseCaseId(201);
        request.setProjectId(301);
        request.setCreatorUserId(1002);
        request.setPdfLink("http://documents/transfer.pdf");
        request.setJobCount(2);
        request.setStatusId(3);
        request.setSn2(21);
        request.setDataOwnerEmail("data.owner@bbva.com"); // nuevo
        request.setTransferInputDtoRequests(jobList);

        assertEquals("com.company.transfer", request.getPack());
        assertEquals(5, request.getDomainId());
        assertEquals("po@bbva.com", request.getProductOwnerEmail());
        assertEquals(201, request.getUseCaseId());
        assertEquals(301, request.getProjectId());
        assertEquals(1002, request.getCreatorUserId());
        assertEquals("http://documents/transfer.pdf", request.getPdfLink());
        assertEquals(2, request.getJobCount());
        assertEquals(3, request.getStatusId());
        assertEquals(21, request.getSn2());
        assertEquals("data.owner@bbva.com", request.getDataOwnerEmail()); // nuevo

        assertNotNull(request.getTransferInputDtoRequests());
        assertEquals(2, request.getTransferInputDtoRequests().size());
        assertEquals("ETL Job 1", request.getTransferInputDtoRequests().get(0).getJobName());
        assertEquals(1, request.getTransferInputDtoRequests().get(0).getJobTypeId());
        assertEquals("Data Migration", request.getTransferInputDtoRequests().get(1).getJobName());
        assertEquals(2, request.getTransferInputDtoRequests().get(1).getJobTypeId());
    }

    @Test
    void testTransferInputDtoRequest_nullablesAreAllowed() {
        TransferInputDtoRequest request = new TransferInputDtoRequest();
        request.setPack("PCK-1");
        request.setProductOwnerEmail(null);
        request.setPdfLink(null);
        request.setTransferInputDtoRequests(null);
        request.setDataOwnerEmail(null); // nuevo

        assertEquals("PCK-1", request.getPack());
        assertNull(request.getProductOwnerEmail());
        assertNull(request.getPdfLink());
        assertNull(request.getTransferInputDtoRequests());
        assertNull(request.getDataOwnerEmail()); // nuevo
    }
}