package com.bbva.dao;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.HandledException;
import com.bbva.core.results.DataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.ProjectMapper;
import com.bbva.dto.catalog.request.ListByCatalogIdDtoRequest;
import com.bbva.dto.catalog.response.ListByCatalogIdDtoResponse;
import com.bbva.dto.catalog.response.ListByCatalogIdGroupByCatalogGroupByElementDtoResponse;
import com.bbva.dto.project.request.*;
import com.bbva.dto.project.response.*;
import com.bbva.dto.project.request.ProjectInfoFilterRequest;
import com.bbva.dto.project.response.ProjectInfoSelectResponse;
import com.bbva.entities.InsertEntity;
import com.bbva.entities.common.PeriodPEntity;
import com.bbva.entities.common.ProjectByPeriodEntity;
import com.bbva.entities.common.ProjectEntity;
import com.bbva.entities.feature.JiraFeatureEntity;
import com.bbva.entities.project.*;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.bbva.util.types.FechaUtil.convertDateToString;

public class ProjectDao {
    private static final Logger log = Logger.getLogger(ProjectDao.class.getName());
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private CatalogDao catalogDao = new CatalogDao();
    public ProjectFilterByNameOrSdatoolDtoResponse filter(ProjectFilterByNameOrSdatoolDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<ProjectFilterEntity> lista;

        Integer recordsCount = 0;
        Integer pagesAmount = 0;

        ProjectFilterByNameOrSdatoolDtoResponse response = new ProjectFilterByNameOrSdatoolDtoResponse();
        List<ProjectFilterByNameOrSdatoolListDtoResponse> projectFilterLista = new ArrayList<>();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            lista = mapper.filter(
                    dto.getPage(),
                    dto.getRecords_amount(),
                    dto.getSdatool(),
                    dto.getName()
            );
            lista.forEach(entity -> {
                ProjectFilterByNameOrSdatoolListDtoResponse projectFilterResponse = new ProjectFilterByNameOrSdatoolListDtoResponse();
                projectFilterResponse.setId(Integer.parseInt(entity.getProject_id()));
                projectFilterResponse.setName(entity.getProject_name());
                projectFilterResponse.setSdatool(entity.getSdatool_id());
                projectFilterResponse.setState(Integer.parseInt(entity.getStatus_type()));
                projectFilterLista.add(projectFilterResponse);
            });
        }
        log.info(JSONUtils.convertFromObjectToJson(response.getData()));
        recordsCount = (lista.size() > 0) ? lista.get(0).getRecords_count() : 0;
        pagesAmount = (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue());

