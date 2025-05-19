package com.bbva.dto.use_case.response;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateOrInsertDtoResponseTest {
    @Test
    void testUpdateOrInsertDtoResponse() {
        UpdateOrInsertDtoResponse response = new UpdateOrInsertDtoResponse();
        response.setLastUpdatedId(1);
        response.setUpdatedRegister(1);
        response.setLastInsertId(1);
        response.setNewRegister(1);
        response.setErrorMessage("Test Error Message");

        assertEquals(1, response.getLastUpdatedId());
        assertEquals(1, response.getUpdatedRegister());
        assertEquals(1, response.getLastInsertId());
        assertEquals(1, response.getNewRegister());
        assertEquals("Test Error Message", response.getErrorMessage());
    }
}
