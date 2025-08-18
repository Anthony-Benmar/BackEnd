package com.bbva.dto.reliability.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransferStatusChangeRequestTest {
    @Test
    void testGettersAndSetters() {
        TransferStatusChangeRequest request = new TransferStatusChangeRequest();

        // Set values
        request.setAction("APPROVE");
        request.setActorRole("KM");
        request.setComment("Aprobado por KM");
        request.setUserId(123);

        // Assertions
        assertEquals("APPROVE", request.getAction());
        assertEquals("KM", request.getActorRole());
        assertEquals("Aprobado por KM", request.getComment());
        assertEquals(123, request.getUserId());
    }
}
