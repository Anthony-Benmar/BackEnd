package com.bbva.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateOrInsertEntityTest {
    @Test
    void testUpdateOrInsertEntity() {
        UpdateOrInsertEntity entity = new UpdateOrInsertEntity();
        entity.setLastUpdatedId(1);
        entity.setUpdatedRegister(2);
        entity.setLastInsertId(3);
        entity.setNewRegister(4);

        assertEquals(1, entity.getLastUpdatedId());
        assertEquals(2, entity.getUpdatedRegister());
        assertEquals(3, entity.getLastInsertId());
        assertEquals(4, entity.getNewRegister());
    }
}
