package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.dto.map_dependency.response.MapDependencyListByProjectResponse;
import com.bbva.dto.project.request.*;
import com.bbva.dto.project.response.*;
import com.bbva.entities.common.PeriodPEntity;
import com.bbva.entities.project.ProjectStatusEntity;
import com.bbva.service.ProjectService;
import com.bbva.dto.feature.response.featureDtoResponse;

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
    public IDataResult<ProjectPortafolioFilterDtoResponse> insert(ProjectPortafolioDTORequest dto) {
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
    public IDataResult<List<MapDependencyListByProjectResponse>> getProcessByProjectId(@Context HttpServletRequest request, @PathParam("projectId") int projectId) {
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
    @Path("/{sdatoolId}/listprojectcatalog")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<ProjectCatalogDtoResponse>> projectCatalogList(@PathParam("sdatoolId") String sdatoolId) {
        IDataResult<List<ProjectCatalogDtoResponse>> result = projectService.listProjectCatalog(sdatoolId);
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
    public IDataResult<InsertProjectParticipantDTO> insertProjectParticipant(
            @PathParam("projectId") String projectId, InsertProjectParticipantDTO request){
        request.setProjectId(Integer.parseInt(projectId));
        IDataResult<InsertProjectParticipantDTO>  result = projectService.insertProjectParticipant(request);
        return result;
    }

    @POST
    @Path("/info")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InsertProjectInfoDTORequest> insertProjectInfo(InsertProjectInfoDTORequest request)
            throws Exception{
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
    public IDataResult<Integer> deleteProjectInfo(@Context HttpServletRequest request, @PathParam("projectId") int projectId) {
        return projectService.deleteProjectInfo(projectId);
    }

    @PUT
    @Path("/info/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ProjectInfoDTO> updateProjectInfo(ProjectInfoDTO dto) {

        try{
            if(projectService.sdatoolIdExistsUpdate(dto.getSdatoolId(), dto.getProjectId())) {
                return new ErrorDataResult<>("El proyecto que desea registrar ya existe, verifique el código SDATOOL");
            }
        } catch (Exception e) {
            return new ErrorDataResult<>("Error en el servicio de actualización de proyecto");
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

    @POST
    @Path("/info/filter/domain")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ProjectInfoFilterByDomainDtoResponse> projectInfoFilterByDomain( ProjectInfoFilterByDomainDtoRequest dto) {
        return projectService.projectInfoFilterByDomain(dto);
    }
    @POST
    @Path("/info/filterAll/domain")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<ProjectInfoFilterAllByDomainDtoResponse> projectInfoFilterAllByDomain( ProjectInfoFilterByDomainDtoRequest dto) {
        return projectService.projectInfoFilterAllByDomain(dto);
    }

    @DELETE
    @Path("/info/{projectId}/document/{documentId}/{updateAuditUser}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Integer> deleteDocument(@Context HttpServletRequest request,
                                               @PathParam("projectId") int projectId,
                                               @PathParam("documentId") int documentId,
                                               @PathParam("updateAuditUser") String updateAuditUser) {
        return projectService.deleteDocument(projectId, documentId, updateAuditUser);
    }

    @PUT
    @Path("/info/document/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InsertProjectDocumentDTO> updateDocument(InsertProjectDocumentDTO dto) {
        return projectService.updateDocument(dto);
    }

    @GET
    @Path("/info/{projectId}/document/{documentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<InsertProjectDocumentDTO>> getDocument(@Context  HttpServletRequest request,
                                                                   @PathParam("projectId") int projectId,
                                                                   @PathParam("documentId") int documentId) {
        return projectService.getDocument(projectId, documentId);
    }

    @DELETE
    @Path("/info/{projectId}/participant/{participantId}/{updateAuditUser}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<Integer> deleteParticipantProject(@Context HttpServletRequest request,
                                               @PathParam("projectId") int projectId,
                                               @PathParam("participantId") int participantId,
                                               @PathParam("updateAuditUser") String updateAuditUser) {
        return projectService.deleteParticipantProject(projectId, participantId, updateAuditUser);
    }

    @PUT
    @Path("/info/participant/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<InsertProjectParticipantDTO> updateParticipant(InsertProjectParticipantDTO dto) {
        return projectService.updateParticipant(dto);
    }

    @GET
    @Path("/calendar")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<SelectCalendarDTO>> getCalendar() {
        return projectService.getCalendar();
    }

    @GET
    @Path("/domain/{domainId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<ProjectByDomainIdDTO>> getProjectsByDomainId(@Context HttpServletRequest request,
                                                                        @PathParam("domainId") String domainId) {
        return projectService.getProjectsByDomainId(domainId);
    }

    @GET
    @Path("/features/{sdatoolId}")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<featureDtoResponse>> getFeaturesByProject(@Context HttpServletRequest request,
                                                               @PathParam("sdatoolId") String sdatoolId) {
        return projectService.getFeaturesByProject(sdatoolId);
    }

    @GET
    @Path("/info/{projectId}/status-tracking")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<ProjectStatusEntity>> getProjectStatusTracking(@Context HttpServletRequest request,
                                                                         @PathParam("projectId") int projectId)
    {
        return projectService.getProjectStatusTracking(projectId);
    }

    @GET
    @Path("/info/{projectId}/participants")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<InsertProjectParticipantDTO>> getProjectParticipants(@Context HttpServletRequest request,
                                                                                 @PathParam("projectId") int projectId)
    {
        return projectService.getProjectParticipants(projectId);
    }

    @GET
    @Path("/get-all")
    @Produces(MediaType.APPLICATION_JSON)
    public IDataResult<List<ProjectByDomainIdDTO>> getProjectsByDomainId(@Context HttpServletRequest request)
    {
        return projectService.getAllProjects();
    }



}
