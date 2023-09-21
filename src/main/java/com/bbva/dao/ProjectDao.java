package com.bbva.dao;

import com.bbva.core.results.DataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.ProjectMapper;
import com.bbva.dto.project.request.ProjectFilterByNameOrSdatoolDtoRequest;
import com.bbva.dto.project.request.ProjectPortafolioFilterDTORequest;
import com.bbva.dto.project.response.*;
import com.bbva.entities.common.PeriodPEntity;
import com.bbva.entities.common.ProjectByPeriodEntity;
import com.bbva.entities.project.ProjectFilterEntity;
import com.bbva.entities.project.ProjectPortafolioEntity;
import com.bbva.entities.project.ProjectPortafolioFilterEntity;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectDao {
    private static final Logger log = Logger.getLogger(ProjectDao.class.getName());

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
        }catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
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
            lista = mapper.portafolioFilter(1, 0, dto.getProjectName(), dto.getDomainType(), dto.getIsRegulatory());
        }
        log.info(JSONUtils.convertFromObjectToJson(response.getData()));
        recordsCount = (lista.size() > 0) ? lista.get(0).getRecordsCount() : 0;
        // pagesAmount = dto.getRecords_amount()>0 ? (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue()) : 1;

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
            return new ErrorDataResult(null, "500",e.getMessage());
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
            return new ErrorDataResult(null, "500",e.getMessage());
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
            return new ErrorDataResult(null, "500",e.getMessage());
        }
    }


}