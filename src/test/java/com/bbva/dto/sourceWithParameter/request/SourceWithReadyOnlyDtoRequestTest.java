package com.bbva.dto.sourceWithParameter.request;

import com.bbva.dto.source_with_parameter.request.SourceWithReadyOnlyDtoRequest;
import org.junit.jupiter.api.Test;

class SourceWithReadyOnlyDtoRequestTest {
    @Test
    void testSourceWithReadyOnlyDtoRequest() {
        // Given
        SourceWithReadyOnlyDtoRequest dto = new SourceWithReadyOnlyDtoRequest();

        // When
        dto.setSourceWithParameterId("1");

        // Then
        // Basic assertions
        assert "1".equals(dto.getSourceWithParameterId())
                : "Expected sourceWithParameterId to be '1'";
        assert dto.getSourceWithParameterId() != null
                : "sourceWithParameterId should not be null";

        // Additional useful assertions:
        // 1. Verify the length if it has a specific format (e.g., UUID, numeric ID)
        assert dto.getSourceWithParameterId().length() > 0
                : "ID should not be empty";

    }
}
