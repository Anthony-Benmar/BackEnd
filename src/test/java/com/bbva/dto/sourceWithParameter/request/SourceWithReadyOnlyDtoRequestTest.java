package com.bbva.dto.sourceWithParameter.request;

import com.bbva.dto.source_with_parameter.request.SourceWithReadyOnlyDtoRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SourceWithReadyOnlyDtoRequestTest {
    @Test
    void testSourceWithReadyOnlyDtoRequest() {
        // Given
        SourceWithReadyOnlyDtoRequest dto = new SourceWithReadyOnlyDtoRequest();
        // When
        dto.setSourceWithParameterId("1");
        assertEquals("1", dto.getSourceWithParameterId());
    }
}
