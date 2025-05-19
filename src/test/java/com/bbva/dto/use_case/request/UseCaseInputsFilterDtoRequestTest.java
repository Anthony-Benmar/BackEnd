package com.bbva.dto.use_case.request;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UseCaseInputsFilterDtoRequestTest {
    @Test
    void testUseCaseInputsFilterDtoRequest() {
        UseCaseInputsFilterDtoRequest request = new UseCaseInputsFilterDtoRequest();
        request.setPage(1);
        request.setRecordsAmount(10);
        request.setDomainName("Test Domain");
        request.setProjectName("Test Project");

        assertEquals(1, request.getPage());
        assertEquals(10, request.getRecordsAmount());
        assertEquals("Test Domain", request.getDomainName());
        assertEquals("Test Project", request.getProjectName());
    }
}