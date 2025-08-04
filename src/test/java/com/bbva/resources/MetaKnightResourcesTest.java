package com.bbva.resources;

import com.bbva.core.results.ErrorDataResult;
import com.bbva.dto.metaknight.request.IngestaRequestDto;
import com.bbva.service.metaknight.IngestaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MetaKnightResourcesTest {

    private IngestaService ingestaServiceMock;
    private MetaKnightResources metaKnightResources;

    @BeforeEach
    void setUp() {
        ingestaServiceMock = mock(IngestaService.class);
        metaKnightResources = new MetaKnightResources() {
            {
                try {
                    var field = MetaKnightResources.class.getDeclaredField("ingestaService");
                    field.setAccessible(true);
                    field.set(this, ingestaServiceMock);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void testProcesarIngestaDownload_Success() throws Exception {
        IngestaRequestDto request = createValidRequest();
        byte[] expectedZipBytes = "fake-zip-content".getBytes();

        when(ingestaServiceMock.procesarIngesta(request)).thenReturn(expectedZipBytes);

        Response response = metaKnightResources.procesarIngestaDownload(request);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        assertEquals("application/zip", response.getHeaderString("Content-Type"));
        assertEquals("attachment; filename=\"ingesta_output.zip\"", response.getHeaderString("Content-Disposition"));
        assertEquals(String.valueOf(expectedZipBytes.length), response.getHeaderString("Content-Length"));

        assertEquals(expectedZipBytes, response.getEntity());

        verify(ingestaServiceMock).procesarIngesta(request);
    }

    @Test
    void testProcesarIngestaDownload_ServiceThrowsException() throws Exception {
        IngestaRequestDto request = createValidRequest();
        String errorMessage = "Error procesando CSV";
        Exception serviceException = new RuntimeException(errorMessage);

        when(ingestaServiceMock.procesarIngesta(request)).thenThrow(serviceException);

        Response response = metaKnightResources.procesarIngestaDownload(request);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals("application/json", response.getHeaderString("Content-Type"));

        Object entity = response.getEntity();
        assertTrue(entity instanceof ErrorDataResult);
        ErrorDataResult<Void> errorResult = (ErrorDataResult<Void>) entity;
        assertEquals("500", errorResult.status);
        assertEquals("Error procesando la ingesta: " + errorMessage, errorResult.message);
        assertNull(errorResult.data);

        verify(ingestaServiceMock).procesarIngesta(request);
    }

    @Test
    void testProcesarIngestaDownload_ServiceThrowsIllegalArgumentException() throws Exception {
        IngestaRequestDto request = createValidRequest();
        String errorMessage = "Schema Raw es requerido";
        IllegalArgumentException serviceException = new IllegalArgumentException(errorMessage);

        when(ingestaServiceMock.procesarIngesta(request)).thenThrow(serviceException);

        Response response = metaKnightResources.procesarIngestaDownload(request);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        ErrorDataResult<Void> errorResult = (ErrorDataResult<Void>) response.getEntity();
        assertEquals("500", errorResult.status);
        assertEquals("Error procesando la ingesta: " + errorMessage, errorResult.message);

        verify(ingestaServiceMock).procesarIngesta(request);
    }

    @Test
    void testProcesarIngestaDownload_EmptyZipResponse() throws Exception {
        IngestaRequestDto request = createValidRequest();
        byte[] emptyZipBytes = new byte[0];

        when(ingestaServiceMock.procesarIngesta(request)).thenReturn(emptyZipBytes);

        Response response = metaKnightResources.procesarIngestaDownload(request);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("0", response.getHeaderString("Content-Length"));
        assertEquals(emptyZipBytes, response.getEntity());

        verify(ingestaServiceMock).procesarIngesta(request);
    }

    @Test
    void testProcesarIngestaDownload_LargeZipResponse() throws Exception {
        IngestaRequestDto request = createValidRequest();
        byte[] largeZipBytes = new byte[1024 * 1024];
        Arrays.fill(largeZipBytes, (byte) 'A');

        when(ingestaServiceMock.procesarIngesta(request)).thenReturn(largeZipBytes);

        Response response = metaKnightResources.procesarIngestaDownload(request);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(String.valueOf(largeZipBytes.length), response.getHeaderString("Content-Length"));
        assertEquals(largeZipBytes, response.getEntity());

        verify(ingestaServiceMock).procesarIngesta(request);
    }

    @Test
    void testProcesarIngestaDownload_NullRequest() throws Exception {
        when(ingestaServiceMock.procesarIngesta(null))
                .thenThrow(new IllegalArgumentException("Request no puede ser nulo"));

        Response response = metaKnightResources.procesarIngestaDownload(null);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        ErrorDataResult<Void> errorResult = (ErrorDataResult<Void>) response.getEntity();
        assertTrue(errorResult.message.contains("Request no puede ser nulo"));

        verify(ingestaServiceMock).procesarIngesta(null);
    }

    private IngestaRequestDto createValidRequest() {
        IngestaRequestDto request = new IngestaRequestDto();
        request.setFrecuencia("Daily");
        request.setUuaaMaster("TESTUA");
        request.setTipoArchivo("csv");
        request.setDelimitador(";");
        request.setParticiones("cutoff_date");
        request.setTieneL1T(false);
        request.setTieneCompactacion(false);
        request.setSdatool("METAKNIGHT-001");
        request.setProyecto("Test Project");
        request.setSm("Test SM");
        request.setPo("Test PO");
        request.setNombreDev("Test Developer");
        request.setRegistroDev("TEST123");
        request.setSchemaRawBase64("dGVzdCxyYXcsZGF0YQ==");
        request.setSchemaMasterBase64("dGVzdCxtYXN0ZXIsZGF0YQ==");
        request.setUsername("testuser");
        request.setToken("testtoken");
        request.setTicketJira("TEST-123");
        return request;
    }
}