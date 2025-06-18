package com.bbva.dto.SingleBase.response;

import com.bbva.dto.single_base.response.SingleBaseDataDtoResponse;
import com.bbva.dto.single_base.response.SingleBasePaginatedResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

class SingleBasePaginatedResponseDTOTest {
    @Test
    void testGettersAndSetters() {
        SingleBasePaginatedResponseDTO dto = new SingleBasePaginatedResponseDTO();

        // Set values
        dto.setTotalCount(100);
        dto.setData(List.of(new SingleBaseDataDtoResponse()));

        // Assert values
        assert dto.getTotalCount() == 100;
        assert dto.getData() != null && !dto.getData().isEmpty();
        assert dto.getData().get(0) instanceof SingleBaseDataDtoResponse;
    }
}
