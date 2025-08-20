package com.bbva.dto.reliability.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateJobDtoRequestTest {

    @Test
    void settersAndGettersWork() {
        UpdateJobDtoRequest dto = new UpdateJobDtoRequest();

        dto.setActorRole("SM");
        dto.setPack("@-AEAL_PACK_004_25.07.2025");
        dto.setJobName("CTKCP4041");
        dto.setComponentName("comp-x");
        dto.setFrequencyId(7);
        dto.setInputPaths("/in/a;/in/b");
        dto.setOutputPath("/out/x");
        dto.setJobTypeId(2);
        dto.setUseCaseId(12);
        dto.setIsCritical("SI");
        dto.setDomainId(3);
        dto.setBitBucketUrl("https://bitbucket/bbva/repo");
        dto.setResponsible("jdoe");
        dto.setJobPhaseId(1);
        dto.setOriginTypeId(1004);
        dto.setException("sin_excepcion");
        dto.setComments("nota de prueba"); // <-- nuevo campo

        assertEquals("SM", dto.getActorRole());
        assertEquals("@-AEAL_PACK_004_25.07.2025", dto.getPack());
        assertEquals("CTKCP4041", dto.getJobName());
        assertEquals("comp-x", dto.getComponentName());
        assertEquals(7, dto.getFrequencyId());
        assertEquals("/in/a;/in/b", dto.getInputPaths());
        assertEquals("/out/x", dto.getOutputPath());
        assertEquals(2, dto.getJobTypeId());
        assertEquals(12, dto.getUseCaseId());
        assertEquals("SI", dto.getIsCritical());
        assertEquals(3, dto.getDomainId());
        assertEquals("https://bitbucket/bbva/repo", dto.getBitBucketUrl());
        assertEquals("jdoe", dto.getResponsible());
        assertEquals(1, dto.getJobPhaseId());
        assertEquals(1004, dto.getOriginTypeId());
        assertEquals("sin_excepcion", dto.getException());
        assertEquals("nota de prueba", dto.getComments()); // <-- aserción para comments
    }
}