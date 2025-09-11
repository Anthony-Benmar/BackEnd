package com.bbva.dto.visa.response;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.bbva.dto.visa_sources.response.VisaSourceValidateExistDtoResponse;

class VisaSourceValidateExistDtoResponseTest {
    @Test
    void testGettersAndSetters() {
        VisaSourceValidateExistDtoResponse dto = new VisaSourceValidateExistDtoResponse();

        dto.setMultipleValidation(true);
        dto.setValidated(false);
        dto.setReplacementId("REP-123");

        assertTrue(dto.getMultipleValidation());
        assertFalse(dto.getValidated());
        assertEquals("REP-123", dto.getReplacementId());
    }
}
