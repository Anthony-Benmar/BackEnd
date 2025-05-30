package com.bbva.dto.reliability.request;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExecutionValidationInputsDtoRequestTest {

    @Test
    void testAllFields() {
        ExecutionValidationInputsDtoRequest dto = new ExecutionValidationInputsDtoRequest();

        dto.setJobName("Daily Sales Report");
        dto.setFrequency("DAILY");
        dto.setComponentName("SalesProcessor");
        dto.setComments("Automated daily sales processing");
        dto.setJobType("ETL");
        dto.setOriginType("Database");
        dto.setPhaseType("Production");
        dto.setInputPaths("/input/sales");
        dto.setOutputPaths("/output/reports");
        dto.setResponsible("john.doe@example.com");
        dto.setStatus("ACTIVE");
        dto.setJsonName("sales_report_config");

        assertEquals("Daily Sales Report", dto.getJobName());
        assertEquals("DAILY", dto.getFrequency());
        assertEquals("SalesProcessor", dto.getComponentName());
        assertEquals("Automated daily sales processing", dto.getComments());
        assertEquals("ETL", dto.getJobType());
        assertEquals("Database", dto.getOriginType());
        assertEquals("Production", dto.getPhaseType());
        assertEquals("/input/sales", dto.getInputPaths());
        assertEquals("/output/reports", dto.getOutputPaths());
        assertEquals("john.doe@example.com", dto.getResponsible());
        assertEquals("ACTIVE", dto.getStatus());
        assertEquals("sales_report_config", dto.getJsonName());
    }
}