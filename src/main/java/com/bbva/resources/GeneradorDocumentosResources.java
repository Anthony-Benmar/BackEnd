package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.jira.request.GeneradorDocumentosMallasRequest;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.JiraResponseDTO;
import com.bbva.service.GeneradorDocumentosService;
import com.bbva.service.JiraValidatorService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/generadorDocumentos")
@Produces(MediaType.APPLICATION_JSON)
public class GeneradorDocumentosResources {
    private GeneradorDocumentosService generadorDocumentosService = new GeneradorDocumentosService();

    @POST
    @Path("/mallas")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String generadorDocumentosMallas(GeneradorDocumentosMallasRequest dto)
            throws Exception {
        return generadorDocumentosService.generarDocumentosMallas(dto);
    }

    @POST
    @Path("/descargar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public Response descargarDocumento(GeneradorDocumentosMallasRequest dto) throws Exception {
        
        byte[] documentoModificado = generadorDocumentosService.generarDocumentoModificado(dto);

        
        return Response.ok(documentoModificado)
                .header("Content-Disposition", "attachment; filename=\"documentoModificado.docx\"")
                .header("Access-Control-Expose-Headers", "Content-Disposition")
                .build();
    }
}
