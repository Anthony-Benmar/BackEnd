package com.bbva.service;
import com.bbva.core.HandledException;
import com.bbva.dto.metaknight.request.IngestaRequestDto;
import com.bbva.service.metaknight.IngestaService;
import com.bbva.util.metaknight.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IngestaServiceTest {

    private IngestaService service;
    private SchemaProcessor schemaProcessorMock;
    private BaseFunctions baseFunctionsMock;
    private Rules rulesMock;
    private DocumentGenerator documentGeneratorMock;
    private ZipGenerator zipGeneratorMock;
    private IssueTicketService issueTicketServiceMock;

    @BeforeEach
    void setUp() throws Exception {
        service = Mockito.spy(new IngestaService());

        schemaProcessorMock = mock(SchemaProcessor.class);
        baseFunctionsMock = mock(BaseFunctions.class);
        rulesMock = mock(Rules.class);
        documentGeneratorMock = mock(DocumentGenerator.class);
        zipGeneratorMock = mock(ZipGenerator.class);
        issueTicketServiceMock = mock(IssueTicketService.class);

        injectMock("schemaProcessor", schemaProcessorMock);
        injectMock("baseFunctions", baseFunctionsMock);
        injectMock("rules", rulesMock);
        injectMock("documentGenerator", documentGeneratorMock);
        injectMock("zipGenerator", zipGeneratorMock);
        injectMock("issueTicketService", issueTicketServiceMock);
    }

    private void injectMock(String fieldName, Object mock) throws Exception {
        Field field = IngestaService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, mock);
    }

    @Test
    void testProcesarIngesta_Success() throws Exception {
        // Arrange
        IngestaRequestDto request = createValidRequestWithRealCSV();
        setupSchemaProcessorMocks();

        byte[] hammurabiDoc = "hammurabi-doc".getBytes();
        byte[] kirbyDoc = "kirby-doc".getBytes();
        when(documentGeneratorMock.generarDocumentoC204Hammurabi(eq(request), any()))
                .thenReturn(hammurabiDoc);
        when(documentGeneratorMock.generarDocumentoC204Kirby(eq(request), any()))
                .thenReturn(kirbyDoc);

        byte[] expectedZip = "zip-content".getBytes();
        when(zipGeneratorMock.crearZip(any())).thenReturn(expectedZip);

        doNothing().when(issueTicketServiceMock).addLabelToIssue(anyString(), anyString(), anyString(), anyString());

        byte[] result = service.procesarIngesta(request);

        assertNotNull(result);
        assertEquals(expectedZip, result);

        verify(issueTicketServiceMock, times(1)).addLabelToIssue(
                request.getUsername(), request.getToken(), request.getTicketJira(), "Metaknight");
        verify(schemaProcessorMock).initialize(any(), any(), eq(request));
        verify(documentGeneratorMock).generarDocumentoC204Hammurabi(eq(request), any());
        verify(documentGeneratorMock).generarDocumentoC204Kirby(eq(request), any());
        verify(zipGeneratorMock).crearZip(any());
    }

    @Test
    void testProcesarIngesta_WithL1T() throws Exception {
        IngestaRequestDto request = createValidRequestWithRealCSV();
        request.setTieneL1T(true);

        setupSchemaProcessorMocks();

        byte[] hammurabiDoc = "hammurabi-doc".getBytes();
        byte[] kirbyDoc = "kirby-doc".getBytes();
        when(documentGeneratorMock.generarDocumentoC204Hammurabi(eq(request), any()))
                .thenReturn(hammurabiDoc);
        when(documentGeneratorMock.generarDocumentoC204Kirby(eq(request), any()))
                .thenReturn(kirbyDoc);

        byte[] expectedZip = "zip-with-l1t".getBytes();
        when(zipGeneratorMock.crearZip(any())).thenReturn(expectedZip);

        doNothing().when(issueTicketServiceMock).addLabelToIssue(anyString(), anyString(), anyString(), anyString());

        byte[] result = service.procesarIngesta(request);

        assertNotNull(result);
        assertEquals(expectedZip, result);

        verify(zipGeneratorMock).crearZip(any());
    }

    @Test
    void testProcesarIngesta_NullRequest() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.procesarIngesta(null)
        );

        assertEquals("Request no puede ser nulo", exception.getMessage());
    }

    @Test
    void testProcesarIngesta_MissingUuaaMaster() {
        IngestaRequestDto request = createValidRequest();
        request.setUuaaMaster(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.procesarIngesta(request)
        );

        assertTrue(exception.getMessage().contains("UUAA Master es requerido"));
    }

    @Test
    void testProcesarIngesta_MissingSchemaRaw() {
        IngestaRequestDto request = createValidRequest();
        request.setSchemaRawBase64(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.procesarIngesta(request)
        );

        assertEquals("Schema Raw es requerido", exception.getMessage());
    }

    @Test
    void testProcesarIngesta_EmptySchemaRaw() {
        IngestaRequestDto request = createValidRequest();
        request.setSchemaRawBase64("");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.procesarIngesta(request)
        );

        assertEquals("Schema Raw es requerido", exception.getMessage());
    }

    @Test
    void testProcesarIngesta_MissingSchemaMaster() {
        IngestaRequestDto request = createValidRequest();
        request.setSchemaMasterBase64(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.procesarIngesta(request)
        );

        assertEquals("Schema Master es requerido", exception.getMessage());
    }

    @Test
    void testProcesarIngesta_MultipleValidationErrors() {
        IngestaRequestDto request = new IngestaRequestDto();
        request.setSchemaRawBase64("valid-base64");
        request.setSchemaMasterBase64("valid-base64");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.procesarIngesta(request)
        );

        assertTrue(exception.getMessage().contains("Errores de validación"));
        assertTrue(exception.getMessage().contains("UUAA Master es requerido"));
        assertTrue(exception.getMessage().contains("Delimitador es requerido"));
        assertTrue(exception.getMessage().contains("Tipo de archivo es requerido"));
        assertTrue(exception.getMessage().contains("Particiones son requeridas"));
    }

    @Test
    void testProcesarIngesta_IssueTicketServiceFails() throws Exception {
        IngestaRequestDto request = createValidRequestWithRealCSV();
        setupSchemaProcessorMocks();

        byte[] hammurabiDoc = "hammurabi-doc".getBytes();
        byte[] kirbyDoc = "kirby-doc".getBytes();
        when(documentGeneratorMock.generarDocumentoC204Hammurabi(eq(request), any()))
                .thenReturn(hammurabiDoc);
        when(documentGeneratorMock.generarDocumentoC204Kirby(eq(request), any()))
                .thenReturn(kirbyDoc);

        byte[] expectedZip = "zip-content".getBytes();
        when(zipGeneratorMock.crearZip(any())).thenReturn(expectedZip);

        doNothing().doThrow(new RuntimeException("Jira error"))
                .when(issueTicketServiceMock).addLabelToIssue(anyString(), anyString(), anyString(), anyString());

        byte[] result = service.procesarIngesta(request);

        assertNotNull(result);
        assertEquals(expectedZip, result);

        verify(issueTicketServiceMock, times(1)).addLabelToIssue(
                request.getUsername(), request.getToken(), request.getTicketJira(), "Metaknight");
    }

    @Test
    void testParsearCsvDesdeBase64_Success() throws Exception {
        String csvContent = "Header1;Header2\nValue1;Value2\nValue3;Value4";
        String base64Csv = Base64.getEncoder().encodeToString(csvContent.getBytes(StandardCharsets.UTF_8));

        Method method = IngestaService.class.getDeclaredMethod("parsearCsvDesdeBase64", String.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> result = (List<Map<String, Object>>) method.invoke(service, base64Csv);

        assertNotNull(result);
        assertEquals(2, result.size());

        Map<String, Object> firstRow = result.get(0);
        assertEquals("Value1", firstRow.get("Header1"));
        assertEquals("Value2", firstRow.get("Header2"));

        Map<String, Object> secondRow = result.get(1);
        assertEquals("Value3", secondRow.get("Header1"));
        assertEquals("Value4", secondRow.get("Header2"));
    }

    @Test
    void testParsearCsvDesdeBase64_InvalidBase64() throws Exception {
        String invalidBase64 = "invalid-base64!!!";

        Method method = IngestaService.class.getDeclaredMethod("parsearCsvDesdeBase64", String.class);
        method.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            method.invoke(service, invalidBase64);
        });

        Throwable cause = exception.getCause();
        assertTrue(cause instanceof HandledException);
        assertTrue(cause.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testGenerarConfiguraciones_Integration() throws Exception {
        IngestaRequestDto request = createValidRequestWithRealCSV();
        setupSchemaProcessorMocks();

        when(baseFunctionsMock.convertStagingJsonToSelectedFormat(any())).thenReturn("staging-config");
        when(baseFunctionsMock.convertStagingInputToSelectedFormat(any())).thenReturn("staging-input");
        when(baseFunctionsMock.convertFinalJsonToSelectedFormat(any())).thenReturn("{}");

        byte[] hammurabiDoc = "hammurabi-doc".getBytes();
        byte[] kirbyDoc = "kirby-doc".getBytes();
        when(documentGeneratorMock.generarDocumentoC204Hammurabi(eq(request), any()))
                .thenReturn(hammurabiDoc);
        when(documentGeneratorMock.generarDocumentoC204Kirby(eq(request), any()))
                .thenReturn(kirbyDoc);

        when(zipGeneratorMock.crearZip(any())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Map<String, byte[]> archivos = invocation.getArgument(0);

            assertTrue(archivos.size() > 10);

            assertTrue(archivos.keySet().stream().anyMatch(key -> key.contains("qlt/staging")));
            assertTrue(archivos.keySet().stream().anyMatch(key -> key.contains("qlt/raw")));
            assertTrue(archivos.keySet().stream().anyMatch(key -> key.contains("qlt/master")));

            assertTrue(archivos.keySet().stream().anyMatch(key -> key.contains("kirby/raw")));
            assertTrue(archivos.keySet().stream().anyMatch(key -> key.contains("kirby/master")));

            assertTrue(archivos.containsKey("hammurabi_C204.docx"));
            assertTrue(archivos.containsKey("kirby_C204.docx"));

            return "generated-zip".getBytes();
        });

        doNothing().when(issueTicketServiceMock).addLabelToIssue(anyString(), anyString(), anyString(), anyString());

        byte[] result = service.procesarIngesta(request);

        assertNotNull(result);
        assertEquals("generated-zip", new String(result));
    }

    @Test
    void testGenerarConfiguracionesL1T_Integration() throws Exception {
        IngestaRequestDto request = createValidRequestWithRealCSV();
        request.setTieneL1T(true);
        setupSchemaProcessorMocks();

        byte[] hammurabiDoc = "hammurabi-doc".getBytes();
        byte[] kirbyDoc = "kirby-doc".getBytes();
        when(documentGeneratorMock.generarDocumentoC204Hammurabi(eq(request), any()))
                .thenReturn(hammurabiDoc);
        when(documentGeneratorMock.generarDocumentoC204Kirby(eq(request), any()))
                .thenReturn(kirbyDoc);

        when(zipGeneratorMock.crearZip(any())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Map<String, byte[]> archivos = invocation.getArgument(0);

            assertTrue(archivos.keySet().stream().anyMatch(key -> key.contains("qlt/l1t")),
                    "Debe contener archivos L1T de Hammurabi");
            assertTrue(archivos.keySet().stream().anyMatch(key -> key.contains("kirby/l1t")),
                    "Debe contener archivos L1T de Kirby");

            return "zip-with-l1t".getBytes();
        });

        doNothing().when(issueTicketServiceMock).addLabelToIssue(anyString(), anyString(), anyString(), anyString());

        byte[] result = service.procesarIngesta(request);

        assertNotNull(result);
        assertEquals("zip-with-l1t", new String(result));
    }

    @Test
    void testValidarRequest_Success() throws Exception {
        IngestaRequestDto request = createValidRequest();

        Method method = IngestaService.class.getDeclaredMethod("validarRequest", IngestaRequestDto.class);
        method.setAccessible(true);

        assertDoesNotThrow(() -> method.invoke(service, request));
    }

    @Test
    void testValidarRequest_EmptyFields() throws Exception {
        IngestaRequestDto request = new IngestaRequestDto();
        request.setUuaaMaster("  ");
        request.setDelimitador("");
        request.setTipoArchivo(null);
        request.setParticiones("");

        Method method = IngestaService.class.getDeclaredMethod("validarRequest", IngestaRequestDto.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(service, request);
        });

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        String errorMessage = exception.getCause().getMessage();
        assertTrue(errorMessage.contains("UUAA Master es requerido"));
        assertTrue(errorMessage.contains("Delimitador es requerido"));
        assertTrue(errorMessage.contains("Tipo de archivo es requerido"));
        assertTrue(errorMessage.contains("Particiones son requeridas"));
    }

    private IngestaRequestDto createValidRequest() {
        IngestaRequestDto request = new IngestaRequestDto();
        request.setFrecuencia("diaria");
        request.setUuaaMaster("test");
        request.setTipoArchivo("csv");
        request.setDelimitador(";");
        request.setParticiones("cutoff_date");
        request.setTieneL1T(false);
        request.setTieneCompactacion(true);
        request.setSdatool("TEST-TOOL");
        request.setProyecto("Proyecto Test");
        request.setSm("SM Test");
        request.setPo("PO Test");
        request.setNombreDev("Dev Test");
        request.setRegistroDev("DEV123");
        request.setSchemaRawBase64("SGVhZGVyMTtIZWFkZXIyClZhbHVlMTtWYWx1ZTI=");
        request.setSchemaMasterBase64("SGVhZGVyMTtIZWFkZXIyClZhbHVlMTtWYWx1ZTI=");
        request.setUsername("testuser");
        request.setToken("testtoken");
        request.setTicketJira("JIRA-123");
        return request;
    }

    private IngestaRequestDto createValidRequestWithRealCSV() {
        IngestaRequestDto request = new IngestaRequestDto();
        request.setFrecuencia("diaria");
        request.setUuaaMaster("test");
        request.setTipoArchivo("csv");
        request.setDelimitador(";");
        request.setParticiones("cutoff_date");
        request.setTieneL1T(false);
        request.setTieneCompactacion(true);
        request.setSdatool("TEST-TOOL");
        request.setProyecto("Proyecto Test");
        request.setSm("SM Test");
        request.setPo("PO Test");
        request.setNombreDev("Dev Test");
        request.setRegistroDev("DEV123");
        request.setUsername("testuser");
        request.setToken("testtoken");
        request.setTicketJira("JIRA-123");

        request.setSchemaRawBase64(createRealRawCSVBase64());
        request.setSchemaMasterBase64(createRealMasterCSVBase64());

        return request;
    }

    private String createRealRawCSVBase64() {
        String csvContent = """
            Physical Name field;Key;Logical Format;Physical name object;Physical name of source object
            field1;True;ALPHANUMERIC(10);test_raw_table;original_staging
            field2;False;STRING;test_raw_table;original_staging
            field3;True;DATE;test_raw_table;original_staging
            cutoff_date;True;STRING;test_raw_table;original_staging
            """;
        return Base64.getEncoder().encodeToString(csvContent.getBytes(StandardCharsets.UTF_8));
    }

    private String createRealMasterCSVBase64() {
        String csvContent = """
            Physical Name field;Source field;Data Type;Physical name object
            master_field1;source_field1;string;test_master_table
            master_field2;source_field2;date;test_master_table
            master_field3;source_field3;timestamp;test_master_table
            calculated_field;Calculated;string;test_master_table
            """;
        return Base64.getEncoder().encodeToString(csvContent.getBytes(StandardCharsets.UTF_8));
    }

    private void setupSchemaProcessorMocks() {
        when(schemaProcessorMock.getDfMasterName()).thenReturn("test_master_table");
        when(schemaProcessorMock.getDfRawName()).thenReturn("test_raw_table");
        when(schemaProcessorMock.getDfRawPath()).thenReturn("/data/raw/test/data/test_raw_table");
        when(schemaProcessorMock.getDfMasterPath()).thenReturn("/data/master/test/data/test_master_table");
        when(schemaProcessorMock.getDfStagingPath()).thenReturn("/in/staging/datax/test/test_staging");
        when(schemaProcessorMock.getArtifactoryPath()).thenReturn("\"artifactory-path\"");
        when(schemaProcessorMock.getSubset()).thenReturn("cutoff_date='${?DATE}'");
        when(schemaProcessorMock.getPartitionList()).thenReturn("\"cutoff_date\"");
        when(schemaProcessorMock.getKeys()).thenReturn(Arrays.asList("field1", "field3"));
        when(schemaProcessorMock.getKeysDict()).thenReturn(Map.of("field1", "ALPHANUMERIC(10)", "field3", "DATE"));
        when(schemaProcessorMock.getIdJsonStaging()).thenReturn("test-pe-hmm-qlt-001s-01");
        when(schemaProcessorMock.getIdJsonRaw()).thenReturn("test-pe-hmm-qlt-001r-01");
        when(schemaProcessorMock.getIdJsonMaster()).thenReturn("test-pe-hmm-qlt-001m-01");
        when(schemaProcessorMock.getTag()).thenReturn("001");
        when(schemaProcessorMock.getTrimAllColumns()).thenReturn("\"source_field1|source_field2|source_field3\"");
        when(schemaProcessorMock.getRawDateColumns()).thenReturn(Arrays.asList("source_field2"));
        when(schemaProcessorMock.getRawTimestampColumns()).thenReturn(Arrays.asList("source_field3"));
        when(schemaProcessorMock.getMasterFieldWithOriginList()).thenReturn(
                Arrays.asList(
                        Arrays.asList("master_field1", "source_field1"),
                        Arrays.asList("master_field2", "source_field2"),
                        Arrays.asList("master_field3", "source_field3"),
                        Arrays.asList("calculated_field", "Calculated")
                ));
        when(schemaProcessorMock.getMasterFieldList()).thenReturn(
                Arrays.asList("master_field1", "master_field2", "master_field3", "calculated_field"));
        when(schemaProcessorMock.getMasterArtifactoryPath()).thenReturn("\"master-artifactory-path\"");
        when(schemaProcessorMock.getRawArtifactoryPath()).thenReturn("\"raw-artifactory-path\"");
    }

    @Test
    void testGenerarConfiguracionesL1T_WithHammurabiAndKirby() throws Exception {
        IngestaRequestDto request = createValidRequestWithRealCSV();
        request.setTieneL1T(true);
        setupSchemaProcessorMocks();

        byte[] hammurabiDoc = "hammurabi-doc".getBytes();
        byte[] kirbyDoc = "kirby-doc".getBytes();
        when(documentGeneratorMock.generarDocumentoC204Hammurabi(eq(request), any()))
                .thenReturn(hammurabiDoc);
        when(documentGeneratorMock.generarDocumentoC204Kirby(eq(request), any()))
                .thenReturn(kirbyDoc);

        when(zipGeneratorMock.crearZip(any())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Map<String, byte[]> archivos = invocation.getArgument(0);

            assertTrue(archivos.keySet().stream().anyMatch(key -> key.contains("qlt/l1t")),
                    "Debe contener archivos L1T de Hammurabi (qlt/l1t)");
            assertTrue(archivos.keySet().stream().anyMatch(key -> key.contains("kirby/l1t")),
                    "Debe contener archivos L1T de Kirby (kirby/l1t)");

            assertTrue(archivos.keySet().stream().anyMatch(key ->
                            key.equals("qlt/l1t/" + schemaProcessorMock.getDfMasterName() + "_l1t.conf")),
                    "Debe contener archivo .conf de L1T Hammurabi");
            assertTrue(archivos.keySet().stream().anyMatch(key ->
                            key.equals("qlt/l1t/" + schemaProcessorMock.getDfMasterName() + "_l1t.json")),
                    "Debe contener archivo .json de L1T Hammurabi");

            assertTrue(archivos.keySet().stream().anyMatch(key ->
                            key.equals("kirby/l1t/" + schemaProcessorMock.getDfMasterName() + "_l1t.conf")),
                    "Debe contener archivo .conf de L1T Kirby");
            assertTrue(archivos.keySet().stream().anyMatch(key ->
                            key.equals("kirby/l1t/" + schemaProcessorMock.getDfMasterName() + "_l1t.json")),
                    "Debe contener archivo .json de L1T Kirby");

            return "zip-with-complete-l1t".getBytes();
        });

        doNothing().when(issueTicketServiceMock).addLabelToIssue(anyString(), anyString(), anyString(), anyString());

        byte[] result = service.procesarIngesta(request);

        assertNotNull(result);
        assertEquals("zip-with-complete-l1t", new String(result));
    }

    @Test
    void testGenerarHammurabiL1T_Content() throws Exception {
        IngestaRequestDto request = createValidRequestWithRealCSV();
        setupSchemaProcessorMocks();

        Method method = IngestaService.class.getDeclaredMethod("generarHammurabiL1T", IngestaRequestDto.class);
        method.setAccessible(true);
        String result = (String) method.invoke(service, request);

        assertNotNull(result);
        assertTrue(result.contains("hammurabi {"), "Debe contener bloque hammurabi");
        assertTrue(result.contains("dataFrameInfo {"), "Debe contener dataFrameInfo");
        assertTrue(result.contains("_l1t"), "Debe contener sufijo _l1t en paths");
        assertTrue(result.contains("ConditionalPerimeterCompletenessRule"),
                "Debe contener regla de completitud L1T");
        assertTrue(result.contains(request.getUuaaMaster()), "Debe contener UUAA master");
        assertTrue(result.contains("${?DATE}"), "Debe contener placeholder de fecha");
    }

    @Test
    void testGenerarHammurabiL1TJson_Content() throws Exception {
        IngestaRequestDto request = createValidRequestWithRealCSV();
        setupSchemaProcessorMocks();

        Method method = IngestaService.class.getDeclaredMethod("generarHammurabiL1TJson", IngestaRequestDto.class);
        method.setAccessible(true);
        String result = (String) method.invoke(service, request);

        assertNotNull(result);
        assertTrue(result.contains("\"_id\""), "Debe contener campo _id");
        assertTrue(result.contains("l1tm-01"), "Debe contener sufijo l1tm-01 en ID");
        assertTrue(result.contains("\"description\""), "Debe contener descripción");
        assertTrue(result.contains("Metaknight"), "Debe mencionar Metaknight");
        assertTrue(result.contains("\"configUrl\""), "Debe contener configUrl");
        assertTrue(result.contains("masterdata"), "Debe apuntar a masterdata");
        assertTrue(result.contains("_l1t"), "Debe contener sufijo _l1t en paths");
        assertTrue(result.contains("hammurabi-lts"), "Debe usar runtime hammurabi-lts");
    }

    @Test
    void testGenerarConfiguracionesL1T_DisabledL1T() throws Exception {
        IngestaRequestDto request = createValidRequestWithRealCSV();
        request.setTieneL1T(false);
        setupSchemaProcessorMocks();

        byte[] hammurabiDoc = "hammurabi-doc".getBytes();
        byte[] kirbyDoc = "kirby-doc".getBytes();
        when(documentGeneratorMock.generarDocumentoC204Hammurabi(eq(request), any()))
                .thenReturn(hammurabiDoc);
        when(documentGeneratorMock.generarDocumentoC204Kirby(eq(request), any()))
                .thenReturn(kirbyDoc);

        when(zipGeneratorMock.crearZip(any())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Map<String, byte[]> archivos = invocation.getArgument(0);

            assertFalse(archivos.keySet().stream().anyMatch(key -> key.contains("qlt/l1t")),
                    "NO debe contener archivos L1T de Hammurabi cuando está deshabilitado");
            assertFalse(archivos.keySet().stream().anyMatch(key -> key.contains("kirby/l1t")),
                    "NO debe contener archivos L1T de Kirby cuando está deshabilitado");

            return "zip-without-l1t".getBytes();
        });

        doNothing().when(issueTicketServiceMock).addLabelToIssue(anyString(), anyString(), anyString(), anyString());

        // Act
        byte[] result = service.procesarIngesta(request);

        // Assert
        assertNotNull(result);
        assertEquals("zip-without-l1t", new String(result));
    }
}
