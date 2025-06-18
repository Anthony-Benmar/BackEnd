package com.bbva.entities.use_case;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UseCaseEntityTest {
    @Test
    void testUseCaseEntity() {
        UseCaseEntity entity = new UseCaseEntity();
        entity.setUseCaseId(1);
        entity.setUseCaseName("Test Use Case");
        entity.setUseCaseDescription("This is a test use case.");

        assertEquals(1, entity.getUseCaseId());
        assertEquals("Test Use Case", entity.getUseCaseName());
        assertEquals("This is a test use case.", entity.getUseCaseDescription());
    }
}
