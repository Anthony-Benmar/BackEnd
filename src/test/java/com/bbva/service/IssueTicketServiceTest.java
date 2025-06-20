package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.issueticket.request.WorkOrderDtoRequest;
import com.bbva.dto.issueticket.request.WorkOrderDtoRequest2;
import com.bbva.dto.issueticket.request.sourceTicketDtoRequest;
import com.bbva.dto.issueticket.response.issueTicketDtoResponse;
import com.bbva.dto.issueticket.response.sourceTicketDtoResponse;
import com.bbva.entities.issueticket.WorkOrderDetail;
import com.bbva.dto.jira.response.IssueResponse;
import com.bbva.dto.jira.request.IssueBulkDto;
import com.bbva.entities.feature.JiraFeatureEntity;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IssueTicketServiceTest {

    IssueTicketService service;

    @BeforeEach
    void setUp() {
        service = Mockito.spy(new IssueTicketService());
    }

    @Test
    void expiredTokenValidate_ShouldReturnTrueWhenExpired() {
        long expired = Instant.now().minusSeconds(3600).getEpochSecond();
        assertTrue(service.expiredTokenValidate(expired));
    }

    @Test
    void expiredTokenValidate_ShouldReturnFalseWhenNotExpired() {
        long future = Instant.now().plusSeconds(3600).getEpochSecond();
        assertFalse(service.expiredTokenValidate(future));
    }

    @Test
    void insert_ReturnsErrorIfNoDetails() throws Exception {
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.workOrderDetail = new ArrayList<>();
        IDataResult result = service.insert(dto);
        assertTrue(result instanceof ErrorDataResult);
        assertEquals("Para poder registrar debe seleccionar al menos una plantilla", result.message);
    }

    @Test
    void insert2_ReturnsFailedIfNoFeatureData() throws Exception {
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setFeature("");
        dto.setJiraProjectName("");
        List<WorkOrderDtoRequest2> list = List.of(dto);
        IDataResult result = service.insert2(list);
        Map<String, Object> data = (Map<String, Object>) result.data;
        List<String> failed = (List<String>) data.get("failed");
        assertFalse(failed.isEmpty());
        assertTrue(failed.get(0).contains("No se tienen datos del Feature a crear"));
    }

    @Test
    void insert2_ReturnsFailedIfNoWorkOrderDetail() throws Exception {
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setFeature("featX");
        dto.setJiraProjectName("JP");
        dto.setWorkOrderDetail( Arrays.asList());
        List<WorkOrderDtoRequest2> list = List.of(dto);
        IDataResult result = service.insert2(list);
        Map<String, Object> data = (Map<String, Object>) result.data;
        List<String> failed = (List<String>) data.get("failed");
        assertFalse(failed.isEmpty());
        assertTrue(failed.get(0).contains("Sin templates seleccionados"));
    }

    @Test
    void createCookieHeader_ReturnsFormattedString() throws Exception {
        List<Cookie> cookies = new ArrayList<>();
        cookies.add(new BasicClientCookie("a", "b"));
        cookies.add(new BasicClientCookie("c", "d"));
        String result = invokeCreateCookieHeader(cookies);
        assertEquals("a=b; c=d", result);
    }

    // Reflection helper for static private method
    private String invokeCreateCookieHeader(List<Cookie> cookies) throws Exception {
        var m = IssueTicketService.class.getDeclaredMethod("createCookieHeader", List.class);
        m.setAccessible(true);
        return (String) m.invoke(null, cookies);
    }
}