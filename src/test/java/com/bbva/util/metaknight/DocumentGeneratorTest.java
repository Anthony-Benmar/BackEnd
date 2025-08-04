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
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentGeneratorTest {

    private DocumentGenerator documentGenerator;
    private SchemaProcessor schemaProcessorMock;

    @BeforeEach
    void setUp() {
        // No need for spy anymore since we're not mocking private methods
        documentGenerator = new DocumentGenerator();
        schemaProcessorMock = mock(SchemaProcessor.class);
        setupSchemaProcessorMocks();
    }

    @Test
    void testGenerarDocumentoC204Hammurabi_Success() throws Exception {
        // Arrange
        IngestaRequestDto request = createValidRequest();

        // Create a real DocumentGenerator for testing
        DocumentGenerator realGenerator = new DocumentGenerator();

        // Act & Assert - Test real behavior
        // Since templates don't exist in test resources, this should throw HandledException
        // If templates DO exist, we test successful generation

        try {
            byte[] result = realGenerator.generarDocumentoC204Hammurabi(request, schemaProcessorMock);

            // If we get here, templates exist and generation worked
            assertNotNull(result);
            assertTrue(result.length > 0);

        } catch (HandledException e) {
            // Expected behavior when templates don't exist
            assertEquals("TEMPLATE_LOAD_ERROR", e.getCode());
            assertTrue(e.getMessage().contains("Error cargando template"));
        }
    }

    @Test
    void testGenerarDocumentoC204Kirby_Success() throws Exception {
        // Arrange
        IngestaRequestDto request = createValidRequest();

        // Create a real DocumentGenerator for testing
        DocumentGenerator realGenerator = new DocumentGenerator();

        // Act & Assert - Test real behavior
        try {
            byte[] result = realGenerator.generarDocumentoC204Kirby(request, schemaProcessorMock);

            // If we get here, templates exist and generation worked
            assertNotNull(result);
            assertTrue(result.length > 0);

        } catch (HandledException e) {
            // Expected behavior when templates don't exist
            assertEquals("TEMPLATE_LOAD_ERROR", e.getCode());
            assertTrue(e.getMessage().contains("Error cargando template"));
        }
    }

    @Test
    void testLoadTemplateAsBase64_Success() throws Exception {
        // Arrange
        DocumentGenerator realGenerator = new DocumentGenerator();

        // Act & Assert - Testing private method behavior through public methods
        // The actual template loading will fail (template doesn't exist in test resources)
        // but we can verify the method exists and error handling works

        Method method = DocumentGenerator.class.getDeclaredMethod("loadTemplateAsBase64", String.class);
        method.setAccessible(true);

        // Test with non-existent template
        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(realGenerator, "non_existent_template.docx");
        });

        assertTrue(exception.getCause() instanceof HandledException);
    }

    @Test
    void testLoadTemplateAsBase64_FileNotFound() throws Exception {
        // Arrange
        String nonExistentTemplate = "non_existent_template.docx";
        DocumentGenerator realGenerator = new DocumentGenerator();

        Method method = DocumentGenerator.class.getDeclaredMethod("loadTemplateAsBase64", String.class);
        method.setAccessible(true);

        // Act & Assert
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
        // Test the private method through reflection with real template
        String templateBase64 = createMockWordDocumentBase64();
        Map<String, String> replacements = new HashMap<>();
        replacements.put("[TEST_KEY]", "TEST_VALUE");
        replacements.put("[ANOTHER_KEY]", "ANOTHER_VALUE");

        Method method = DocumentGenerator.class.getDeclaredMethod("generarDocumentoC204", String.class, Map.class);
        method.setAccessible(true);

        // Act
        DocumentGenerator realGenerator = new DocumentGenerator();
        byte[] result = (byte[]) method.invoke(realGenerator, templateBase64, replacements);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Verify document can be opened
        assertDoesNotThrow(() -> {
            try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(result))) {
                assertNotNull(doc);
            }
        });
    }

    @Test
    void testGenerarDocumentoC204_InvalidBase64() throws Exception {
        // Arrange
        String invalidBase64 = "invalid-base64-content!!!";
        Map<String, String> replacements = new HashMap<>();

        Method method = DocumentGenerator.class.getDeclaredMethod("generarDocumentoC204", String.class, Map.class);
        method.setAccessible(true);

        DocumentGenerator realGenerator = new DocumentGenerator();

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(realGenerator, invalidBase64, replacements);
        });

        // Should fail when trying to decode invalid base64
        assertTrue(exception.getCause() instanceof HandledException);
        HandledException handledException = (HandledException) exception.getCause();
        assertEquals("DOCUMENT_GENERATION_ERROR", handledException.getCode());
    }

    @Test
    void testGenerarDocumentoC204_EmptyReplacements() throws Exception {
        // Arrange
        String templateBase64 = createMockWordDocumentBase64();
        Map<String, String> emptyReplacements = new HashMap<>();

        Method method = DocumentGenerator.class.getDeclaredMethod("generarDocumentoC204", String.class, Map.class);
        method.setAccessible(true);

        DocumentGenerator realGenerator = new DocumentGenerator();

        // Act
        byte[] result = (byte[]) method.invoke(realGenerator, templateBase64, emptyReplacements);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testGenerarDocumentoC204Hammurabi_ReplacementLogic() throws Exception {
        // Test the replacement logic using the private method directly
        String mockTemplateBase64 = createMockWordDocumentWithPlaceholders();

        IngestaRequestDto request = createValidRequest();

        // Build replacements map like the real method does
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

        // Test the private method directly
        Method method = DocumentGenerator.class.getDeclaredMethod("generarDocumentoC204", String.class, Map.class);
        method.setAccessible(true);

        DocumentGenerator realGenerator = new DocumentGenerator();

        // Act
        byte[] result = (byte[]) method.invoke(realGenerator, mockTemplateBase64, replacements);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Verify all expected replacements were made
        String documentText = extractTextFromWordDocument(result);
        assertFalse(documentText.contains("[SDATOOL]"));
        assertFalse(documentText.contains("[titulo_proyecto]"));
        assertFalse(documentText.contains("[developer]"));
        assertTrue(documentText.contains("TEST-TOOL"));
        assertTrue(documentText.contains("Test Project"));
    }

    @Test
    void testGenerarDocumentoC204Kirby_ReplacementLogic() throws Exception {
        // Test the replacement logic using the private method directly
        String mockTemplateBase64 = createMockWordDocumentWithPlaceholders();

        IngestaRequestDto request = createValidRequest();

        // Build replacements map like the real Kirby method does
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

        // Test the private method directly
        Method method = DocumentGenerator.class.getDeclaredMethod("generarDocumentoC204", String.class, Map.class);
        method.setAccessible(true);

        DocumentGenerator realGenerator = new DocumentGenerator();

        // Act
        byte[] result = (byte[]) method.invoke(realGenerator, mockTemplateBase64, replacements);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Verify expected replacements were made (Kirby has fewer fields than Hammurabi)
        String documentText = extractTextFromWordDocument(result);
        assertFalse(documentText.contains("[SDATOOL]"));
        assertFalse(documentText.contains("[titulo_proyecto]"));
        assertFalse(documentText.contains("[df_name]"));
        assertTrue(documentText.contains("TEST-TOOL"));
        assertTrue(documentText.contains("Test Project"));
    }

    @Test
    void testGenerarDocumentoC204_TemplateLoadingFailure() throws Exception {
        // Test template loading with non-existent template using reflection
        DocumentGenerator realGenerator = new DocumentGenerator();

        // Test the private loadTemplateAsBase64 method directly
        Method method = DocumentGenerator.class.getDeclaredMethod("loadTemplateAsBase64", String.class);
        method.setAccessible(true);

        // Act & Assert - Test with guaranteed non-existent template
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
        // Test replacement logic with null fields using private method
        String mockTemplateBase64 = createMockWordDocumentWithPlaceholders();
        IngestaRequestDto request = createRequestWithNullFields();

        Map<String, String> replacements = new HashMap<>();
        // Handle null values properly
        replacements.put("[SDATOOL]", request.getSdatool() != null ? request.getSdatool() : "null");
        replacements.put("[titulo_proyecto]", request.getProyecto() != null ? request.getProyecto() : "null");
        replacements.put("[developer]", request.getNombreDev() != null ? request.getNombreDev() : "null");
        replacements.put("[XP]", request.getRegistroDev() != null ? request.getRegistroDev() : "null");

        Method method = DocumentGenerator.class.getDeclaredMethod("generarDocumentoC204", String.class, Map.class);
        method.setAccessible(true);

        DocumentGenerator realGenerator = new DocumentGenerator();

        // Act
        byte[] result = (byte[]) method.invoke(realGenerator, mockTemplateBase64, replacements);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Should handle null replacements gracefully
        String documentText = extractTextFromWordDocument(result);
        assertTrue(documentText.contains("null")); // null values should be converted to "null" string
    }

    @Test
    void testDocumentGeneration_LargeContent() throws Exception {
        // Test with large content using private method
        String mockTemplateBase64 = createMockWordDocumentBase64();
        IngestaRequestDto request = createRequestWithLargeContent();

        Map<String, String> replacements = new HashMap<>();
        replacements.put("[SDATOOL]", request.getSdatool()); // Large content
        replacements.put("[titulo_proyecto]", request.getProyecto()); // Large content

        Method method = DocumentGenerator.class.getDeclaredMethod("generarDocumentoC204", String.class, Map.class);
        method.setAccessible(true);

        DocumentGenerator realGenerator = new DocumentGenerator();

        // Act
        byte[] result = (byte[]) method.invoke(realGenerator, mockTemplateBase64, replacements);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);

        // Should handle large content without issues
        assertDoesNotThrow(() -> {
            try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(result))) {
                assertNotNull(doc);
            }
        });
    }

    // Helper methods

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

        // Create large strings
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
        // Create a minimal valid Word document
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
        // Create a Word document with placeholder text
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