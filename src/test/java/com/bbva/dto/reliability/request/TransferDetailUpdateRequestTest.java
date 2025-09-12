package com.bbva.dto.reliability.request;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransferDetailUpdateRequestTest {

    @Test
    void testHeaderSettersAndGetters() {
        TransferDetailUpdateRequest.Header header = new TransferDetailUpdateRequest.Header();
        header.setDomainId(1);
        header.setUseCaseId(2);
        header.setComments("Comentario de prueba");

        assertEquals(1, header.getDomainId());
        assertEquals(2, header.getUseCaseId());
        assertEquals("Comentario de prueba", header.getComments());
    }

    @Test
    void testJobSettersAndGetters() {
        TransferDetailUpdateRequest.Job job = new TransferDetailUpdateRequest.Job();
        job.setJobName("JobTest");
        job.setComponentName("ComponentX");
        job.setFrequencyId(10);
        job.setInputPaths("/input/path");
        job.setOutputPath("/output/path");
        job.setJobTypeId(5);
        job.setUseCaseId(3);
        job.setIsCritical("Y");
        job.setDomainId(7);
        job.setBitBucketUrl("http://bitbucket/repo");
        job.setResponsible("John Doe");
        job.setJobPhaseId(4);
        job.setOriginTypeId(8);
        job.setException("NullPointer");
        job.setComments("Job comment");
        // NUEVOS CAMPOS
        job.setAplicativoSip("SIP-MOD");
        job.setDetails("Detalle extendido para validación");

        assertEquals("JobTest", job.getJobName());
        assertEquals("ComponentX", job.getComponentName());
        assertEquals(10, job.getFrequencyId());
        assertEquals("/input/path", job.getInputPaths());
        assertEquals("/output/path", job.getOutputPath());
        assertEquals(5, job.getJobTypeId());
        assertEquals(3, job.getUseCaseId());
        assertEquals("Y", job.getIsCritical());
        assertEquals(7, job.getDomainId());
        assertEquals("http://bitbucket/repo", job.getBitBucketUrl());
        assertEquals("John Doe", job.getResponsible());
        assertEquals(4, job.getJobPhaseId());
        assertEquals(8, job.getOriginTypeId());
        assertEquals("NullPointer", job.getException());
        assertEquals("Job comment", job.getComments());
        // Asserts nuevos
        assertEquals("SIP-MOD", job.getAplicativoSip());
        assertEquals("Detalle extendido para validación", job.getDetails());
    }

    @Test
    void testTransferDetailUpdateRequestWithHeaderAndJobs() {
        TransferDetailUpdateRequest request = new TransferDetailUpdateRequest();

        TransferDetailUpdateRequest.Header header = new TransferDetailUpdateRequest.Header();
        header.setDomainId(100);
        header.setUseCaseId(200);
        header.setComments("Header comments");

        TransferDetailUpdateRequest.Job job = new TransferDetailUpdateRequest.Job();
        job.setJobName("MainJob");
        job.setAplicativoSip("APP-MAIN");
        job.setDetails("Detalle del job principal");

        request.setHeader(header);
        request.setJobs(List.of(job));

        assertNotNull(request.getHeader());
        assertEquals(100, request.getHeader().getDomainId());
        assertEquals(200, request.getHeader().getUseCaseId());
        assertEquals("Header comments", request.getHeader().getComments());

        assertNotNull(request.getJobs());
        assertEquals(1, request.getJobs().size());
        assertEquals("MainJob", request.getJobs().get(0).getJobName());
        assertEquals("APP-MAIN", request.getJobs().get(0).getAplicativoSip());
        assertEquals("Detalle del job principal", request.getJobs().get(0).getDetails());
    }
}