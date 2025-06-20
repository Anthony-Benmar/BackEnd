package com.bbva.dto.jira.response;

import com.bbva.dto.jira.request.Fields;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IssueResponseTest {

    private IssueResponse issueResponse;

    @BeforeEach
    void setUp() {
        issueResponse = new IssueResponse();
    }

    @Test
    void testSetAndGetExpand() {
        String expand = "names,schema";
        issueResponse.setExpand(expand);
        assertEquals(expand, issueResponse.getExpand());
    }

    @Test
    void testSetAndGetId() {
        String id = "10001";
        issueResponse.setId(id);
        assertEquals(id, issueResponse.getId());
    }

    @Test
    void testSetAndGetSelf() {
        String self = "http://jira.bbva.com/rest/api/2/issue/10001";
        issueResponse.setSelf(self);
        assertEquals(self, issueResponse.getSelf());
    }

    @Test
    void testSetAndGetKey() {
        String key = "PROJ-123";
        issueResponse.setKey(key);
        assertEquals(key, issueResponse.getKey());
    }

    @Test
    void testSetAndGetFields() {
        Fields fields = new Fields();
        fields.setSummary("Test summary");
        issueResponse.setFields(fields);
        assertEquals(fields, issueResponse.getFields());
        assertEquals("Test summary", issueResponse.getFields().getSummary());
    }

    @Test
    void testDefaultValues() {
        assertNull(issueResponse.getExpand());
        assertNull(issueResponse.getId());
        assertNull(issueResponse.getSelf());
        assertNull(issueResponse.getKey());
        assertNull(issueResponse.getFields());
    }
}