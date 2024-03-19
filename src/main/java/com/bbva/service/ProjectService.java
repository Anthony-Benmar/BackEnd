package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.MapDependencyDao;
import com.bbva.dao.ProjectDao;
import com.bbva.dao.UseCaseDefinitionDao;
import com.bbva.dao.UserDao;
import com.bbva.dto.map_dependency.response.MapDependencyListByProjectResponse;
import com.bbva.dto.project.request.*;
import com.bbva.dto.project.response.ProjectListForSelectDtoResponse;
import com.bbva.dto.project.response.ProjectFilterByNameOrSdatoolDtoResponse;
import com.bbva.dto.project.response.ProjectPortafolioFilterDtoResponse;
import com.bbva.dto.project.response.ProjectPortafolioSelectResponse;
import com.bbva.entities.User;
import com.bbva.entities.common.PeriodPEntity;
import com.bbva.entities.map_dependecy.MapDependencyEntity;
import com.bbva.entities.project.ProjectPortafolioEntity;
import com.bbva.entities.use_case_definition.UseCaseDefinitionEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectService {
    private final ProjectDao projectDao = new ProjectDao();
    private static final Logger log = Logger.getLogger(ProjectService.class.getName());

    public IDataResult<ProjectFilterByNameOrSdatoolDtoResponse> filter(ProjectFilterByNameOrSdatoolDtoRequest dto) {
        var result = projectDao.filter(dto);
        return new SuccessDataResult(result, "Succesfull");
    }

    public IDataResult<ProjectPortafolioSelectResponse> selectProject(int projectId) {
        var result = new ProjectPortafolioSelectResponse();
        var mapDependencyDao = new MapDependencyDao();
        var userDao = new UserDao();
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

        return new SuccessDataResult(result);
    }

    public IDataResult<List<ProjectListForSelectDtoResponse>> listForSelect(PeriodPEntity period) {
        var result = projectDao.listForSelect(period);
        return new SuccessDataResult(result);
    }

    public IDataResult<List<ProjectListForSelectDtoResponse>> listForSelect() {
        var result = projectDao.listForSelect();
        return new SuccessDataResult(result, "Succesfull");
    }
    
    public IDataResult<ProjectPortafolioFilterDtoResponse> portafolioFilter(ProjectPortafolioFilterDTORequest dto) {
        var result = projectDao.portafolioFilter(dto);
        return new SuccessDataResult(result);
    }

    public IDataResult<ProjectPortafolioFilterDtoResponse> insertProject(ProjectPortafolioDTORequest dto)
            throws ExecutionException, InterruptedException {
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
                UseCaseDefinitionDao caseDefinitionDao = new UseCaseDefinitionDao();
                UseCaseDefinitionEntity caseDefinition = new UseCaseDefinitionEntity(0, projectId, null, null);
                caseDefinition.setStatusType(1);

                var resultUseCase = caseDefinitionDao.insert(caseDefinition);

                if (resultUseCase.success) {
                    MapDependencyDao mapDependencyDao = new MapDependencyDao();

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
                    return new ErrorDataResult(resultUseCase.message);
                }
                return new SuccessDataResult(null);
            } else {
                return new ErrorDataResult(resultProject.message);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult( e.getMessage());
        }
    }

    public IDataResult<ProjectPortafolioFilterDtoResponse> updateProject(ProjectPortafolioDTORequest dto)
            throws ExecutionException, InterruptedException {

        try {
            if (dto.getProjectId().equals(0)) {
                return new ErrorDataResult(null,"500", "ProjectId must to be not null");
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
                return new ErrorDataResult(null,"500", resultProject.message);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null,"500", e.getMessage());
        }
    }
    public IDataResult<ProjectPortafolioFilterDtoResponse> deleteProject(int projectId)
            throws ExecutionException, InterruptedException {

        try {
            var res = projectDao.deleteProject(projectId);

            if (res.success) {
                return new SuccessDataResult(projectId);
            } else {
                return new ErrorDataResult(projectId,"500", "No se pudo eliminar proyecto");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(projectId,"500", "No se pudo eliminar proyecto");
        }
    }

    public IDataResult<List<MapDependencyListByProjectResponse>> getProcessByProjectId(int projectId)
            throws ExecutionException, InterruptedException {

        try {
            var mapDependencyDao = new MapDependencyDao();
            var result = mapDependencyDao.listMapDependencyByProjectId(projectId);
            return new SuccessDataResult(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(projectId,"500", "No se pudieron obtener los procesos.");
        }
    }

    public IDataResult<InsertProjectDocumentDTO> insertProjectDocument(InsertProjectDocumentDTO dto) {
        var result = projectDao.insertProjectDocument(dto);
        return new SuccessDataResult(result);
    }

    public IDataResult<InsertProjectParticipantDTO> insertProjectParticipant(InsertProjectParticipantDTO dto) {
        var result = projectDao.insertProjectParticipant(dto);
        return new SuccessDataResult(result);
    }
}