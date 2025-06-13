package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.ExceptionBaseMapper;
import com.bbva.dto.exception_base.request.ExceptionBasePaginationDtoRequest;
import com.bbva.dto.exception_base.response.ExceptionBaseDataDtoResponse;
import com.bbva.dto.exception_base.request.ExceptionBaseReadOnlyDtoRequest;
import com.bbva.dto.exception_base.response.ExceptionBaseReadOnlyDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionBaseDao {
    private static final Logger log = Logger.getLogger(ExceptionBaseDao.class.getName());

    public List<ExceptionBaseDataDtoResponse> getExceptionsWithSource(ExceptionBasePaginationDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        List<ExceptionBaseDataDtoResponse> result = null;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ExceptionBaseMapper mapper = session.getMapper(ExceptionBaseMapper.class);
            result = mapper.getExceptionsDataWithFilters(
                    dto.getLimit(),
                    dto.getOffset(),
                    dto.getRequestingProject(),
                    dto.getApprovalResponsible(),
                    dto.getRegistrationDate(),
                    dto.getQuarterYearSprint()
            );
            if (result == null) {
                result = List.of();
            }
            log.info("ExceptionBaseDao - Registros obtenidos: " + result.size());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error en getExceptionsWithSource: " + e.getMessage(), e);
        }
        return result;
    }

    public int getExceptionsTotalCount(ExceptionBasePaginationDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        int totalCount = 0;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ExceptionBaseMapper mapper = session.getMapper(ExceptionBaseMapper.class);
            totalCount = mapper.getExceptionsTotalCountWithFilters(
                    dto.getRequestingProject(),
                    dto.getApprovalResponsible(),
                    dto.getRegistrationDate(),
                    dto.getQuarterYearSprint()
            );
            log.info("ExceptionBaseDao - Total filtrado: " + totalCount);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error en getExceptionsTotalCount: " + e.getMessage(), e);
        }
        return totalCount;
    }

    public ExceptionBaseReadOnlyDtoResponse getExceptionById(Integer exceptionId) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ExceptionBaseMapper mapper = session.getMapper(ExceptionBaseMapper.class);
            return mapper.getExceptionById(exceptionId);
        }
    }

    // Métodos para combos
    public List<String> getDistinctRequestingProjects() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(ExceptionBaseMapper.class).getDistinctRequestingProjects();
        }
    }

    public List<String> getDistinctApprovalResponsibles() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(ExceptionBaseMapper.class).getDistinctApprovalResponsibles();
        }
    }

    public List<String> getDistinctRegistrationDates() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(ExceptionBaseMapper.class).getDistinctRegistrationDates();
        }
    }

    public List<String> getDistinctQuarterYearSprints() {
        try (SqlSession session = MyBatisConnectionFactory.getInstance().openSession()) {
            return session.getMapper(ExceptionBaseMapper.class).getDistinctQuarterYearSprints();
        }
    }
}