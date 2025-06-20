package com.bbva.dto.jira.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FieldsTest {

    private Fields fields;

    @BeforeEach
    void setUp() {
        fields = new Fields();
    }

    @Test
    void testSetAndGetSummary() {
        String summary = "Test summary";
        fields.setSummary(summary);
        assertEquals(summary, fields.getSummary());
    }

    @Test
    void testSetAndGetDescription() {
        String description = "This is a description";
        fields.setDescription(description);
        assertEquals(description, fields.getDescription());
    }

    @Test
    void testSetAndGetLabels() {
        List<String> labels = Arrays.asList("bug", "urgent");
        fields.setLabels(labels);
        assertEquals(labels, fields.getLabels());
    }

    @Test
    void testSetAndGetLastViewed() {
        LocalDateTime now = LocalDateTime.now();
        fields.setLastViewed(now);
        assertEquals(now, fields.getLastViewed());
    }

    @Test
    void testSetAndGetCustomfield10006() {
        String featureName = "Feature-1";
        fields.setCustomfield_10006(featureName);
        assertEquals(featureName, fields.getCustomfield_10006());
    }

    @Test
    void testSetAndGetAssignee() {
        Assignee assignee = new Assignee();
        fields.setAssignee(assignee);
        assertEquals(assignee, fields.getAssignee());
    }

    @Test
    void testSetAndGetReporter() {
        Reporter reporter = new Reporter();
        fields.setReporter(reporter);
        assertEquals(reporter, fields.getReporter());
    }

    @Test
    void testSetAndGetCreated() {
        LocalDateTime created = LocalDateTime.of(2024, 1, 1, 12, 0);
        fields.setCreated(created);
        assertEquals(created, fields.getCreated());
    }

    @Test
    void testSetAndGetCustomfield16300() {
        Customfield cf1 = new Customfield();
        Customfield cf2 = new Customfield();
        fields.setCustomfield_16300(Arrays.asList(cf1, cf2));
        assertEquals(2, fields.getCustomfield_16300().size());
        assertTrue(fields.getCustomfield_16300().contains(cf1));
        assertTrue(fields.getCustomfield_16300().contains(cf2));
    }

    @Test
    void testDefaultValues() {
        assertNull(fields.getSummary());
        assertNull(fields.getAssignee());
        assertNull(fields.getLabels());
    }
}