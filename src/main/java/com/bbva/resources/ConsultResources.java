package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.pgc.response.PgcDocumentLisItem;
import com.bbva.dto.pgc.response.PgcConceptLisItem;
import com.bbva.entities.pgc.PgcConcept;
import com.bbva.service.ConsultService;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/pgc")
@Produces(MediaType.APPLICATION_JSON)
public class ConsultResources {

    private final ConsultService consultService = new ConsultService();

    @GET
    @Path("/documents/processed")
    public IDataResult<List<PgcDocumentLisItem>> listProcessed(@Context HttpServletRequest request) {
        return consultService.getProcessedForList();
    }

    @GET
    @Path("/concepts/by-document/{documentId}")
    public IDataResult<List<PgcConceptLisItem>> listByDocumentId(@Context HttpServletRequest request,
                                                                 @PathParam("documentId") int documentId) {
        return consultService.getListByDocumentId(documentId);
    }
}
