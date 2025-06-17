package com.bbva.dto.sourceWithParameter.request;

import com.bbva.dto.source_with_parameter.request.SourceWithReadyOnlyDtoRequest;
import org.junit.jupiter.api.Test;

class SourceWithReadyOnlyDtoRequestTest {
  @Test
    void testSourceWithReadyOnlyDtoRequest() {
        // Given
        SourceWithReadyOnlyDtoRequest sourceWithReadyOnlyDtoRequest = new SourceWithReadyOnlyDtoRequest();

        // When
        sourceWithReadyOnlyDtoRequest.setSourceWithParameterId("1");

        // Then
        assert sourceWithReadyOnlyDtoRequest.getSourceWithParameterId() == "1";
    }
}
