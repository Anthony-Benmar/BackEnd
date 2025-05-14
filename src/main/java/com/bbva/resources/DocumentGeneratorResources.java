package com.bbva.resources;

import com.bbva.dao.ProjectDao;
import com.bbva.dto.documentgenerator.request.DocumentGeneratorMeshRequest;
import com.bbva.service.BitbucketApiService;
import com.bbva.service.DocumentGeneratorService;
import com.bbva.service.JiraApiService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/documentGenerator")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentGeneratorResources {
    private final ProjectDao projectDao = new ProjectDao();
    private final BitbucketApiService bitbucketApiService = new BitbucketApiService();
    private final JiraApiService jiraApiService = new JiraApiService();
    private final DocumentGeneratorService documentGeneratorService = new DocumentGeneratorService(projectDao, bitbucketApiService, jiraApiService);
    private static final String CONTENTDISPOSITION = "Content-Disposition";

    @POST
    @Path("/meshCases")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public Response generateDocumentMeshCases(DocumentGeneratorMeshRequest dto) {
        
        byte[] documentFilled = documentGeneratorService.generateDocumentMeshCases(dto);
        String documentName = documentGeneratorService.generateNameMeshCases(dto);
        
        return Response.ok(documentFilled)
                .header(CONTENTDISPOSITION, "attachment; filename=\"C204 - MALLA - " + documentName + ".docx\"")
                .header("Access-Control-Expose-Headers", CONTENTDISPOSITION)
                .build();
    }

    @POST
    @Path("/meshTracking")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public Response generateDocumentMeshTracking(DocumentGeneratorMeshRequest dto) {

        byte[] documentoModificado = documentGeneratorService.generateDocumentMeshTracking(dto);
        String nombreDocumento = documentGeneratorService.generateNameMeshTracking(dto);

        return Response.ok(documentoModificado)
                .header(CONTENTDISPOSITION, "attachment; filename=\"P110-Plantilla_Seguimiento de Mallas_"+nombreDocumento+"_v2.xlsx\"")
                .header("Access-Control-Expose-Headers", CONTENTDISPOSITION)
                .build();
    }
}
