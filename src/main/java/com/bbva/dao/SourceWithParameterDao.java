package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.SourceWithParameterMapper;
import com.bbva.dto.source_with_parameter.request.SourceWithParameterPaginationDtoRequest;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDataDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

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
            result = mapper.getSourcesWithParameterWithFilters(
                    dto.getLimit(),
                    dto.getOffset(),
                    dto.getId(),
                    dto.getTdsSource(),
                    dto.getUuaaMaster(),
                    dto.getModelOwner(),
                    dto.getStatus(),
                    dto.getOriginType(),
                    dto.getTdsOpinionDebt(),
                    dto.getEffectivenessDebt()
            );
        }catch (Exception e) {
            log.info("Error en getSourceWithParameter: "+ e.getMessage());
        }
        return result;
    }
    public int getSourceWithParameterTotalCount(SourceWithParameterPaginationDtoRequest dto) {
        int totalCount = 0;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            SourceWithParameterMapper mapper = session.getMapper(SourceWithParameterMapper.class);
            totalCount = mapper.getSourcesWithParameterTotalCountWithFilters(
                    dto.getId(),
                    dto.getTdsSource(),
                    dto.getUuaaMaster(),
                    dto.getModelOwner(),
                    dto.getStatus(),
                    dto.getOriginType(),
                    dto.getTdsOpinionDebt(),
                    dto.getEffectivenessDebt()
            );
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
    // Métodos para combos
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
}
