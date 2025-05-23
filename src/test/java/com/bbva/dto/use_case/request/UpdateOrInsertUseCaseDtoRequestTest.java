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
        request.setDeliveredPiId(1);
        request.setCritical(1);
        request.setIsRegulatory(1);
        request.setUseCaseScope(1);
        request.setOperativeModel(1);

        assertEquals(1, request.getUseCaseId());
        assertEquals("Test Use Case", request.getUseCaseName());
        assertEquals("This is a test use case.", request.getUseCaseDescription());
        assertEquals(2, request.getDomainId());
        assertEquals("insert", request.getAction());
        assertEquals("user123", request.getUserId());
        assertEquals(1, request.getDeliveredPiId());
        assertEquals(1, request.getCritical());
        assertEquals(1, request.getIsRegulatory());
        assertEquals(1, request.getUseCaseScope());
        assertEquals(1, request.getOperativeModel());
    }
}
