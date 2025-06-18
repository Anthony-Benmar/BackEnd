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

        // Additional assertions:
        // 1. Verify data list size is exactly 1
        assert dto.getData().size() == 1 : "Expected data list size to be 1";

        // 2. Verify totalCount is not negative (assuming business logic)
        assert dto.getTotalCount() >= 0 : "Total count should not be negative";

        // 3. Verify toString() contains expected fields (if implemented)
        assert dto.toString().contains("totalCount=100") : "toString should include totalCount";
    }
}
