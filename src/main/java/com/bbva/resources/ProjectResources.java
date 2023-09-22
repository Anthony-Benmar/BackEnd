package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.dto.map_dependency.response.MapDependencyListByProjectResponse;
import com.bbva.dto.project.request.ProjectFilterByNameOrSdatoolDtoRequest;
import com.bbva.dto.project.request.ProjectPortafolioDTORequest;
import com.bbva.dto.project.request.ProjectPortafolioFilterDTORequest;
import com.bbva.dto.project.response.ProjectListForSelectDtoResponse;
import com.bbva.dto.project.response.ProjectFilterByNameOrSdatoolDtoResponse;
import com.bbva.dto.project.response.ProjectPortafolioFilterDtoResponse;
import com.bbva.dto.project.response.ProjectPortafolioSelectResponse;
import com.bbva.entities.common.PeriodPEntity;
import com.bbva.service.ProjectService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Path("/project")
@Produces(MediaType.APPLICATION_JSON)
public class ProjectResources {
    private ProjectService projectService = new ProjectService();

    @POST
    @Path("/filter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ProjectFilterByNameOrSdatoolDtoResponse> list(ProjectFilterByNameOrSdatoolDtoRequest dto) {
        return projectService.filter(dto);
    }

    @GET
    @Path("/portfolio/detail/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ProjectPortafolioSelectResponse> projectaPortfolio(@Context HttpServletRequest request, @PathParam("projectId") int projectId) {
        return projectService.selectProject(projectId);
    }

    @POST
    @Path("/portfolio/filter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ProjectPortafolioFilterDtoResponse> list(ProjectPortafolioFilterDTORequest dto) {
        return projectService.portafolioFilter(dto);
    }

    @POST
    @Path("/portfolio/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ProjectPortafolioFilterDtoResponse> insert(ProjectPortafolioDTORequest dto)
            throws ExecutionException, InterruptedException
    {
        return projectService.insertProject(dto);
    }

    @PUT
    @Path("/portfolio/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ProjectPortafolioFilterDtoResponse> update(ProjectPortafolioDTORequest dto)
            throws ExecutionException, InterruptedException
    {
        return projectService.updateProject(dto);
    }

    @DELETE
    @Path("/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ProjectPortafolioFilterDtoResponse> delete(@Context HttpServletRequest request, @PathParam("projectId") int projectId)
            throws ExecutionException, InterruptedException
    {
        return projectService.deleteProject(projectId);
    }

    @GET
    @Path("/{projectId}/process")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<MapDependencyListByProjectResponse>> getProcessByProjectId(@Context HttpServletRequest request, @PathParam("projectId") int projectId)
            throws ExecutionException, InterruptedException
    {
        return projectService.getProcessByProjectId(projectId);
    }

    @POST
    @Path("/listforselect")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<ProjectListForSelectDtoResponse>> projectSelect(PeriodPEntity period) {
        IDataResult<List<ProjectListForSelectDtoResponse>> result = projectService.listForSelect(period);
        return result;
    }

    @POST
    @Path("/listAll")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<ProjectListForSelectDtoResponse>> projectSelect() {
        IDataResult<List<ProjectListForSelectDtoResponse>> result = projectService.listForSelect();
        return result;
    }

}