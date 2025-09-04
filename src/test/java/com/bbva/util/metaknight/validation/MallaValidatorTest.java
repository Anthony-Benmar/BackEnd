package com.bbva.util.metaknight.validation;

import com.bbva.core.exception.MallaGenerationException;
import com.bbva.dto.metaknight.request.IngestaRequestDto;
import com.bbva.dto.metaknight.request.MallaRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class MallaValidatorTest {

    private MallaValidator mallaValidator;

    @BeforeEach
    void setUp() {
        mallaValidator = new MallaValidator();
    }

    @Test
    @DisplayName("validarDatosIngesta - Request válido con generarMallas true")
    void testValidarDatosIngesta_ValidRequest_Success() throws MallaGenerationException {
        IngestaRequestDto request = createValidIngestaRequest();
        request.setGenerarMallas(true);

        assertDoesNotThrow(() -> mallaValidator.validarDatosIngesta(request));
    }

    @Test
    @DisplayName("validarDatosIngesta - GenerarMallas false no valida campos")
    void testValidarDatosIngesta_GenerarMallasFalse_NoValidation() throws MallaGenerationException {
        IngestaRequestDto request = new IngestaRequestDto();
        request.setGenerarMallas(false);

        assertDoesNotThrow(() -> mallaValidator.validarDatosIngesta(request));
    }

    @Test
    @DisplayName("validarDatosIngesta - UUAA Master nulo con generarMallas true")
    void testValidarDatosIngesta_NullUuaaMaster_ThrowsException() {
        IngestaRequestDto request = createValidIngestaRequest();
        request.setGenerarMallas(true);
        request.setUuaaMaster(null);

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosIngesta(request));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("UUAA Master es requerida"));
    }

    @Test
    @DisplayName("validarDatosIngesta - UUAA Master vacío")
    void testValidarDatosIngesta_EmptyUuaaMaster_ThrowsException() {
        IngestaRequestDto request = createValidIngestaRequest();
        request.setGenerarMallas(true);
        request.setUuaaMaster("");

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosIngesta(request));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("UUAA Master es requerida"));
    }

    @Test
    @DisplayName("validarDatosIngesta - UUAA Master formato inválido - 1 caracter")
    void testValidarDatosIngesta_InvalidUuaaFormat_OneChar_ThrowsException() {
        IngestaRequestDto request = createValidIngestaRequest();
        request.setGenerarMallas(true);
        request.setUuaaMaster("A");

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosIngesta(request));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("exactamente 4 caracteres"));
    }

    @Test
    @DisplayName("validarDatosIngesta - UUAA Master formato inválido - 5 caracteres")
    void testValidarDatosIngesta_InvalidUuaaFormat_FiveChars_ThrowsException() {
        IngestaRequestDto request = createValidIngestaRequest();
        request.setGenerarMallas(true);
        request.setUuaaMaster("ABCDE");

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosIngesta(request));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("exactamente 4 caracteres"));
    }

    @Test
    @DisplayName("validarDatosIngesta - UUAA Master formato inválido - con números")
    void testValidarDatosIngesta_InvalidUuaaFormat_WithNumbers_ThrowsException() {
        IngestaRequestDto request = createValidIngestaRequest();
        request.setGenerarMallas(true);
        request.setUuaaMaster("123A");

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosIngesta(request));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("exactamente 4 caracteres"));
    }

    @Test
    @DisplayName("validarDatosIngesta - Registro desarrollador corto")
    void testValidarDatosIngesta_ShortRegistroDev_ThrowsException() {
        IngestaRequestDto request = createValidIngestaRequest();
        request.setGenerarMallas(true);
        request.setRegistroDev("12345");

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosIngesta(request));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("al menos 6 caracteres"));
    }

    @Test
    @DisplayName("validarDatosMalla - Datos válidos")
    void testValidarDatosMalla_ValidData_Success() throws MallaGenerationException {
        MallaRequestDto mallaData = createValidMallaRequest();

        assertDoesNotThrow(() -> mallaValidator.validarDatosMalla(mallaData));
    }

    @Test
    @DisplayName("validarDatosMalla - CreationUser vacío")
    void testValidarDatosMalla_EmptyCreationUser_ThrowsException() {
        MallaRequestDto mallaData = createValidMallaRequest();
        mallaData.setCreationUser("");

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosMalla(mallaData));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("es requerido"));
    }

    @Test
    @DisplayName("validarDatosMalla - Email formato inválido")
    void testValidarDatosMalla_InvalidEmail_ThrowsException() {
        MallaRequestDto mallaData = createValidMallaRequest();
        mallaData.setTeamEmail("invalid-email");

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosMalla(mallaData));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("formato"));
    }

    @Test
    @DisplayName("validarDatosMalla - Fecha creación formato inválido")
    void testValidarDatosMalla_InvalidCreationDate_ThrowsException() {
        MallaRequestDto mallaData = createValidMallaRequest();
        mallaData.setCreationDate("2024-01-01");

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosMalla(mallaData));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("formato YYYYMMDD"));
    }

    @Test
    @DisplayName("validarDatosMalla - Hora creación formato inválido")
    void testValidarDatosMalla_InvalidCreationTime_ThrowsException() {
        MallaRequestDto mallaData = createValidMallaRequest();
        mallaData.setCreationTime("12:30:45");

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosMalla(mallaData));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("formato HHMMSS"));
    }

    @Test
    @DisplayName("validarDatosMalla - Transfer time formato inválido")
    void testValidarDatosMalla_InvalidTransferTime_ThrowsException() {
        MallaRequestDto mallaData = createValidMallaRequest();
        mallaData.setTransferTimeFrom("12:30");
        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosMalla(mallaData));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("formato HHMM"));
    }

    @Test
    @DisplayName("validarDatosMalla - Jobname muy corto")
    void testValidarDatosMalla_ShortJobname_ThrowsException() {
        MallaRequestDto mallaData = createValidMallaRequest();
        mallaData.setTransferJobname("AB");

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosMalla(mallaData));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("debe tener entre"));
    }

    @Test
    @DisplayName("validarDatosMalla - UUAA inconsistente")
    void testValidarDatosMalla_InconsistentUuaa_ThrowsException() {
        MallaRequestDto mallaData = createValidMallaRequest();
        mallaData.setUuaa("TEST");
        mallaData.setUuaaLowercase("different");

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosMalla(mallaData));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("no son consistentes"));
    }

    @Test
    @DisplayName("validarXmlGenerado - XML válido")
    void testValidarXmlGenerado_ValidXml_Success() throws MallaGenerationException {
        String validXml = "<JOB JOBNAME=\"TEST_JOB\" APPLICATION=\"TEST\" CMDLINE=\"echo test\"></JOB>";

        assertDoesNotThrow(() -> mallaValidator.validarXmlGenerado(validXml, "DATIO"));
    }

    @Test
    @DisplayName("validarXmlGenerado - XML vacío")
    void testValidarXmlGenerado_EmptyXml_ThrowsException() {
        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarXmlGenerado("", "DATIO"));

        assertEquals("MALLA_XML_GENERATION_ERROR", exception.getErrorCode());
        assertEquals("Error generando XML de malla", exception.getMessage());
        assertTrue(exception.getDetails().contains("vacío") || exception.getDetails().contains("vac"));
    }

    @Test
    @DisplayName("validarXmlGenerado - XML sin formato válido")
    void testValidarXmlGenerado_InvalidFormat_ThrowsException() {
        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarXmlGenerado("not xml content", "ADA"));

        assertEquals("MALLA_XML_GENERATION_ERROR", exception.getErrorCode());
        assertEquals("Error generando XML de malla", exception.getMessage());
        assertTrue(exception.getDetails().contains("formato"));
    }

    @Test
    @DisplayName("validarXmlGenerado - XML sin jobs")
    void testValidarXmlGenerado_NoJobs_ThrowsException() {
        String xmlWithoutJobs = "<FOLDER><CONFIG>test</CONFIG></FOLDER>";

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarXmlGenerado(xmlWithoutJobs, "DATIO"));

        assertEquals("MALLA_XML_GENERATION_ERROR", exception.getErrorCode());
        assertEquals("Error generando XML de malla", exception.getMessage());
        assertTrue(exception.getDetails().contains("jobs") || exception.getDetails().contains("JOB"));
    }

    @Test
    @DisplayName("validarXmlGenerado - XML sin elemento JOBNAME")
    void testValidarXmlGenerado_MissingJobname_ThrowsException() {
        String xmlWithoutJobname = "<JOB APPLICATION=\"TEST\" CMDLINE=\"echo\"></JOB>";

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarXmlGenerado(xmlWithoutJobname, "DATIO"));

        assertEquals("MALLA_XML_GENERATION_ERROR", exception.getErrorCode());
        assertEquals("Error generando XML de malla", exception.getMessage());
        assertTrue(exception.getDetails().contains("JOBNAME"));
    }

    @Test
    @DisplayName("validarDatosMalla - Jobname con caracteres especiales")
    void testValidarDatosMalla_JobnameWithSpecialChars_ThrowsException() {
        MallaRequestDto mallaData = createValidMallaRequest();
        mallaData.setTransferJobname("TEST_JOB!@#");

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosMalla(mallaData));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("alfanum"));
    }

    @Test
    @DisplayName("validarDatosMalla - Jobname muy largo")
    void testValidarDatosMalla_LongJobname_ThrowsException() {
        MallaRequestDto mallaData = createValidMallaRequest();
        mallaData.setTransferJobname("VERYLONGJOBNAMETHATEXCEEDS20CHARS");

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosMalla(mallaData));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("debe tener entre"));
    }

    @Test
    @DisplayName("validarDatosMalla - Namespace sin UUAA")
    void testValidarDatosMalla_NamespaceWithoutUuaa_ThrowsException() {
        MallaRequestDto mallaData = createValidMallaRequest();
        mallaData.setUuaa("TEST");
        mallaData.setUuaaLowercase("test");
        mallaData.setNamespace("different-namespace");

        MallaGenerationException exception = assertThrows(MallaGenerationException.class,
                () -> mallaValidator.validarDatosMalla(mallaData));

        assertEquals("MALLA_VALIDATION_ERROR", exception.getErrorCode());
        assertTrue(exception.getDetails().contains("no contiene la UUAA"));
    }

    private IngestaRequestDto createValidIngestaRequest() {
        IngestaRequestDto request = new IngestaRequestDto();
        request.setUuaaMaster("TEST");
        request.setRegistroDev("testuser123");
        request.setNombreDev("Test Developer");
        request.setProyecto("Test Project");
        return request;
    }

    private MallaRequestDto createValidMallaRequest() {
        MallaRequestDto mallaData = new MallaRequestDto();
        mallaData.setCreationUser("testuser");
        mallaData.setUuaa("TEST");
        mallaData.setUuaaLowercase("test");
        mallaData.setNamespace("test-namespace");
        mallaData.setParentFolder("test-folder");
        mallaData.setCreationDate("20240101");
        mallaData.setCreationTime("120000");
        mallaData.setTeamEmail("test@example.com");
        mallaData.setTransferTimeFrom("1200");

        mallaData.setTransferJobname("TESTTRANSFER");
        mallaData.setCopyJobname("TESTCOPYJOB");
        mallaData.setFwJobname("TESTFWJOB01");
        mallaData.setHmmStgJobname("TESTHMMSTG");
        mallaData.setKrbRawJobname("TESTKRBRAW");
        mallaData.setHmmRawJobname("TESTHMMRAW");
        mallaData.setKrbMasterJobname("TESTKRBMASTER");
        mallaData.setHmmMasterJobname("TESTHMMMASTER");
        mallaData.setErase1Jobname("TESTERASE1");
        mallaData.setErase2Jobname("TESTERASE2");

        return mallaData;
    }
}