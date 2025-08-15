package com.bbva.dto.reliability.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReliabilityPackAdvancedFilterRequestTest {
    @Test
    void defaults_y_setters_getters() {
        var dto = new ReliabilityPackAdvancedFilterRequest();

        // Defaults
        assertEquals(1, dto.getPage());
        assertEquals(10, dto.getRecordsAmount());
        assertNull(dto.getDomainName());
        assertNull(dto.getUseCase());
        assertNull(dto.getRole());
        assertNull(dto.getTab());

        // Setters / Getters
        dto.setDomainName("FIN");
        dto.setUseCase("PFM");
        dto.setRole("KM");
        dto.setTab("EN_PROGRESO");
        dto.setPage(2);
        dto.setRecordsAmount(25);

        assertEquals("FIN", dto.getDomainName());
        assertEquals("PFM", dto.getUseCase());
        assertEquals("KM", dto.getRole());
        assertEquals("EN_PROGRESO", dto.getTab());
        assertEquals(2, dto.getPage());
        assertEquals(25, dto.getRecordsAmount());
    }
}
