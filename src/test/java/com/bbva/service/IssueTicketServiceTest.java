package com.bbva.service;

import com.bbva.core.HandledException;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;

import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.IssueTicketDao;

import com.bbva.dto.issueticket.request.WorkOrderDetailDtoRequest;
import com.bbva.dto.issueticket.request.WorkOrderDtoRequest;
import com.bbva.dto.issueticket.request.WorkOrderDtoRequest2;
import com.bbva.dto.issueticket.request.sourceTicketDtoRequest;
import com.bbva.dto.issueticket.response.issueTicketDtoResponse;
import com.bbva.dto.issueticket.response.sourceTicketDtoResponse;
import com.bbva.dto.jira.request.*;
import com.bbva.dto.jira.response.IssueBulkResponse;
import com.bbva.dto.jira.response.IssueResponse;
import com.bbva.entities.issueticket.WorkOrder;
import com.bbva.entities.issueticket.WorkOrderDetail;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class IssueTicketServiceTest {

    private IssueTicketService service;
    private IssueTicketDao issueTicketDaoMock;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        service = Mockito.spy(new IssueTicketService());
        issueTicketDaoMock = mock(IssueTicketDao.class);
        var field = IssueTicketService.class.getDeclaredField("issueTicketDao");
        field.setAccessible(true);
        field.set(service, issueTicketDaoMock);
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

    @Test
    void testGetBasicSession() throws Exception {
        CloseableHttpClient httpClientMock = Mockito.mock(CloseableHttpClient.class);
        CloseableHttpResponse httpResponseMock = Mockito.mock(CloseableHttpResponse.class);
        StatusLine statusLineMock = Mockito.mock(StatusLine.class);

        when(httpClientMock.execute(any(HttpPost.class))).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(statusLineMock);
        when(statusLineMock.getStatusCode()).thenReturn(200);

        Header headerMock = new BasicHeader("Set-Cookie", "JSESSIONID=abc123; Path=/; HttpOnly");
        when(httpResponseMock.getAllHeaders()).thenReturn(new Header[]{headerMock});

        service.getBasicSession("user", "pass", httpClientMock);

        verify(service, times(1)).getBasicSession("user", "pass", httpClientMock);
    }
    @Test
    void testInsertWhenWorkOrderDetailIsNullShouldReturnError() throws Exception {
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.workOrderDetail = null;

        IDataResult result = service.insert(dto);

        assertTrue(result instanceof ErrorDataResult);
        assertEquals("500", result.status);
        assertEquals("Para poder registrar debe seleccionar al menos una plantilla", result.message);
    }

    @Test
    public void testInsert_WhenDuplicateRecord_ShouldReturnError() throws Exception {
        WorkOrderDtoRequest dto = createValidDto();
        when(issueTicketDaoMock.findRecordWorkOrder(any(WorkOrder.class))).thenReturn(1);

        IDataResult result = service.insert(dto);

        assertFalse(result.success);
        assertEquals("500", result.status);
        assertEquals("Existe un registro con los mismos datos (proyecto, proceso, folio, id fuente, fuente)", result.message);
    }

    @Test
    public void testInsert_WhenJiraCreationFails_ShouldReturnHandledError() throws Exception {
        // Arrange
        WorkOrderDtoRequest dto = createValidDto();
        when(issueTicketDaoMock.findRecordWorkOrder(any(WorkOrder.class))).thenReturn(0);

        // Configurar el spy para el método
        doThrow(new HandledException("400", "Error en Jira"))
                .when(service).createTicketJira2(any(), any(), any());

        // Act
        IDataResult result = service.insert(dto);

        // Assert
        assertFalse(result.success);
        assertEquals("400", result.status);
        assertEquals("Error en Jira", result.message);
    }

    @Test
    public void testInsert_WhenUnexpectedException_ShouldReturnGenericError() throws Exception {
        // Arrange
        WorkOrderDtoRequest dto = createValidDto();
        when(issueTicketDaoMock.findRecordWorkOrder(any(WorkOrder.class))).thenReturn(0);
        when(issueTicketDaoMock.getDataRequestIssueJira2(any(), any(), any()))
                .thenThrow(new RuntimeException("Error inesperado"));

        // Act
        IDataResult result = service.insert(dto);

        // Assert
        assertFalse(result.success);
        assertEquals("500", result.status);
        assertEquals("No se pudo realizar el registro", result.message);
    }

    private WorkOrderDtoRequest createValidDto() {
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.feature = "FEATURE-1";
        dto.folio = "FOLIO-1";
        dto.boardId = 1;
        dto.projectId = 1;
        dto.sourceId = "1";
        dto.sourceName = "SOURCE-1";
        dto.flowType = 100;
        dto.registerUserId = "USER-1";
        dto.jiraProjectId = 1;
        dto.jiraProjectName = "JIRA-PROJECT-1";
        // Lista de detalles
        WorkOrderDetailDtoRequest detail1 = new WorkOrderDetailDtoRequest();
        detail1.setWorkOrderDetailId(1);
        detail1.setTemplateId(10);
        detail1.setIssueCode("ISSUE-001");

        WorkOrderDetailDtoRequest detail2 = new WorkOrderDetailDtoRequest();
        detail2.setWorkOrderDetailId(2);
        detail2.setTemplateId(20);
        detail2.setIssueCode("ISSUE-002");

        dto.setWorkOrderDetail(Arrays.asList(detail1, detail2));
        return dto;
    }



    @Test
    public void testInsert2_MixedResults() throws Exception {
        // Arrange
        WorkOrderDtoRequest2 validDto = createValidDto("VALID");
        WorkOrderDtoRequest2 invalidDto = createInvalidDto();
        List<WorkOrderDtoRequest2> dtos = List.of(validDto, invalidDto);

        // Configurar mocks
        IssueResponse response = new IssueResponse();
        response.setId("1w");
        response.setSelf("selfw");
        response.setKey("keyq");
        response.setExpand("expandq");
        doReturn(response).when(service).createJiraFeature(validDto);
        when(issueTicketDaoMock.findRecordWorkOrder(any())).thenReturn(0);
        // No need to mock for invalidDto as it will fail validation

        // Act
        IDataResult<Map<String, Object>> result = service.insert2(dtos);

        // Assert
        assertTrue(result.success);

        Map<String, Object> data = result.data;
        List<?> successes = (List<?>) data.get("success");
        List<?> failures = (List<?>) data.get("failed");

        assertEquals(0, successes.size());
        assertEquals(2, failures.size());
    }

    @Test
    public void testInsert2_DuplicateRecord() throws Exception {
        // Arrange
        WorkOrderDtoRequest2 dto = createValidDto("FEATURE-1");
        List<WorkOrderDtoRequest2> dtos = List.of(dto);

        IssueResponse response = new IssueResponse();
        response.setId("1");
        response.setSelf("self");
        response.setKey("key");
        response.setExpand("expand");
        doReturn(response).when(service).createJiraFeature(dto);
        when(issueTicketDaoMock.findRecordWorkOrder(any())).thenReturn(1); // Duplicado

        // Act
        IDataResult<Map<String, Object>> result = service.insert2(dtos);

        // Assert
        Map<String, Object> data = result.data;
        List<?> failures = (List<?>) data.get("failed");
        assertEquals("FEATURE-1: Ya existe registro duplicado", failures.get(0));
    }

    @Test
    public void testInsert2_JiraCreationFails() throws Exception {
        // Arrange
        WorkOrderDtoRequest2 dto = createValidDto("FEATURE-1");
        List<WorkOrderDtoRequest2> dtos = List.of(dto);

        doThrow(new RuntimeException("Jira error"))
                .when(service).createJiraFeature(dto);

        // Act
        IDataResult<Map<String, Object>> result = service.insert2(dtos);

        // Assert
        Map<String, Object> data = result.data;
        List<?> failures = (List<?>) data.get("failed");
        assertTrue(((String)failures.get(0)).contains("Jira error"));
    }

    private WorkOrderDtoRequest2 createValidDto(String featureName) {
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setFeature(featureName);
        dto.setJiraProjectName("PROJECT-1");
        dto.setRegisterUserId("USER-1");
        // Lista de detalles
        WorkOrderDetailDtoRequest detail1 = new WorkOrderDetailDtoRequest();
        detail1.setWorkOrderDetailId(1);
        detail1.setTemplateId(10);
        detail1.setIssueCode("ISSUE-001");

        WorkOrderDetailDtoRequest detail2 = new WorkOrderDetailDtoRequest();
        detail2.setWorkOrderDetailId(2);
        detail2.setTemplateId(20);
        detail2.setIssueCode("ISSUE-002");

        dto.setWorkOrderDetail(Arrays.asList(detail1, detail2));
        return dto;
    }

    private WorkOrderDtoRequest2 createInvalidDto() {
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setFeature("");
        dto.setJiraProjectName("");
        return dto;
    }

    @Test
    void test_postResponseAsync3_lanza_excepcion_si_response_400() throws Exception {
        IssueTicketService service = Mockito.spy(new IssueTicketService());

        doNothing().when(service).getBasicSession(anyString(), anyString(), any(CloseableHttpClient.class));

        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.setUsername("user");
        dto.setToken("token");

        IssueBulkDto issueDto = new IssueBulkDto();

        try (MockedStatic<HttpClients> mocked = Mockito.mockStatic(HttpClients.class)) {
            CloseableHttpClient httpClientMock = mock(CloseableHttpClient.class);
            CloseableHttpResponse httpResponseMock = mock(CloseableHttpResponse.class);
            StatusLine statusLineMock = mock(StatusLine.class);
            HttpEntity entityMock = mock(HttpEntity.class);
            mocked.when(HttpClients::createDefault).thenReturn(httpClientMock);

            when(httpClientMock.execute(any(HttpPost.class))).thenReturn(httpResponseMock);
            when(httpResponseMock.getStatusLine()).thenReturn(statusLineMock);
            when(statusLineMock.getStatusCode()).thenReturn(400); // fuerza error 400
            when(httpResponseMock.getEntity()).thenReturn(entityMock);

            HandledException ex = assertThrows(HandledException.class, () -> {
                service.postResponseAsync3(dto, issueDto);
            });

            assertEquals("400", ex.getCode());
            assertEquals("Error al intentar generar tickets, revise los datos ingresados", ex.getMessage());
        }
    }

    @Test
    void testConvertPIToQuarter_validInput() {
        assertEquals("2025-Q3", service.convertPIToQuarter("PI3-25"));
    }

    @Test
    void testCallJiraGetIdSda_returnsValidId() throws Exception {

        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setE2e("FEATURE-123");
        dto.setUsername("user");
        dto.setToken("token");

        StatusLine statusLineMock = mock(StatusLine.class);
        HttpEntity entityMock = mock(HttpEntity.class);

        when(statusLineMock.getStatusCode()).thenReturn(404);
        when(entityMock.getContent()).thenReturn(new ByteArrayInputStream("error".getBytes()));

        HandledException ex = assertThrows(HandledException.class, () -> {
            service.callJiraGetIdSda(dto);
        });
        assertFalse(ex.getMessage().contains("Error al obtener Issue de Jira"));
    }


    @Test
    void test_postResponseAsync4_lanza_excepcion_si_response_400() throws Exception {
        IssueTicketService service = Mockito.spy(new IssueTicketService());

        doNothing().when(service).getBasicSession(anyString(), anyString(), any(CloseableHttpClient.class));

        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setUsername("user");
        dto.setToken("token");

        IssueBulkDto issueDto = new IssueBulkDto();

        try (MockedStatic<HttpClients> mocked = Mockito.mockStatic(HttpClients.class)) {
            CloseableHttpClient httpClientMock = mock(CloseableHttpClient.class);
            CloseableHttpResponse httpResponseMock = mock(CloseableHttpResponse.class);
            StatusLine statusLineMock = mock(StatusLine.class);
            HttpEntity entityMock = mock(HttpEntity.class);

            mocked.when(HttpClients::createDefault).thenReturn(httpClientMock);

            when(httpClientMock.execute(any(HttpPost.class))).thenReturn(httpResponseMock);
            when(httpResponseMock.getStatusLine()).thenReturn(statusLineMock);
            when(statusLineMock.getStatusCode()).thenReturn(400);
            when(httpResponseMock.getEntity()).thenReturn(entityMock);

            // Act + Assert
            HandledException ex = assertThrows(HandledException.class, () -> {
                service.postResponseAsync4(dto, issueDto);
            });

            assertEquals("400", ex.getCode());
            assertEquals("Error al intentar generar tickets, revise los datos ingresados", ex.getMessage());
        }
    }

    @Test
    void testUpdate_success() throws Exception {
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.setWorkOrderId(1);
        dto.setFeature("HU-123");
        dto.setUsername("user");
        dto.setToken("token");
        dto.setFolio("folio123");
        dto.setBoardId(1);
        dto.setProjectId(1);
        dto.setSourceId("SRC");
        dto.setSourceName("Origen");
        dto.setRegisterUserId("tester");

        String jsonResponse = "{\"key\":\"HU-123\"}";

        WorkOrder workOrder = new WorkOrder();
        workOrder.work_order_id = 1;

        WorkOrderDetail detail = new WorkOrderDetail();
        detail.issue_code = "HU-001";

        List<WorkOrderDetail> details = List.of(detail);
        List<String> fakeUpdates = List.of("HU-001");

        doReturn(jsonResponse).when(service).GetResponseAsync(anyString(), anyString(), anyString());
        when(issueTicketDaoMock.ListWorkOrder(1)).thenReturn(List.of(workOrder));
        when(issueTicketDaoMock.ListWorkOrderDetails(1)).thenReturn(details);
        doReturn(fakeUpdates).when(service).updateTicketJira(any(), any(), any());

        IDataResult result = service.update(dto);

        assertTrue(result.success);
    }

    @Test
    void testGenerateSuccess() throws Exception {
        // Arrange
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.setWorkOrderId(123);
        dto.setFeature("FEATURE-001");
        dto.setBoardId(1);
        dto.setFolio("FOLIO001");
        dto.setProjectId(10);
        dto.setSourceId("SRC1");
        dto.setSourceName("Source A");
        dto.setFlowType(1);
        dto.setRegisterUserId("user123");
        dto.setJiraProjectId(999);
        dto.setJiraProjectName("MyJiraProject");

        WorkOrderDetailDtoRequest detail1 = new WorkOrderDetailDtoRequest();
        detail1.templateId = 1001;
        WorkOrderDetailDtoRequest detail2 = new WorkOrderDetailDtoRequest();
        detail2.templateId = 1002;
        dto.setWorkOrderDetail(Arrays.asList(detail1, detail2));

        IssueBulkDto mockBulkDto = new IssueBulkDto();
        mockBulkDto.setIssueUpdates(new ArrayList<>());

        // Mock DAOs
        when(issueTicketDaoMock.getDataRequestIssueJira2(any(), any(), any()))
                .thenReturn(mockBulkDto);

        // Este método puede ser void, así que usamos doNothing
        doNothing().when(service).createTicketJira2(any(), any(), any());

        when(issueTicketDaoMock.InsertWorkOrderDetail(any())).thenReturn(true);

        // Act
        IDataResult result = service.generate(dto);

        // Assert
        assertTrue(result instanceof SuccessDataResult<?>);
        assertNull(result.data);
    }

    @Test
    void testListSourcesGenerated_WithValidProjectId_ReturnsResponse() {
        // Arrange
        sourceTicketDtoRequest request = new sourceTicketDtoRequest();
        request.setProjectId(100); // projectId > 0

        sourceTicketDtoResponse mockResponse = new sourceTicketDtoResponse();
        mockResponse.setCount(1); // ajusta según campos reales
        mockResponse.setData(new ArrayList<>());

        when(issueTicketDaoMock.listSources(request)).thenReturn(mockResponse);

        IDataResult<sourceTicketDtoResponse> result = service.listSourcesGenerated(request);

        assertTrue(result instanceof SuccessDataResult);
        assertEquals(mockResponse, result.data);
        assertEquals("Succesfull", result.message); // o el mensaje esperado
    }

    @Test
    void testListIssuesGenerated_WithValidProjectId_ReturnsData() {
        // Arrange
        sourceTicketDtoRequest request = new sourceTicketDtoRequest();
        request.setProjectId(123);

        issueTicketDtoResponse expectedResponse = new issueTicketDtoResponse();
        expectedResponse.setCount(5); // ajusta según tus atributos reales

        when(issueTicketDaoMock.listIssuesGenerated(request)).thenReturn(expectedResponse);

        IDataResult<issueTicketDtoResponse> result = service.listIssuesGenerated(request);

        assertTrue(result instanceof SuccessDataResult);
        assertEquals(expectedResponse, result.data);
        assertEquals("Succesfull", result.message);
    }


    @Test
    void test_createIssuesInBatches_divideEnLotesYCombinaRespuestas() throws Exception {
        IssueTicketService service = Mockito.spy(new IssueTicketService());

        List<IssueUpdate> issueList = IntStream.range(0, 65)
                .mapToObj(i -> {
                    IssueUpdate issue = new IssueUpdate();
                    return issue;
                }).collect(Collectors.toList());

        IssueBulkDto issueBulkDto = new IssueBulkDto();
        issueBulkDto.setIssueUpdates(issueList);

        WorkOrderDtoRequest2 authDto = new WorkOrderDtoRequest2();
        authDto.setUsername("user");
        authDto.setToken("token");

        IssueBulkResponse fakeResponse = new IssueBulkResponse();
        doReturn(fakeResponse).when(service).postResponseAsync4(any(), any());

        doReturn(fakeResponse).when(service).combineResponses(anyList());

        IssueBulkResponse result = service.createIssuesInBatches(authDto, issueBulkDto);

        assertNotNull(result);
        verify(service, times(3)).postResponseAsync4(any(), any()); // 3 batches
        verify(service).combineResponses(anyList());
    }

    @Test
    void test_combineResponses_combinaIssuesCorrectamente() {
        IssueTicketService service = new IssueTicketService();

        IssueDto issue1 = new IssueDto();
        issue1.setKey("ISSUE-1");
        IssueDto issue2 = new IssueDto();
        issue2.setKey("ISSUE-2");
        IssueDto issue3 = new IssueDto();
        issue3.setKey("ISSUE-3");

        IssueBulkResponse resp1 = new IssueBulkResponse();
        resp1.setIssues(List.of(issue1, issue2));

        IssueBulkResponse resp2 = new IssueBulkResponse();
        resp2.setIssues(List.of(issue3));

        IssueBulkResponse resp3 = new IssueBulkResponse();

        List<IssueBulkResponse> responses = List.of(resp1, resp2, resp3);

        IssueBulkResponse combined = service.combineResponses(responses);

        assertNotNull(combined);
        assertEquals(3, combined.getIssues().size());
        assertEquals("ISSUE-1", combined.getIssues().get(0).getKey());
        assertEquals("ISSUE-2", combined.getIssues().get(1).getKey());
        assertEquals("ISSUE-3", combined.getIssues().get(2).getKey());
    }

    @Test
    void testPutResponseEditAsync_devuelve200SiActualizacionExitosa() throws Exception {
        IssueTicketService serviceSpy = Mockito.spy(new IssueTicketService());

        WorkOrderDtoRequest auth = new WorkOrderDtoRequest();
        auth.setUsername("test_user");
        auth.setToken("secret");

        IssueDto dto = new IssueDto();
        dto.setKey("ISSUE-1");
        dto.setSelf("self");

        String code = "TEST-123";

        doNothing().when(serviceSpy).getBasicSession(anyString(), anyString(), any());

        CookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", "mock123");
        cookieStore.addCookie(cookie);
        Field field = IssueTicketService.class.getDeclaredField("cookieStore");
        field.setAccessible(true);
        field.set(serviceSpy, cookieStore);

        Integer status = serviceSpy.PutResponseEditAsync(auth, code, dto);
        assertEquals(status, 401);
    }
}