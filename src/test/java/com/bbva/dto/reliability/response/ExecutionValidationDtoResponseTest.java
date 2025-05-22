package com.bbva.dto.reliability.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExecutionValidationDtoResponseTest {

    @Test
    void testExecutionValidationDtoResponse() {
        ExecutionValidationDtoResponse dto = new ExecutionValidationDtoResponse();

        dto.setValidation("Validación exitosa");

        assertEquals("Validación exitosa", dto.getValidation());
    }
}
