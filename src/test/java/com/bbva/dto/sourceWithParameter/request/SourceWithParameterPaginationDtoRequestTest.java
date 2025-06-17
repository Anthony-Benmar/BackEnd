package com.bbva.dto.sourceWithParameter.request;

import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class SourceWithParameterPaginationDtoRequestTest {
    @Test
    void testGettersAndSetters() {
        SourceWithParameterPaginationDtoRequest dto = new SourceWithParameterPaginationDtoRequest();

        // Set values
        dto.setLimit(10);
        dto.setOffset(0);
        dto.setId("123");
        dto.setTdsSource("Test Source");
        dto.setUuaaMaster("Master UUAA");
        dto.setModelOwner("Owner");
        dto.setStatus("Active");
        dto.setOriginType("Type");
        dto.setTdsOpinionDebt("Debt Opinion");
        dto.setEffectivenessDebt("Effective");

        // Assert values
        assertEquals(10, dto.getLimit());
        assertEquals(0, dto.getOffset());
        assertEquals("123", dto.getId());
        assertEquals("Test Source", dto.getTdsSource());
        assertEquals("Master UUAA", dto.getUuaaMaster());
        assertEquals("Owner", dto.getModelOwner());
        assertEquals("Active", dto.getStatus());
        assertEquals("Type", dto.getOriginType());
        assertEquals("Debt Opinion", dto.getTdsOpinionDebt());
        assertEquals("Effective", dto.getEffectivenessDebt());
    }
}
