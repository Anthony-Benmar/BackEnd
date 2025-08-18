package com.bbva.dto.reliability.request;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReliabilityPackInputFilterRequestTest {

    @Test
    void testGettersAndSetters() {
        ReliabilityPackInputFilterRequest request = new ReliabilityPackInputFilterRequest();

        // Set values
        request.setDomainName("GRM");
        request.setUseCase("U001");
        request.setRole("KM");
        request.setTab("EN_PROGRESO");
        request.setPage(2);
        request.setRecordsAmount(10);

        // Assertions
        assertEquals("GRM", request.getDomainName());
        assertEquals("U001", request.getUseCase());
        assertEquals("KM", request.getRole());
        assertEquals("EN_PROGRESO", request.getTab());
        assertEquals(2, request.getPage());
        assertEquals(10, request.getRecordsAmount());
    }
}
