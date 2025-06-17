package com.bbva.dto.exception.response;


import com.bbva.dto.exception_base.response.ExceptionBaseDataDtoResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionBaseDataDtoResponseTest {
    @Test
    void gettersAndSetters() {
        ExceptionBaseDataDtoResponse response = new ExceptionBaseDataDtoResponse();
        response.setId(1);
        response.setSourceId("source-123");
        response.setTdsDescription("Test TDS Description");
        response.setTdsSource("Test TDS Source");
        response.setRequestingProject("Test Project");
        response.setApprovalResponsible("Test Approver");
        response.setRequestStatus("Pending");
        response.setRegistrationDate("2023-10-01");
        response.setQuarterYearSprint("Q4-2023 Sprint 1");
        response.setShutdownCommitmentDate("2023-12-31");
        response.setShutdownCommitmentStatus("Active");
        response.setShutdownProject("Test Shutdown Project");

        assertBasicFields(response);
    }
    private void assertBasicFields(ExceptionBaseDataDtoResponse dto) {
        assertEquals(1, dto.getId());
        assertEquals("source-123", dto.getSourceId());
        assertEquals("Test TDS Description", dto.getTdsDescription());
        assertEquals("Test TDS Source", dto.getTdsSource());
        assertEquals("Test Project", dto.getRequestingProject());
        assertEquals("Test Approver", dto.getApprovalResponsible());
        assertEquals("Pending", dto.getRequestStatus());
        assertEquals("2023-10-01", dto.getRegistrationDate());
        assertEquals("Q4-2023 Sprint 1", dto.getQuarterYearSprint());
        assertEquals("2023-12-31", dto.getShutdownCommitmentDate());
        assertEquals("Active", dto.getShutdownCommitmentStatus());
        assertEquals("Test Shutdown Project", dto.getShutdownProject());
    }
}
