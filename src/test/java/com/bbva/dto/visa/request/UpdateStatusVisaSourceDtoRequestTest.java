package com.bbva.dto.visa.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.bbva.dto.visa_sources.request.UpdateStatusVisaSourceDtoRequest;

class UpdateStatusVisaSourceDtoRequestTest {
    @Test
    void testGettersAndSetters() {
        UpdateStatusVisaSourceDtoRequest dto = new UpdateStatusVisaSourceDtoRequest();

        dto.setId(10);
        dto.setStatus("Finalizado");

        assertEquals(10, dto.getId());
        assertEquals("Finalizado", dto.getStatus());
    }
}