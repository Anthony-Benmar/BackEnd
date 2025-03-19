package com.bbva.resources;

import com.bbva.dto.jira.request.GeneradorDocumentosMallasRequest;
import com.bbva.service.GeneradorDocumentosService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/generadorDocumentos")
@Produces(MediaType.APPLICATION_JSON)
public class GeneradorDocumentosResources {
    private GeneradorDocumentosService generadorDocumentosService = new GeneradorDocumentosService();
    private final String contentDisposition = "Content-Disposition";

    @POST
    @Path("/c204Mallas")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public Response generarC204MallasDocumento(GeneradorDocumentosMallasRequest dto) throws Exception {
        
        byte[] documentoModificado = generadorDocumentosService.generarC204MallasDocumento(dto);
        String nombreDocumento = generadorDocumentosService.generarC204MallasNombre(dto);
        
        return Response.ok(documentoModificado)
                .header(contentDisposition, "attachment; filename=\"C204 - MALLA - "+nombreDocumento+".docx\"")
                .header("Access-Control-Expose-Headers", contentDisposition)
                .build();
    }

    @POST
    @Path("/p110Mallas")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public Response generarP110MallasDocumento(GeneradorDocumentosMallasRequest dto) throws Exception {

        byte[] documentoModificado = generadorDocumentosService.generarP110MallasDocumento(dto);
        String nombreDocumento = generadorDocumentosService.generarP110MallasNombre(dto);

        return Response.ok(documentoModificado)
                .header(contentDisposition, "attachment; filename=\"P110-Plantilla_Seguimiento de Mallas_"+nombreDocumento+"_v1.xlsx\"")
                .header("Access-Control-Expose-Headers", contentDisposition)
                .build();
    }
}
