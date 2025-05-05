package com.bbva.dto.project.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectInfoFilterRequestTest {

    @Test
    void testGettersAndSetters() {
        ProjectInfoFilterRequest filterRequest = new ProjectInfoFilterRequest();
        filterRequest.setPage(1);
        filterRequest.setRecords_amount(10);
        filterRequest.setProjectId(12345);
        filterRequest.setSdatoolIdOrProjectName("TestProject");
        filterRequest.setDomainId("Domain123");
        filterRequest.setStatusType("Active");
        filterRequest.setProjectType("TypeA");
        filterRequest.setWowType("Agile");
        filterRequest.setStartQ("2025-Q1");
        filterRequest.setEndQ("2025-Q2");

        assertEquals(1, filterRequest.getPage());
        assertEquals(10, filterRequest.getRecords_amount());
        assertEquals(12345, filterRequest.getProjectId());
        assertEquals("TestProject", filterRequest.getSdatoolIdOrProjectName());
        assertEquals("Domain123", filterRequest.getDomainId());
        assertEquals("Active", filterRequest.getStatusType());
        assertEquals("TypeA", filterRequest.getProjectType());
        assertEquals("Agile", filterRequest.getWowType());
        assertEquals("2025-Q1", filterRequest.getStartQ());
        assertEquals("2025-Q2", filterRequest.getEndQ());
    }
}
