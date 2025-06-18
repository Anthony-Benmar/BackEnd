package com.bbva.dto.SingleBase.request;

import com.bbva.dto.single_base.request.SingleBaseReadOnlyDtoRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SingleBaseReadOnlyDtoRequestTest {
    @Test
    void testGettersAndSetters() {
        SingleBaseReadOnlyDtoRequest dto = new SingleBaseReadOnlyDtoRequest();

        // Set value
        dto.setSingleBaseId("1");

        // Assert value
        assertEquals("1", dto.getSingleBaseId());
    }
}
