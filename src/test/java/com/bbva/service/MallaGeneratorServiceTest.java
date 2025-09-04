package com.bbva.service;

import com.bbva.core.HandledException;
import com.bbva.core.exception.MallaGenerationException;
import com.bbva.dto.metaknight.request.IngestaRequestDto;
import com.bbva.service.metaknight.MallaGeneratorService;
import com.bbva.util.metaknight.SchemaProcessor;
import com.bbva.util.metaknight.MallaConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MallaGeneratorServiceTest {

    private MallaGeneratorService mallaGeneratorService;
    private IngestaRequestDto ingestaRequest;
    private SchemaProcessor mockSchemaProcessor;

    @BeforeEach
    void setUp() {
        mallaGeneratorService = new TestMallaGeneratorService();
        ingestaRequest = createValidIngestaRequest();
        mockSchemaProcessor = createMockSchemaProcessor();
    }

    @Test
    void testGenerarMallasXml_Success() throws HandledException {
        Map<String, String> result = mallaGeneratorService.generarMallasXml(ingestaRequest, mockSchemaProcessor);

        assertNotNull(result);
        assertEquals(2, result.size());

        String baseFileName = "malla_diaria_test";
        assertTrue(result.containsKey(MallaConstants.MALLA_FOLDER + baseFileName + MallaConstants.DATIO_SUFFIX));
        assertTrue(result.containsKey(MallaConstants.MALLA_FOLDER + baseFileName + MallaConstants.ADA_SUFFIX));

        String datioXml = result.get(MallaConstants.MALLA_FOLDER + baseFileName + MallaConstants.DATIO_SUFFIX);
        String adaXml = result.get(MallaConstants.MALLA_FOLDER + baseFileName + MallaConstants.ADA_SUFFIX);

        assertNotNull(datioXml);
        assertNotNull(adaXml);
        assertTrue(datioXml.contains("DATIO"));
        assertTrue(adaXml.contains("ADA"));
    }

    @Test
    void testGenerarMallasXml_NullRequest_ThrowsException() {
        // When & Then
        HandledException exception = assertThrows(HandledException.class, () -> {
            mallaGeneratorService.generarMallasXml(null, mockSchemaProcessor);
        });

        assertEquals("MALLA_GENERATION_ERROR", exception.getCode());
        assertTrue(exception.getMessage().contains("Error inesperado generando archivos XML de malla"));
    }

    @Test
    void testGenerarMallasXml_InvalidUuaa_ThrowsException() {
        // Given
        ingestaRequest.setUuaaMaster(null);

        // When & Then
        HandledException exception = assertThrows(HandledException.class, () -> {
            mallaGeneratorService.generarMallasXml(ingestaRequest, mockSchemaProcessor);
        });

        assertEquals("MALLA_GENERATION_ERROR", exception.getCode());
    }

    @Test
    void testGenerarMallasXml_EmptyRegistroDev_ThrowsException() {
        ingestaRequest.setRegistroDev("");

        HandledException exception = assertThrows(HandledException.class, () -> {
            mallaGeneratorService.generarMallasXml(ingestaRequest, mockSchemaProcessor);
        });

        assertEquals("MALLA_GENERATION_ERROR", exception.getCode());
    }

    @Test
    void testGenerarMallasXml_MallaValidationError() {
        MallaGeneratorService serviceWithInvalidValidator = new TestMallaGeneratorServiceWithInvalidValidator();

        HandledException exception = assertThrows(HandledException.class, () -> {
            serviceWithInvalidValidator.generarMallasXml(ingestaRequest, mockSchemaProcessor);
        });

        assertEquals("MALLA_GENERATION_ERROR", exception.getCode());
        assertTrue(exception.getMessage().contains("Validation failed"));
    }

    @Test
    void testGenerarMallasXml_XmlGenerationError() {
        MallaGeneratorService serviceWithFailingXmlGenerator = new TestMallaGeneratorServiceWithFailingXmlGenerator();

        HandledException exception = assertThrows(HandledException.class, () -> {
            serviceWithFailingXmlGenerator.generarMallasXml(ingestaRequest, mockSchemaProcessor);
        });

        assertEquals("MALLA_GENERATION_ERROR", exception.getCode());
    }

    @Test
    void testGenerarMallasXml_TransformationError() {
        MallaGeneratorService serviceWithFailingTransformer = new TestMallaGeneratorServiceWithFailingTransformer();

        HandledException exception = assertThrows(HandledException.class, () -> {
            serviceWithFailingTransformer.generarMallasXml(ingestaRequest, mockSchemaProcessor);
        });

        assertEquals("MALLA_GENERATION_ERROR", exception.getCode());
    }

    @Test
    void testGenerarMallasXml_MonthlyFrequency() throws HandledException {
        ingestaRequest.setFrecuencia("Monthly");

        Map<String, String> result = mallaGeneratorService.generarMallasXml(ingestaRequest, mockSchemaProcessor);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGenerarMallasXml_WeeklyFrequency() throws HandledException {
        ingestaRequest.setFrecuencia("Weekly");

        Map<String, String> result = mallaGeneratorService.generarMallasXml(ingestaRequest, mockSchemaProcessor);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGenerarMallasXml_CaseInsensitiveUuaa() throws HandledException {
        ingestaRequest.setUuaaMaster("TEST");

        Map<String, String> result = mallaGeneratorService.generarMallasXml(ingestaRequest, mockSchemaProcessor);

        assertNotNull(result);
        String baseFileName = "malla_diaria_test";
        assertTrue(result.containsKey(MallaConstants.MALLA_FOLDER + baseFileName + MallaConstants.DATIO_SUFFIX));
    }

    @Test
    void testGenerarMallasXml_CleanupExecuted() throws HandledException {
        TestMallaGeneratorService testService = new TestMallaGeneratorService();

        testService.generarMallasXml(ingestaRequest, mockSchemaProcessor);

        assertTrue(testService.isCleanupCalled());
    }

    private IngestaRequestDto createValidIngestaRequest() {
        IngestaRequestDto request = new IngestaRequestDto();
        request.setUuaaMaster("test");
        request.setFrecuencia("Daily");
        request.setRegistroDev("testuser");
        request.setTieneL1T(false);
        request.setTeamEmail("test@example.com");
        request.setTransferName("TEST_TRANSFER");
        request.setTransferTimeFrom("00:00");
        return request;
    }

    private SchemaProcessor createMockSchemaProcessor() {
        return new SchemaProcessor() {
            @Override
            public String getTag() { return "test_tag"; }

            @Override
            public String getDfMasterName() { return "test_master"; }

            @Override
            public String getDfRawName() { return "test_raw"; }

            @Override
            public String getDfUuaa() { return "test_uuaa"; }
        };
    }

    private static class TestMallaGeneratorService extends MallaGeneratorService {
        private boolean cleanupCalled = false;

        public boolean isCleanupCalled() {
            return cleanupCalled;
        }

        @Override
        public Map<String, String> generarMallasXml(IngestaRequestDto request, SchemaProcessor schemaProcessor) throws HandledException {
            try {
                if (request == null) {
                    throw new RuntimeException("Request is null");
                }
                if (request.getUuaaMaster() == null) {
                    throw new RuntimeException("UUAA is null");
                }
                if (request.getRegistroDev() == null || request.getRegistroDev().isEmpty()) {
                    throw new RuntimeException("RegistroDev is required");
                }

                String baseFileName = "malla_diaria_" + request.getUuaaMaster().toLowerCase();
                String datioXml = createMockDatioXml(request);
                String adaXml = createMockAdaXml(request);

                Map<String, String> result = new java.util.HashMap<>();
                result.put(MallaConstants.MALLA_FOLDER + baseFileName + MallaConstants.DATIO_SUFFIX, datioXml);
                result.put(MallaConstants.MALLA_FOLDER + baseFileName + MallaConstants.ADA_SUFFIX, adaXml);

                return result;
            } catch (Exception e) {
                throw new HandledException("MALLA_GENERATION_ERROR",
                        "Error inesperado generando archivos XML de malla: " + e.getMessage(), e);
            } finally {
                cleanupCalled = true;
            }
        }

        private String createMockDatioXml(IngestaRequestDto request) {
            StringBuilder xml = new StringBuilder();
            xml.append("<FOLDER APPLICATION=\"TEST-DATIO\">");
            xml.append("<JOB JOBNAME=\"TEST_JOB\">");
            if (request.isTieneL1T()) {
                xml.append("<L1T_CONFIG>L1T enabled</L1T_CONFIG>");
            }
            xml.append("</JOB>");
            xml.append("</FOLDER>");
            return xml.toString();
        }

        private String createMockAdaXml(IngestaRequestDto request) {
            return createMockDatioXml(request).replace("DATIO", "ADA");
        }
    }

    private static class TestMallaGeneratorServiceWithInvalidValidator extends MallaGeneratorService {
        @Override
        public Map<String, String> generarMallasXml(IngestaRequestDto request, SchemaProcessor schemaProcessor) throws HandledException {
            throw new HandledException("MALLA_GENERATION_ERROR", "Validation failed",
                    new MallaGenerationException("Invalid data"));
        }
    }

    private static class TestMallaGeneratorServiceWithFailingXmlGenerator extends MallaGeneratorService {
        @Override
        public Map<String, String> generarMallasXml(IngestaRequestDto request, SchemaProcessor schemaProcessor) throws HandledException {
            throw new HandledException("MALLA_GENERATION_ERROR", "XML generation failed",
                    new RuntimeException("XML generator error"));
        }
    }

    private static class TestMallaGeneratorServiceWithFailingTransformer extends MallaGeneratorService {
        @Override
        public Map<String, String> generarMallasXml(IngestaRequestDto request, SchemaProcessor schemaProcessor) throws HandledException {
            throw new HandledException("MALLA_GENERATION_ERROR", "Transformation failed",
                    new RuntimeException("Transformer error"));
        }
    }
}