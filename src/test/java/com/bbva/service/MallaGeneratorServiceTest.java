package com.bbva.service;

import com.bbva.core.HandledException;
import com.bbva.core.exception.MallaGenerationException;
import com.bbva.dto.metaknight.request.IngestaRequestDto;
import com.bbva.dto.metaknight.request.MallaRequestDto;
import com.bbva.service.metaknight.ControlMAnalyzer;
import com.bbva.service.metaknight.MallaGeneratorService;
import com.bbva.service.metaknight.MallaTransformerService;
import com.bbva.service.metaknight.OptimizedGitRepositoryService;
import com.bbva.util.metaknight.SchemaProcessor;
import com.bbva.util.metaknight.XmlMallaGenerator;
import com.bbva.util.metaknight.validation.MallaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MallaGeneratorServiceTest {

    private MallaGeneratorService mallaGeneratorService;

    @Mock
    private XmlMallaGenerator xmlGenerator;

    @Mock
    private MallaTransformerService transformerService;

    @Mock
    private MallaValidator mallaValidator;

    @Mock
    private OptimizedGitRepositoryService gitRepositoryService;

    @Mock
    private SchemaProcessor schemaProcessor;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mallaGeneratorService = new MallaGeneratorService();

        injectMockDependency("xmlGenerator", xmlGenerator);
        injectMockDependency("transformerService", transformerService);
        injectMockDependency("mallaValidator", mallaValidator);
        injectMockDependency("gitRepositoryService", gitRepositoryService);
    }

    @Test
    @DisplayName("generarMallasXml - Con L1T habilitado")
    void testGenerarMallasXml_WithL1TEnabled() throws Exception {
        IngestaRequestDto request = createValidRequest();
        request.setTieneL1T(true);

        setupSchemaProcessorMocks();

        when(xmlGenerator.generarFlujoCompletoXml(any(MallaRequestDto.class)))
                .thenReturn("<DATIO>test datio content</DATIO>");
        when(transformerService.transformarDatioToAda(anyString(), any(MallaRequestDto.class)))
                .thenReturn("<ADA>test ada content</ADA>");

        try (MockedConstruction<ControlMAnalyzer> mockedControlM = mockConstruction(
                ControlMAnalyzer.class,
                (mock, context) -> {
                    setupControlMAnalyzerMock(mock);
                })) {

            Map<String, String> result = mallaGeneratorService.generarMallasXml(request, schemaProcessor);

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(mallaValidator).validarDatosIngesta(request);
            verify(xmlGenerator).generarFlujoCompletoXml(any(MallaRequestDto.class));
            verify(transformerService).transformarDatioToAda(anyString(), any(MallaRequestDto.class));
            verify(gitRepositoryService).cleanupCache();
        }
    }

    @Test
    @DisplayName("generarMallasXml - Error en validación inicial")
    void testGenerarMallasXml_ValidationError() throws Exception {
        IngestaRequestDto request = createValidRequest();
        MallaGenerationException validationException =
                MallaGenerationException.validationError("Validation failed");

        doThrow(validationException).when(mallaValidator).validarDatosIngesta(request);

        HandledException exception = assertThrows(HandledException.class,
                () -> mallaGeneratorService.generarMallasXml(request, schemaProcessor));

        assertEquals("MALLA_GENERATION_ERROR", exception.getCode());
        assertTrue(exception.getMessage().contains("MALLA_VALIDATION_ERROR"));
        verify(gitRepositoryService).cleanupCache();
    }

    @Test
    @DisplayName("construirDatosMallaConDatosReales - Error en construcción")
    void testConstruirDatosMallaConDatosReales_Error() throws Exception {
        IngestaRequestDto request = createValidRequest();
        request.setUuaaMaster(null);

        Method method = MallaGeneratorService.class.getDeclaredMethod(
                "construirDatosMallaConDatosReales", IngestaRequestDto.class, SchemaProcessor.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(mallaGeneratorService, request, schemaProcessor));

        assertTrue(exception.getCause() instanceof MallaGenerationException);
        MallaGenerationException mallaException = (MallaGenerationException) exception.getCause();
        assertEquals("MALLA_CONFIGURATION_ERROR", mallaException.getErrorCode());
    }

    @Test
    @DisplayName("generarJobIdsConTagReal - Genera IDs correctamente")
    void testGenerarJobIdsConTagReal() throws Exception {
        MallaRequestDto mallaData = new MallaRequestDto();
        String uuaaLower = "test";
        String tag = "testtag";

        Method method = MallaGeneratorService.class.getDeclaredMethod(
                "generarJobIdsConTagReal", MallaRequestDto.class, String.class, String.class);
        method.setAccessible(true);

        method.invoke(mallaGeneratorService, mallaData, uuaaLower, tag);

        assertNotNull(mallaData.getHmmStgJobid());
        assertNotNull(mallaData.getKrbRawJobid());
        assertNotNull(mallaData.getHmmRawJobid());
        assertNotNull(mallaData.getKrbMasterJobid());
        assertNotNull(mallaData.getHmmMasterJobid());
        assertNotNull(mallaData.getKrbL1tJobid());
        assertNotNull(mallaData.getHmmL1tJobid());

        assertTrue(mallaData.getHmmStgJobid().contains(uuaaLower));
        assertTrue(mallaData.getHmmStgJobid().contains(tag));
    }

    @Test
    @DisplayName("generarDatosConControlMAnalyzer - Sin L1T")
    void testGenerarDatosConControlMAnalyzer_WithoutL1T() throws Exception {
        MallaRequestDto mallaData = new MallaRequestDto();
        mallaData.setUuaa("TEST");
        mallaData.setUuaaLowercase("test");

        IngestaRequestDto request = createValidRequest();
        request.setTieneL1T(false);
        setupSchemaProcessorMocks();

        try (MockedConstruction<ControlMAnalyzer> mockedControlM = mockConstruction(
                ControlMAnalyzer.class,
                (mock, context) -> setupControlMAnalyzerMock(mock))) {

            Method method = MallaGeneratorService.class.getDeclaredMethod(
                    "generarDatosConControlMAnalyzer", MallaRequestDto.class, IngestaRequestDto.class, SchemaProcessor.class);
            method.setAccessible(true);

            method.invoke(mallaGeneratorService, mallaData, request, schemaProcessor);

            assertEquals("test@example.com", mallaData.getTeamEmail());
            assertNull(mallaData.getKrbL1tJobname());
            assertNull(mallaData.getHmmL1tJobname());
            assertNotNull(mallaData.getL1tSourceName());
            assertEquals("test_master_l1t", mallaData.getL1tSourceName());
        }
    }

    @Test
    @DisplayName("generarDatosConControlMAnalyzer - Con L1T")
    void testGenerarDatosConControlMAnalyzer_WithL1T() throws Exception {
        MallaRequestDto mallaData = new MallaRequestDto();
        mallaData.setUuaa("TEST");
        mallaData.setUuaaLowercase("test");

        IngestaRequestDto request = createValidRequest();
        request.setTieneL1T(true);
        setupSchemaProcessorMocks();

        try (MockedConstruction<ControlMAnalyzer> mockedControlM = mockConstruction(
                ControlMAnalyzer.class,
                (mock, context) -> setupControlMAnalyzerMock(mock))) {

            Method method = MallaGeneratorService.class.getDeclaredMethod(
                    "generarDatosConControlMAnalyzer", MallaRequestDto.class, IngestaRequestDto.class, SchemaProcessor.class);
            method.setAccessible(true);

            method.invoke(mallaGeneratorService, mallaData, request, schemaProcessor);

            assertEquals("test@example.com", mallaData.getTeamEmail());
            assertNotNull(mallaData.getKrbL1tJobname());
            assertNotNull(mallaData.getHmmL1tJobname());
            assertNotNull(mallaData.getL1tSourceName());
            assertEquals("test_master_l1t", mallaData.getL1tSourceName());
        }
    }

    @Test
    @DisplayName("generarDatosConControlMAnalyzer - SchemaProcessor nulo")
    void testGenerarDatosConControlMAnalyzer_NullSchemaProcessor() throws Exception {
        MallaRequestDto mallaData = new MallaRequestDto();
        mallaData.setUuaa("TEST");
        mallaData.setUuaaLowercase("test");

        IngestaRequestDto request = createValidRequest();

        try (MockedConstruction<ControlMAnalyzer> mockedControlM = mockConstruction(
                ControlMAnalyzer.class,
                (mock, context) -> setupControlMAnalyzerMock(mock))) {

            Method method = MallaGeneratorService.class.getDeclaredMethod(
                    "generarDatosConControlMAnalyzer", MallaRequestDto.class, IngestaRequestDto.class, SchemaProcessor.class);
            method.setAccessible(true);

            method.invoke(mallaGeneratorService, mallaData, request, null);

            assertEquals("test@example.com", mallaData.getTeamEmail());
        }
    }

    @Test
    @DisplayName("Cleanup ejecutado en caso de excepción durante cleanup")
    void testCleanupCacheException() throws Exception {
        IngestaRequestDto request = createValidRequest();
        setupSchemaProcessorMocks();

        when(xmlGenerator.generarFlujoCompletoXml(any(MallaRequestDto.class)))
                .thenReturn("<DATIO>content</DATIO>");
        when(transformerService.transformarDatioToAda(anyString(), any(MallaRequestDto.class)))
                .thenReturn("<ADA>content</ADA>");

        doThrow(new RuntimeException("Cleanup failed")).when(gitRepositoryService).cleanupCache();

        try (MockedConstruction<ControlMAnalyzer> mockedControlM = mockConstruction(
                ControlMAnalyzer.class,
                (mock, context) -> setupControlMAnalyzerMock(mock))) {

            Map<String, String> result = assertDoesNotThrow(() ->
                    mallaGeneratorService.generarMallasXml(request, schemaProcessor));

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(gitRepositoryService).cleanupCache();
        }
    }

    @Test
    @DisplayName("Validación de XML - DATIO y ADA")
    void testXmlValidation() throws Exception {
        IngestaRequestDto request = createValidRequest();
        setupSchemaProcessorMocks();

        String datioXml = "<DATIO>content</DATIO>";
        String adaXml = "<ADA>content</ADA>";

        when(xmlGenerator.generarFlujoCompletoXml(any(MallaRequestDto.class))).thenReturn(datioXml);
        when(transformerService.transformarDatioToAda(anyString(), any(MallaRequestDto.class))).thenReturn(adaXml);

        try (MockedConstruction<ControlMAnalyzer> mockedControlM = mockConstruction(
                ControlMAnalyzer.class,
                (mock, context) -> setupControlMAnalyzerMock(mock))) {

            mallaGeneratorService.generarMallasXml(request, schemaProcessor);

            verify(mallaValidator).validarXmlGenerado(datioXml, "DATIO");
            verify(mallaValidator).validarXmlGenerado(adaXml, "ADA");
        }
    }

    private IngestaRequestDto createValidRequest() {
        IngestaRequestDto request = new IngestaRequestDto();
        request.setUuaaMaster("test");
        request.setRegistroDev("testuser");
        request.setFrecuencia("Daily");
        request.setTeamEmail("test@example.com");
        request.setTransferName("TEST_TRANSFER");
        request.setTransferTimeFrom("0000");
        request.setTieneL1T(false);
        request.setNombreDev("Test Developer");
        request.setProyecto("Test Project");
        request.setGenerarMallas(true);
        return request;
    }

    private void setupSchemaProcessorMocks() {
        when(schemaProcessor.getDfMasterName()).thenReturn("test_master");
        when(schemaProcessor.getDfRawName()).thenReturn("test_raw");
        when(schemaProcessor.getDfUuaa()).thenReturn("test_uuaa");
        when(schemaProcessor.getTag()).thenReturn("test_tag");
    }

    private void setupControlMAnalyzerMock(ControlMAnalyzer mock) {
        when(mock.getNamespace()).thenReturn("test-namespace");
        when(mock.getParentFolder()).thenReturn("test-folder");
        when(mock.getTransfer()).thenReturn("TESTTP0001");
        when(mock.getCopy()).thenReturn("TESTDP0001");
        when(mock.getFw()).thenReturn("TESTWP0001");
        when(mock.getHs()).thenReturn("TESTVP0001");
        when(mock.getKbr()).thenReturn("TESTCP0001");
        when(mock.getHr()).thenReturn("TESTVP0002");
        when(mock.getKbm()).thenReturn("TESTCP0002");
        when(mock.getHm()).thenReturn("TESTVP0003");
        when(mock.getD1()).thenReturn("TESTDP0002");
        when(mock.getD2()).thenReturn("TESTDP0003");
        when(mock.getKrbL1t()).thenReturn("TESTCP0003");
        when(mock.getHmmL1t()).thenReturn("TESTVP0004");
    }

    private void injectMockDependency(String fieldName, Object mockObject) throws Exception {
        Field field = MallaGeneratorService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(mallaGeneratorService, mockObject);
    }
}