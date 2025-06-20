package com.bbva.dto.jira.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IssueFeatureDtoTest {

    private IssueFeatureDto issueFeatureDto;

    @BeforeEach
    void setUp() {
        issueFeatureDto = new IssueFeatureDto();
    }

    @Test
    void testSetAndGetFields() {
        Fields fields = new Fields();
        fields.setSummary("Feature summary");
        issueFeatureDto.setFields(fields);
        assertEquals(fields, issueFeatureDto.getFields());
        assertEquals("Feature summary", issueFeatureDto.getFields().getSummary());
    }

    @Test
    void testDefaultFieldsIsNull() {
        assertNull(issueFeatureDto.getFields());
    }
}