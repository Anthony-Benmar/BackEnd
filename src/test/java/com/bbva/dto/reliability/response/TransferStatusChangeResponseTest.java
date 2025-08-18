package com.bbva.dto.reliability.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferStatusChangeResponseTest {

    @Test
    void testBuilderAndGetters() {
        TransferStatusChangeResponse dto = TransferStatusChangeResponse.builder()
                .pack("PACK123")
                .oldStatus(1)
                .newStatus(2)
                .build();

        assertEquals("PACK123", dto.getPack());
        assertEquals(1, dto.getOldStatus());
        assertEquals(2, dto.getNewStatus());
    }

    @Test
    void testAllArgsConstructor() {
        TransferStatusChangeResponse dto = new TransferStatusChangeResponse(
                "PACK999", // pack
                5,         // oldStatus
                6          // newStatus
        );

        assertEquals("PACK999", dto.getPack());
        assertEquals(5, dto.getOldStatus());
        assertEquals(6, dto.getNewStatus());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        TransferStatusChangeResponse dto = new TransferStatusChangeResponse();

        dto.setPack("PACK000");
        dto.setOldStatus(10);
        dto.setNewStatus(20);

        assertEquals("PACK000", dto.getPack());
        assertEquals(10, dto.getOldStatus());
        assertEquals(20, dto.getNewStatus());
    }
}