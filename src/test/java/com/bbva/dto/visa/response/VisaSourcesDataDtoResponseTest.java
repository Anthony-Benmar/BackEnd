package com.bbva.dto.visa.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.bbva.dto.visa_sources.response.VisaSourcesDataDtoResponse;

class VisaSourcesDataDtoResponseTest {
    @Test
    void testGettersAndSetters() {
        VisaSourcesDataDtoResponse dto = new VisaSourcesDataDtoResponse();

        dto.setId(1);
        dto.setSourceType("Database");
        dto.setUserStory("US-101");
        dto.setQuarter("Q1");
        dto.setRegisterDate("2025-09-10");
        dto.setSdatoolProject("ProjectX");
        dto.setSdatoolFinal("FinalData");
        dto.setFunctionalAnalist("Analyst1");
        dto.setDomain("Finance");
        dto.setFolio("FOL-001");
        dto.setTdsProposalName("ProposalName");
        dto.setTdsDescription("Description");
        dto.setTdsProof("Proof1");
        dto.setOriginSource("SystemA");
        dto.setOriginType("TypeB");
        dto.setOwnerModel("ModelOwner");
        dto.setUuaaRaw("RAW");
        dto.setUuaaMaster("MASTER");
        dto.setCriticalTable("Table1");
        dto.setFunctionalChecklist("Checklist1");
        dto.setStructure("STRUCT");
        dto.setStatus("Active");

        assertEquals(1, dto.getId());
        assertEquals("Database", dto.getSourceType());
        assertEquals("US-101", dto.getUserStory());
        assertEquals("Q1", dto.getQuarter());
        assertEquals("2025-09-10", dto.getRegisterDate());
        assertEquals("ProjectX", dto.getSdatoolProject());
        assertEquals("FinalData", dto.getSdatoolFinal());
        assertEquals("Analyst1", dto.getFunctionalAnalist());
        assertEquals("Finance", dto.getDomain());
        assertEquals("FOL-001", dto.getFolio());
        assertEquals("ProposalName", dto.getTdsProposalName());
        assertEquals("Description", dto.getTdsDescription());
        assertEquals("Proof1", dto.getTdsProof());
        assertEquals("SystemA", dto.getOriginSource());
        assertEquals("TypeB", dto.getOriginType());
        assertEquals("ModelOwner", dto.getOwnerModel());
        assertEquals("RAW", dto.getUuaaRaw());
        assertEquals("MASTER", dto.getUuaaMaster());
        assertEquals("Table1", dto.getCriticalTable());
        assertEquals("Checklist1", dto.getFunctionalChecklist());
        assertEquals("STRUCT", dto.getStructure());
        assertEquals("Active", dto.getStatus());
    }
}

