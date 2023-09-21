package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.government.response.SourceDefinitionDTOResponse;
import com.bbva.dto.project.request.ProjectPortafolioDTORequest;
import com.bbva.dto.project.response.ProjectPortafolioFilterDtoResponse;
import com.bbva.dto.project.response.ProjectPortafolioSelectResponse;
import com.bbva.entities.government.SourceConceptEntity;
import com.bbva.entities.government.SourceDefinitionEntity;
import com.bbva.service.GovernmentService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Path("/government")
@Produces(MediaType.APPLICATION_JSON)
public class GovernmentResources {

    private GovernmentService governmentService = new GovernmentService();

    @GET
    @Path("/listsourcedefinition/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<SourceDefinitionDTOResponse>> listSourceDefinition(
            @Context HttpServletRequest request,
            @PathParam("projectId") int projectId) {
        return governmentService.listSourceDefinition(projectId);
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
    @Path("/insertsource")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<SourceDefinitionEntity> insertSourceDef(SourceDefinitionEntity dto)
            throws ExecutionException, InterruptedException
    {
        return governmentService.insertSourceDef(dto);
    }

    @PUT
    @Path("/updatesource")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<SourceDefinitionEntity> updateSource(SourceDefinitionEntity dto)
            throws ExecutionException, InterruptedException
    {
        return governmentService.updateSourceDef(dto);
    }

    @DELETE
    @Path("/deleteresource/{ucDataId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<SourceConceptEntity> deleteSource(@Context HttpServletRequest request, @PathParam("ucDataId") int ucDataId)
            throws ExecutionException, InterruptedException
    {
        return governmentService.deleteConcept(ucDataId);
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
