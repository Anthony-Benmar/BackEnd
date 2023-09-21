package com.bbva.dao;

import com.bbva.core.results.DataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.GovernmentMapper;
import com.bbva.dto.government.response.SourceConceptDefDTOResponse;
import com.bbva.dto.government.response.SourceDefinitionDTOResponse;
import com.bbva.entities.government.SourceConceptEntity;
import com.bbva.entities.government.SourceDefinitionEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GovernmentDao {

    private static final Logger log = Logger.getLogger(GovernmentDao.class.getName());

    public List<SourceDefinitionDTOResponse> listSourceDefinition(int projectId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<SourceDefinitionDTOResponse> sourcesDefinition = new ArrayList<>();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
            List<SourceDefinitionEntity> sources = mapper.listSources(projectId);

            sources.forEach(item -> {
                SourceDefinitionDTOResponse object = new SourceDefinitionDTOResponse(
                    item.getUc_source_id(),
                    item.getUse_case_id(),
                    item.getUc_source_name(),
                    item.getUc_source_desc(),
                    item.getUc_source_type(),
                    item.getElement_name(),
                    item.getUc_frequency_type(),
                    item.getDepth_month_number(),
                    item.getAns_desc(),
                    item.getPriority_number()
                );
                sourcesDefinition.add(object);
            });

        }catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return sourcesDefinition;
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

    public DataResult<SourceDefinitionEntity> insertSourceDef(SourceDefinitionEntity item) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
                mapper.insertSourceDef(item);
                session.commit();
                return new SuccessDataResult(item);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(item,"500", e.getMessage());
        }
    }

    public DataResult<SourceConceptEntity> insertConcept(SourceConceptEntity item) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
                mapper.insertConcept(item);
                session.commit();
                return new SuccessDataResult(item);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(item, "500", e.getMessage());
        }
    }

    public DataResult<SourceDefinitionEntity> updateSourceDef(SourceDefinitionEntity item) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
                mapper.updateSourceDef(item);
                session.commit();
                return new SuccessDataResult(item);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(item, "500", e.getMessage());
        }
    }

    public DataResult<SourceConceptEntity> updateConcept(SourceConceptEntity item) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                GovernmentMapper mapper = session.getMapper(GovernmentMapper.class);
                mapper.updateConcept(item);
                session.commit();
                return new SuccessDataResult(item);
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