        response.setCount(recordsCount);
        response.setPages_amount(pagesAmount);
        response.setData(projectFilterLista);
        return response;
    }

    public List<ProjectListForSelectDtoResponse> listForSelect(PeriodPEntity period) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            List<ProjectListForSelectDtoResponse> projectList = new ArrayList<>();
            List<ProjectByPeriodEntity> projectEntityList = mapper.listProjectsByPeriod(period.getPeriod_id());
            projectEntityList.forEach(projectBPeriodEntity -> {
                ProjectListForSelectDtoResponse objectProject = new ProjectListForSelectDtoResponse();
                objectProject.setId(Integer.parseInt(projectBPeriodEntity.getProject_id()));
                objectProject.setSdatool(projectBPeriodEntity.getSdatool_id());
                objectProject.setName(projectBPeriodEntity.getProject_name());
                projectList.add(objectProject);
            });
            return projectList;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public List<ProjectCatalogDtoResponse> listProjectCatalog(String sdatoolId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            List<ProjectCatalogDtoResponse> projectCatalogList = new ArrayList<>();
            List<ProjectCatalogEntity> projectEntityList = mapper.listProjectCatalog(sdatoolId);

            projectEntityList.forEach(projectCatalogEntity -> {
                ProjectCatalogDtoResponse objectProject = new ProjectCatalogDtoResponse();
                objectProject.setSdatoolId(projectCatalogEntity.getSdatoolId());
                objectProject.setProjectName(projectCatalogEntity.getProjectName());
                objectProject.setSn1(projectCatalogEntity.getSn1());
                objectProject.setSn1Desc(projectCatalogEntity.getSn1Desc());
                objectProject.setSn2(projectCatalogEntity.getSn2());
                objectProject.setSn2ProjectId(projectCatalogEntity.getSn2ProjectId());
                objectProject.setCodigo5Digitos(projectCatalogEntity.getCodigo5Digitos());
                projectCatalogList.add(objectProject);
            });

            return projectCatalogList;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public List<ProjectListForSelectDtoResponse> listForSelect() {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            List<ProjectListForSelectDtoResponse> projectList = new ArrayList<>();
            List<ProjectEntity> projectEntityList = mapper.listforselect();
            projectEntityList.forEach(projectEntity -> {
                ProjectListForSelectDtoResponse objectProject = new ProjectListForSelectDtoResponse();
                objectProject.setId(Integer.parseInt(projectEntity.getProject_id()));
                objectProject.setSdatool(projectEntity.getSdatool_id());
                objectProject.setName(projectEntity.getProject_name());
                projectList.add(objectProject);
            });
            return projectList;
        }
    }

    public ProjectPortafolioFilterDtoResponse portafolioFilter(ProjectPortafolioFilterDTORequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<ProjectPortafolioFilterEntity> lista;

        Integer recordsCount = 0;
        Integer pagesAmount = 0;

        ProjectPortafolioFilterDtoResponse response = new ProjectPortafolioFilterDtoResponse();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            lista = mapper.portafolioFilter(
                    dto.getPage(),
                    dto.getRecords_amount(),
                    dto.getProjectId(),
                    dto.getDomainType(),
                    dto.getIsRegulatory(),
                    dto.getWithSources()
            );
        }
        log.info(JSONUtils.convertFromObjectToJson(response.getData()));
        recordsCount = (lista.size() > 0) ? lista.get(0).getRecordsCount() : 0;
        pagesAmount = dto.getRecords_amount() > 0 ? (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue()) : 1;

        response.setCount(recordsCount);
        response.setPages_amount(pagesAmount);
        response.setData(lista);
        return response;
    }

    public ProjectPortafolioEntity projectById(int projectId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        ProjectPortafolioEntity project;

        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            project = mapper.getProjectById(projectId);
        }
        return project;
    }

    public ProjectEntity findById(int projectId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();

        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            return mapper.findById(projectId);
        }
    }

    public DataResult<ProjectPortafolioEntity> insertProject(ProjectPortafolioEntity item) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
                projectMapper.insertProject(item);
                session.commit();
                return new SuccessDataResult(item);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, "500", e.getMessage());
        }
    }

    public DataResult<ProjectPortafolioEntity> updateProject(ProjectPortafolioEntity item) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
                projectMapper.updateProject(item);
                session.commit();
                return new SuccessDataResult(item);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, "500", e.getMessage());
        }
    }

    public DataResult<ProjectPortafolioEntity> deleteProject(int projectId) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
                projectMapper.deleteProject(projectId);
                session.commit();
                return new SuccessDataResult(projectId);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, "500", e.getMessage());
        }
    }

    public DataResult<ProjectPortafolioEntity> deleteProjectInfo(int projectId) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
                projectMapper.deleteProjectInfo(projectId);
                session.commit();
                return new SuccessDataResult(projectId);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public boolean updateProjectInfo(ProjectInfoDTO dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
            projectMapper.updateProjectInfo(dto);
            session.commit();
            return true;
        }
    }

    public InsertProjectDocumentDTO insertProjectDocument(InsertProjectDocumentDTO dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
            InsertEntity result = projectMapper.insertProjectDocument(dto);
            session.commit();
            dto.setDocumentId(result.getLast_insert_id());
            return dto;
        }
    }

    public boolean sdatoolIdExists(String sdatoolId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            int count = mapper.countBySdatoolId(sdatoolId);
            return count > 0;
        }
    }

    public boolean sdatoolIdExistsUpdate(String sdatoolId, int projectId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            int count = mapper.countBySdatoolIdUpdate(sdatoolId, projectId);
            return count > 0;
        }
    }


    public InsertProjectParticipantDTO insertProjectParticipant(InsertProjectParticipantDTO dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
            InsertEntity result = projectMapper.insertProjectParticipant(dto);
            session.commit();
            dto.setProjectParticipantId(result.getLast_insert_id());
            return dto;
        }
    }

    public InsertProjectInfoDTORequest insertProjectInfo(InsertProjectInfoDTORequest dto)
            throws Exception
    {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
            try {
                var result = projectMapper.insertProjectInfo(dto);
                dto.setProjectId(result.getLast_insert_id());
                if (dto.participants != null && dto.participants.size() > 0) {
                    dto.participants.stream().forEach(participant -> {
                        participant.setProjectId(dto.projectId);
                        participant.setCreateAuditUser(dto.createAuditUser);
                    });
                    projectMapper.insertProjectParticipants(dto.participants);
                }
                if (dto.documents != null && dto.documents.size() > 0) {
                    dto.documents.stream().forEach(document -> {
                        document.setProjectId(dto.projectId);
                        document.setCreateAuditUser(dto.createAuditUser);
                    });
                    projectMapper.insertProjectDocuments(dto.documents);
                }
                session.commit();
            } catch (Exception e) {
                session.rollback();
                log.log(Level.SEVERE, e.getMessage(), e);
                throw new HandledException("500", "No se pudo registrar el proyecto");
            }
        }
        return dto;
    }

    public ProjectInfoFilterResponse projectInfoFilter(ProjectInfoFilterRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<ProjectInfoSelectResponse> lista;

        Integer recordsCount = 0;
        Integer pagesAmount = 0;

        var response = new ProjectInfoFilterResponse();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
            lista = projectMapper.projectInfoFilter(dto);
        }

        recordsCount = (lista.size() > 0) ? (int) lista.stream().count() : 0;
        pagesAmount = dto.getRecords_amount() > 0 ? (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue()) : 1;

        if (dto.records_amount > 0) {
            lista = lista.stream()
                    .skip((long) dto.records_amount * (dto.page - 1))
                    .limit(dto.records_amount)
                    .collect(Collectors.toList());
        }

        for (ProjectInfoSelectResponse item : lista) {
            if (item.getCreateAuditDate() != null) {
                item.setCreateAuditDate_S(convertDateToString(item.getCreateAuditDate(), DATE_FORMAT));
            }
            if (item.getUpdateAuditDate() != null) {
                item.setUpdateAuditDate_S(convertDateToString(item.getUpdateAuditDate(), DATE_FORMAT));
            }

        }

        response.setCount(recordsCount);
        response.setPages_amount(pagesAmount);
        response.setData(lista);
        log.info(JSONUtils.convertFromObjectToJson(response.getData()));

        return response;
    }

    public ProjectInfoFilterByDomainDtoResponse projectInfoFilterByDomain(ProjectInfoFilterByDomainDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<ProjectInfoSelectByDomainDtoResponse> lista;

        Integer recordsCount = 0;
        Integer pagesAmount = 0;

        var response = new ProjectInfoFilterByDomainDtoResponse();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
            lista = projectMapper.projectInfoFilterByDomain(dto.projectId, dto.domainId);
        }

        recordsCount = (lista.size() > 0) ? (int) lista.stream().count() : 0;
        pagesAmount = dto.getRecords_amount() > 0 ? (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue()) : 1;

        if (dto.records_amount > 0) {
            lista = lista.stream()
                    .skip(dto.records_amount * (dto.page - 1))
                    .limit(dto.records_amount)
                    .collect(Collectors.toList());
        }
        response.setCount(recordsCount);
        response.setPages_amount(pagesAmount);
        response.setData(lista);
        log.info(JSONUtils.convertFromObjectToJson(response.getData()));

        return response;
    }

    public ProjectInfoFilterAllByDomainDtoResponse projectInfoFilterAllByDomain(ProjectInfoFilterByDomainDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<ProjectInfoSelectAllByDomainDtoResponse> lista;

        Integer recordsCount = 0;
        Integer pagesAmount = 0;

        var response = new ProjectInfoFilterAllByDomainDtoResponse();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
            lista = projectMapper.projectInfoFilterAllByDomain(dto.projectId, dto.domainId);
        }

        recordsCount = (lista.size() > 0) ? (int) lista.stream().count() : 0;
        pagesAmount = dto.getRecords_amount() > 0 ? (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue()) : 1;

        if (dto.records_amount > 0) {
            lista = lista.stream()
                    .skip(dto.records_amount * (dto.page - 1))
                    .limit(dto.records_amount)
                    .collect(Collectors.toList());
        }

        for (ProjectInfoSelectAllByDomainDtoResponse item : lista) {
            if (item.getCreateAuditDate() != null) {
                item.setCreateAuditDate_S(convertDateToString(item.getCreateAuditDate(), DATE_FORMAT));
            }
            if (item.getUpdateAuditDate() != null) {
                item.setUpdateAuditDate_S(convertDateToString(item.getUpdateAuditDate(), DATE_FORMAT));
            }

        }

        response.setCount(recordsCount);
        response.setPages_amount(pagesAmount);
        response.setData(lista);
        log.info(JSONUtils.convertFromObjectToJson(response.getData()));

        return response;
    }

    public DataResult<Integer> deleteDocument(int projectId, int documentId, String updateAuditUser) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
                projectMapper.deleteDocument(projectId, documentId, updateAuditUser);
                session.commit();
                return new SuccessDataResult(projectId);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public boolean updateDocument(InsertProjectDocumentDTO dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
            projectMapper.updateDocument(dto);
            session.commit();
            return true;
        }
    }

    public List<InsertProjectDocumentDTO> getDocument(int projectId, int documentId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            List<InsertProjectDocumentDTO> documentList = mapper.getDocument(projectId, documentId);
            return documentList;
        }
    }

    public DataResult<Integer> deleteParticipantProject(int projectId, int participantId, String updateAuditUser) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
                projectMapper.deleteParticipantProject(projectId, participantId, updateAuditUser);
                session.commit();
                return new SuccessDataResult(projectId);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public boolean updateParticipant(InsertProjectParticipantDTO dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
            var result = projectMapper.updateParticipant(dto);
            session.commit();
            return result;
        }
    }

    public List<InsertProjectParticipantDTO> getProjectParticipants(int projectId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            var participantsList = mapper.getProjectParticipants(projectId);
            return participantsList;
        }
    }

    public List<SelectCalendarDTO> getAllCalendar() {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            return mapper.getAllCalendar();
        }
    }

    public List<ProjectByDomainIdDTO> getProjectsByDomainId(String domainId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            return mapper.getProjectsByDomainId(domainId);
        }
    }

    public List<JiraFeatureEntity> getFeaturesByProject(String sdatoolId, String featureKey) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            return mapper.getFeaturesByProject(sdatoolId, featureKey);
        }
    }

    public List<ProjectInfoSelectResponse> listProjects(ProjectInfoFilterRequest dto) {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session
                    .getMapper(ProjectMapper.class)
                    .projectInfoFilter(dto);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<ProjectStatusEntity> getProjectStatusTracking(int projectId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<ProjectStatusEntity> projectStatusesList;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            projectStatusesList = mapper.getProjectStatusTracking(projectId);

            for (ProjectStatusEntity item : projectStatusesList) {
                if (item.getStartDate() != null) {
                    item.setStartDateStr(convertDateToString(item.getStartDate(), DATE_FORMAT));
                }
            }
            return projectStatusesList;
        }
    }
    public List<ProjectRoleDetailEntity> getProjectRoles(int projectId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<ProjectRoleDetailEntity> projectRoleDetailEntityList;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            projectRoleDetailEntityList = mapper.getProjectRoles(projectId);
            return projectRoleDetailEntityList;
        }
    }

    public ProjectDevResponse getProjectDevSU(String email) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        ProjectDevResponse projectDevResponse;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            projectDevResponse = mapper.getProjectDevSU(email);
            return projectDevResponse;
        }
    }

    public ProjectDevResponse getProjectDevPP(String email) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        ProjectDevResponse projectDevResponse;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            projectDevResponse = mapper.getProjectDevPP(email);
            return projectDevResponse;
        }
    }

    public List<ProjectByDomainIdDTO> getAllProjects() {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ProjectMapper mapper = session.getMapper(ProjectMapper.class);
            return mapper.listProjects();
        }
    }

    public List<ProjectValidationParamsDtoResponse> validateInfoProjectByProjectId(int projectId) {
        ListByCatalogIdDtoRequest requestParticipant = new ListByCatalogIdDtoRequest();
        requestParticipant.setCatalog(new int[]{1037});
        ListByCatalogIdDtoResponse listParticipant = catalogDao.getCatalogoByCatalogoId(requestParticipant);

        ListByCatalogIdDtoRequest requestDocuments = new ListByCatalogIdDtoRequest();
        requestDocuments.setCatalog(new int[]{1036});
        ListByCatalogIdDtoResponse listDocuments = catalogDao.getCatalogoByCatalogoId(requestDocuments);

        List<InsertProjectParticipantDTO> projectParticipantDTOS = getProjectParticipants(projectId);
        List<InsertProjectDocumentDTO> documentDTOS = getDocument(projectId, 0);

        List<Integer> idsParticipantsProyect = projectParticipantDTOS.stream()
                .map(InsertProjectParticipantDTO::getProjectRolType)
                .toList();

        List<Integer> idsDocumentsProyect = documentDTOS.stream()
                .map(InsertProjectDocumentDTO::getDocumentType)
                .toList();

        List<ProjectValidationParamsDtoResponse> errors = new ArrayList<>();
        errors.addAll(validate(
                Arrays.asList(1, 5),
                listParticipant.getCatalog().get(0).getElement(),
                idsParticipantsProyect
        ));

        errors.addAll(validate(
                Arrays.asList(1, 2, 3),
                listDocuments.getCatalog().get(0).getElement(),
                idsDocumentsProyect
        ));
        return errors;
    }

    public List<ProjectValidationParamsDtoResponse> validate(
            List<Integer> idsRequired,
            List<ListByCatalogIdGroupByCatalogGroupByElementDtoResponse> catalog,
            List<Integer> idsProyect
    ) {
        Set<Integer> idsPresents = new HashSet<>(idsProyect);
        return idsRequired.stream()
                .filter(id -> !idsPresents.contains(id))
                .flatMap(x ->
                        catalog.stream()
                                .filter(e -> e.getElementId() == x)
                                .findFirst().stream().map(e -> ProjectValidationParamsDtoResponse
                                        .builder()
                                        .type(e.getDescription())
                                        .message("No cuenta con " + e.getDescription())
                                        .build())
                )
                .toList();
    }
}