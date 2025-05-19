package com.bbva.dto.project.request;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsertProjectInfoDTORequestTest {
    @Test
    void testInsertProjectInfoDTORequest() {
        InsertProjectInfoDTORequest request = new InsertProjectInfoDTORequest();
        request.setProjectId(1);
        request.setSdatoolId("SDATool123");
        request.setProjectName("Project Name");
        request.setProjectDesc("Project Description");
        request.setPortafolioCode("Portfolio Code");
        request.setDomainId("Domain ID");
        request.setTtvType(1);
        request.setRegulatoryType(2);
        request.setProjectType(3);
        request.setCategoryType(4);
        request.setClassificationType(5);
        request.setStartPiId(6);
        request.setEndPiId(7);
        request.setFinalStartPiId(8);
        request.setFinalEndPiId(9);
        request.setWowType(10);
        request.setCountryPriorityType(11);
        request.setCreateAuditUser("User");
        request.setParticipants(List.of(new InsertProjectParticipantDTO()));
        request.setDocuments(List.of(new InsertProjectDocumentDTO()));
        request.setStatusType(12);
        request.setUseCaseId(13);

        assertEquals(1, request.getProjectId());
        assertEquals("SDATool123", request.getSdatoolId());
        assertEquals("Project Name", request.getProjectName());
        assertEquals("Project Description", request.getProjectDesc());
        assertEquals("Portfolio Code", request.getPortafolioCode());
        assertEquals("Domain ID", request.getDomainId());
        assertEquals(1, request.getTtvType());
        assertEquals(2, request.getRegulatoryType());
        assertEquals(3, request.getProjectType());
        assertEquals(4, request.getCategoryType());
        assertEquals(5, request.getClassificationType());
        assertEquals(6, request.getStartPiId());
        assertEquals(7, request.getEndPiId());
        assertEquals(8, request.getFinalStartPiId());
        assertEquals(9, request.getFinalEndPiId());
        assertEquals(10, request.getWowType());
        assertEquals(11, request.getCountryPriorityType());
        assertEquals("User", request.getCreateAuditUser());
        assertEquals(1, request.getParticipants().size());
        assertEquals(1, request.getDocuments().size());
        assertEquals(12, request.getStatusType());
        assertEquals(13, request.getUseCaseId());
    }
}
