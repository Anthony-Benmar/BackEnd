package com.bbva.dto.SingleBase.response;

import com.bbva.dto.single_base.response.SingleBaseDataDtoResponse;
import com.bbva.dto.single_base.response.SingleBasePaginatedResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SingleBasePaginatedResponseDTOTest {
    @Test
    void testGettersAndSetters() {
        SingleBasePaginatedResponseDTO dto = new SingleBasePaginatedResponseDTO();

        // Set values
        dto.setTotalCount(100);
        dto.setData(List.of(new SingleBaseDataDtoResponse()));

        // Assert values
        assertEquals(100, dto.getTotalCount());
        assertEquals(1, dto.getData().size());
    }
}
