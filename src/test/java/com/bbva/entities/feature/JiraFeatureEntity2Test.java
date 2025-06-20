package com.bbva.entities.feature;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JiraFeatureEntity2Test {

    @Test
    void testAllArgsConstructor() {
        int featureId = 101;
        String featureKey = "KEY-001";
        String featureName = "Nueva Feature";
        String featureUrl = "http://jira/browse/KEY-001";
        String description = "Descripción de la feature";
        Integer jiraProjectId = 555;
        String jiraProjectName = "PROYECTO-JIRA";
        int boardId = 42;
        String status = "En Progreso";
        Date createdDate = new Date();

        JiraFeatureEntity2 feature = new JiraFeatureEntity2(
                featureId,
                featureKey,
                featureName,
                featureUrl,
                description,
                jiraProjectId,
                jiraProjectName,
                boardId,
                status,
                createdDate
        );

        assertNotNull(feature);
        assertEquals(featureId, feature.getFeatureId());
        assertEquals(featureKey, feature.getFeatureKey());
        assertEquals(featureName, feature.getFeatureName());
        assertEquals(featureUrl, feature.getFeatureUrl());
        assertEquals(description, feature.getDescription());
        assertEquals(jiraProjectId, feature.getJiraProjectId());
        assertEquals(jiraProjectName, feature.getJiraProjectName());
        assertEquals(boardId, feature.getBoardId());
        assertEquals(status, feature.getStatus());
        assertEquals(createdDate, feature.getCreatedDate());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        JiraFeatureEntity2 feature = new JiraFeatureEntity2();

        feature.setFeatureId(202);
        feature.setFeatureKey("KEY-002");
        feature.setFeatureName("Feature Setter");
        feature.setFeatureUrl("http://jira/browse/KEY-002");
        feature.setDescription("Otra descripción");
        feature.setJiraProjectId(888);
        feature.setJiraProjectName("JIRA-TEST");
        feature.setBoardId(77);
        feature.setStatus("Finalizado");
        Date fecha = new Date();
        feature.setCreatedDate(fecha);

        assertNotNull(feature);
        assertEquals(202, feature.getFeatureId());
        assertEquals("KEY-002", feature.getFeatureKey());
        assertEquals("Feature Setter", feature.getFeatureName());
        assertEquals("http://jira/browse/KEY-002", feature.getFeatureUrl());
        assertEquals("Otra descripción", feature.getDescription());
        assertEquals(888, feature.getJiraProjectId());
        assertEquals("JIRA-TEST", feature.getJiraProjectName());
        assertEquals(77, feature.getBoardId());
        assertEquals("Finalizado", feature.getStatus());
        assertEquals(fecha, feature.getCreatedDate());
    }
}