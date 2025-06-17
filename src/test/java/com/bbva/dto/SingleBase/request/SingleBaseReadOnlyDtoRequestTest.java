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
        assert dto.getSingleBaseId() == "1";
    }
}
