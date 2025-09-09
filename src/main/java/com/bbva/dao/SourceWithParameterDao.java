package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.SourceWithParameterMapper;
import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.List;
import java.util.logging.Logger;

public class SourceWithParameterDao {
    private static final Logger log = Logger.getLogger(SourceWithParameterDao.class.getName());
    private final SqlSessionFactory sqlSessionFactory;

    public SourceWithParameterDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public List<SourceWithParameterDataDtoResponse> getSourceWithParameter(SourceWithParameterPaginationDtoRequest dto){
        List<SourceWithParameterDataDtoResponse> result = null;
        try(SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            result = mapper.getSourcesWithParameterWithFilters(dto);
        }catch (Exception e) {
            log.info("Error en getSourceWithParameter: "+ e.getMessage());
        }
        return result;
    }
    public int getSourceWithParameterTotalCount(SourceWithParameterPaginationDtoRequest dto) {
        int totalCount = 0;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            totalCount = mapper.getSourcesWithParameterTotalCountWithFilters(dto);
        } catch (Exception e) {
            log.info("Error en getSourceWithParameterTotalCount: "+ e.getMessage());
        }
        return totalCount;
    }
    public SourceWithParameterDataDtoResponse getSourceWithParameterById(String singleId){
        SourceWithParameterDataDtoResponse result = null;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            result = mapper.getSourceWithParameterById(singleId);
        } catch (Exception e) {
            log.info("Error en getSourceWithParameterById: "+ e.getMessage());
            return null;
        }
        return result;
    }
    public List<String> getDistinctStatuses() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SourceWithParameterMapper.class).getStatus();
        }
    }
    public List<String> getDistinctOriginTypes() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SourceWithParameterMapper.class).getOriginType();
        }
    }
    public List<String> getDistinctTdsOpinionDebts() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SourceWithParameterMapper.class).getTdsOpinionDebt();
        }
    }
    public List<String> getDistinctEffectivenessDebts() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SourceWithParameterMapper.class).getEffectivenessDebt();
        }
    }
    public boolean update(SourceWithParameterDataDtoResponse dto) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);

            mapper.updateSource(dto);

            session.commit();
            return true;
        } catch (Exception e) {
            log.severe("Error en SP_NEW_UPDATE_SOURCE: " + e.getMessage());
            return false;
        }
    }
    public List<String> getCommentsBySourceIdAndType(String sourceId, String commentType) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            return mapper.getCommentsBySourceIdAndType(sourceId, commentType);
        }
    }

    public void saveCommentBySourceIdAndType(String sourceId, String commentType, String comment) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            mapper.saveCommentBySourceIdAndType(sourceId, commentType, comment);
            session.commit();
        }
    }

    public void insertModifyHistory(SourceWithParameterDataDtoResponse dto) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            mapper.insertModifyHistory(dto);
            session.commit();
        }
    }

    public boolean insert(SourceWithParameterDataDtoResponse dto) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            mapper.insertSource(dto);

            if (dto.getReplacementId() != null && !dto.getReplacementId().isEmpty()) {
                String oldReplacementIds = mapper.getReplacementIds(dto.getReplacementId());
                String newReplacementIds = (oldReplacementIds == null || oldReplacementIds.isEmpty())
                        ? dto.getId()
                        : oldReplacementIds + "," + dto.getId();

                mapper.updateReplacementId(newReplacementIds, dto.getReplacementId());
            }

            session.commit();
            return true;
        }
    }

    public String getMaxSourceId() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            return mapper.getMaxSourceId();
        }
    }
    public boolean existsReplacementId(String replacementId) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            return mapper.countById(replacementId) > 0;
        }
    }
    public String getStatusById(String sourceId) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            return mapper.getStatusById(sourceId);
        }
    }
}
