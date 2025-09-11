package com.bbva.dto.visa.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.bbva.dto.visa_sources.response.VisaSourceApproveDtoResponse;

class VisaSourceApproveDtoResponseTest {
    @Test
    void testGettersAndSetters() {
        VisaSourceApproveDtoResponse dto = new VisaSourceApproveDtoResponse();

        dto.setId("VS-001");
        dto.setMessage("Visa source approved successfully");

        assertEquals("VS-001", dto.getId());
        assertEquals("Visa source approved successfully", dto.getMessage());
    }
}