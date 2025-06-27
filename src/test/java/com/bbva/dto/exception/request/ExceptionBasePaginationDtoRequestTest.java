package com.bbva.dto.exception.request;

import com.bbva.dto.exception_base.request.ExceptionBasePaginationDtoRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class ExceptionBasePaginationDtoRequestTest {
    @Test
    void testGettersAndSetters() {
        ExceptionBasePaginationDtoRequest request = new ExceptionBasePaginationDtoRequest();
        request.setLimit(10);
        request.setOffset(5);
        request.setRequestingProject("Project A");
        request.setApprovalResponsible("Responsible B");
        request.setRegistrationDate("2023-10-01");
        request.setQuarterYearSprint("Q4-2023");

        // Assertions can be added here to verify the values if needed
        assertBasicFields(request);
    }
    private void assertBasicFields(ExceptionBasePaginationDtoRequest dto) {
        assertEquals(10, dto.getLimit());
        assertEquals(5, dto.getOffset());
        assertEquals("Project A", dto.getRequestingProject());
        assertEquals("Responsible B", dto.getApprovalResponsible());
        assertEquals("2023-10-01", dto.getRegistrationDate());
        assertEquals("Q4-2023", dto.getQuarterYearSprint());
    }
}
