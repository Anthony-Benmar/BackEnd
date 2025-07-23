package com.bbva.dto.efectivity.response;

import com.bbva.dto.efectivity_base.response.EfectivityBaseDataDtoResponse;
import com.bbva.dto.efectivity_base.response.EfectivityBasePaginatedResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EfectivityBasePaginatedResponseDTOTest {
     @Test
    void gettersAndSetters() {
        EfectivityBasePaginatedResponseDTO response = new EfectivityBasePaginatedResponseDTO();
        response.setTotalCount(100);
        response.setData(List.of(new EfectivityBaseDataDtoResponse()));

        assertBasicFields(response);
        // Assertions can be added here to verify the values if needed
     }
    private void assertBasicFields(EfectivityBasePaginatedResponseDTO dto) {
        assertEquals(100, dto.getTotalCount());
        assertEquals(1, dto.getData().size());
        // Additional assertions can be added here to verify the contents of the data list
    }
}
