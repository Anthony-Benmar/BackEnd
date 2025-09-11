package com.bbva.dto.visa.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.bbva.dto.visa_sources.request.RegisterVisaSourceDtoRequest;

class RegisterVisaSourceDtoRequestTest {
    @Test
    void testGettersAndSetters() {
        RegisterVisaSourceDtoRequest dto = new RegisterVisaSourceDtoRequest();

        dto.setId(1);
        dto.setSourceType("Database");
        dto.setUserStory("US-123");
        dto.setQuarter("Q3");
        dto.setRegisterDate("2025-09-10");
        dto.setSdatoolProject("ProjectX");
        dto.setSdatoolFinal("FinalX");
        dto.setFunctionalAnalist("Analyst1");
        dto.setDomain("Finance");
        dto.setFolio("FOLIO-001");
        dto.setTdsProposalName("ProposalName");
        dto.setTdsDescription("Description");
        dto.setTdsProof("Proof1");
        dto.setTdsProof2("Proof2");
        dto.setOriginSource("SourceX");
        dto.setOriginType("TypeX");
        dto.setOwnerModel("OwnerX");
        dto.setUuaaRaw("RAW1");
        dto.setUuaaMaster("MASTER1");
        dto.setCriticalTable("Table1");
        dto.setFunctionalChecklist("Checklist1");
        dto.setStructure("Structure1");
        dto.setStatus("Active");

        assertEquals(1, dto.getId());
        assertEquals("Database", dto.getSourceType());
        assertEquals("US-123", dto.getUserStory());
        assertEquals("Q3", dto.getQuarter());
        assertEquals("2025-09-10", dto.getRegisterDate());
        assertEquals("ProjectX", dto.getSdatoolProject());
        assertEquals("FinalX", dto.getSdatoolFinal());
        assertEquals("Analyst1", dto.getFunctionalAnalist());
        assertEquals("Finance", dto.getDomain());
        assertEquals("FOLIO-001", dto.getFolio());
        assertEquals("ProposalName", dto.getTdsProposalName());
        assertEquals("Description", dto.getTdsDescription());
        assertEquals("Proof1", dto.getTdsProof());
        assertEquals("Proof2", dto.getTdsProof2());
        assertEquals("SourceX", dto.getOriginSource());
        assertEquals("TypeX", dto.getOriginType());
        assertEquals("OwnerX", dto.getOwnerModel());
        assertEquals("RAW1", dto.getUuaaRaw());
        assertEquals("MASTER1", dto.getUuaaMaster());
        assertEquals("Table1", dto.getCriticalTable());
        assertEquals("Checklist1", dto.getFunctionalChecklist());
        assertEquals("Structure1", dto.getStructure());
        assertEquals("Active", dto.getStatus());
    }
}

