package com.bbva.dto.reliability.response;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExecutionValidationAllDtoResponseTest {

    @Test
    void testBuilderPattern() {
        ExecutionValidationAllDtoResponse dto = ExecutionValidationAllDtoResponse.builder()
                .jobName("job1")
                .validation("valid")
                .build();

        assertEquals("job1", dto.getJobName());
        assertEquals("valid", dto.getValidation());

        dto.setJobName("job2");
        dto.setValidation("invalid");

        assertEquals("job2", dto.getJobName());
        assertEquals("invalid", dto.getValidation());
    }
}
