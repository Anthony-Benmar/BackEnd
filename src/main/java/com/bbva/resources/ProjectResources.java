package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.dto.map_dependency.response.MapDependencyListByProjectResponse;
import com.bbva.dto.project.request.*;
import com.bbva.dto.project.response.*;
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

    @POST
    @Path("/{projectId}/document")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InsertProjectDocumentDTO> insertProjectDocument(
            @PathParam("projectId") String projectId, InsertProjectDocumentDTO request){
        request.setProjectId(Integer.parseInt(projectId));
        IDataResult<InsertProjectDocumentDTO>  result = projectService.insertProjectDocument(request);
        return result;
    }

    @POST
    @Path("/{projectId}/participant")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InsertProjectParticipantDTO> insertProjectDocument(
            @PathParam("projectId") String projectId, InsertProjectParticipantDTO request){
        request.setProjectId(Integer.parseInt(projectId));
        IDataResult<InsertProjectParticipantDTO>  result = projectService.insertProjectParticipant(request);
        return result;
    }

    @POST
    @Path("/info")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InsertProjectInfoDTORequest> insertProjectInfo(InsertProjectInfoDTORequest request){
        if (projectService.sdatoolIdExists(request.getSdatoolId())) {
            return new ErrorDataResult("El proyecto que desea registrar ya existe, verifique el código SDATOOL");
        }
        IDataResult<InsertProjectInfoDTORequest>  result = projectService.insertProjectInfo(request);
        result.setMessage("Proyecto creado con éxito");

        return result;
    }

    @DELETE
    @Path("/info/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Integer> deleteProjectInfo(@Context HttpServletRequest request, @PathParam("projectId") int projectId)
            throws ExecutionException, InterruptedException
    {
        return projectService.deleteProjectInfo(projectId);
    }

    @PUT
    @Path("/info/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ProjectInfoDTO> updateProjectInfo(ProjectInfoDTO dto)
            throws ExecutionException, InterruptedException
    {
        if(projectService.sdatoolIdExists(dto.getSdatoolId())) {
            return new ErrorDataResult<>("El proyecto que desea registrar ya existe, verifique el código SDATOOL");
        }
        return projectService.updateProjectInfo(dto);
    }

    @POST
    @Path("/info/filter")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ProjectInfoFilterResponse> projectInfoFilter(ProjectInfoFilterRequest dto) {
        return projectService.projectInfoFilter(dto);
    }

    @DELETE
    @Path("/info/{projectId}/document/{documentId}/{updateAuditUser}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Integer> deleteDocument(@Context HttpServletRequest request,
                                               @PathParam("projectId") int projectId,
                                               @PathParam("documentId") int documentId,
                                               @PathParam("updateAuditUser") String updateAuditUser)
            throws ExecutionException, InterruptedException
    {
        return projectService.deleteDocument(projectId, documentId, updateAuditUser);
    }

    @PUT
    @Path("/info/document/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InsertProjectDocumentDTO> updateDocument(InsertProjectDocumentDTO dto)
            throws ExecutionException, InterruptedException
    {
        return projectService.updateDocument(dto);
    }

    @GET
    @Path("/info/{projectId}/document/{documentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<InsertProjectDocumentDTO>> getDocument(@Context  HttpServletRequest request,
                                                                   @PathParam("projectId") int projectId,
                                                                   @PathParam("documentId") int documentId)
            throws ExecutionException, InterruptedException
    {
        return projectService.getDocument(projectId, documentId);
    }

    @DELETE
    @Path("/info/{projectId}/participant/{participantId}/{updateAuditUser}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Integer> deleteParticipantProject(@Context HttpServletRequest request,
                                               @PathParam("projectId") int projectId,
                                               @PathParam("participantId") int participantId,
                                               @PathParam("updateAuditUser") String updateAuditUser)
            throws ExecutionException, InterruptedException
    {
        return projectService.deleteParticipantProject(projectId, participantId, updateAuditUser);
    }
    @PUT
    @Path("/info/participant/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InsertProjectParticipantDTO> updateParticipant(InsertProjectParticipantDTO dto)
            throws ExecutionException, InterruptedException
    {
        return projectService.updateParticipant(dto);
    }

    @GET
    @Path("/info/{projectId}/participants")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<InsertProjectParticipantDTO>> getProjectParticipants(@Context HttpServletRequest request,
                                                                   @PathParam("projectId") int projectId)
            throws ExecutionException, InterruptedException
    {
        return projectService.getProjectParticipants(projectId);
    }

    @GET
    @Path("/calendar")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<SelectCalendarDTO>> getCalendar()
            throws ExecutionException, InterruptedException
    {
        return projectService.getCalendar();
    }
}