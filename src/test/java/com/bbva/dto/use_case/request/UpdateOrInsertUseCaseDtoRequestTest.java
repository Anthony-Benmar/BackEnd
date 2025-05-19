package com.bbva.dto.use_case.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateOrInsertUseCaseDtoRequestTest {
    @Test
    void testUpdateOrInsertUseCaseDtoRequest() {
        UpdateOrInsertUseCaseDtoRequest request = new UpdateOrInsertUseCaseDtoRequest();
        request.setUseCaseId(1);
        request.setUseCaseName("Test Use Case");
        request.setUseCaseDescription("This is a test use case.");
        request.setDomainId(2);
        request.setAction("insert");
        request.setUserId("user123");

        assertEquals(1, request.getUseCaseId());
        assertEquals("Test Use Case", request.getUseCaseName());
        assertEquals("This is a test use case.", request.getUseCaseDescription());
        assertEquals(2, request.getDomainId());
        assertEquals("insert", request.getAction());
        assertEquals("user123", request.getUserId());
    }
}
