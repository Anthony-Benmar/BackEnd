package com.bbva.dto.exception.response;

import com.bbva.dto.exception_base.response.ExceptionBaseReadOnlyDtoResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

 class ExceptionBaseReadOnlyDtoResponseTest {
    @Test
    void gettersAndSetters() {
        ExceptionBaseReadOnlyDtoResponse response = new ExceptionBaseReadOnlyDtoResponse();
        response.setId("123");
        response.setSourceId("123");
        response.setTdsDescription("Description of TDS");
        response.setTdsSource("Source A");
        response.setRequestingProject("Project A");
        response.setApprovalResponsible("Responsible Y");
        response.setRequestStatus("Pending");
        response.setRegistrationDate("2023-10-01");
        response.setQuarterYearSprint("Q4-2023");
        response.setShutdownCommitmentDate("2023-12-31");
        response.setShutdownCommitmentStatus("Not Started");
        response.setShutdownProject("Shutdown Project A");
        response.setSourceId("123");


        assertBasicFields(response);
    }
    private void assertBasicFields(ExceptionBaseReadOnlyDtoResponse dto) {
        assertEquals("123", dto.getId());
        assertEquals("123", dto.getSourceId());
        assertEquals("Description of TDS", dto.getTdsDescription());
        assertEquals("Source A", dto.getTdsSource());
        assertEquals("Project A", dto.getRequestingProject());
        assertEquals("Responsible Y", dto.getApprovalResponsible());
        assertEquals("Pending", dto.getRequestStatus());
        assertEquals("2023-10-01", dto.getRegistrationDate());
        assertEquals("Q4-2023", dto.getQuarterYearSprint());
        assertEquals("2023-12-31", dto.getShutdownCommitmentDate());
        assertEquals("Not Started", dto.getShutdownCommitmentStatus());
        assertEquals("Shutdown Project A", dto.getShutdownProject());
    }
}
