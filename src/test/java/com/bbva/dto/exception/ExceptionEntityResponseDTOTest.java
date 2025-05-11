package com.bbva.dto.exception;

import com.bbva.dto.exception.response.ExceptionEntityResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExceptionEntityResponseDTOTest {
     @Test
     void testGettersAndSetters() {
         ExceptionEntityResponseDTO dto = new ExceptionEntityResponseDTO();

         // Set values
         dto.setSourceId(1);
         dto.setTdsDescription("Test Description");
         dto.setTdsSource("Test Source");
         dto.setRequestingProject("Test Project");
         dto.setRequestStatus("Pending");
         dto.setRegistrationDate(new Date());
         dto.setQuarterYearSprint("Q1-2023");
         dto.setShutdownCommitmentDate("2023-12-31");
         dto.setShutdownCommitmentStatus("On Track");
         dto.setShutdownProject("Shutdown Project");

         // Assert values
         assertEquals(1, dto.getSourceId());
         assertEquals("Test Description", dto.getTdsDescription());
         assertEquals("Test Source", dto.getTdsSource());
         assertEquals("Test Project", dto.getRequestingProject());
         assertEquals("Pending", dto.getRequestStatus());
         assertNotNull(dto.getRegistrationDate());
         assertEquals("Q1-2023", dto.getQuarterYearSprint());
         assertEquals("2023-12-31", dto.getShutdownCommitmentDate());
         assertEquals("On Track", dto.getShutdownCommitmentStatus());
         assertEquals("Shutdown Project", dto.getShutdownProject());
     }
}
