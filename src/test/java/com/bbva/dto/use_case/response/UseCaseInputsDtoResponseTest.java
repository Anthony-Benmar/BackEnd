package com.bbva.dto.use_case.response;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UseCaseInputsDtoResponseTest {
    @Test
    void testUseCaseInputsDtoResponse() {
        UseCaseInputsDtoResponse useCaseInputsDtoResponse = new UseCaseInputsDtoResponse();
        useCaseInputsDtoResponse.setUseCaseId(1);
        useCaseInputsDtoResponse.setDomainName("Domain Name");
        useCaseInputsDtoResponse.setDomainId(2);
        useCaseInputsDtoResponse.setUseCaseName("Use Case Name");
        useCaseInputsDtoResponse.setUseCaseDescription("Use Case Description");
        useCaseInputsDtoResponse.setProjectCount(3);
        useCaseInputsDtoResponse.setProjects("Project 1, Project 2");
        useCaseInputsDtoResponse.setDeliveredPiId(1);
        useCaseInputsDtoResponse.setPiLargeName("Pi Large Name");
        useCaseInputsDtoResponse.setCritical(1);
        useCaseInputsDtoResponse.setCriticalDesc("Critical Description");
        useCaseInputsDtoResponse.setIsRegulatory(1);
        useCaseInputsDtoResponse.setRegulatoryDesc("Regulatory Description");
        useCaseInputsDtoResponse.setUseCaseScope(1);
        useCaseInputsDtoResponse.setUseCaseScopeDesc("Use Case Scope Description");
        useCaseInputsDtoResponse.setOperativeModel(2);
        useCaseInputsDtoResponse.setOperativeModelDesc("Operative Model Description");

        assertEquals(1, useCaseInputsDtoResponse.getUseCaseId());
        assertEquals("Domain Name", useCaseInputsDtoResponse.getDomainName());
        assertEquals(2, useCaseInputsDtoResponse.getDomainId());
        assertEquals("Use Case Name", useCaseInputsDtoResponse.getUseCaseName());
        assertEquals("Use Case Description", useCaseInputsDtoResponse.getUseCaseDescription());
        assertEquals(3, useCaseInputsDtoResponse.getProjectCount());
        assertEquals("Project 1, Project 2", useCaseInputsDtoResponse.getProjects());
        assertEquals(1, useCaseInputsDtoResponse.getDeliveredPiId());
        assertEquals("Pi Large Name", useCaseInputsDtoResponse.getPiLargeName());
        assertEquals(1, useCaseInputsDtoResponse.getCritical());
        assertEquals("Critical Description", useCaseInputsDtoResponse.getCriticalDesc());
        assertEquals(1, useCaseInputsDtoResponse.getIsRegulatory());
        assertEquals("Regulatory Description", useCaseInputsDtoResponse.getRegulatoryDesc());
        assertEquals(1, useCaseInputsDtoResponse.getUseCaseScope());
        assertEquals("Use Case Scope Description", useCaseInputsDtoResponse.getUseCaseScopeDesc());
        assertEquals(2, useCaseInputsDtoResponse.getOperativeModel());
        assertEquals("Operative Model Description", useCaseInputsDtoResponse.getOperativeModelDesc());
    }
}