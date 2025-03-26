package com.bbva.entities.jiravalidator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InfoJiraProjectTest {

    @Test
    void testAllArgsConstructor() {
        String sdatoolId = "SDT001";
        String projectName = "Jira Integration";
        String projectDesc = "Project for Jira integration testing";
        String participantEmail = "test@bbva.com";
        String projectRolType = "Developer";
        String projectRolName = "Software Engineer";
        String teamBackLogId = "TBL001";
        String teamBackLogName = "Backlog Team A";

        InfoJiraProject infoProject = new InfoJiraProject(
                sdatoolId, projectName, projectDesc, participantEmail, projectRolType,
                projectRolName, teamBackLogId, teamBackLogName
        );

        assertNotNull(infoProject);
        assertEquals(sdatoolId, infoProject.getSdatoolId());
        assertEquals(projectName, infoProject.getProjectName());
        assertEquals(projectDesc, infoProject.getProjectDesc());
        assertEquals(participantEmail, infoProject.getParticipantEmail());
        assertEquals(projectRolType, infoProject.getProjectRolType());
        assertEquals(projectRolName, infoProject.getProjectRolName());
        assertEquals(teamBackLogId, infoProject.getTeamBackLogId());
        assertEquals(teamBackLogName, infoProject.getTeamBackLogName());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        InfoJiraProject infoProject = new InfoJiraProject();

        infoProject.setSdatoolId("SDT002");
        infoProject.setProjectName("Jira Validator Project");
        infoProject.setProjectDesc("Validator for Jira projects");
        infoProject.setParticipantEmail("participant@bbva.com");
        infoProject.setProjectRolType("Tester");
        infoProject.setProjectRolName("QA Engineer");
        infoProject.setTeamBackLogId("TBL002");
        infoProject.setTeamBackLogName("Backlog Team B");

        assertNotNull(infoProject);
        assertEquals("SDT002", infoProject.getSdatoolId());
        assertEquals("Jira Validator Project", infoProject.getProjectName());
        assertEquals("Validator for Jira projects", infoProject.getProjectDesc());
        assertEquals("participant@bbva.com", infoProject.getParticipantEmail());
        assertEquals("Tester", infoProject.getProjectRolType());
        assertEquals("QA Engineer", infoProject.getProjectRolName());
        assertEquals("TBL002", infoProject.getTeamBackLogId());
        assertEquals("Backlog Team B", infoProject.getTeamBackLogName());
    }
}
