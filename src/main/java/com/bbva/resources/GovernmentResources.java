package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.government.request.*;
import com.bbva.dto.government.response.FilterSourceResponseDTO;
import com.bbva.dto.government.response.SourceDefinitionDTOResponse;
import com.bbva.entities.government.SourceConceptEntity;
import com.bbva.service.GovernmentService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Path("/government")
@Produces(MediaType.APPLICATION_JSON)
public class GovernmentResources {

    private GovernmentService governmentService = new GovernmentService();

    @POST
    @Path("/source/filter")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<FilterSourceResponseDTO> filterSource(FilterSourceRequestDTO dto) {
        return governmentService.filterSource(dto);
    }

    @GET
    @Path("/sourcebyid/{sourceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<SourceDefinitionDTOResponse> sourceConceptById(
            @Context HttpServletRequest request,
            @PathParam("sourceId") int sourceId) {
        return governmentService.getSourceById(sourceId);
    }

    @GET
    @Path("/listconceptsbysource/{sourceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<SourceConceptEntity>> listConceptsBySource(
            @Context HttpServletRequest request,
            @PathParam("sourceId") int sourceId) {
        return governmentService.listSourceConceptEntity(sourceId);
    }

    @POST
    @Path("/source")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertSourceDef(InsertSourceRequestDTO dto) {
        var response = governmentService.insertSourceDef(dto);
        if(response.success) {
            return Response.ok().status(Response.Status.CREATED).entity(response).build();
        }
        return Response.ok().status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
    }

    @POST
    @Path("/source/{uc_source_id}/concept")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertConcept(@PathParam("uc_source_id") int ucSourceId, InsertConceptRequestDTO dto) {
        var response = governmentService.insertConcept(ucSourceId, dto);
        if(response.success) {
            return Response.ok().status(Response.Status.CREATED).entity(response).build();
        }
        return Response.ok().status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
    }

    @PUT
    @Path("/source/{uc_source_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSource(@PathParam("uc_source_id") int ucSourceId,
                                 UpdateSourceRequestDTO dto) {
        var response = governmentService.updateSourceDef(ucSourceId, dto);
        if(response.success) {
            return Response.ok().status(Response.Status.OK).entity(response).build();
        }
        return Response.ok().status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
    }

    @PUT
    @Path("/source/{uc_source_id}/concept/{uc_data_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateConcept(@PathParam("uc_source_id") int ucSourceId,
                                  @PathParam("uc_data_id") int ucDataId,
                                 UpdateConceptRequestDTO dto) {
        var response = governmentService.updateConcept(ucSourceId, ucDataId, dto);
        if(response.success) {
            return Response.ok().status(Response.Status.OK).entity(response).build();
        }
        return Response.ok().status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
    }

    @DELETE
    @Path("/deleteresource/{ucDataId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<SourceConceptEntity> deleteConcept(@Context HttpServletRequest request, @PathParam("ucDataId") int ucDataId)
            throws ExecutionException, InterruptedException
    {
        return governmentService.deleteConcept(ucDataId);
    }

    @DELETE
    @Path("/source/{uc_source_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<SourceConceptEntity> deleteSource(@Context HttpServletRequest request, @PathParam("uc_source_id") int ucDataId)
            throws ExecutionException, InterruptedException
    {
        return governmentService.deleteSource(ucDataId);
    }

    @DELETE
    @Path("/process/{dependencyId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<SourceConceptEntity> deleteMapDependency(@Context HttpServletRequest request, @PathParam("dependencyId") int dependencyId)
            throws ExecutionException, InterruptedException
    {
        return governmentService.deleteMapDependency(dependencyId);
    }

}
