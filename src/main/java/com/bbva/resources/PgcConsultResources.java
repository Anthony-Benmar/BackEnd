package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.pgc.response.PgcConceptLisItem;
import com.bbva.dto.pgc.response.PgcDocumentListItem;
import com.bbva.service.PgcConsultService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import java.util.List;

@Path("/pgc")
public class PgcConsultResources {
    private final PgcConsultService consultService = new PgcConsultService();

    /** Listado PROCESSED “liviano” (usa SP_GET_DOCUMENTS y DTO PgcDocumentListItem) */
    @GET
    @Path("/documents/processed")
    public IDataResult<List<PgcDocumentListItem>> listProcessed(@Context HttpServletRequest request) {
        return consultService.getProcessedForList();
    }

    /** Listado “liviano” por documentId (usa SP_GET_CONCEPTS y DTO PgcConceptLisItem) */
    @GET
    @Path("/concepts/by-document/{documentId}")
    public IDataResult<List<PgcConceptLisItem>> listByDocumentId(@Context HttpServletRequest request,
                                                                 @PathParam("documentId") int documentId) {
        return consultService.getListByDocumentId(documentId);
    }
}
