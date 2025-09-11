package com.bbva.dto.visa.request;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.bbva.dto.visa_sources.request.ApproveVisaSourceDtoRequest;

class ApproveVisaSourceDtoRequestTest {
    @Test
    void testGettersAndSetters() {
        ApproveVisaSourceDtoRequest dto = new ApproveVisaSourceDtoRequest();

        dto.setId(101);
        dto.setSourceId("SRC-001");
        dto.setIsMinorChange(true);
        dto.setIsSubstitution(false);

        assertEquals(101, dto.getId());
        assertEquals("SRC-001", dto.getSourceId());
        assertTrue(dto.getIsMinorChange());
        assertFalse(dto.getIsSubstitution());
    }
}