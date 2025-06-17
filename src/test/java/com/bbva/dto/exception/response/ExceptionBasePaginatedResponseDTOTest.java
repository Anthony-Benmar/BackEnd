package com.bbva.dto.exception.response;

import com.bbva.dto.exception_base.response.ExceptionBaseDataDtoResponse;
import com.bbva.dto.exception_base.response.ExceptionBasePaginatedResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExceptionBasePaginatedResponseDTOTest {
    @Test
    void gettersAndSetters() {
        ExceptionBasePaginatedResponseDTO response = new ExceptionBasePaginatedResponseDTO();
        response.setTotalCount(100);
        response.setData(List.of(new ExceptionBaseDataDtoResponse()));
        assertBasicFields(response);
        // Assertions can be added here to verify the values if needed
    }
    private void assertBasicFields(ExceptionBasePaginatedResponseDTO dto) {
        assertEquals(100, dto.getTotalCount());
        assertNotNull(dto.getData());
        // Additional assertions can be added here to verify the contents of the data list
    }
}
