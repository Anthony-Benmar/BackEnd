package com.bbva.resources;

import com.bbva.dto.documentgenerator.request.DocumentGeneratorMeshRequest;
import com.bbva.service.DocumentGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DocumentGeneratorResourcesTest {

    private DocumentGeneratorResources documentGeneratorResources;
    private DocumentGeneratorService documentGeneratorServiceMock;

    @BeforeEach
    void setUp() throws Exception {
        documentGeneratorResources = new DocumentGeneratorResources();
        documentGeneratorServiceMock = Mockito.mock(DocumentGeneratorService.class);
        Field serviceField = DocumentGeneratorResources.class.getDeclaredField("documentGeneratorService");
        serviceField.setAccessible(true);
        serviceField.set(documentGeneratorResources, documentGeneratorServiceMock);
    }

    @Test
    void testGenerateDocumentMeshCases() {
        DocumentGeneratorMeshRequest mockRequest = new DocumentGeneratorMeshRequest();
        byte[] documentoMock = Base64.getDecoder().decode("VGhpcyBpcyBhIHRlc3Q=");
        when(documentGeneratorServiceMock.generateDocumentMeshCases(mockRequest)).thenReturn(documentoMock);
        when(documentGeneratorServiceMock.generateNameMeshCases(mockRequest)).thenReturn("MockNombre");
        Response response = documentGeneratorResources. generateDocumentMeshCases(mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("attachment; filename=\"C204 - MALLA - MockNombre.docx\"",
                response.getHeaderString("Content-Disposition"));

        verify(documentGeneratorServiceMock, times(1)).generateDocumentMeshCases(mockRequest);
        verify(documentGeneratorServiceMock, times(1)).generateNameMeshCases(mockRequest);
    }

    @Test
    void testGenerateDocumentMeshTracking() {
        DocumentGeneratorMeshRequest mockRequest = new DocumentGeneratorMeshRequest();
        byte[] documentoMock = Base64.getDecoder().decode("VGhpcyBpcyBhIHRlc3Q=");
        when(documentGeneratorServiceMock.generateDocumentMeshTracking(mockRequest)).thenReturn(documentoMock);
        when(documentGeneratorServiceMock.generateNameMeshTracking(mockRequest)).thenReturn("MockNombre");

        Response response = documentGeneratorResources.generateDocumentMeshTracking(mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("attachment; filename=\"P110-Plantilla_Seguimiento de Mallas_MockNombre_v2.xlsx\"",
                response.getHeaderString("Content-Disposition"));

        verify(documentGeneratorServiceMock, times(1)).generateDocumentMeshTracking(mockRequest);
        verify(documentGeneratorServiceMock, times(1)).generateNameMeshTracking(mockRequest);
    }
}
