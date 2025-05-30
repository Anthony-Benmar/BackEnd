package com.bbva.dto.reliability.response;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExecutionValidationAllDtoResponseTest {

    @Test
    void testBuilderPattern() {
        ExecutionValidationAllDtoResponse response = ExecutionValidationAllDtoResponse.builder()
                .jobName("Daily Sales Report")
                .validation("SUCCESS")
                .build();

        assertNotNull(response);
        assertEquals("Daily Sales Report", response.getJobName());
        assertEquals("SUCCESS", response.getValidation());
    }
}
