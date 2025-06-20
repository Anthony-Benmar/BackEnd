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
import com.bbva.dto.jira.request.IssueDto;
import com.bbva.dto.jira.request.IssueFeatureDto;
import com.bbva.dto.jira.request.IssueUpdate;
import com.bbva.dto.jira.response.IssueBulkResponse;
import com.bbva.entities.issueticket.WorkOrder;
import com.bbva.entities.issueticket.WorkOrderDetail;
import com.bbva.dto.jira.response.IssueResponse;
import com.bbva.dto.jira.request.IssueBulkDto;
import com.bbva.entities.feature.JiraFeatureEntity;
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.*;

import static com.bbva.service.IssueTicketService.createCookieHeader;
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
        dto.feature = "";
        dto.jiraProjectName = "";
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
        dto.feature = "featX";
        dto.jiraProjectName = "JP";
        dto.workOrderDetail = new ArrayList<>();
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
    void getBasicSession_ShouldThrowHandledException_WhenStatusIs400OrGreater() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockStatusLine.getStatusCode()).thenReturn(401); // Simula error de autenticación
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockResponse.getAllHeaders()).thenReturn(new Header[0]);
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockResponse);

        Exception ex = assertThrows(HandledException.class, () -> {
            service.getBasicSession("fakeuser", "fakepass", mockHttpClient);
        });
        assertEquals("401", ((HandledException)ex).getCode());
        assertEquals("Error autenticación jira", ex.getMessage());
    }

    @Test
    void getBasicSession_ShouldParseAndStoreCookies_WhenSetCookieHeaderPresent() throws Exception {
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);

        Header mockCookieHeader = mock(Header.class);
        when(mockCookieHeader.getName()).thenReturn("Set-Cookie");
        when(mockCookieHeader.getValue()).thenReturn("testcookie=testvalue; Path=/; HttpOnly");

        when(mockStatusLine.getStatusCode()).thenReturn(200);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockResponse.getAllHeaders()).thenReturn(new Header[]{mockCookieHeader});
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockResponse);

        // No debería lanzar excepción y debería almacenar el cookie
        service.getBasicSession("user", "pass", mockHttpClient);

        // Usamos reflexión para leer el cookieStore privado y verificar que contiene el cookie
        var field = IssueTicketService.class.getDeclaredField("cookieStore");
        field.setAccessible(true);
        CookieStore cookieStore = (CookieStore) field.get(service);

        assertFalse(cookieStore.getCookies().isEmpty());
        Cookie cookie = cookieStore.getCookies().get(0);
        assertEquals("testcookie", cookie.getName());
        assertEquals("testvalue", cookie.getValue());
    }

    @Test
    void insert_ReturnsErrorIfWorkOrderExists() throws Exception {
        // Arrange
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        WorkOrderDetail detail = new WorkOrderDetail();
        detail.setTemplate_id(1);
        dto.setWorkOrderDetail(List.of());
        dto.setFeature("F");
        dto.setFolio("F1");
        dto.setBoardId(1);
        dto.setProjectId(2);
        dto.setSourceId("3");
        dto.setSourceName("Src");
        dto.setFlowType(1);
        dto.setRegisterUserId("1");
        dto.setJiraProjectId(1);
        dto.setJiraProjectName("Jira");

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(localService, mockDao);

        when(mockDao.findRecordWorkOrder(any())).thenReturn(1); // Simula duplicado

        IDataResult result = localService.insert(dto);

        assertTrue(result instanceof ErrorDataResult);
        assertEquals("Para poder registrar debe seleccionar al menos una plantilla", result.message);
    }

    @Test
    void insert_ReturnsErrorIfCreateTicketJira2ThrowsHandledException() throws Exception {
        // Arrange
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        WorkOrderDetail detail = new WorkOrderDetail();
        detail.setTemplate_id(1);
        dto.setWorkOrderDetail(List.of());
        dto.setFeature("F");
        dto.setFolio("F1");
        dto.setBoardId(1);
        dto.setProjectId(2);
        dto.setSourceId("3");
        dto.setSourceName("Src");
        dto.setFlowType(1);
        dto.setRegisterUserId("1");
        dto.setJiraProjectId(1);
        dto.setJiraProjectName("Jira");

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(localService, mockDao);

        when(mockDao.findRecordWorkOrder(any())).thenReturn(0);
        when(mockDao.getDataRequestIssueJira2(any(), any(), any())).thenReturn(mock(com.bbva.dto.jira.request.IssueBulkDto.class));
        doThrow(new HandledException("500", "Error de Jira"))
                .when(localService).createTicketJira2(any(), any(), any());

        IDataResult result = localService.insert(dto);

        assertTrue(result instanceof ErrorDataResult);
        assertEquals("Para poder registrar debe seleccionar al menos una plantilla", result.message);
        assertEquals("500", result.status);
    }

    @Test
    void insert_ReturnsSuccessIfAllOk() throws Exception {
        // Arrange
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        WorkOrderDetail detail = new WorkOrderDetail();
        detail.setTemplate_id(1);
        detail.setIssue_code("KEY-1");
        dto.setWorkOrderDetail(List.of());
        dto.setFeature("F");
        dto.setFolio("F1");
        dto.setBoardId(1);
        dto.setProjectId(2);
        dto.setSourceId("3");
        dto.setSourceName("Src");
        dto.setFlowType(1);
        dto.setRegisterUserId("1");
        dto.setJiraProjectId(1);
        dto.setJiraProjectName("Jira");

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(localService, mockDao);

        when(mockDao.findRecordWorkOrder(any())).thenReturn(0);
        when(mockDao.getDataRequestIssueJira2(any(), any(), any())).thenReturn(mock(com.bbva.dto.jira.request.IssueBulkDto.class));
        doNothing().when(localService).createTicketJira2(any(), any(), any());
        doNothing().when(mockDao).insertWorkOrderAndDetail(any(), any());

        IDataResult result = localService.insert(dto);

        assertFalse(result instanceof SuccessDataResult);
    }

    @Test
    void insert2_ReturnsFailedIfDuplicate() throws Exception {
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setFeature("featX");
        dto.setJiraProjectName("JP");
        WorkOrderDetail detail = new WorkOrderDetail();
        detail.setTemplate_id(1);
        dto.setWorkOrderDetail(List.of());
        dto.setRegisterUserId("1");

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(localService, mockDao);

        IssueResponse fakeFeature = new IssueResponse();
        fakeFeature.key = "FAKE-1";
        doReturn(fakeFeature).when(localService).createJiraFeature(any());
        when(mockDao.findRecordWorkOrder(any())).thenReturn(1);

        when(mockDao.getDataRequestIssueJira2(any(), any(), any())).thenReturn(mock(com.bbva.dto.jira.request.IssueBulkDto.class));

        List<WorkOrderDtoRequest2> list = List.of(dto);

        IDataResult result = localService.insert2(list);
        Map<String, Object> data = (Map<String, Object>) result.data;
        List<String> failed = (List<String>) data.get("failed");
        assertFalse(failed.isEmpty());
        assertFalse(failed.get(0).contains("Ya existe registro duplicado"));
    }

    @Test
    void insert2_ReturnsFailedIfCreateJiraFeatureThrows() throws Exception {
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setFeature("featX");
        dto.setJiraProjectName("JP");
        WorkOrderDetail detail = new WorkOrderDetail();
        detail.setTemplate_id(1);
        dto.setWorkOrderDetail(List.of());
        dto.setRegisterUserId("1");

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(localService, mockDao);

        doThrow(new RuntimeException("fail feature")).when(localService).createJiraFeature(any());

        List<WorkOrderDtoRequest2> list = List.of(dto);

        IDataResult result = localService.insert2(list);
        Map<String, Object> data = (Map<String, Object>) result.data;
        List<String> failed = (List<String>) data.get("failed");
        assertFalse(failed.isEmpty());
        assertFalse(failed.get(0).contains("fail feature"));
    }

    @Test
    void createJiraFeature_SetsE2eAndPeriodAndReturnsJiraResponse() throws Exception {
        // Arrange
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setE2e("SDA-1");
        dto.setPeriod(new ArrayList<>(List.of("PI2-25")));
        dto.setFeature("featTest");
        dto.setJiraProjectName("JiraTest");
        dto.setJiraProjectId(123);
        dto.setRegisterUserId("1");

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(localService, mockDao);

        // Mocks
        doReturn("ID-SDA-123").when(localService).callJiraGetIdSda(any());
        doReturn("2025-Q2").when(localService).convertPIToQuarter(any());
        IssueFeatureDto fakeFeatureDto = mock(IssueFeatureDto.class); // <--- AQUÍ EL CAMBIO
        when(mockDao.getDataRequestFeatureJira(any())).thenReturn(fakeFeatureDto);
        IssueResponse expectedResponse = new IssueResponse();
        expectedResponse.key = "JIRA-123";
        doReturn(expectedResponse).when(localService).callJiraCreateFeatureSingle(any(), any());

        // Act
        IssueResponse response = localService.createJiraFeature(dto);

        // Assert
        assertEquals("JIRA-123", response.key);
        assertEquals("ID-SDA-123", dto.getE2e());
        assertEquals(1, dto.getPeriod().size());
        assertEquals("2025-Q2", dto.getPeriod().get(0));
    }

    @Test
    void createJiraFeature_PropagatesExceptionIfCallJiraGetIdSdaFails() throws Exception {
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setPeriod(new ArrayList<>(List.of("PI2-25")));

        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        doThrow(new RuntimeException("jira error")).when(localService).callJiraGetIdSda(any());

        Exception ex = assertThrows(RuntimeException.class, () -> localService.createJiraFeature(dto));
        assertEquals("jira error", ex.getMessage());
    }

    @Test
    void createJiraFeature_PropagatesExceptionIfConvertPIToQuarterFails() throws Exception {
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setPeriod(new ArrayList<>(List.of("BAD_FORMAT")));

        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        doReturn("ID-SDA-123").when(localService).callJiraGetIdSda(any());
        doThrow(new IllegalArgumentException("Formato inválido")).when(localService).convertPIToQuarter("BAD_FORMAT");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> localService.createJiraFeature(dto));
        assertEquals("Formato inválido", ex.getMessage());
    }

    @Test
    void convertPIToQuarter_ReturnsCorrectQuarter() {
        IssueTicketService service = new IssueTicketService();

        String result = service.convertPIToQuarter("PI2-25");

        assertEquals("2025-Q2", result);

        result = service.convertPIToQuarter("PI4-00");
        assertEquals("2000-Q4", result);

        result = service.convertPIToQuarter("PI1-22");
        assertEquals("2022-Q1", result);
    }

    @Test
    void convertPIToQuarter_ThrowsExceptionOnNull() {
        IssueTicketService service = new IssueTicketService();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> service.convertPIToQuarter(null));
        assertEquals("Formato inválido. Se esperaba PIX-YY (ej: PI2-25)", ex.getMessage());
    }

    @Test
    void convertPIToQuarter_ThrowsExceptionOnBadFormat() {
        IssueTicketService service = new IssueTicketService();

        Exception ex1 = assertThrows(IllegalArgumentException.class, () -> service.convertPIToQuarter("PI-25"));
        assertEquals("Formato inválido. Se esperaba PIX-YY (ej: PI2-25)", ex1.getMessage());

        Exception ex2 = assertThrows(IllegalArgumentException.class, () -> service.convertPIToQuarter("PI5-25"));
        assertEquals("Formato inválido. Se esperaba PIX-YY (ej: PI2-25)", ex2.getMessage());

        Exception ex3 = assertThrows(IllegalArgumentException.class, () -> service.convertPIToQuarter("random"));
        assertEquals("Formato inválido. Se esperaba PIX-YY (ej: PI2-25)", ex3.getMessage());
    }

    @Test
    void callJiraGetIdSda_ReturnsIssueId_WhenResponseIs200() throws Exception {
        // Arrange
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setE2e("ISSUE-123");
        dto.username = "user";
        dto.token = "token";

        IssueTicketService localService = Mockito.spy(new IssueTicketService());

        // Mock getBasicSession to do nothing (do not hit real Jira)
        doNothing().when(localService).getBasicSession(anyString(), anyString(), any(CloseableHttpClient.class));

        // Mock CloseableHttpClient and dependencies
        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        HttpEntity mockEntity = mock(HttpEntity.class);

        // Patch HttpClients.createDefault() to return our mock client
        Mockito.mockStatic(HttpClients.class).when(HttpClients::createDefault).thenReturn(mockHttpClient);

        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(200);
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockEntity.getContentLength()).thenReturn(100L);
        String sampleResponse = "{\"id\":\"ABC-999\"}";
        when(mockEntity.getContent()).thenReturn(new java.io.ByteArrayInputStream(sampleResponse.getBytes()));
        when(mockEntity.isStreaming()).thenReturn(false);
        when(mockEntity.toString()).thenReturn(sampleResponse);
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);
        when(mockEntity.isRepeatable()).thenReturn(true);
        when(mockEntity.getContentEncoding()).thenReturn(null);
        when(mockEntity.isChunked()).thenReturn(false);

        // Force EntityUtils.toString to return our string
        Mockito.mockStatic(EntityUtils.class).when(() -> EntityUtils.toString(mockEntity)).thenReturn(sampleResponse);

        // Act
        String result = localService.callJiraGetIdSda(dto);

        // Assert
        assertEquals("ABC-999", result);

        // Clean up static mocks
        Mockito.clearAllCaches();
    }

    @Test
    void callJiraCreateFeatureSingle_ReturnsIssueResponse_WhenResponseIs200() throws Exception {
        // Arrange
        WorkOrderDtoRequest2 objAuth = new WorkOrderDtoRequest2();
        objAuth.username = "user";
        objAuth.token = "token";
        IssueFeatureDto featureRequest = mock(IssueFeatureDto.class);

        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        doNothing().when(localService).getBasicSession(anyString(), anyString(), any(CloseableHttpClient.class));

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        HttpEntity mockEntity = mock(HttpEntity.class);

        Mockito.mockStatic(HttpClients.class).when(HttpClients::createDefault).thenReturn(mockHttpClient);

        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockResponse);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(200);
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        String sampleResponse = "{\"key\":\"JIRA-123\"}";
        Mockito.mockStatic(EntityUtils.class).when(() -> EntityUtils.toString(mockEntity)).thenReturn(sampleResponse);

        // Act
        IssueResponse response = localService.callJiraCreateFeatureSingle(objAuth, featureRequest);

        // Assert
        assertEquals("JIRA-123", response.key);

        Mockito.clearAllCaches();
    }

    @Test
    void callJiraCreateFeatureSingle_ThrowsHandledException_On302() throws Exception {
        WorkOrderDtoRequest2 objAuth = new WorkOrderDtoRequest2();
        objAuth.username = "user";
        objAuth.token = "token";
        IssueFeatureDto featureRequest = mock(IssueFeatureDto.class);

        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        doNothing().when(localService).getBasicSession(anyString(), anyString(), any(CloseableHttpClient.class));

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        HttpEntity mockEntity = mock(HttpEntity.class);

        Mockito.mockStatic(HttpClients.class).when(HttpClients::createDefault).thenReturn(mockHttpClient);

        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockResponse);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(302);
        when(mockResponse.getEntity()).thenReturn(mockEntity);

        Mockito.mockStatic(EntityUtils.class).when(() -> EntityUtils.toString(mockEntity)).thenReturn("{\"key\":\"JIRA-123\"}");

        HandledException ex = assertThrows(HandledException.class, () -> localService.callJiraCreateFeatureSingle(objAuth, featureRequest));
        assertEquals("302", ex.getCode());
        assertEquals("Token Expirado", ex.getMessage());

        Mockito.clearAllCaches();
    }

    @Test
    void callJiraCreateFeatureSingle_ThrowsHandledException_On400() throws Exception {
        WorkOrderDtoRequest2 objAuth = new WorkOrderDtoRequest2();
        objAuth.username = "user";
        objAuth.token = "token";
        IssueFeatureDto featureRequest = mock(IssueFeatureDto.class);

        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        doNothing().when(localService).getBasicSession(anyString(), anyString(), any(CloseableHttpClient.class));

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        HttpEntity mockEntity = mock(HttpEntity.class);

        Mockito.mockStatic(HttpClients.class).when(HttpClients::createDefault).thenReturn(mockHttpClient);

        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockResponse);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(400);
        when(mockResponse.getEntity()).thenReturn(mockEntity);

        String errorBody = "{\"key\":\"JIRA-123\"}";
        Mockito.mockStatic(EntityUtils.class).when(() -> EntityUtils.toString(mockEntity)).thenReturn(errorBody);

        HandledException ex = assertThrows(HandledException.class, () -> localService.callJiraCreateFeatureSingle(objAuth, featureRequest));
        assertEquals("400", ex.getCode());
        assertTrue(ex.getMessage().contains("Error al crear Feature en Jira"));

        Mockito.clearAllCaches();
    }

    @Test
    void update_ReturnsErrorIfWorkOrderIdIsZero() throws Exception {
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.setWorkOrderId(0);

        IssueTicketService service = new IssueTicketService();

        IDataResult result = service.update(dto);

        assertTrue(result instanceof ErrorDataResult);
        assertEquals("Es necesario el código de registro (workOrderId) para la edición", result.message);
        assertEquals("500", result.status);
    }

    @Test
    void update_ReturnsSuccessIfAllWorkOrderDetailsUpdate() throws Exception {
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.setWorkOrderId(2);
        dto.setFeature("FKEY-1");
        dto.setFolio("FOLIO1");
        dto.setBoardId(1);
        dto.setProjectId(2);
        dto.setSourceId("3");
        dto.setSourceName("SRC");
        dto.setFlowType(1);
        dto.setRegisterUserId("user");
        dto.setUsername("username");
        dto.setToken("token");

        // Mocks y spies
        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(localService, mockDao);

        // Mock GetResponseAsync
        String jiraResponse = "{\"key\":\"NEWKEY-1\"}";
        doReturn(jiraResponse).when(localService).GetResponseAsync(anyString(), anyString(), anyString());

        // Mock ListWorkOrder
        WorkOrder workOrder = new WorkOrder();
        workOrder.work_order_id = 2;
        workOrder.board_id = 9;
        workOrder.feature = "FKEY-1";
        workOrder.folio = "FOLIO1";
        workOrder.source_id = "3";
        workOrder.source_name = "SRC";
        when(mockDao.ListWorkOrder(2)).thenReturn(List.of(workOrder));

        // Mock ListWorkOrderDetails
        WorkOrderDetail detail1 = new WorkOrderDetail();
        detail1.setIssue_code("ISSUE-1");
        WorkOrderDetail detail2 = new WorkOrderDetail();
        detail2.setIssue_code("ISSUE-2");
        when(mockDao.ListWorkOrderDetails(2)).thenReturn(List.of(detail1, detail2));

        // Mock updateTicketJira
        doReturn(List.of("ISSUE-1", "ISSUE-2"))
                .when(localService).updateTicketJira(any(), any(), any());

        // Mock UpdateWorkOrder
        when(mockDao.UpdateWorkOrder(any())).thenReturn(true);

        IDataResult result = localService.update(dto);

        assertTrue(result instanceof SuccessDataResult);
        assertEquals(result.message, "Succesfull"); // No mensaje especial porque todos se actualizaron
    }

    @Test
    void update_ReturnsPartialSuccessIfSomeTicketsFail() throws Exception {
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.setWorkOrderId(2);
        dto.setFeature("FKEY-1");
        dto.setFolio("FOLIO1");
        dto.setBoardId(1);
        dto.setProjectId(2);
        dto.setSourceId("3");
        dto.setSourceName("SRC");
        dto.setFlowType(1);
        dto.setRegisterUserId("user");
        dto.setUsername("username");
        dto.setToken("token");

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(localService, mockDao);

        String jiraResponse = "{\"key\":\"NEWKEY-1\"}";
        doReturn(jiraResponse).when(localService).GetResponseAsync(anyString(), anyString(), anyString());

        WorkOrder workOrder = new WorkOrder();
        workOrder.work_order_id = 2;
        workOrder.board_id = 9;
        workOrder.feature = "FKEY-1";
        workOrder.folio = "FOLIO1";
        workOrder.source_id = "3";
        workOrder.source_name = "SRC";
        when(mockDao.ListWorkOrder(2)).thenReturn(List.of(workOrder));

        WorkOrderDetail detail1 = new WorkOrderDetail();
        detail1.setIssue_code("ISSUE-1");
        WorkOrderDetail detail2 = new WorkOrderDetail();
        detail2.setIssue_code("ISSUE-2");
        when(mockDao.ListWorkOrderDetails(2)).thenReturn(List.of(detail1, detail2));

        // updateTicketJira solo actualiza una HU
        doReturn(List.of("ISSUE-1"))
                .when(localService).updateTicketJira(any(), any(), any());

        when(mockDao.UpdateWorkOrder(any())).thenReturn(true);

        IDataResult result = localService.update(dto);

        assertTrue(result instanceof SuccessDataResult);
        assertEquals("Algunas HUs no pudieron actualizarse, esto puede deberse a que se encuentren en un estado no permitido para la actualización o no existan en Jira", result.message);
    }

    @Test
    void generate_ReturnsSuccessIfAllOk() throws Exception {
        // Arrange
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.setWorkOrderId(7);
        dto.setFeature("FEAT-1");
        dto.setFolio("FOLIOX");
        dto.setBoardId(1);
        dto.setProjectId(2);
        dto.setSourceId("SRCID");
        dto.setSourceName("SRCNAME");
        dto.setFlowType(1);
        dto.setRegisterUserId("user");
        dto.setJiraProjectId(100);
        dto.setJiraProjectName("JIRA-PROJ");
        WorkOrderDetailDtoRequest templateDetail = new WorkOrderDetailDtoRequest();
        dto.setWorkOrderDetail(List.of(templateDetail)); // <--- lista con un elemento

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(localService, mockDao);

        // Mocks
        doNothing().when(localService).createTicketJira2(any(), any(), any());
        when(mockDao.getDataRequestIssueJira2(any(), any(), any())).thenReturn(mock(com.bbva.dto.jira.request.IssueBulkDto.class));
        when(mockDao.InsertWorkOrderDetail(any())).thenReturn(true);

        // Simular que luego del createTicketJira2, el detalle tiene issue_code
        doAnswer(invocation -> {
            List<WorkOrderDetail> list = invocation.getArgument(2);
            list.get(0).setIssue_code("ISSUE-999");
            return null;
        }).when(localService).createTicketJira2(any(), any(), any());

        // Act
        IDataResult result = localService.generate(dto);

        // Assert
        assertTrue(result instanceof SuccessDataResult);
        assertEquals("Succesfull", result.message);
    }

    @Test
    void listSourcesGenerated_ReturnsDaoResult_WhenProjectIdIsPositive() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        sourceTicketDtoRequest dto = new sourceTicketDtoRequest();
        dto.setProjectId(42);

        sourceTicketDtoResponse expectedResponse = new sourceTicketDtoResponse();
        // puedes poblar expectedResponse si necesitas validar campos

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService service = new IssueTicketService();
        // Inyecta el mock si es necesario (con reflection si es private)
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(service, mockDao);

        when(mockDao.listSources(dto)).thenReturn(expectedResponse);

        // Act
        IDataResult<sourceTicketDtoResponse> result = service.listSourcesGenerated(dto);

        // Assert
        assertTrue(result instanceof SuccessDataResult);
    }

    @Test
    void listSourcesGenerated_ReturnsEmptyModel_WhenProjectIdIsZeroOrNegative() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        sourceTicketDtoRequest dto = new sourceTicketDtoRequest();
        dto.setProjectId(0);

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService service = new IssueTicketService();
        // Inyecta el mock si es necesario (con reflection si es private)
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(service, mockDao);

        // Act
        IDataResult<sourceTicketDtoResponse> result = service.listSourcesGenerated(dto);

        // Assert
        assertTrue(result instanceof SuccessDataResult);
    }

    @Test
    void listIssuesGenerated_ReturnsDaoResult_WhenProjectIdIsPositive() throws Exception {
        // Arrange
        sourceTicketDtoRequest dto = new sourceTicketDtoRequest();
        dto.setProjectId(123);

        issueTicketDtoResponse expectedResponse = new issueTicketDtoResponse();
        // Puedes poblar expectedResponse para validar campos concretos

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService service = new IssueTicketService();
        // Inyecta el mock usando reflection si es necesario
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(service, mockDao);

        when(mockDao.listIssuesGenerated(dto)).thenReturn(expectedResponse);

        // Act
        IDataResult<issueTicketDtoResponse> result = service.listIssuesGenerated(dto);

        // Assert
        assertTrue(result instanceof SuccessDataResult);
    }

    @Test
    void listIssuesGenerated_ReturnsEmptyModel_WhenProjectIdIsZeroOrNegative() throws Exception {
        // Arrange
        sourceTicketDtoRequest dto = new sourceTicketDtoRequest();
        dto.setProjectId(0);

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService service = new IssueTicketService();
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(service, mockDao);

        // Act
        IDataResult<issueTicketDtoResponse> result = service.listIssuesGenerated(dto);

        // Assert
        assertTrue(result instanceof SuccessDataResult);
        assertNotNull(result.data);
    }

    @Test
    void updateTicketJira_ReturnsAllSuccessTickets() throws Exception {
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.feature = "FKEY";
        dto.jiraProjectId = 1;
        dto.jiraProjectName = "JiraName";
        WorkOrder workOrder = new WorkOrder();
        List<WorkOrderDetail> details = Arrays.asList(new WorkOrderDetail(), new WorkOrderDetail());

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        // Inyectar mockDao
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(localService, mockDao);

        Map<String, IssueDto> issues = new LinkedHashMap<>();
        issues.put("TICKET-1", new IssueDto());
        issues.put("TICKET-2", new IssueDto());
        when(mockDao.getDataRequestIssueJiraEdit(any(), any(), any())).thenReturn(issues);

        // Mock PutResponseEditAsync - siempre éxito (códigos < 400)
        doReturn(200).when(localService).PutResponseEditAsync(any(), eq("TICKET-1"), any());
        doReturn(200).when(localService).PutResponseEditAsync(any(), eq("TICKET-2"), any());

        List<String> result = localService.updateTicketJira(dto, workOrder, details);

        assertEquals(Arrays.asList("TICKET-1", "TICKET-2"), result);
    }

    @Test
    void updateTicketJira_ThrowsHandledException_WhenAllFail() throws Exception {
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.feature = "FKEY";
        dto.jiraProjectId = 1;
        dto.jiraProjectName = "JiraName";
        WorkOrder workOrder = new WorkOrder();
        List<WorkOrderDetail> details = Arrays.asList(new WorkOrderDetail(), new WorkOrderDetail());

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(localService, mockDao);

        Map<String, IssueDto> issues = new LinkedHashMap<>();
        issues.put("TICKET-1", new IssueDto());
        issues.put("TICKET-2", new IssueDto());
        when(mockDao.getDataRequestIssueJiraEdit(any(), any(), any())).thenReturn(issues);

        // Mock PutResponseEditAsync - siempre error (códigos >= 400)
        doReturn(400).when(localService).PutResponseEditAsync(any(), eq("TICKET-1"), any());
        doReturn(401).when(localService).PutResponseEditAsync(any(), eq("TICKET-2"), any());

        HandledException ex = assertThrows(
                HandledException.class,
                () -> localService.updateTicketJira(dto, workOrder, details)
        );
        assertTrue(ex.getMessage().contains("No se pudo actualizar ninguna HU"));
    }

    @Test
    void updateTicketJira_ReturnsOnlySuccessTickets_WhenPartialFails() throws Exception {
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.feature = "FKEY";
        dto.jiraProjectId = 1;
        dto.jiraProjectName = "JiraName";
        WorkOrder workOrder = new WorkOrder();
        List<WorkOrderDetail> details = Arrays.asList(new WorkOrderDetail(), new WorkOrderDetail());

        IssueTicketDao mockDao = mock(IssueTicketDao.class);
        IssueTicketService localService = Mockito.spy(new IssueTicketService());
        var daoField = IssueTicketService.class.getDeclaredField("issueTicketDao");
        daoField.setAccessible(true);
        daoField.set(localService, mockDao);

        Map<String, IssueDto> issues = new LinkedHashMap<>();
        issues.put("TICKET-1", new IssueDto());
        issues.put("TICKET-2", new IssueDto());
        when(mockDao.getDataRequestIssueJiraEdit(any(), any(), any())).thenReturn(issues);

        // Mock PutResponseEditAsync - uno falla, uno éxito
        doReturn(400).when(localService).PutResponseEditAsync(any(), eq("TICKET-1"), any());
        doReturn(200).when(localService).PutResponseEditAsync(any(), eq("TICKET-2"), any());

        List<String> result = localService.updateTicketJira(dto, workOrder, details);

        assertEquals(Collections.singletonList("TICKET-2"), result);
    }

    @Test
    void PostResponseAsync3_ThrowsHandledException_OnTokenExpired302() throws Exception {
        // Arrange
        WorkOrderDtoRequest objAuth = new WorkOrderDtoRequest();
        objAuth.username = "user";
        objAuth.token = "token";
        IssueBulkDto issueJira = new IssueBulkDto();

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        when(mockStatusLine.getStatusCode()).thenReturn(302);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockResponse);
    }

    @Test
    void PostResponseAsync3_ThrowsHandledException_OnClientError() throws Exception {
        // Arrange
        WorkOrderDtoRequest objAuth = new WorkOrderDtoRequest();
        objAuth.username = "user";
        objAuth.token = "token";
        IssueBulkDto issueJira = new IssueBulkDto();

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        when(mockStatusLine.getStatusCode()).thenReturn(400);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockResponse);
    }

    @Test
    void PostResponseAsync4_ThrowsHandledException_OnTokenExpired302() throws Exception {
        WorkOrderDtoRequest2 objAuth = new WorkOrderDtoRequest2();
        objAuth.username = "user";
        objAuth.token = "token";
        IssueBulkDto issueJira = new IssueBulkDto();

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockStatusLine.getStatusCode()).thenReturn(302);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockResponse);
    }

    @Test
    void PostResponseAsync4_ThrowsHandledException_OnClientError() throws Exception {
        WorkOrderDtoRequest2 objAuth = new WorkOrderDtoRequest2();
        objAuth.username = "user";
        objAuth.token = "token";
        IssueBulkDto issueJira = new IssueBulkDto();

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockStatusLine.getStatusCode()).thenReturn(400);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockHttpClient.execute(any(HttpPost.class))).thenReturn(mockResponse);
    }
    @Test
    void createIssuesInBatches_ThrowsException_WhenNoIssues() {
        IssueBulkDto dto = mock(IssueBulkDto.class);
        when(dto.getIssueUpdates()).thenReturn(Collections.emptyList());

        IssueTicketService service = Mockito.spy(new IssueTicketService());
        WorkOrderDtoRequest2 auth = new WorkOrderDtoRequest2();

        Exception ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.createIssuesInBatches(auth, dto)
        );
        assertTrue(ex.getMessage().contains("No hay issues para crear"));
    }

    @Test
    void createIssuesInBatches_SingleBatch_CallsPostResponseAsync4Once() throws Exception {
        // Arrange
        List<IssueUpdate> updates = new ArrayList<>();
        for (int i = 0; i < 10; ++i) updates.add(new IssueUpdate());
        IssueBulkDto dto = mock(IssueBulkDto.class);
        when(dto.getIssueUpdates()).thenReturn(updates);

        IssueBulkResponse fakeResponse = new IssueBulkResponse();
        IssueTicketService service = Mockito.spy(new IssueTicketService());
        doReturn(fakeResponse).when(service).PostResponseAsync4(any(), any());
        doReturn(fakeResponse).when(service).combineResponses(anyList());

        WorkOrderDtoRequest2 auth = new WorkOrderDtoRequest2();

        // Act
        IssueBulkResponse result = service.createIssuesInBatches(auth, dto);

        // Assert
        verify(service, times(1)).PostResponseAsync4(any(), any());
        verify(service, times(1)).combineResponses(anyList());
        assertSame(fakeResponse, result);
    }

    @Test
    void createIssuesInBatches_MultipleBatches_CallsPostResponseAsync4MultipleTimes() throws Exception {
        // Arrange
        List<IssueUpdate> updates = new ArrayList<>();
        for (int i = 0; i < 65; ++i) updates.add(new IssueUpdate());
        IssueBulkDto dto = mock(IssueBulkDto.class);
        when(dto.getIssueUpdates()).thenReturn(updates);

        IssueBulkResponse resp1 = new IssueBulkResponse();
        IssueBulkResponse resp2 = new IssueBulkResponse();
        IssueBulkResponse resp3 = new IssueBulkResponse();
        IssueBulkResponse combined = new IssueBulkResponse();

        IssueTicketService service = Mockito.spy(new IssueTicketService());
        // Simula que cada llamada devuelve una respuesta distinta
        doReturn(resp1, resp2, resp3).when(service).PostResponseAsync4(any(), any());
        doReturn(combined).when(service).combineResponses(anyList());

        WorkOrderDtoRequest2 auth = new WorkOrderDtoRequest2();

        // Opcional: omite el sleep (puedes usar doNothing().when(service).Thread.sleep(anyLong()) si lo extraes)
        // Act
        IssueBulkResponse result = service.createIssuesInBatches(auth, dto);

        // Assert
        verify(service, times(3)).PostResponseAsync4(any(), any());
        verify(service, times(1)).combineResponses(anyList());
        assertSame(combined, result);
    }

    @Test
    void createIssuesInBatches_SendsCorrectBatches() throws Exception {
        // Arrange
        List<IssueUpdate> updates = new ArrayList<>();
        for (int i = 0; i < 35; ++i) updates.add(new IssueUpdate());
        IssueBulkDto dto = mock(IssueBulkDto.class);
        when(dto.getIssueUpdates()).thenReturn(updates);

        IssueBulkResponse resp1 = new IssueBulkResponse();
        IssueBulkResponse resp2 = new IssueBulkResponse();
        IssueBulkResponse combined = new IssueBulkResponse();

        IssueTicketService service = Mockito.spy(new IssueTicketService());
        doReturn(resp1, resp2).when(service).PostResponseAsync4(any(), any());
        doReturn(combined).when(service).combineResponses(anyList());

        WorkOrderDtoRequest2 auth = new WorkOrderDtoRequest2();

        // Captura los DTOs enviados a PostResponseAsync4
        ArgumentCaptor<IssueBulkDto> dtoCaptor = ArgumentCaptor.forClass(IssueBulkDto.class);

        // Act
        IssueBulkResponse result = service.createIssuesInBatches(auth, dto);

        // Assert: se llama dos veces porque 35/30 = 2 batches
        verify(service, times(2)).PostResponseAsync4(eq(auth), dtoCaptor.capture());
        List<IssueBulkDto> sentDtos = dtoCaptor.getAllValues();
        // Primer batch de 30, segundo de 5
        assertEquals(30, sentDtos.get(0).getIssueUpdates().size());
        assertEquals(5, sentDtos.get(1).getIssueUpdates().size());
        assertSame(combined, result);
    }


    @Test
    void combineResponses_CombineSeveralResponses_AllIssuesIncluded() {
        IssueDto issue1 = new IssueDto();
        IssueDto issue2 = new IssueDto();
        IssueDto issue3 = new IssueDto();
        IssueBulkResponse resp1 = new IssueBulkResponse();
        IssueBulkResponse resp2 = new IssueBulkResponse();

        IssueTicketService service = new IssueTicketService();

        IssueBulkResponse combined = service.combineResponses(Arrays.asList(resp1, resp2));

        assertNotNull(combined.issues);
        assertEquals(0, combined.issues.size());
        assertFalse(combined.issues.contains(issue1));
        assertFalse(combined.issues.contains(issue2));
        assertFalse(combined.issues.contains(issue3));
    }

    @Test
    void combineResponses_HandlesNullIssuesList() {
        IssueDto issue1 = new IssueDto();
        IssueBulkResponse resp1 = new IssueBulkResponse();
        IssueBulkResponse resp2 = new IssueBulkResponse(); // null issues

        IssueTicketService service = new IssueTicketService();

        IssueBulkResponse combined = service.combineResponses(Arrays.asList(resp1, resp2));

        assertNotNull(combined.issues);
        assertEquals(0, combined.issues.size());
    }

    @Test
    void combineResponses_EmptyList_YieldsEmptyIssues() {
        IssueTicketService service = new IssueTicketService();
        IssueBulkResponse combined = service.combineResponses(new ArrayList<>());
        assertNotNull(combined.issues);
        assertTrue(combined.issues.isEmpty());
    }

    @Test
    void combineResponses_SingleResponse() {
        IssueDto issue1 = new IssueDto();
        IssueBulkResponse resp1 = new IssueBulkResponse();

        IssueTicketService service = new IssueTicketService();

        IssueBulkResponse combined = service.combineResponses(Collections.singletonList(resp1));

        assertNotNull(combined.issues);
        assertEquals(0, combined.issues.size());
    }

    @Test
    void PutResponseEditAsync_ReturnsResponseCode_OnSuccess() throws Exception {
        WorkOrderDtoRequest objAuth = new WorkOrderDtoRequest();
        objAuth.username = "user";
        objAuth.token = "token";
        IssueDto issueJira = new IssueDto();

        String ticketCode = "ABC-123";

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        HttpEntity mockEntity = mock(HttpEntity.class);

        when(mockStatusLine.getStatusCode()).thenReturn(204); // Normal Jira success for PUT
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockHttpClient.execute(any(HttpPut.class))).thenReturn(mockResponse);
    }

    @Test
    void PutResponseEditAsync_ThrowsHandledException_OnTokenExpired() throws Exception {
        WorkOrderDtoRequest objAuth = new WorkOrderDtoRequest();
        objAuth.username = "user";
        objAuth.token = "token";
        IssueDto issueJira = new IssueDto();

        String ticketCode = "ABC-123";

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockStatusLine.getStatusCode()).thenReturn(302);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockHttpClient.execute(any(HttpPut.class))).thenReturn(mockResponse);
    }

    @Test
    void PutResponseEditAsync_SetsCookieHeaderAndContentType() throws Exception {
        WorkOrderDtoRequest objAuth = new WorkOrderDtoRequest();
        objAuth.username = "user";
        objAuth.token = "token";
        IssueDto issueJira = new IssueDto();

        String ticketCode = "ABC-123";

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        HttpEntity mockEntity = mock(HttpEntity.class);

        when(mockStatusLine.getStatusCode()).thenReturn(204);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockHttpClient.execute(any(HttpPut.class))).thenReturn(mockResponse);
    }

    @Test
    void GetResponseAsync_ReturnsResponseBody_OnSuccess() throws Exception {
        String username = "user";
        String password = "pass";
        String apiPath = "/endpoint";
        String expectedResponse = "{\"result\": \"ok\"}";

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        HttpEntity mockEntity = mock(HttpEntity.class);

        when(mockStatusLine.getStatusCode()).thenReturn(200);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);

        Mockito.mockStatic(org.apache.http.util.EntityUtils.class);
        when(org.apache.http.util.EntityUtils.toString(mockEntity)).thenReturn(expectedResponse);
    }

    @Test
    void GetResponseAsync_ThrowsHandledException_OnTokenExpired302() throws Exception {
        String username = "user";
        String password = "pass";
        String apiPath = "/endpoint";

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockStatusLine.getStatusCode()).thenReturn(302);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);
    }

    @Test
    void GetResponseAsync_ThrowsHandledException_OnClientError400() throws Exception {
        String username = "user";
        String password = "pass";
        String apiPath = "/endpoint";

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);

        when(mockStatusLine.getStatusCode()).thenReturn(400);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockResponse);
    }

    @Test
    void createCookieHeader_ReturnsEmpty_WhenListIsEmpty() {
        List<Cookie> cookies = Collections.emptyList();
        String header = createCookieHeader(cookies);
        assertEquals("", header);
    }

    @Test
    void createCookieHeader_ReturnsSingleCookieFormat() {
        Cookie cookie = new BasicClientCookie("JSESSIONID", "abc123");
        String header = createCookieHeader(Collections.singletonList(cookie));
        assertEquals("JSESSIONID=abc123", header);
    }

    @Test
    void createCookieHeader_ReturnsMultipleCookiesFormat() {
        Cookie c1 = new BasicClientCookie("JSESSIONID", "abc123");
        Cookie c2 = new BasicClientCookie("XSRF-TOKEN", "xyz789");
        String header = createCookieHeader(Arrays.asList(c1, c2));
        assertEquals("JSESSIONID=abc123; XSRF-TOKEN=xyz789", header);
    }

    @Test
    void createCookieHeader_HandlesSpecialCharacters() {
        Cookie c1 = new BasicClientCookie("token", "va=l;ue");
        String header = createCookieHeader(Collections.singletonList(c1));
        assertEquals("token=va=l;ue", header);
    }
}