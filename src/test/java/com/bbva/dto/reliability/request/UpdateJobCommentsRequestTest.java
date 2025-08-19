package com.bbva.dto.reliability.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateJobCommentsRequestTest {

    @Test
    void gettersAndSettersWork() {
        UpdateJobCommentsRequest dto = new UpdateJobCommentsRequest();
        dto.setActorRole("KM");
        dto.setComments("Comentario de prueba");

        assertEquals("KM", dto.getActorRole());
        assertEquals("Comentario de prueba", dto.getComments());
    }

    @Test
    void defaultValuesAreNull() {
        UpdateJobCommentsRequest dto = new UpdateJobCommentsRequest();
        assertNull(dto.getActorRole());
        assertNull(dto.getComments());
    }

    @Test
    void allowsEmptyStrings() {
        UpdateJobCommentsRequest dto = new UpdateJobCommentsRequest();
        dto.setActorRole("");
        dto.setComments("");

        assertEquals("", dto.getActorRole());
        assertEquals("", dto.getComments());
    }
}
