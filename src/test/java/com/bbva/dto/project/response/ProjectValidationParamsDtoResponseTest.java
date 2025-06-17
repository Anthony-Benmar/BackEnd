package com.bbva.dto.project.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectValidationParamsDtoResponseTest {
    @Test
    void testBuilderAndGetters() {
        String expectedType = "ERROR";
        String expectedMessage = "Missing project ID";

        ProjectValidationParamsDtoResponse dto = ProjectValidationParamsDtoResponse.builder()
                .type(expectedType)
                .message(expectedMessage)
                .build();

        assertEquals(expectedType, dto.getType());
        assertEquals(expectedMessage, dto.getMessage());
    }
    @Test
    void testSettersAndGetters() {
        ProjectValidationParamsDtoResponse dto = new ProjectValidationParamsDtoResponse();
        dto.setType("WARNING");
        dto.setMessage("Optional field");

        assertEquals("WARNING", dto.getType());
        assertEquals("Optional field", dto.getMessage());
    }
}
