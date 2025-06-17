package com.bbva.dto.sourceWithParameter.response;

import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterPaginatedResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SourceWithParameterPaginatedResponseDTOTest {
    @Test
    void testGettersAndSetters() {
        SourceWithParameterPaginatedResponseDTO dto = new SourceWithParameterPaginatedResponseDTO();

        // Set values
        dto.setTotalCount(100);
        dto.setData(List.of(new SourceWithParameterDataDtoResponse()));

        // Assert values
        assertEquals(100, dto.getTotalCount());
        assertEquals(1, dto.getData().size());
    }
}
