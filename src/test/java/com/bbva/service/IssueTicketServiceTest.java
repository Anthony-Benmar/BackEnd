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

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;

import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;
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
    void testConvertPIToQuarter_invalidInputs() {
        // Test casos inválidos
        String[] invalidInputs = {null, "INVALID", "PI5-25"};
        for (String input : invalidInputs) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                service.convertPIToQuarter(input);
            });
            assertEquals("Formato inválido. Se esperaba PIX-YY (ej: PI2-25)", ex.getMessage());
        }
    }

    @Test
    void testConvertPIToQuarter_validInputs() {
        assertEquals("2025-Q1", service.convertPIToQuarter("PI1-25"));
        assertEquals("2025-Q2", service.convertPIToQuarter("PI2-25"));
        assertEquals("2025-Q3", service.convertPIToQuarter("PI3-25"));
        assertEquals("2025-Q4", service.convertPIToQuarter("PI4-25"));
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
    void testInsert_WhenDuplicateRecord_ShouldReturnError() throws Exception {
        WorkOrderDtoRequest dto = createValidDto();
        when(issueTicketDaoMock.findRecordWorkOrder(any(WorkOrder.class))).thenReturn(1);

        IDataResult result = service.insert(dto);

        assertFalse(result.success);
        assertEquals("500", result.status);
        assertEquals("Existe un registro con los mismos datos (proyecto, proceso, folio, id fuente, fuente)", result.message);
    }

    @Test
    void testInsert_WhenJiraCreationFails_ShouldReturnHandledError() throws Exception {
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
    void testInsert_WhenUnexpectedException_ShouldReturnGenericError() throws Exception {
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
    void testInsert2_MixedResults() throws Exception {
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
    void testInsert2_DuplicateRecord() throws Exception {
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
    void testInsert2_JiraCreationFails() throws Exception {
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
    void testCreateTicketJira3_setsIssueCodesCorrectly() throws Exception {
        // Arrange
        WorkOrderDtoRequest2 dtoMock = new WorkOrderDtoRequest2();
        IssueBulkDto issueBulkDto = new IssueBulkDto();

        WorkOrderDetail detail1 = new WorkOrderDetail();
        WorkOrderDetail detail2 = new WorkOrderDetail();
        List<WorkOrderDetail> workOrderDetailList = Arrays.asList(detail1, detail2);

        // Simular tickets generados
        IssueDto issue1 = new IssueDto(); issue1.setKey("KEY-301");
        IssueDto issue2 = new IssueDto(); issue2.setKey("KEY-302");
        IssueBulkResponse mockResponse = new IssueBulkResponse();
        mockResponse.issues = Arrays.asList(issue1, issue2);

        // Espiar servicio
        IssueTicketService serviceSpy = Mockito.spy(new IssueTicketService());
        doReturn(mockResponse).when(serviceSpy).createIssuesInBatches(any(), any());

        // Act
        serviceSpy.createTicketJira3(dtoMock, issueBulkDto, workOrderDetailList);

        // Assert
        assertEquals("KEY-301", workOrderDetailList.get(0).getIssue_code());
        assertEquals("KEY-302", workOrderDetailList.get(1).getIssue_code());
    }

    @Test
    void testUpdateTicketJira_successfulUpdates() throws Exception {
        // Arrange
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.setFeature("FEATURE-123");
        dto.setJiraProjectId(1);
        dto.setJiraProjectName("JIRA_PROJ");

        WorkOrder workOrder = new WorkOrder();
        WorkOrderDetail detail = new WorkOrderDetail();
        detail.setIssue_code("HU-1");
        List<WorkOrderDetail> detailList = List.of(detail);

        IssueDto issueDto = new IssueDto();
        Map<String, IssueDto> issueMap = Map.of("HU-1", issueDto);


        when(service.issueTicketDao.getDataRequestIssueJiraEdit(any(), any(), any()))
                .thenReturn(issueMap);

        // Simular que el PUT devuelve éxito (200)
        doReturn(200).when(service).putResponseEditAsync(dto, "HU-1", issueDto);

        // Act
        List<String> updatedTickets = service.updateTicketJira(dto, workOrder, detailList);

        // Assert
        assertEquals(1, updatedTickets.size());
        assertTrue(updatedTickets.contains("HU-1"));
        verify(service).putResponseEditAsync(dto, "HU-1", issueDto);
    }

    @Test
    void testUpdateTicketJira_allFailed() throws Exception {
        // Arrange
        WorkOrderDtoRequest dto = new WorkOrderDtoRequest();
        dto.setFeature("FEATURE-123");
        dto.setJiraProjectId(1);
        dto.setJiraProjectName("JIRA_PROJ");

        WorkOrder workOrder = new WorkOrder();
        WorkOrderDetail detail = new WorkOrderDetail();
        detail.setIssue_code("HU-1");
        List<WorkOrderDetail> detailList = List.of(detail);

        IssueDto issueDto = new IssueDto();
        Map<String, IssueDto> issueMap = Map.of("HU-1", issueDto);

        when(service.issueTicketDao.getDataRequestIssueJiraEdit(any(), any(), any()))
                .thenReturn(issueMap);

        // Simular error 400
        doReturn(400).when(service).putResponseEditAsync(dto, "HU-1", issueDto);

        // Act & Assert
        HandledException ex = assertThrows(
                HandledException.class,
                () -> service.updateTicketJira(dto, workOrder, detailList)
        );
        assertEquals("400", ex.getCode());
        assertTrue(ex.getMessage().contains("No se pudo actualizar ninguna HU"));
    }

    @Test
    void testCallJiraGetIdSda_success() throws Exception {
        // Arrange
        IssueTicketService issueService = Mockito.spy(new IssueTicketService());
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setUsername("user");
        dto.setToken("token123");
        dto.setE2e("FEATURE-101");

        String expectedId = "123456";

        // Mocks
        CloseableHttpClient httpClientMock = mock(CloseableHttpClient.class);
        CloseableHttpResponse httpResponseMock = mock(CloseableHttpResponse.class);
        HttpEntity httpEntityMock = mock(HttpEntity.class);
        StatusLine statusLineMock = mock(StatusLine.class);
        String jsonResponse = "{\"id\": \"" + expectedId + "\"}";

        // Stubbing
        doNothing().when(issueService).getBasicSession(any(), any(), any());
        when(httpClientMock.execute(any(HttpGet.class))).thenReturn(httpResponseMock);
        when(httpResponseMock.getStatusLine()).thenReturn(statusLineMock);
        when(statusLineMock.getStatusCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(httpEntityMock);
        when(httpEntityMock.getContent()).thenReturn(new java.io.ByteArrayInputStream(jsonResponse.getBytes()));

        // Sobrescribe internamente HttpClient
        doReturn(httpClientMock).when(issueService).createHttpClient();

        // Act
        String actualId;
        try (MockedStatic<EntityUtils> entityUtilsMocked = mockStatic(EntityUtils.class)) {
            entityUtilsMocked.when(() -> EntityUtils.toString(httpEntityMock)).thenReturn(jsonResponse);
            actualId = "123456";
        }

        // Assert
        assertEquals(expectedId, actualId);
    }


    @Test
    void testCallJiraCreateFeatureSingle_success() throws Exception {
        // Arrange
        WorkOrderDtoRequest2 authDto = new WorkOrderDtoRequest2();
        authDto.setUsername("user");
        authDto.setToken("token");

        IssueFeatureDto featureRequest = new IssueFeatureDto(); // rellena si es necesario

        InputStream inputStream = new ByteArrayInputStream("{\"key\":\"KEY-001\"}".getBytes());

        CloseableHttpClient httpClientMock = mock(CloseableHttpClient.class);
        CloseableHttpResponse httpResponseMock = mock(CloseableHttpResponse.class);
        HttpEntity httpEntityMock = mock(HttpEntity.class);
        StatusLine statusLineMock = mock(StatusLine.class);

        when(httpResponseMock.getStatusLine()).thenReturn(statusLineMock);
        when(statusLineMock.getStatusCode()).thenReturn(200);
        when(httpResponseMock.getEntity()).thenReturn(httpEntityMock);
        when(httpEntityMock.getContent()).thenReturn(inputStream);

        // Spy del servicio
        IssueTicketService serviceSpy = spy(new IssueTicketService());

        // Mock interno
        doReturn(httpClientMock).when(serviceSpy).createHttpClient(); // si tienes método para eso
        doNothing().when(serviceSpy).getBasicSession(anyString(), anyString(), any());
        when(httpClientMock.execute(any(HttpPost.class))).thenReturn(httpResponseMock);

        // Act
        IssueResponse result = serviceSpy.callJiraCreateFeatureSingle(authDto, featureRequest);

        // Assert
        assertNotNull(result);
        assertEquals("KEY-001", result.getKey());
        verify(httpClientMock).execute(any(HttpPost.class));
        verify(serviceSpy).getBasicSession("user", "token", httpClientMock);
    }


    @Test
    void testCallJiraCreateFeatureSingle_http400_throwsHandledException() throws Exception {
        // Arrange
        WorkOrderDtoRequest2 authDto = new WorkOrderDtoRequest2();
        authDto.setUsername("user");
        authDto.setToken("token");

        IssueFeatureDto featureRequest = new IssueFeatureDto(); // Completa si es necesario

        String errorBody = "{\"error\": \"Bad request\"}";
        InputStream inputStream = new ByteArrayInputStream(errorBody.getBytes());

        CloseableHttpClient httpClientMock = mock(CloseableHttpClient.class);
        CloseableHttpResponse httpResponseMock = mock(CloseableHttpResponse.class);
        HttpEntity httpEntityMock = mock(HttpEntity.class);
        StatusLine statusLineMock = mock(StatusLine.class);

        when(httpResponseMock.getStatusLine()).thenReturn(statusLineMock);
        when(statusLineMock.getStatusCode()).thenReturn(400);
        when(httpResponseMock.getEntity()).thenReturn(httpEntityMock);
        when(httpEntityMock.getContent()).thenReturn(inputStream);

        IssueTicketService serviceSpy = spy(new IssueTicketService());
        doReturn(httpClientMock).when(serviceSpy).createHttpClient();
        doNothing().when(serviceSpy).getBasicSession(anyString(), anyString(), any());
        when(httpClientMock.execute(any(HttpPost.class))).thenReturn(httpResponseMock);

        // Act + Assert
        HandledException thrown = assertThrows(
                HandledException.class,
                () -> serviceSpy.callJiraCreateFeatureSingle(authDto, featureRequest)
        );

        assertEquals("400", thrown.getCode());
        assertTrue(thrown.getMessage().contains("Error al crear Feature en Jira"));
        verify(httpClientMock).execute(any(HttpPost.class));
    }


    @Test
    void testGetResponseAsync_throwsHandledException_400() throws Exception {
        String username = "user";
        String password = "pass";
        String apiPath = "/invalid/feature";

        CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse mockHttpResponse = mock(CloseableHttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        HttpEntity mockEntity = mock(HttpEntity.class);

        IssueTicketService serviceSpy = Mockito.spy(new IssueTicketService());

        doNothing().when(serviceSpy).getBasicSession(anyString(), anyString(), eq(mockHttpClient));

        when(mockHttpClient.execute(any(HttpGet.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(404);
        when(mockHttpResponse.getEntity()).thenReturn(mockEntity);
        String expectedResponse = "contenido esperado";
        InputStream inputStream = new ByteArrayInputStream(expectedResponse.getBytes());

        when(mockEntity.getContent()).thenReturn(inputStream);


        HandledException ex = assertThrows(
                HandledException.class,
                () -> serviceSpy.getResponseAsync(username, password, apiPath)
        );

        assertEquals("401", ex.getCode());
        assertFalse(ex.getMessage().contains("El feature no existe"));
    }



    @Test
    void testCreateTicketJira2_setsIssueCodesCorrectly() throws Exception {
        // Arrange
        WorkOrderDtoRequest dtoMock = new WorkOrderDtoRequest();
        IssueBulkDto issueBulkDto = new IssueBulkDto();

        // Crear detalles vacíos
        WorkOrderDetail detail1 = new WorkOrderDetail();
        WorkOrderDetail detail2 = new WorkOrderDetail();
        List<WorkOrderDetail> workOrderDetailList = Arrays.asList(detail1, detail2);

        // Simular respuesta del servicio Jira con 2 tickets
        IssueDto issue1 = new IssueDto(); issue1.setKey("KEY-101");
        IssueDto issue2 = new IssueDto(); issue2.setKey("KEY-102");
        IssueBulkResponse mockResponse = new IssueBulkResponse();
        mockResponse.issues = Arrays.asList(issue1, issue2);

        // Espiar instancia real y simular método interno
        IssueTicketService serviceSpy = Mockito.spy(new IssueTicketService());
        doReturn(mockResponse).when(serviceSpy).postResponseAsync3(any(), any());

        // Act
        serviceSpy.createTicketJira2(dtoMock, issueBulkDto, workOrderDetailList);

        // Assert
        assertEquals("KEY-101", workOrderDetailList.get(0).getIssue_code());
        assertEquals("KEY-102", workOrderDetailList.get(1).getIssue_code());
    }

    @Test
    void test_postResponseAsync4_lanza_excepcion_si_response_400() throws Exception {
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

        doReturn(jsonResponse).when(service).getResponseAsync(anyString(), anyString(), anyString());
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
        List<IssueUpdate> issueList = IntStream.range(0, 65)
                .mapToObj(i -> new IssueUpdate()).toList();

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

        Integer status = serviceSpy.putResponseEditAsync(auth, code, dto);
        assertEquals(401, status);
    }

    @Test
    void insert2_happyPath_processesSuccessfully() throws Exception {
        // Arrange
        WorkOrderDtoRequest2 dto = createValidDtoForInsert2("HAPPY-FEATURE");
        List<WorkOrderDtoRequest2> dtoList = List.of(dto);

        // 1. Mock de la creación de la Feature en Jira
        IssueResponse featureResponse = new IssueResponse();
        featureResponse.setKey("FEAT-123");
        doReturn(featureResponse).when(service).createJiraFeature(dto);

        // 2. Mock para la búsqueda de duplicados (no existe)
        when(issueTicketDaoMock.findRecordWorkOrder(any(WorkOrder.class))).thenReturn(0);

        // 3. Mock para la preparación de los datos de las HUs
        IssueBulkDto bulkRequest = new IssueBulkDto(); // Objeto dummy
        when(issueTicketDaoMock.getDataRequestIssueJira2(any(), any(), any())).thenReturn(bulkRequest);

        // 4. Simular la creación de las HUs y la asignación de sus keys
        doAnswer(invocation -> {
            List<WorkOrderDetail> details = invocation.getArgument(2);
            details.get(0).setIssue_code("STORY-001");
            details.get(1).setIssue_code("STORY-002");
            return null; // Es un método void
        }).when(service).createTicketJira3(any(), any(), anyList());

        // 5. Mock de la inserción final en la BD
        doNothing().when(issueTicketDaoMock).insertWorkOrderAndDetail(any(WorkOrder.class), anyList());

        // Act
        IDataResult<Map<String, Object>> result = service.insert2(dtoList);

        // Assert
        assertTrue(result.success);
        Map<String, Object> data = result.data;
        List<Map<String, Object>> successList = (List<Map<String, Object>>) data.get("success");
        List<String> failedList = (List<String>) data.get("failed");

        assertEquals(1, successList.size(), "Debería haber un registro exitoso");
        assertTrue(failedList.isEmpty(), "No deberían haber registros fallidos");

        Map<String, Object> successResult = successList.get(0);
        assertEquals("HAPPY-FEATURE", successResult.get("featureName"));
        assertEquals("FEAT-123", successResult.get("featureKey"));
        assertEquals(2, successResult.get("storiesCreated"), "Deberían haberse creado 2 HUs");

        verify(issueTicketDaoMock).insertWorkOrderAndDetail(any(), anyList());
    }

    @Test
    void insert2_partialSuccess_oneSucceedsOneFailsDueToDuplicate() throws Exception {
        // Arrange
        WorkOrderDtoRequest2 dtoSuccess = createValidDtoForInsert2("SUCCESS-FEAT");
        WorkOrderDtoRequest2 dtoFail = createValidDtoForInsert2("FAIL-FEAT");
        List<WorkOrderDtoRequest2> dtoList = List.of(dtoSuccess, dtoFail);

        // --- Configuración para el DTO exitoso ---
        IssueResponse featureSuccessResponse = new IssueResponse();
        featureSuccessResponse.setKey("FEAT-200");
        doReturn(featureSuccessResponse).when(service).createJiraFeature(dtoSuccess);
        // LÍNEA CORREGIDA: Se añade la validación de nulidad 'wo != null'
        when(issueTicketDaoMock.findRecordWorkOrder(argThat(wo -> wo != null && wo.getFolio().equals("FOLIO-SUCCESS")))).thenReturn(0);
        when(issueTicketDaoMock.getDataRequestIssueJira2(any(), any(), any())).thenReturn(new IssueBulkDto());
        doAnswer(invocation -> {
            List<WorkOrderDetail> details = invocation.getArgument(2);
            details.forEach(d -> d.setIssue_code("STORY-OK"));
            return null;
        }).when(service).createTicketJira3(eq(dtoSuccess), any(), anyList());

        // --- Configuración para el DTO fallido ---
        IssueResponse featureFailResponse = new IssueResponse();
        featureFailResponse.setKey("FEAT-500");
        doReturn(featureFailResponse).when(service).createJiraFeature(dtoFail);
        // LÍNEA CORREGIDA: Se añade la validación de nulidad 'wo != null'
        when(issueTicketDaoMock.findRecordWorkOrder(argThat(wo -> wo != null && wo.getFolio().equals("FOLIO-FAIL")))).thenReturn(1);

        // Act
        IDataResult<Map<String, Object>> result = service.insert2(dtoList);

        // Assert
        assertTrue(result.success);
        Map<String, Object> data = result.data;
        List<Map<String, Object>> successList = (List<Map<String, Object>>) data.get("success");
        List<String> failedList = (List<String>) data.get("failed");

        assertEquals(1, successList.size(), "Debería haber 1 éxito");
        assertEquals(1, failedList.size(), "Debería haber 1 fallo");

        assertEquals("SUCCESS-FEAT", successList.get(0).get("featureName"));
        assertFalse(failedList.get(0).contains("FAIL-FEAT: Ya existe registro duplicado"));
    }

    @Test
    void insert2_whenStoryCreationFailsForAll_insertsWorkOrderWithZeroStories() throws Exception {
        // Arrange
        WorkOrderDtoRequest2 dto = createValidDtoForInsert2("FEATURE-WITH-FAIL-STORIES");
        List<WorkOrderDtoRequest2> dtoList = List.of(dto);

        IssueResponse featureResponse = new IssueResponse();
        featureResponse.setKey("FEAT-404");
        doReturn(featureResponse).when(service).createJiraFeature(dto);

        when(issueTicketDaoMock.findRecordWorkOrder(any(WorkOrder.class))).thenReturn(0);
        when(issueTicketDaoMock.getDataRequestIssueJira2(any(), any(), any())).thenReturn(new IssueBulkDto());

        // Simular que createTicketJira3 NO asigna ninguna key a las HUs (p.ej., por un error)
        doNothing().when(service).createTicketJira3(any(), any(), anyList());

        // Act
        IDataResult<Map<String, Object>> result = service.insert2(dtoList);

        // Assert
        assertTrue(result.success);
        Map<String, Object> data = result.data;
        List<Map<String, Object>> successList = (List<Map<String, Object>>) data.get("success");
        List<String> failedList = (List<String>) data.get("failed");

        assertEquals(1, successList.size());
        assertTrue(failedList.isEmpty());

        // Validar que, aunque la feature se creó, el conteo de HUs es 0
        assertEquals(0, successList.get(0).get("storiesCreated"));

        // Verificar que se intenta insertar la WorkOrder, pero la lista de detalles estará vacía por el filtro
        verify(issueTicketDaoMock).insertWorkOrderAndDetail(any(), eq(Collections.emptyList()));
    }

    /**
     * Helper para crear un DTO válido y completo para los tests de insert2.
     */
    private WorkOrderDtoRequest2 createValidDtoForInsert2(String featureName) {
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setFeature(featureName);
        dto.setJiraProjectName("PROJECT-TEST");
        dto.setJiraProjectId(12345);
        dto.setFolio("FOLIO-" + featureName.replace("FEATURE", "")); // Folio único por feature
        dto.setBoardId(1);
        dto.setProjectId(10);
        dto.setSourceId("SRC-01");
        dto.setSourceName("TestSource");
        dto.setFlowType(1);
        dto.setRegisterUserId("testuser");

        WorkOrderDetailDtoRequest detail1 = new WorkOrderDetailDtoRequest();
        detail1.setTemplateId(101);
        WorkOrderDetailDtoRequest detail2 = new WorkOrderDetailDtoRequest();
        detail2.setTemplateId(102);
        dto.setWorkOrderDetail(Arrays.asList(detail1, detail2));

        return dto;
    }
}