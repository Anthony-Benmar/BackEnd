package com.bbva.dto.efectivity.request;

import com.bbva.dto.efectivity_base.request.EfectivityBasePaginationDtoRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EfectivityBasePaginationDtoRequestTest {
    @Test
    void testGettersAndSetters() {
        EfectivityBasePaginationDtoRequest request = new EfectivityBasePaginationDtoRequest();
        request.setLimit(10);
        request.setOffset(5);
        request.setSdatoolProject("Project A");
        request.setSprintDate("2023-10-01");
        request.setRegisterDate("2023-10-02");
        request.setEfficiency("High");

        assertBasicFields(request);
    }
    private void assertBasicFields(EfectivityBasePaginationDtoRequest dto) {
        assertEquals(10, dto.getLimit());
        assertEquals(5, dto.getOffset());
        assertEquals("Project A", dto.getSdatoolProject());
        assertEquals("2023-10-01", dto.getSprintDate());
        assertEquals("2023-10-02", dto.getRegisterDate());
        assertEquals("High", dto.getEfficiency());
    }
}
