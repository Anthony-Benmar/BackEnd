package com.bbva.util.metaknight;

import com.bbva.core.HandledException;
import com.bbva.dto.metaknight.request.IngestaRequestDto;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentGeneratorTest {

    private SchemaProcessor schemaProcessorMock;

    @BeforeEach
    void setUp() {
        schemaProcessorMock = mock(SchemaProcessor.class);
        setupSchemaProcessorMocks();
    }

    @Test
    void testGenerarDocumentoC204Hammurabi_Success() throws Exception {
        IngestaRequestDto request = createValidRequest();

        DocumentGenerator realGenerator = new DocumentGenerator();

        try {
            byte[] result = realGenerator.generarDocumentoC204Hammurabi(request, schemaProcessorMock);

            assertNotNull(result);
            assertTrue(result.length > 0);

        } catch (HandledException e) {
            assertEquals("TEMPLATE_LOAD_ERROR", e.getCode());
            assertTrue(e.getMessage().contains("Error cargando template"));
        }
    }

    @Test
    void testGenerarDocumentoC204Kirby_Success() throws Exception {
        IngestaRequestDto request = createValidRequest();

        DocumentGenerator realGenerator = new DocumentGenerator();

        try {
            byte[] result = realGenerator.generarDocumentoC204Kirby(request, schemaProcessorMock);

            assertNotNull(result);
            assertTrue(result.length > 0);

        } catch (HandledException e) {
            assertEquals("TEMPLATE_LOAD_ERROR", e.getCode());
            assertTrue(e.getMessage().contains("Error cargando template"));
        }
    }

    @Test
    void testLoadTemplateAsBase64_Success() throws Exception {
        DocumentGenerator realGenerator = new DocumentGenerator();

        Method method = DocumentGenerator.class.getDeclaredMethod("loadTemplateAsBase64", String.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(realGenerator, "non_existent_template.docx");
        });

        assertTrue(exception.getCause() instanceof HandledException);
    }

    @Test
    void testLoadTemplateAsBase64_FileNotFound() throws Exception {
        String nonExistentTemplate = "non_existent_template.docx";
        DocumentGenerator realGenerator = new DocumentGenerator();

        Method method = DocumentGenerator.class.getDeclaredMethod("loadTemplateAsBase64", String.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(realGenerator, nonExistentTemplate);
        });

        assertTrue(exception.getCause() instanceof HandledException);
        HandledException handledException = (HandledException) exception.getCause();
        assertEquals("TEMPLATE_LOAD_ERROR", handledException.getCode());
        assertTrue(handledException.getMessage().contains("Error cargando template"));
    }

    @Test
    void testGenerarDocumentoC204_WithReplacements() throws Exception {
        String templateBase64 = createMockWordDocumentBase64();
        Map<String, String> replacements = new HashMap<>();
        replacements.put("[TEST_KEY]", "TEST_VALUE");
        replacements.put("[ANOTHER_KEY]", "ANOTHER_VALUE");

        Method method = DocumentGenerator.class.getDeclaredMethod("generarDocumentoC204", String.class, Map.class);
        method.setAccessible(true);

        DocumentGenerator realGenerator = new DocumentGenerator();
        byte[] result = (byte[]) method.invoke(realGenerator, templateBase64, replacements);

        assertNotNull(result);
        assertTrue(result.length > 0);

        assertDoesNotThrow(() -> {
            try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(result))) {
                assertNotNull(doc);
            }
        });
    }

    @Test
    void testGenerarDocumentoC204_InvalidBase64() throws Exception {
        String invalidBase64 = "invalid-base64-content!!!";
        Map<String, String> replacements = new HashMap<>();

        Method method = DocumentGenerator.class.getDeclaredMethod("generarDocumentoC204", String.class, Map.class);
        method.setAccessible(true);

        DocumentGenerator realGenerator = new DocumentGenerator();

        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(realGenerator, invalidBase64, replacements);
        });

        assertTrue(exception.getCause() instanceof HandledException);
        HandledException handledException = (HandledException) exception.getCause();
        assertEquals("DOCUMENT_GENERATION_ERROR", handledException.getCode());
    }

    @Test
    void testGenerarDocumentoC204_EmptyReplacements() throws Exception {
        String templateBase64 = createMockWordDocumentBase64();
        Map<String, String> emptyReplacements = new HashMap<>();

        Method method = DocumentGenerator.class.getDeclaredMethod("generarDocumentoC204", String.class, Map.class);
        method.setAccessible(true);

        DocumentGenerator realGenerator = new DocumentGenerator();

        byte[] result = (byte[]) method.invoke(realGenerator, templateBase64, emptyReplacements);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testGenerarDocumentoC204Hammurabi_ReplacementLogic() throws Exception {
        String mockTemplateBase64 = createMockWordDocumentWithPlaceholders();

        IngestaRequestDto request = createValidRequest();

        Map<String, String> replacements = new HashMap<>();
        replacements.put("[SDATOOL]", request.getSdatool());
        replacements.put("[titulo_proyecto]", request.getProyecto());
        replacements.put("[developer]", request.getNombreDev());
        replacements.put("[XP]", request.getRegistroDev());
        replacements.put("[df_name]", schemaProcessorMock.getDfMasterName());
        replacements.put("[SM]", request.getSm());
        replacements.put("[PO]", request.getPo());
        replacements.put("[job_staging_id]", schemaProcessorMock.getIdJsonStaging());
        replacements.put("[job_raw_id]", schemaProcessorMock.getIdJsonRaw());
        replacements.put("[job_master_id]", schemaProcessorMock.getIdJsonMaster());

        Method method = DocumentGenerator.class.getDeclaredMethod("generarDocumentoC204", String.class, Map.class);
        method.setAccessible(true);

        DocumentGenerator realGenerator = new DocumentGenerator();

        byte[] result = (byte[]) method.invoke(realGenerator, mockTemplateBase64, replacements);

        assertNotNull(result);
        assertTrue(result.length > 0);

        String documentText = extractTextFromWordDocument(result);
        assertFalse(documentText.contains("[SDATOOL]"));
        assertFalse(documentText.contains("[titulo_proyecto]"));
        assertFalse(documentText.contains("[developer]"));
        assertTrue(documentText.contains("TEST-TOOL"));
        assertTrue(documentText.contains("Test Project"));
    }

    @Test
    void testGenerarDocumentoC204Kirby_ReplacementLogic() throws Exception {
        String mockTemplateBase64 = createMockWordDocumentWithPlaceholders();

        IngestaRequestDto request = createValidRequest();

        Map<String, String> replacements = new HashMap<>();
        replacements.put("[SDATOOL]", request.getSdatool());
        replacements.put("[titulo_proyecto]", request.getProyecto());
        replacements.put("[developer]", request.getNombreDev());
        replacements.put("[XP]", request.getRegistroDev());
        replacements.put("[df_name]", schemaProcessorMock.getDfMasterName());
        replacements.put("[SM]", request.getSm());
        replacements.put("[PO]", request.getPo());
        replacements.put("[job_raw_id]", schemaProcessorMock.getIdJsonRaw());
        replacements.put("[job_master_id]", schemaProcessorMock.getIdJsonMaster());

        Method method = DocumentGenerator.class.getDeclaredMethod("generarDocumentoC204", String.class, Map.class);
        method.setAccessible(true);

        DocumentGenerator realGenerator = new DocumentGenerator();

        byte[] result = (byte[]) method.invoke(realGenerator, mockTemplateBase64, replacements);

        assertNotNull(result);
        assertTrue(result.length > 0);

        String documentText = extractTextFromWordDocument(result);
        assertFalse(documentText.contains("[SDATOOL]"));
        assertFalse(documentText.contains("[titulo_proyecto]"));
        assertFalse(documentText.contains("[df_name]"));
        assertTrue(documentText.contains("TEST-TOOL"));
        assertTrue(documentText.contains("Test Project"));
    }

    @Test
    void testGenerarDocumentoC204_TemplateLoadingFailure() throws Exception {
        DocumentGenerator realGenerator = new DocumentGenerator();

        Method method = DocumentGenerator.class.getDeclaredMethod("loadTemplateAsBase64", String.class);
        method.setAccessible(true);

        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(realGenerator, "definitely_does_not_exist.docx");
        });

        assertTrue(exception.getCause() instanceof HandledException);
        HandledException handledException = (HandledException) exception.getCause();
        assertEquals("TEMPLATE_LOAD_ERROR", handledException.getCode());
        assertTrue(handledException.getMessage().contains("Error cargando template"));
    }

    @Test
    void testDocumentGeneration_WithNullFields() throws Exception {
        String mockTemplateBase64 = createMockWordDocumentWithPlaceholders();
        IngestaRequestDto request = createRequestWithNullFields();

        Map<String, String> replacements = new HashMap<>();
        replacements.put("[SDATOOL]", request.getSdatool() != null ? request.getSdatool() : "null");
        replacements.put("[titulo_proyecto]", request.getProyecto() != null ? request.getProyecto() : "null");
        replacements.put("[developer]", request.getNombreDev() != null ? request.getNombreDev() : "null");
        replacements.put("[XP]", request.getRegistroDev() != null ? request.getRegistroDev() : "null");

        Method method = DocumentGenerator.class.getDeclaredMethod("generarDocumentoC204", String.class, Map.class);
        method.setAccessible(true);

        DocumentGenerator realGenerator = new DocumentGenerator();

        byte[] result = (byte[]) method.invoke(realGenerator, mockTemplateBase64, replacements);

        assertNotNull(result);
        assertTrue(result.length > 0);

        String documentText = extractTextFromWordDocument(result);
        assertTrue(documentText.contains("null"));
    }

    @Test
    void testDocumentGeneration_LargeContent() throws Exception {
        String mockTemplateBase64 = createMockWordDocumentBase64();
        IngestaRequestDto request = createRequestWithLargeContent();

        Map<String, String> replacements = new HashMap<>();
        replacements.put("[SDATOOL]", request.getSdatool());
        replacements.put("[titulo_proyecto]", request.getProyecto());

        Method method = DocumentGenerator.class.getDeclaredMethod("generarDocumentoC204", String.class, Map.class);
        method.setAccessible(true);

        DocumentGenerator realGenerator = new DocumentGenerator();

        byte[] result = (byte[]) method.invoke(realGenerator, mockTemplateBase64, replacements);

        assertNotNull(result);
        assertTrue(result.length > 0);

        assertDoesNotThrow(() -> {
            try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(result))) {
                assertNotNull(doc);
            }
        });
    }

    private IngestaRequestDto createValidRequest() {
        IngestaRequestDto request = new IngestaRequestDto();
        request.setSdatool("TEST-TOOL");
        request.setProyecto("Test Project");
        request.setNombreDev("Test Developer");
        request.setRegistroDev("DEV123");
        request.setSm("Test SM");
        request.setPo("Test PO");
        return request;
    }

    private IngestaRequestDto createRequestWithNullFields() {
        IngestaRequestDto request = new IngestaRequestDto();
        request.setSdatool(null);
        request.setProyecto(null);
        request.setNombreDev("Test Developer");
        request.setRegistroDev("DEV123");
        request.setSm(null);
        request.setPo("Test PO");
        return request;
    }

    private IngestaRequestDto createRequestWithLargeContent() {
        IngestaRequestDto request = new IngestaRequestDto();

        StringBuilder largeString = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeString.append("Large content line ").append(i).append(". ");
        }

        request.setSdatool(largeString.toString());
        request.setProyecto("Large Project: " + largeString.toString());
        request.setNombreDev("Test Developer");
        request.setRegistroDev("DEV123");
        request.setSm("Test SM");
        request.setPo("Test PO");
        return request;
    }

    private void setupSchemaProcessorMocks() {
        when(schemaProcessorMock.getDfMasterName()).thenReturn("test_master_table");
        when(schemaProcessorMock.getIdJsonStaging()).thenReturn("test-pe-hmm-qlt-001s-01");
        when(schemaProcessorMock.getIdJsonRaw()).thenReturn("test-pe-hmm-qlt-001r-01");
        when(schemaProcessorMock.getIdJsonMaster()).thenReturn("test-pe-hmm-qlt-001m-01");
    }

    private String createMockWordDocumentBase64() throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("Mock template content");

            try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                document.write(baos);
                byte[] docBytes = baos.toByteArray();
                return Base64.getEncoder().encodeToString(docBytes);
            }
        }
    }

    private String createMockWordDocumentWithPlaceholders() throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("SDATOOL: [SDATOOL], Project: [titulo_proyecto], Dev: [developer], " +
                    "XP: [XP], Table: [df_name], SM: [SM], PO: [PO]");

            try (java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                document.write(baos);
                byte[] docBytes = baos.toByteArray();
                return Base64.getEncoder().encodeToString(docBytes);
            }
        }
    }

    private String extractTextFromWordDocument(byte[] docBytes) throws Exception {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(docBytes))) {
            StringBuilder text = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                text.append(paragraph.getText()).append(" ");
            }
            return text.toString();
        }
    }
}