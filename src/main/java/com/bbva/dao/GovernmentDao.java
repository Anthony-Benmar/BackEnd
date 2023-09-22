package com.bbva.dao;

import com.bbva.core.results.DataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.GovernmentMapper;
import com.bbva.database.mappers.ProjectMapper;
import com.bbva.dto.government.request.*;
import com.bbva.dto.government.response.FilterSourceResponseDTO;
import com.bbva.dto.government.response.SourceConceptDefDTOResponse;
import com.bbva.dto.government.response.SourceDefinitionDTOResponse;
import com.bbva.dto.project.response.ProjectPortafolioFilterDtoResponse;
import com.bbva.entities.InsertEntity;
import com.bbva.entities.UpdateEntity;
import com.bbva.entities.government.SourceConceptEntity;
import com.bbva.entities.government.SourceDefinitionEntity;
import com.bbva.entities.project.ProjectPortafolioFilterEntity;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionException;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GovernmentDao {

    private static final Logger log = Logger.getLogger(GovernmentDao.class.getName());

    public FilterSourceResponseDTO filterSource(FilterSourceRequestDTO dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<SourceDefinitionDTOResponse> lista;

        Integer recordsCount = 0;
        Integer pagesAmount = 0;

        FilterSourceResponseDTO response = new FilterSourceResponseDTO();

        try (SqlSession session = sqlSessionFactory.openSession()) {
            GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
            lista = mapper.sourceFilter(
                    dto.getPage(),
                    dto.getRecords_amount(),
                    dto.getProjectId(),
                    dto.getDomainType()
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

    public SourceConceptDefDTOResponse getSourceById(int sourceId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        SourceConceptDefDTOResponse sourceConceptDef = null;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
            SourceDefinitionEntity sourceById = mapper.getSourceById(sourceId);
            sourceConceptDef = new SourceConceptDefDTOResponse(
                    sourceById.getUc_source_id(),
                    sourceById.getUse_case_id(),
                    sourceById.getUc_source_name(),
                    sourceById.getUc_source_desc(),
                    sourceById.getUc_source_type(),
                    sourceById.getElement_name(),
                    sourceById.getUc_frequency_type(),
                    sourceById.getDepth_month_number(),
                    sourceById.getAns_desc(),
                    sourceById.getPriority_number(),
                    sourceById.getProject_id(),
                    sourceById.getProject_name(),
                    sourceById.getPortafolio_code(),
                    sourceById.getSystem_owner_id(),
                    null
            );

            List<SourceConceptEntity> concepts = mapper.listSourceConcepts(sourceId);
            sourceConceptDef.setSourceConceptEntityList(concepts);

        }catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return sourceConceptDef;
    }

    public List<SourceConceptEntity> listSourceConceptEntity(int sourceId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<SourceConceptEntity> concepts =null;

        try (SqlSession session = sqlSessionFactory.openSession()) {
            GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
            concepts = mapper.listSourceConcepts(sourceId);
        }catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return concepts;
    }

    public DataResult<InsertEntity> insertSourceDef(InsertSourceRequestDTO item) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();

            try (SqlSession session = sqlSessionFactory.openSession()) {
                GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
                InsertEntity insert_entity = mapper.insertSourceDef(item);
                session.commit();
                return new SuccessDataResult(insert_entity);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(item,"500", e.getMessage());
        }
    }

    public DataResult<InsertEntity> insertConcept(InsertConceptRequestDTO item) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();

            try (SqlSession session = sqlSessionFactory.openSession()) {
                GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
                InsertEntity insert_entity = mapper.insertConcept(item);
                session.commit();
                return new SuccessDataResult(insert_entity);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(item, "500", e.getMessage());
        }
    }

    public DataResult<UpdateEntity> updateSourceDef(UpdateSourceRequestDTO item) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {                GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
                UpdateEntity update_entity = mapper.updateSourceDef(item);
                session.commit();
                return new SuccessDataResult(update_entity);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(item, "500", e.getMessage());
        }
    }

    public DataResult<SourceConceptEntity> updateConcept(UpdateConceptRequestDTO item) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
                UpdateEntity update_entity = mapper.updateConcept(item);
                session.commit();
                return new SuccessDataResult(update_entity);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(item, "500", e.getMessage());
        }
    }

    public DataResult<SourceConceptEntity> deleteConcept(int uc_data_id) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
                mapper.deleteConcept(uc_data_id);
                session.commit();
                return new SuccessDataResult(uc_data_id);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, "500", e.getMessage());
        }
    }

    public DataResult<SourceConceptEntity> deleteSource(int uc_source_id) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
                mapper.deleteSource(uc_source_id);
                session.commit();
                return new SuccessDataResult(uc_source_id);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, "500", e.getMessage());
        }
    }

    public DataResult deleteMapDependency(int dependencyId) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
                mapper.deleteMapDependency(dependencyId);
                session.commit();
                return new SuccessDataResult(dependencyId);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, "500", e.getMessage());
        }
    }
}
