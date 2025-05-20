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

        assertEquals(1, useCaseInputsDtoResponse.getUseCaseId());
        assertEquals("Domain Name", useCaseInputsDtoResponse.getDomainName());
        assertEquals(2, useCaseInputsDtoResponse.getDomainId());
        assertEquals("Use Case Name", useCaseInputsDtoResponse.getUseCaseName());
        assertEquals("Use Case Description", useCaseInputsDtoResponse.getUseCaseDescription());
        assertEquals(3, useCaseInputsDtoResponse.getProjectCount());
        assertEquals("Project 1, Project 2", useCaseInputsDtoResponse.getProjects());
    }
}