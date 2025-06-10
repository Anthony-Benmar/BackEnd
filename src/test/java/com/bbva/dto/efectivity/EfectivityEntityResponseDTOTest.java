package com.bbva.dto.efectivity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EfectivityEntityResponseDTOTest {
     @Test
     void testGettersAndSetters() {
         EfectivityEntityResponseDTO dto = new EfectivityEntityResponseDTO();

         dto.setTicketCode("TICKET123");
         dto.setSprintDate("2023-01-01");
         dto.setSdatoolProject("ProjectA");
         dto.setSdatoolFinalProject("FinalProjectA");
         dto.setFolio("FOLIO123");
         dto.setTdsDescription("Description");
         dto.setRegisterDate("2023-01-02");
         dto.setAnalystAmbassador("John Doe");
         dto.setRegistrationResponsible("Jane Doe");
         dto.setBuildObservations("Build OK");
         dto.setRegistrationObservations("No issues");
         dto.setId(1);

         assertEquals("TICKET123", dto.getTicketCode());
         assertEquals("2023-01-01", dto.getSprintDate());
         assertEquals("ProjectA", dto.getSdatoolProject());
         assertEquals("FinalProjectA", dto.getSdatoolFinalProject());
         assertEquals("FOLIO123", dto.getFolio());
         assertEquals("Description", dto.getTdsDescription());
         assertEquals("2023-01-02", dto.getRegisterDate());
         assertEquals("John Doe", dto.getAnalystAmbassador());
         assertEquals("Jane Doe", dto.getRegistrationResponsible());
         assertEquals("Build OK", dto.getBuildObservations());
         assertEquals("No issues", dto.getRegistrationObservations());
         assertEquals(1, dto.getId());
     }
}
