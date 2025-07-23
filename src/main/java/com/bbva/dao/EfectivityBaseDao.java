package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.EfectivityBaseMapper;
import com.bbva.dto.efectivity_base.request.EfectivityBasePaginationDtoRequest;
import com.bbva.dto.efectivity_base.response.EfectivityBaseDataDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Logger;

public class EfectivityBaseDao {
    private static final Logger log = Logger.getLogger(EfectivityBaseDao.class.getName());
    private final SqlSessionFactory sqlSessionFactory;

    public EfectivityBaseDao(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public List<EfectivityBaseDataDtoResponse> getBaseEfectivityWithSource(EfectivityBasePaginationDtoRequest dto) {
        List<EfectivityBaseDataDtoResponse> result = null;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            EfectivityBaseMapper mapper = session.getMapper(EfectivityBaseMapper.class);
            result = mapper.getBaseEfectivityDataWithFilters(
                    dto.getLimit(),
                    dto.getOffset(),
                    dto.getSdatoolProject(),
                    dto.getSprintDate(),
                    dto.getRegisterDate(),
                    dto.getEfficiency()
            );
            if (result == null) {
                result = List.of();
            }
        } catch (Exception e) {
            log.info("EfectivityBaseDao - No se encontraron registros con los filtros proporcionados."+ e.getMessage());
        }
        return result;
    }

    public int getBaseEfectivityTotalCount(EfectivityBasePaginationDtoRequest dto) {
        int totalCount = 0;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            EfectivityBaseMapper mapper = session.getMapper(EfectivityBaseMapper.class);
            totalCount = mapper.getBaseEfectivityTotalCountWithFilters(
                    dto.getSdatoolProject(),
                    dto.getSprintDate(),
                    dto.getRegisterDate(),
                    dto.getEfficiency()
            );
        } catch (Exception e) {
            log.info("Error en getBaseEfectivityTotalCount "+ e.getMessage());
        }
        return totalCount;
    }
    public EfectivityBaseDataDtoResponse getBaseEfectivityById(String singleId) {
        EfectivityBaseDataDtoResponse result = null;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            EfectivityBaseMapper mapper = session.getMapper(EfectivityBaseMapper.class);
            result = mapper.getBaseEfectivityById(singleId);
        } catch (Exception e) {
            log.info("Error en getBaseEfectivityById: "+ e.getMessage());
        }
        return result;
    }

    // Métodos para combos
    public List<String> getDistinctSdatoolProjects() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(EfectivityBaseMapper.class).getDistinctSdatoolProjects();
        }
    }

    public List<String> getDistinctSprintDates() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(EfectivityBaseMapper.class).getDistinctSprintDates();
        }
    }

    public List<java.sql.Date> getDistinctRegisterDates() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(EfectivityBaseMapper.class).getDistinctRegisterDates();
        }
    }

    public List<String> getDistinctEfficiencies() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(EfectivityBaseMapper.class).getDistinctEfficiencies();
        }
    }
}