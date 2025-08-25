package com.bbva.core.exception;

/**
 * Excepción personalizada para errores específicos en la generación de mallas XML
 */
public class MallaGenerationException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String errorCode;
    private final String details;

    /**
     * Constructor con mensaje básico
     */
    public MallaGenerationException(String message) {
        super(message);
        this.errorCode = "MALLA_ERROR";
        this.details = null;
    }

    /**
     * Constructor con mensaje y causa
     */
    public MallaGenerationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "MALLA_ERROR";
        this.details = null;
    }

    /**
     * Constructor completo con código de error y detalles
     */
    public MallaGenerationException(String errorCode, String message, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    /**
     * Constructor completo con código de error, detalles y causa
     */
    public MallaGenerationException(String errorCode, String message, String details, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = details;
    }

    /**
     * Obtiene el código de error específico
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Obtiene los detalles adicionales del error
     */
    public String getDetails() {
        return details;
    }

    /**
     * Obtiene un mensaje completo con código de error y detalles
     */
    public String getFullMessage() {
        StringBuilder fullMessage = new StringBuilder();
        fullMessage.append("[").append(errorCode).append("] ").append(getMessage());

        if (details != null && !details.trim().isEmpty()) {
            fullMessage.append(" - Detalles: ").append(details);
        }

        return fullMessage.toString();
    }

    @Override
    public String toString() {
        return String.format("MallaGenerationException{errorCode='%s', message='%s', details='%s'}",
                errorCode, getMessage(), details);
    }

    // Métodos estáticos de fábrica para casos comunes

    public static MallaGenerationException xmlGenerationError(String details, Throwable cause) {
        return new MallaGenerationException("MALLA_XML_GENERATION_ERROR",
                "Error generando XML de malla", details, cause);
    }

    public static MallaGenerationException transformationError(String details, Throwable cause) {
        return new MallaGenerationException("MALLA_TRANSFORMATION_ERROR",
                "Error transformando malla de DATIO a ADA", details, cause);
    }

    public static MallaGenerationException configurationError(String details) {
        return new MallaGenerationException("MALLA_CONFIGURATION_ERROR",
                "Error en configuración de malla", details);
    }

    public static MallaGenerationException validationError(String details) {
        return new MallaGenerationException("MALLA_VALIDATION_ERROR",
                "Error de validación en datos de malla", details);
    }

    public static MallaGenerationException templateError(String details, Throwable cause) {
        return new MallaGenerationException("MALLA_TEMPLATE_ERROR",
                "Error procesando plantilla de malla", details, cause);
    }
}