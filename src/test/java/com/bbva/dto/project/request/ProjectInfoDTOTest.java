package com.bbva.dto.project.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectInfoDTOTest {
    @Test
    void testProjectInfoDTO() {
        ProjectInfoDTO projectInfoDTO = new ProjectInfoDTO();
        projectInfoDTO.setProjectId(1);
        projectInfoDTO.setSdatoolId("SDATool123");
        projectInfoDTO.setProjectName("Project Name");
        projectInfoDTO.setProjectDesc("Project Description");
        projectInfoDTO.setPortafolioCode("Portfolio Code");
        projectInfoDTO.setDomainId("Domain ID");
        projectInfoDTO.setTtvType(1);
        projectInfoDTO.setRegulatoryType(2);
        projectInfoDTO.setProjectType(3);
        projectInfoDTO.setCategoryType(4);
        projectInfoDTO.setClassificationType(5);
        projectInfoDTO.setStartPiId(6);
        projectInfoDTO.setEndPiId(7);
        projectInfoDTO.setFinalStartPiId(8);
        projectInfoDTO.setFinalEndPiId(9);
        projectInfoDTO.setWowType(10);
        projectInfoDTO.setCountryPriorityType(11);
        projectInfoDTO.setCreateAuditUser("User");
        projectInfoDTO.setStatusType(12);
        projectInfoDTO.setUseCaseId(13);

        assertEquals(1, projectInfoDTO.getProjectId());
        assertEquals("SDATool123", projectInfoDTO.getSdatoolId());
        assertEquals("Project Name", projectInfoDTO.getProjectName());
        assertEquals("Project Description", projectInfoDTO.getProjectDesc());
        assertEquals("Portfolio Code", projectInfoDTO.getPortafolioCode());
        assertEquals("Domain ID", projectInfoDTO.getDomainId());
        assertEquals(1, projectInfoDTO.getTtvType());
        assertEquals(2, projectInfoDTO.getRegulatoryType());
        assertEquals(3, projectInfoDTO.getProjectType());
        assertEquals(4, projectInfoDTO.getCategoryType());
        assertEquals(5, projectInfoDTO.getClassificationType());
        assertEquals(6, projectInfoDTO.getStartPiId());
        assertEquals(7, projectInfoDTO.getEndPiId());
        assertEquals(8, projectInfoDTO.getFinalStartPiId());
        assertEquals(9, projectInfoDTO.getFinalEndPiId());
        assertEquals(10, projectInfoDTO.getWowType());
        assertEquals(11, projectInfoDTO.getCountryPriorityType());
        assertEquals("User", projectInfoDTO.getCreateAuditUser());
        assertEquals(12, projectInfoDTO.getStatusType());
        assertEquals(13, projectInfoDTO.getUseCaseId());
    }
}
