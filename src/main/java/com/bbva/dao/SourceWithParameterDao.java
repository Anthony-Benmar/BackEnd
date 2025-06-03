package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.SourceWithParameterMapper;
import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SourceWithParameterDao {
    private static final Logger log = Logger.getLogger(SourceWithParameterDao.class.getName());

    public List<SourceWithParameterDataDtoResponse> getSourceWithParameter(SourceWithParameterPaginationDtoRequest dto){
        List<SourceWithParameterDataDtoResponse> result = null;
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try(SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            result = mapper.getSourcesWithParameterWithFilters(
                    dto.getLimit(),
                    dto.getOffset(),
                    dto.getTdsDescription(),
                    dto.getTdsSource(),
                    dto.getReplacementId(),
                    dto.getOriginType(),
                    dto.getTdsOpinionDebt(),
                    dto.getEffectivenessDebt()
            );
        }catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }
    public int getSourceWithParameterTotalCount(SourceWithParameterPaginationDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        int totalCount = 0;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            totalCount = mapper.getSourcesWithParameterTotalCountWithFilters(
                    dto.getTdsDescription(),
                    dto.getTdsSource(),
                    dto.getReplacementId(),
                    dto.getOriginType(),
                    dto.getTdsOpinionDebt(),
                    dto.getEffectivenessDebt()
            );
            log.info("SourceWithParameterDao - Total filtrado: " + totalCount);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error en getSourceWithParameterTotalCount: " + e.getMessage(), e);
        }
        return totalCount;
    }
    public SourceWithParameterDataDtoResponse getSourceWithParameterById(Integer singleId){
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            return mapper.getSourceWithParameterById(singleId);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error en getSourceWithParameterById: " + e.getMessage(), e);
            return null;
        }
    }
    // Métodos para combos
    public List<String> getDistinctTdsDescriptions() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SourceWithParameterMapper.class).getTdsDescription();
        }
    }
    public List<String> getDistinctTdsSources() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SourceWithParameterMapper.class).getTdsSource();
        }
    }
    public List<String> getDistinctReplacementIds() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(SourceWithParameterMapper.class).getReplacementId();
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
}
