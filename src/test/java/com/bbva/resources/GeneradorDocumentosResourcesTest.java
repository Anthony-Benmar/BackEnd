package com.bbva.resources;

import com.bbva.dto.jira.request.GeneradorDocumentosMallasRequest;
import com.bbva.service.GeneradorDocumentosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GeneradorDocumentosResourcesTest {

    private GeneradorDocumentosResources resources;
    private GeneradorDocumentosService generadorDocumentosServiceMock;

    @BeforeEach
    void setUp() throws Exception {
        resources = new GeneradorDocumentosResources();
        generadorDocumentosServiceMock = Mockito.mock(GeneradorDocumentosService.class);
        Field serviceField = GeneradorDocumentosResources.class.getDeclaredField("generadorDocumentosService");
        serviceField.setAccessible(true);
        serviceField.set(resources, generadorDocumentosServiceMock);
    }

    @Test
    void testGenerarC204MallasDocumento() {
        GeneradorDocumentosMallasRequest mockRequest = new GeneradorDocumentosMallasRequest();
        byte[] documentoMock = Base64.getDecoder().decode("VGhpcyBpcyBhIHRlc3Q=");
        when(generadorDocumentosServiceMock.generarC204MallasDocumento(mockRequest)).thenReturn(documentoMock);
        when(generadorDocumentosServiceMock.generarC204MallasNombre(mockRequest)).thenReturn("MockNombre");
        Response response = resources.generarC204MallasDocumento(mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("attachment; filename=\"C204 - MALLA - MockNombre.docx\"",
                response.getHeaderString("Content-Disposition"));

        verify(generadorDocumentosServiceMock, times(1)).generarC204MallasDocumento(mockRequest);
        verify(generadorDocumentosServiceMock, times(1)).generarC204MallasNombre(mockRequest);
    }

    @Test
    void testGenerarP110MallasDocumento() {
        GeneradorDocumentosMallasRequest mockRequest = new GeneradorDocumentosMallasRequest();
        byte[] documentoMock = Base64.getDecoder().decode("VGhpcyBpcyBhIHRlc3Q=");
        when(generadorDocumentosServiceMock.generarP110MallasDocumento(mockRequest)).thenReturn(documentoMock);
        when(generadorDocumentosServiceMock.generarP110MallasNombre(mockRequest)).thenReturn("MockNombre");

        Response response = resources.generarP110MallasDocumento(mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("attachment; filename=\"P110-Plantilla_Seguimiento de Mallas_MockNombre_v1.xlsx\"",
                response.getHeaderString("Content-Disposition"));

        verify(generadorDocumentosServiceMock, times(1)).generarP110MallasDocumento(mockRequest);
        verify(generadorDocumentosServiceMock, times(1)).generarP110MallasNombre(mockRequest);
    }
}
