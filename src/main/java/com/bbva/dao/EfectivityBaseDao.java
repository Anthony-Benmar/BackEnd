package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.EfectivityBaseMapper;
import com.bbva.dto.efectivity_base.request.EfectivityBasePaginationDtoRequest;
import com.bbva.dto.efectivity_base.response.EfectivityBaseDataDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
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
            log.info("EfectivityBaseDao - Registros obtenidos: " + result.size());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error en getBaseEfectivityWithSource: " + e.getMessage(), e);
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
            log.info("EfectivityBaseDao - Total filtrado: " + totalCount);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error en getBaseEfectivityTotalCount: " + e.getMessage(), e);
        }
        return totalCount;
    }
    public EfectivityBaseDataDtoResponse getBaseEfectivityById(String singleId) {
        EfectivityBaseDataDtoResponse result = null;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            EfectivityBaseMapper mapper = session.getMapper(EfectivityBaseMapper.class);
            result = mapper.getBaseEfectivityById(singleId);
            log.info("EfectivityBaseDao - Registro obtenido por ID: " + (result != null ? result.getId() : "null"));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error en getBaseEfectivityById: " + e.getMessage(), e);
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