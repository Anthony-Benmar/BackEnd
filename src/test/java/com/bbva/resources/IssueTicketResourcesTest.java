package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.issueticket.request.WorkOrderDtoRequest;
import com.bbva.dto.issueticket.request.WorkOrderDtoRequest2;
import com.bbva.dto.issueticket.request.sourceTicketDtoRequest;
import com.bbva.dto.issueticket.response.issueTicketDtoResponse;
import com.bbva.dto.issueticket.response.sourceTicketDtoResponse;
import com.bbva.service.IssueTicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class IssueTicketResourcesTest {
    private IssueTicketService issueTicketServiceMock;
    private IssueTicketResources issueTicketResources;

    @BeforeEach
    void setUp() {
        issueTicketServiceMock = mock(IssueTicketService.class);
        issueTicketResources = new IssueTicketResources() {
            {
                try {
                    var field = IssueTicketResources.class.getDeclaredField("issueTicketService");
                    field.setAccessible(true);
                    field.set(this, issueTicketServiceMock);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void testInsertIssueTicket() throws Exception {
        WorkOrderDtoRequest request = new WorkOrderDtoRequest();

        IDataResult<Void> dataResult = new SuccessDataResult<Void>(null);

        when(issueTicketServiceMock.insert(request)).thenReturn(dataResult);

        IDataResult result = issueTicketResources.insert(request);

        assertNotNull(result);
        assertEquals(dataResult, result);
        verify(issueTicketServiceMock).insert(request);
    }

    @Test
    void testInsert2IssueTicket() throws Exception {
        WorkOrderDtoRequest2 request = new WorkOrderDtoRequest2();

        List<Map<String, Object>> successFeatures = List.of();
        List<String> failedFeatures = List.of();

        IDataResult<Map<String, Object>> dataResult = new SuccessDataResult<>(Map.of(
                "success", successFeatures,
                "failed", failedFeatures
        ));

        when(issueTicketServiceMock.insert2(List.of(request))).thenReturn(dataResult);

        IDataResult<Map<String, Object>> result = issueTicketResources.insertFeatures(List.of(request));

        assertNotNull(result);
        assertEquals(dataResult, result);
        verify(issueTicketServiceMock).insert2(List.of(request));
    }

    @Test
    void testUpdateIssueTicket() throws Exception {
        WorkOrderDtoRequest request = new WorkOrderDtoRequest();

        IDataResult<Void> dataResult = new SuccessDataResult<Void>(null);

        when(issueTicketServiceMock.update(request)).thenReturn(dataResult);

        IDataResult<Void> result = issueTicketResources.update(request);

        assertNotNull(result);
        assertEquals(dataResult, result);
        verify(issueTicketServiceMock).update(request);
    }

    @Test
    void testGenerateIssueTicket() throws Exception {
        WorkOrderDtoRequest request = new WorkOrderDtoRequest();

        IDataResult<Void> dataResult = new SuccessDataResult<Void>(null);

        when(issueTicketServiceMock.generate(request)).thenReturn(dataResult);

        IDataResult<Void> result = issueTicketResources.generate(request);

        assertNotNull(result);
        assertEquals(dataResult, result);
        verify(issueTicketServiceMock).generate(request);
    }

    @Test
    void testListSourcesGeneratedIssueTicket() {
        sourceTicketDtoRequest request = new sourceTicketDtoRequest();
        sourceTicketDtoResponse sourceTicketDtoResponse = new sourceTicketDtoResponse();
        IDataResult<sourceTicketDtoResponse> dataResult = new SuccessDataResult<>(sourceTicketDtoResponse);

        when(issueTicketServiceMock.listSourcesGenerated(request)).thenReturn(dataResult);

        IDataResult<sourceTicketDtoResponse> result = issueTicketResources.listSourcesGenerated(request);

        assertNotNull(result);
        assertEquals(dataResult, result);
        verify(issueTicketServiceMock).listSourcesGenerated(request);
    }

    @Test
    void testListIssuesGeneratedIssueTicket() {
        sourceTicketDtoRequest request = new sourceTicketDtoRequest();
        issueTicketDtoResponse sourceTicketDtoResponse = new issueTicketDtoResponse();
        IDataResult<issueTicketDtoResponse> dataResult = new SuccessDataResult<>(sourceTicketDtoResponse);

        when(issueTicketServiceMock.listIssuesGenerated(request)).thenReturn(dataResult);

        IDataResult<issueTicketDtoResponse> result = issueTicketResources.listIssuesGenerated(request);

        assertNotNull(result);
        assertEquals(dataResult, result);
        verify(issueTicketServiceMock).listIssuesGenerated(request);
    }
}
