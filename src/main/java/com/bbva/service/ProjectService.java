package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.*;
import com.bbva.dto.feature.response.featureDtoResponse;
import com.bbva.dto.map_dependency.response.MapDependencyListByProjectResponse;
import com.bbva.dto.project.request.*;
import com.bbva.dto.project.response.*;
import com.bbva.entities.User;
import com.bbva.entities.common.PeriodPEntity;
import com.bbva.entities.map_dependecy.MapDependencyEntity;
import com.bbva.entities.project.ProjectPortafolioEntity;
import com.bbva.entities.project.ProjectStatusEntity;
import com.bbva.entities.use_case_definition.UseCaseDefinitionEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProjectService {
    private final ProjectDao projectDao = new ProjectDao();
    private final UserDao userDao = new UserDao();
    private final MapDependencyDao mapDependencyDao = new MapDependencyDao();
    private final UseCaseDefinitionDao caseDefinitionDao = new UseCaseDefinitionDao();
    private static final Logger log = Logger.getLogger(ProjectService.class.getName());
    private static final String DELETE_PROYECT = "No se pudo eliminar proyecto";

    public IDataResult<ProjectFilterByNameOrSdatoolDtoResponse> filter(ProjectFilterByNameOrSdatoolDtoRequest dto) {
        var result = projectDao.filter(dto);
        return new SuccessDataResult<>(result, "Succesfull");
    }

    public IDataResult<ProjectPortafolioSelectResponse> selectProject(int projectId) {
        var result = new ProjectPortafolioSelectResponse();
        var project = projectDao.projectById(projectId);
        if (project != null){
            var arrayUserId = new int[] {project.getProductOwnerId(), project.getSponsorOwnerId()};
            var dataUser = userDao.findByUserId(arrayUserId);
            if (dataUser!=null){
                var productOwner = dataUser.stream()
                        .filter(f->f.userId.equals(project.getProductOwnerId()))
                        .findFirst().orElse(new User());
                var sponsorOwner = dataUser.stream()
                        .filter(f->f.userId.equals(project.getSponsorOwnerId()))
                        .findFirst().orElse(new User());
                result.setProductOwnerCode(productOwner.employeeId);
                result.setProductOwnerName(productOwner.fullName);
                result.setSponsorOwnerCode(sponsorOwner.employeeId);
                result.setSponsorName(sponsorOwner.fullName);
            }
        }
        List<MapDependencyListByProjectResponse> listMapDependency = mapDependencyDao.listMapDependencyByProjectId(projectId);

        result.setProjectId(project.getProjectId());
        result.setProjectName(project.getProjectName());
        result.setProjectDesc(project.getProjectDesc());
        result.setSdatoolId(project.getSdatoolId());
        result.setPortafolioCode(project.getPortafolioCode());
        result.setProjectType(project.getProjectType());
        result.setSponsorId(project.getSponsorOwnerId());
        result.setProductOwnerId(project.getProductOwnerId());
        result.setRegulatoryProjectBoolean(project.getRegulatoryProjectBoolean());
        result.setProjectDomainType(project.getProjectDomainType());
        result.setRuleAssociatedLink(project.getRuleAssociatedLink());
        result.setPeriodId(project.getPeriodId());
        result.setStatusType(project.getStatusType());
        result.setProcess(listMapDependency);

        return new SuccessDataResult<>(result);
    }

    public IDataResult<List<ProjectListForSelectDtoResponse>> listForSelect(PeriodPEntity period) {
        var result = projectDao.listForSelect(period);
        return new SuccessDataResult<>(result);
    }

    public IDataResult<List<ProjectCatalogDtoResponse>> listProjectCatalog(String sdatoolId) {
        var result = projectDao.listProjectCatalog(sdatoolId);
        return new SuccessDataResult<>(result);
    }

    public IDataResult<List<ProjectListForSelectDtoResponse>> listForSelect() {
        var result = projectDao.listForSelect();
        return new SuccessDataResult<>(result, "Succesfull");
    }
    
    public IDataResult<ProjectPortafolioFilterDtoResponse> portafolioFilter(ProjectPortafolioFilterDTORequest dto) {
        var result = projectDao.portafolioFilter(dto);
        return new SuccessDataResult<>(result);
    }

    public IDataResult<ProjectPortafolioFilterDtoResponse> insertProject(ProjectPortafolioDTORequest dto) {
        try {
            ProjectPortafolioEntity project = new ProjectPortafolioEntity(
                    dto.getProjectId(),
                    dto.getProjectName(),
                    dto.getProjectDesc(),
                    dto.getSdatoolId(),
                    dto.getPortafolioCode(),
                    dto.getProjectType(),
                    dto.getSponsorId(),
                    dto.getProductOwnerId(),
                    dto.getRegulatoryProjectBoolean(),
                    dto.getProjectDomainType(),
                    dto.getRuleAssociatedLink(),
                    dto.getPeriodId());
            project.setStatusType(dto.getStatusType());

            var resultProject = projectDao.insertProject(project);

            if (resultProject.success) {
                int projectId = project.getProjectId();
                UseCaseDefinitionEntity caseDefinition = new UseCaseDefinitionEntity(0, projectId, null, null);
                caseDefinition.setStatusType(1);

                var resultUseCase = caseDefinitionDao.insert(caseDefinition);

                if (resultUseCase.success) {
                    dto.getProcess().forEach(x -> {
                        MapDependencyEntity mapDependency = new MapDependencyEntity(
                                0,
                                caseDefinition.getUseCaseId(),
                                x.getKeyDataProcessType(),
                                x.getProcessName(),
                                x.getSloOwnerId(),
                                x.getArisCode(),
                                x.getDependencyMapLink()
                        );
                        mapDependency.setStatusType(1);
                        mapDependencyDao.insert(mapDependency);
                    });
                } else {
                    return new ErrorDataResult<>(resultUseCase.message);
                }
                return new SuccessDataResult<>(null);
            } else {
                return new ErrorDataResult<>(resultProject.message);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(e.getMessage());
        }
    }

    public IDataResult<ProjectPortafolioFilterDtoResponse> updateProject(ProjectPortafolioDTORequest dto)
            throws ExecutionException, InterruptedException {

        try {
            if (dto.getProjectId().equals(0)) {
                return new ErrorDataResult<>(null,"500", "ProjectId must to be not null");
            }

            ProjectPortafolioEntity project = new ProjectPortafolioEntity(
                    dto.getProjectId(),
                    dto.getProjectName(),
                    dto.getProjectDesc(),
                    dto.getSdatoolId(),
                    dto.getPortafolioCode(),
                    dto.getProjectType(),
                    dto.getSponsorId(),
                    dto.getProductOwnerId(),
                    dto.getRegulatoryProjectBoolean(),
                    dto.getProjectDomainType(),
                    dto.getRuleAssociatedLink(),
                    dto.getPeriodId());
            project.setStatusType(dto.getStatusType());

            var resultProject = projectDao.updateProject(project);

            if (resultProject.success) {
                return new SuccessDataResult(dto);
            } else {
                return new ErrorDataResult<>(null,"500", resultProject.message);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null,"500", e.getMessage());
        }
    }
    public IDataResult<ProjectPortafolioFilterDtoResponse> deleteProject(int projectId)
            throws ExecutionException, InterruptedException {

        try {
            var res = projectDao.deleteProject(projectId);

            if (res.success) {
                return new SuccessDataResult(projectId);
            } else {
                return new ErrorDataResult<>(null,"500", DELETE_PROYECT);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null,"500", DELETE_PROYECT);
        }
    }

    public IDataResult<Integer> deleteProjectInfo(int projectId) {
        try {
            var res = projectDao.deleteProjectInfo(projectId);
            if (!res.success)
                return new ErrorDataResult<>(projectId, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, DELETE_PROYECT);

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(projectId, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, DELETE_PROYECT);
        }
        return new SuccessDataResult<>(projectId);
    }

    public IDataResult<ProjectInfoDTO> updateProjectInfo(ProjectInfoDTO dto) {
        try {
            if (dto.getProjectId().equals(0))
                return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "ProjectId must to be not null");

            projectDao.updateProjectInfo(dto);

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null,HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return new SuccessDataResult<>(dto);
    }

    public IDataResult<List<MapDependencyListByProjectResponse>> getProcessByProjectId(int projectId) {
        try {
            var result = mapDependencyDao.listMapDependencyByProjectId(projectId);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null,"500", "No se pudieron obtener los procesos.");
        }
    }

    public IDataResult<InsertProjectDocumentDTO> insertProjectDocument(InsertProjectDocumentDTO dto) {
        var result = projectDao.insertProjectDocument(dto);
        return new SuccessDataResult<>(result);
    }

    public IDataResult<InsertProjectParticipantDTO> insertProjectParticipant(InsertProjectParticipantDTO dto) {
        var result = projectDao.insertProjectParticipant(dto);
        return new SuccessDataResult<>(result);
    }

    public IDataResult<InsertProjectInfoDTORequest> insertProjectInfo(InsertProjectInfoDTORequest dto)  throws Exception{
        var result = projectDao.insertProjectInfo(dto);
        return new SuccessDataResult<>(result);
    }

    public IDataResult<ProjectInfoFilterResponse> projectInfoFilter(ProjectInfoFilterRequest dto) {
        var result = projectDao.projectInfoFilter(dto);
        return new SuccessDataResult<>(result);
    }
    public IDataResult<ProjectInfoFilterByDomainDtoResponse> projectInfoFilterByDomain(ProjectInfoFilterByDomainDtoRequest dto) {
        var result = projectDao.projectInfoFilterByDomain(dto);
        return new SuccessDataResult<>(result);
    }

    public IDataResult<ProjectInfoFilterAllByDomainDtoResponse> projectInfoFilterAllByDomain(ProjectInfoFilterByDomainDtoRequest dto) {
        var result = projectDao.projectInfoFilterAllByDomain(dto);
        return new SuccessDataResult<>(result);
    }

    public IDataResult<Integer> deleteDocument(int projectId, int documentId, String updateAuditUser) {
        try {
            var res = projectDao.deleteDocument(projectId, documentId, updateAuditUser);
            if (!res.success)
                return new ErrorDataResult<>(projectId, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, DELETE_PROYECT);

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(projectId, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, DELETE_PROYECT);
        }
        return new SuccessDataResult<>(projectId);
    }

    public IDataResult<InsertProjectDocumentDTO> updateDocument(InsertProjectDocumentDTO dto) {
        try {
            if (dto.getProjectId().equals(0) | dto.getDocumentId().equals(0))
                return new ErrorDataResult<>(null,HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "projectId or documentId must to be not null or 0");

            projectDao.updateDocument(dto);

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return new SuccessDataResult<>(dto);
    }

    public IDataResult<List<InsertProjectDocumentDTO>> getDocument(int projectId, int documentId) {
        try {
            var result = projectDao.getDocument(projectId, documentId);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<Integer> deleteParticipantProject(int projectId, int participantId, String updateAuditUser) {

        try {
            var res = projectDao.deleteParticipantProject(projectId, participantId, updateAuditUser);
            if (!res.success)
                return new ErrorDataResult<>(projectId, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, DELETE_PROYECT);

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(projectId, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, DELETE_PROYECT);
        }
        return new SuccessDataResult<>(projectId);
    }

    public IDataResult<InsertProjectParticipantDTO> updateParticipant(InsertProjectParticipantDTO dto) {

        try {
            if (dto.getProjectId().equals(0) | dto.getProjectParticipantId().equals(0))
                return new ErrorDataResult<>(null,HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "projectId or projectParticipantId must to be not null or 0");

            projectDao.updateParticipant(dto);

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return new SuccessDataResult<>(dto);
    }

    public IDataResult<List<InsertProjectParticipantDTO>> getProjectParticipants(int projectId) {

        try {
            var result = projectDao.getProjectParticipants(projectId);
            return new SuccessDataResult(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(projectId, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<List<SelectCalendarDTO>> getCalendar() {
        try {
            var result = projectDao.getAllCalendar();
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public boolean sdatoolIdExists(String sdatoolId) {
        return projectDao.sdatoolIdExists(sdatoolId);
    }

    public boolean sdatoolIdExistsUpdate(String sdatoolId, int projectId) {
        try {
            return projectDao.sdatoolIdExistsUpdate(sdatoolId, projectId);
        }catch (Exception e){
            log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    public IDataResult<List<ProjectByDomainIdDTO>> getProjectsByDomainId(String domainId) {
        try {
            var result = projectDao.getProjectsByDomainId(domainId);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<List<ProjectByDomainIdDTO>> getAllProjects() {
        try {
            var result = projectDao.getAllProjects();
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<List<featureDtoResponse>> getFeaturesByProject(String sdatoolId) {
        try {
            var listFeaturesEntity = projectDao.getFeaturesByProject(sdatoolId, "");
            var listFeatureDto = listFeaturesEntity.stream()
                    .map(s -> new featureDtoResponse(s.featureId, s.featureKey, s.featureName, s.sdatoolId, s.teamBacklog,
                            s.jiraProjectId, s.jiraProjectName))
                    .collect(Collectors.toList());
            return new SuccessDataResult<>(listFeatureDto);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<List<ProjectStatusEntity>> getProjectStatusTracking(int projectId) {
        try {
            List<ProjectStatusEntity> result = projectDao.getProjectStatusTracking(projectId);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}