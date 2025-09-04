package com.bbva.core.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class MallaGenerationExceptionTest {

    @Test
    @DisplayName("Constructor con mensaje - Crea excepción con código por defecto")
    void testConstructorWithMessage() {
        String message = "Test error message";

        MallaGenerationException exception = new MallaGenerationException(message);

        assertEquals(message, exception.getMessage());
        assertEquals("MALLA_ERROR", exception.getErrorCode());
        assertNull(exception.getDetails());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Constructor con mensaje y causa")
    void testConstructorWithMessageAndCause() {
        String message = "Test error message";
        RuntimeException cause = new RuntimeException("Cause exception");

        MallaGenerationException exception = new MallaGenerationException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals("MALLA_ERROR", exception.getErrorCode());
        assertNull(exception.getDetails());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Constructor completo sin causa")
    void testConstructorWithErrorCodeMessageDetails() {
        String errorCode = "CUSTOM_ERROR";
        String message = "Custom error message";
        String details = "Error details";

        MallaGenerationException exception = new MallaGenerationException(errorCode, message, details);

        assertEquals(message, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(details, exception.getDetails());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Constructor completo con causa")
    void testConstructorWithErrorCodeMessageDetailsAndCause() {
        String errorCode = "CUSTOM_ERROR";
        String message = "Custom error message";
        String details = "Error details";
        RuntimeException cause = new RuntimeException("Cause exception");

        MallaGenerationException exception = new MallaGenerationException(errorCode, message, details, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(details, exception.getDetails());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("getFullMessage - Con detalles")
    void testGetFullMessageWithDetails() {
        String errorCode = "TEST_ERROR";
        String message = "Test message";
        String details = "Test details";
        MallaGenerationException exception = new MallaGenerationException(errorCode, message, details);

        String fullMessage = exception.getFullMessage();

        assertEquals("[TEST_ERROR] Test message - Detalles: Test details", fullMessage);
    }

    @Test
    @DisplayName("getFullMessage - Sin detalles")
    void testGetFullMessageWithoutDetails() {
        String errorCode = "TEST_ERROR";
        String message = "Test message";
        MallaGenerationException exception = new MallaGenerationException(errorCode, message, null);

        String fullMessage = exception.getFullMessage();

        assertEquals("[TEST_ERROR] Test message", fullMessage);
    }

    @Test
    @DisplayName("getFullMessage - Con detalles vacíos")
    void testGetFullMessageWithEmptyDetails() {
        String errorCode = "TEST_ERROR";
        String message = "Test message";
        String details = "   ";
        MallaGenerationException exception = new MallaGenerationException(errorCode, message, details);

        String fullMessage = exception.getFullMessage();

        assertEquals("[TEST_ERROR] Test message", fullMessage);
    }

    @Test
    @DisplayName("toString - Formato correcto")
    void testToString() {
        String errorCode = "TEST_ERROR";
        String message = "Test message";
        String details = "Test details";
        MallaGenerationException exception = new MallaGenerationException(errorCode, message, details);

        String result = exception.toString();

        String expected = "MallaGenerationException{errorCode='TEST_ERROR', message='Test message', details='Test details'}";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("toString - Con detalles nulos")
    void testToStringWithNullDetails() {
        String errorCode = "TEST_ERROR";
        String message = "Test message";
        MallaGenerationException exception = new MallaGenerationException(errorCode, message, null);

        String result = exception.toString();

        String expected = "MallaGenerationException{errorCode='TEST_ERROR', message='Test message', details='null'}";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("xmlGenerationError - Método factory")
    void testXmlGenerationError() {
        String details = "XML generation failed";
        RuntimeException cause = new RuntimeException("XML error");

        MallaGenerationException exception = MallaGenerationException.xmlGenerationError(details, cause);

        assertEquals("Error generando XML de malla", exception.getMessage());
        assertEquals("MALLA_XML_GENERATION_ERROR", exception.getErrorCode());
        assertEquals(details, exception.getDetails());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("transformationError - Método factory")
    void testTransformationError() {
        String details = "Transformation failed";
        RuntimeException cause = new RuntimeException("Transform error");

        MallaGenerationException exception = MallaGenerationException.transformationError(details, cause);

        assertEquals("Error transformando malla de DATIO a ADA", exception.getMessage());
        assertEquals("MALLA_TRANSFORMATION_ERROR", exception.getErrorCode());
        assertEquals(details, exception.getDetails());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("configurationError - Método factory")
    void testConfigurationError() {
        String details = "Configuration is invalid";

        MallaGenerationException exception = MallaGenerationException.configurationError(details);

        assertEquals("Error en configuración de malla", exception.getMessage());
        assertEquals("MALLA_CONFIGURATION_ERROR", exception.getErrorCode());
        assertEquals(details, exception.getDetails());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("validationError - Método factory")
    void testValidationError() {
        String details = "Validation failed";

        MallaGenerationException exception = MallaGenerationException.validationError(details);

        assertEquals("Error de validación en datos de malla", exception.getMessage());
        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertEquals(details, exception.getDetails());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("templateError - Método factory")
    void testTemplateError() {
        String details = "Template processing failed";
        RuntimeException cause = new RuntimeException("Template error");

        MallaGenerationException exception = MallaGenerationException.templateError(details, cause);

        assertEquals("Error procesando plantilla de malla", exception.getMessage());
        assertEquals("MALLA_TEMPLATE_ERROR", exception.getErrorCode());
        assertEquals(details, exception.getDetails());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Serial version UID - Verificar que está definido")
    void testSerialVersionUID() throws NoSuchFieldException {
        java.lang.reflect.Field field = MallaGenerationException.class.getDeclaredField("serialVersionUID");

        assertNotNull(field);
        assertEquals(long.class, field.getType());
        assertTrue(java.lang.reflect.Modifier.isStatic(field.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isFinal(field.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPrivate(field.getModifiers()));
    }

    @Test
    @DisplayName("Factory methods - Con detalles nulos")
    void testFactoryMethodsWithNullDetails() {
        MallaGenerationException configException = MallaGenerationException.configurationError(null);
        MallaGenerationException validationException = MallaGenerationException.validationError(null);

        assertNull(configException.getDetails());
        assertNull(validationException.getDetails());
        assertEquals("MALLA_CONFIGURATION_ERROR", configException.getErrorCode());
        assertEquals("MALLA_VALIDATION_ERROR", validationException.getErrorCode());
    }

    @Test
    @DisplayName("Factory methods - Con causa nula")
    void testFactoryMethodsWithNullCause() {
        MallaGenerationException xmlException = MallaGenerationException.xmlGenerationError("details", null);
        MallaGenerationException transformException = MallaGenerationException.transformationError("details", null);
        MallaGenerationException templateException = MallaGenerationException.templateError("details", null);

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
        MallaGenerationException exception = new MallaGenerationException("test");

        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    @DisplayName("Getter methods - Verificar anotaciones Lombok")
    void testGetterMethods() {
        String errorCode = "TEST_CODE";
        String message = "Test message";
        String details = "Test details";
        MallaGenerationException exception = new MallaGenerationException(errorCode, message, details);

        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(details, exception.getDetails());

        assertDoesNotThrow(() -> exception.getClass().getMethod("getErrorCode"));
        assertDoesNotThrow(() -> exception.getClass().getMethod("getDetails"));
    }
}