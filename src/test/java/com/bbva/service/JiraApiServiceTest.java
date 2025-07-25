package com.bbva.service;

import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class JiraApiServiceTest {

    private JiraApiService service;

    @BeforeEach
    void setUp() {
        service = new JiraApiService();
    }

    @Test
    void testGetQuerySuffixURL_notNull() {
        String url = service.getQuerySuffixURL();
        assertNotNull(url);
        assertTrue(url.contains("maxResults="));
        assertTrue(url.contains("fields="));
    }

    @Test
    void testBuildJiraQueryUrl() {
        List<String> issues = List.of("PROY-123", "PROY-456");
        String query = service.buildJiraQueryUrl(issues);
        assertTrue(query.contains("PROY-123"));
        assertTrue(query.contains("PROY-456"));
        assertTrue(query.contains("fields="));
    }
}
