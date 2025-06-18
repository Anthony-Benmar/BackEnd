package com.bbva.dto.SingleBase.request;

import com.bbva.dto.single_base.request.SingleBaseReadOnlyDtoRequest;
import org.junit.jupiter.api.Test;

class SingleBaseReadOnlyDtoRequestTest {
    @Test
    void testGettersAndSetters() {
        SingleBaseReadOnlyDtoRequest dto = new SingleBaseReadOnlyDtoRequest();

        // Set value
        dto.setSingleBaseId("1");

        // Assert value
        assert dto.getSingleBaseId().equals("1");
        assert dto.getSingleBaseId() != null;

        // Additional assertion to verify the exact value
        assert "1".equals(dto.getSingleBaseId()) : "Expected singleBaseId to be '1'";
    }
}
