package com.bbva.entities.project;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ProjectStatusEntityTest {

    @Test
    void testProjectStatusEntityGettersAndSetters() {
        Integer projectId = 1;
        Integer statusId = 2;
        String statusName = "In Progress";
        Date startDate = new Date();
        String startDateStr = "2025-04-04";
        String piLargeName = "piLargeName";

        ProjectStatusEntity entity = new ProjectStatusEntity();
        entity.setProjectId(projectId);
        entity.setStatusId(statusId);
        entity.setStatusName(statusName);
        entity.setStartDate(startDate);
        entity.setStartDateStr(startDateStr);
        entity.setPiLargeName(piLargeName);

        assertNotNull(entity);
        assertEquals(projectId, entity.getProjectId());
        assertEquals(statusId, entity.getStatusId());
        assertEquals(statusName, entity.getStatusName());
        assertEquals(startDate, entity.getStartDate());
        assertEquals(startDateStr, entity.getStartDateStr());
        assertEquals(piLargeName, entity.getPiLargeName());
    }

    @Test
    void testDefaultConstructor() {
        ProjectStatusEntity entity = new ProjectStatusEntity();

        assertNotNull(entity);
        assertNull(entity.getProjectId());
        assertNull(entity.getStatusId());
        assertNull(entity.getStatusName());
        assertNull(entity.getStartDate());
        assertNull(entity.getStartDateStr());
    }

    @Test
    void testSetters() {
        ProjectStatusEntity entity = new ProjectStatusEntity();

        Integer projectId = 10;
        Integer statusId = 20;
        String statusName = "Completed";
        Date startDate = new Date();
        String startDateStr = "2025-04-04";

        entity.setProjectId(projectId);
        entity.setStatusId(statusId);
        entity.setStatusName(statusName);
        entity.setStartDate(startDate);
        entity.setStartDateStr(startDateStr);

        assertEquals(projectId, entity.getProjectId());
        assertEquals(statusId, entity.getStatusId());
        assertEquals(statusName, entity.getStatusName());
        assertEquals(startDate, entity.getStartDate());
        assertEquals(startDateStr, entity.getStartDateStr());
    }
}