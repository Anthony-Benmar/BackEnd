package com.bbva.dto.efectivity.response;

import com.bbva.dto.efectivity_base.response.EfectivityBaseDataReadOnlyDtoResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EfectivityBaseDataReadOnlyDtoResponseTest {
     @Test
     void gettersAndSetters() {
         EfectivityBaseDataReadOnlyDtoResponse response = new EfectivityBaseDataReadOnlyDtoResponse();
         response.setId("123");
         response.setTicketCode("TICKET-456");
         response.setSprintDate("2023-10-01");
         response.setSdatoolProject("Project A");
         response.setSdatoolFinalProject("Final Project A");
         response.setFolio("FOLIO-789");
         response.setTdsDescription("Description of TDS");
         response.setRegisterDate("2023-10-02");
         response.setAnalystAmbassador("Analyst X");
         response.setRegistrationResponsible("Responsible Y");
         response.setBuildObservations("Build observations here.");
         response.setRegistrationObservations("Registration observations here.");
         response.setSourceTable("Source Table A");

         assertBasicFields(response);
     }
     private void  assertBasicFields(EfectivityBaseDataReadOnlyDtoResponse dto) {
         assertEquals("123", dto.getId());
         assertEquals("TICKET-456", dto.getTicketCode());
         assertEquals("2023-10-01", dto.getSprintDate());
         assertEquals("Project A", dto.getSdatoolProject());
         assertEquals("Final Project A", dto.getSdatoolFinalProject());
         assertEquals("FOLIO-789", dto.getFolio());
         assertEquals("Description of TDS", dto.getTdsDescription());
         assertEquals("2023-10-02", dto.getRegisterDate());
         assertEquals("Analyst X", dto.getAnalystAmbassador());
         assertEquals("Responsible Y", dto.getRegistrationResponsible());
         assertEquals("Build observations here.", dto.getBuildObservations());
         assertEquals("Registration observations here.", dto.getRegistrationObservations());
         assertEquals("Source Table A", dto.getSourceTable());
     }
}
