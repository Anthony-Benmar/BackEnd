package com.bbva.dto.reliability.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JobExecutionHistoryDtoResponseTest {
    @Test
    void testDefaultValues() {
        JobExecutionHistoryDtoResponse dto = new JobExecutionHistoryDtoResponse();
        assertNull(dto.getPeriod(), "Por defecto period debe ser null");
        assertNull(dto.getExecutionStatus(), "Por defecto executionStatus debe ser null");
    }

    @Test
    void testGettersAndSetters() {
        JobExecutionHistoryDtoResponse dto = new JobExecutionHistoryDtoResponse();
        Long expectedPeriod = 159L;
        String expectedStatus = "SUCCESS";

        dto.setPeriod(expectedPeriod);
        dto.setExecutionStatus(expectedStatus);

        assertEquals(expectedPeriod, dto.getPeriod(), "El getter de period debe devolver lo seteado");
        assertEquals(expectedStatus, dto.getExecutionStatus(), "El getter de executionStatus debe devolver lo seteado");
    }
}
