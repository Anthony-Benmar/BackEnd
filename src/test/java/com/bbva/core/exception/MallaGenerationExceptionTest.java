package com.bbva.core.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class MallaGenerationExceptionTest {

    @Test
    @DisplayName("Constructor con mensaje - Crea excepción con código por defecto")
    void testConstructorWithMessage() {
        // Given
        String message = "Test error message";

        // When
        MallaGenerationException exception = new MallaGenerationException(message);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals("MALLA_ERROR", exception.getErrorCode());
        assertNull(exception.getDetails());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Constructor con mensaje y causa")
    void testConstructorWithMessageAndCause() {
        // Given
        String message = "Test error message";
        RuntimeException cause = new RuntimeException("Cause exception");

        // When
        MallaGenerationException exception = new MallaGenerationException(message, cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals("MALLA_ERROR", exception.getErrorCode());
        assertNull(exception.getDetails());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Constructor completo sin causa")
    void testConstructorWithErrorCodeMessageDetails() {
        // Given
        String errorCode = "CUSTOM_ERROR";
        String message = "Custom error message";
        String details = "Error details";

        // When
        MallaGenerationException exception = new MallaGenerationException(errorCode, message, details);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(details, exception.getDetails());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Constructor completo con causa")
    void testConstructorWithErrorCodeMessageDetailsAndCause() {
        // Given
        String errorCode = "CUSTOM_ERROR";
        String message = "Custom error message";
        String details = "Error details";
        RuntimeException cause = new RuntimeException("Cause exception");

        // When
        MallaGenerationException exception = new MallaGenerationException(errorCode, message, details, cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(details, exception.getDetails());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("getFullMessage - Con detalles")
    void testGetFullMessageWithDetails() {
        // Given
        String errorCode = "TEST_ERROR";
        String message = "Test message";
        String details = "Test details";
        MallaGenerationException exception = new MallaGenerationException(errorCode, message, details);

        // When
        String fullMessage = exception.getFullMessage();

        // Then
        assertEquals("[TEST_ERROR] Test message - Detalles: Test details", fullMessage);
    }

    @Test
    @DisplayName("getFullMessage - Sin detalles")
    void testGetFullMessageWithoutDetails() {
        // Given
        String errorCode = "TEST_ERROR";
        String message = "Test message";
        MallaGenerationException exception = new MallaGenerationException(errorCode, message, null);

        // When
        String fullMessage = exception.getFullMessage();

        // Then
        assertEquals("[TEST_ERROR] Test message", fullMessage);
    }

    @Test
    @DisplayName("getFullMessage - Con detalles vacíos")
    void testGetFullMessageWithEmptyDetails() {
        // Given
        String errorCode = "TEST_ERROR";
        String message = "Test message";
        String details = "   ";
        MallaGenerationException exception = new MallaGenerationException(errorCode, message, details);

        // When
        String fullMessage = exception.getFullMessage();

        // Then
        assertEquals("[TEST_ERROR] Test message", fullMessage);
    }

    @Test
    @DisplayName("toString - Formato correcto")
    void testToString() {
        // Given
        String errorCode = "TEST_ERROR";
        String message = "Test message";
        String details = "Test details";
        MallaGenerationException exception = new MallaGenerationException(errorCode, message, details);

        // When
        String result = exception.toString();

        // Then
        String expected = "MallaGenerationException{errorCode='TEST_ERROR', message='Test message', details='Test details'}";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("toString - Con detalles nulos")
    void testToStringWithNullDetails() {
        // Given
        String errorCode = "TEST_ERROR";
        String message = "Test message";
        MallaGenerationException exception = new MallaGenerationException(errorCode, message, null);

        // When
        String result = exception.toString();

        // Then
        String expected = "MallaGenerationException{errorCode='TEST_ERROR', message='Test message', details='null'}";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("xmlGenerationError - Método factory")
    void testXmlGenerationError() {
        // Given
        String details = "XML generation failed";
        RuntimeException cause = new RuntimeException("XML error");

        // When
        MallaGenerationException exception = MallaGenerationException.xmlGenerationError(details, cause);

        // Then
        assertEquals("Error generando XML de malla", exception.getMessage());
        assertEquals("MALLA_XML_GENERATION_ERROR", exception.getErrorCode());
        assertEquals(details, exception.getDetails());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("transformationError - Método factory")
    void testTransformationError() {
        // Given
        String details = "Transformation failed";
        RuntimeException cause = new RuntimeException("Transform error");

        // When
        MallaGenerationException exception = MallaGenerationException.transformationError(details, cause);

        // Then
        assertEquals("Error transformando malla de DATIO a ADA", exception.getMessage());
        assertEquals("MALLA_TRANSFORMATION_ERROR", exception.getErrorCode());
        assertEquals(details, exception.getDetails());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("configurationError - Método factory")
    void testConfigurationError() {
        // Given
        String details = "Configuration is invalid";

        // When
        MallaGenerationException exception = MallaGenerationException.configurationError(details);

        // Then
        assertEquals("Error en configuración de malla", exception.getMessage());
        assertEquals("MALLA_CONFIGURATION_ERROR", exception.getErrorCode());
        assertEquals(details, exception.getDetails());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("validationError - Método factory")
    void testValidationError() {
        // Given
        String details = "Validation failed";

        // When
        MallaGenerationException exception = MallaGenerationException.validationError(details);

        // Then
        assertEquals("Error de validación en datos de malla", exception.getMessage());
        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertEquals(details, exception.getDetails());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("templateError - Método factory")
    void testTemplateError() {
        // Given
        String details = "Template processing failed";
        RuntimeException cause = new RuntimeException("Template error");

        // When
        MallaGenerationException exception = MallaGenerationException.templateError(details, cause);

        // Then
        assertEquals("Error procesando plantilla de malla", exception.getMessage());
        assertEquals("MALLA_TEMPLATE_ERROR", exception.getErrorCode());
        assertEquals(details, exception.getDetails());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Serial version UID - Verificar que está definido")
    void testSerialVersionUID() throws NoSuchFieldException {
        // When
        java.lang.reflect.Field field = MallaGenerationException.class.getDeclaredField("serialVersionUID");

        // Then
        assertNotNull(field);
        assertEquals(long.class, field.getType());
        assertTrue(java.lang.reflect.Modifier.isStatic(field.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isFinal(field.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPrivate(field.getModifiers()));
    }

    @Test
    @DisplayName("Factory methods - Con detalles nulos")
    void testFactoryMethodsWithNullDetails() {
        // When
        MallaGenerationException configException = MallaGenerationException.configurationError(null);
        MallaGenerationException validationException = MallaGenerationException.validationError(null);

        // Then
        assertNull(configException.getDetails());
        assertNull(validationException.getDetails());
        assertEquals("MALLA_CONFIGURATION_ERROR", configException.getErrorCode());
        assertEquals("MALLA_VALIDATION_ERROR", validationException.getErrorCode());
    }

    @Test
    @DisplayName("Factory methods - Con causa nula")
    void testFactoryMethodsWithNullCause() {
        // When
        MallaGenerationException xmlException = MallaGenerationException.xmlGenerationError("details", null);
        MallaGenerationException transformException = MallaGenerationException.transformationError("details", null);
        MallaGenerationException templateException = MallaGenerationException.templateError("details", null);

        // Then
        assertNull(xmlException.getCause());
        assertNull(transformException.getCause());
        assertNull(templateException.getCause());
        assertEquals("MALLA_XML_GENERATION_ERROR", xmlException.getErrorCode());
        assertEquals("MALLA_TRANSFORMATION_ERROR", transformException.getErrorCode());
        assertEquals("MALLA_TEMPLATE_ERROR", templateException.getErrorCode());
    }

    @Test
    @DisplayName("Herencia - Extiende Exception")
    void testInheritance() {
        // Given
        MallaGenerationException exception = new MallaGenerationException("test");

        // Then
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    @DisplayName("Getter methods - Verificar anotaciones Lombok")
    void testGetterMethods() {
        // Given
        String errorCode = "TEST_CODE";
        String message = "Test message";
        String details = "Test details";
        MallaGenerationException exception = new MallaGenerationException(errorCode, message, details);

        // When & Then
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(details, exception.getDetails());

        // Verify methods exist and are public
        assertDoesNotThrow(() -> exception.getClass().getMethod("getErrorCode"));
        assertDoesNotThrow(() -> exception.getClass().getMethod("getDetails"));
    }
}