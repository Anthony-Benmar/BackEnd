package com.bbva.service;

import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JiraApiServiceTest {

    private JiraApiService service;

    @BeforeEach
    void setUp() {
        service = Mockito.spy(new JiraApiService());
    }

    @Test
    void testGetIssueMetadata_success() throws Exception {
        JiraValidatorByUrlRequest dto = new JiraValidatorByUrlRequest();
        dto.setUserName("user");
        dto.setToken("token");

        String issueKey = "TEST-1234";
        String jsonResponse = "{\"fields\":{\"summary\":\"Issue summary\"}}";

        doReturn(jsonResponse).when(service).GetJiraAsync(eq("user"), eq("token"),
                eq("https://jira.globaldevtools.bbva.com/rest/api/2/issue/" + issueKey + "?expand=changelog"));

        JsonObject result = service.getIssueMetadata(dto, issueKey);

        assertNotNull(result);
        assertTrue(result.has("fields"));
    }

    @Test
    void testGetIssueMetadata_responseIsNull() throws Exception {
        JiraValidatorByUrlRequest dto = new JiraValidatorByUrlRequest();
        dto.setUserName("user");
        dto.setToken("token");

        String issueKey = "TEST-1234";

        doReturn(null).when(service).GetJiraAsync(any(), any(), any());

        JsonObject result = service.getIssueMetadata(dto, issueKey);

        assertNull(result);
    }

    @Test
    void testGetIssueMetadata_responseIsBlank() throws Exception {
        JiraValidatorByUrlRequest dto = new JiraValidatorByUrlRequest();
        dto.setUserName("user");
        dto.setToken("token");

        String issueKey = "TEST-1234";

        doReturn("   ").when(service).GetJiraAsync(any(), any(), any());

        JsonObject result = service.getIssueMetadata(dto, issueKey);

        assertNull(result);
    }
}

