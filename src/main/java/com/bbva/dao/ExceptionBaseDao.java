package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.ExceptionBaseMapper;
import com.bbva.dto.exception_base.request.ExceptionBasePaginationDtoRequest;
import com.bbva.dto.exception_base.response.ExceptionBaseDataDtoResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.logging.Logger;

public class ExceptionBaseDao {
    private static final Logger log = Logger.getLogger(ExceptionBaseDao.class.getName());
    private final SqlSessionFactory sqlSessionFactory;

    public ExceptionBaseDao(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public List<ExceptionBaseDataDtoResponse> getExceptionsWithSource(ExceptionBasePaginationDtoRequest dto) {
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
        } catch (Exception e) {
            log.info("Error en getExceptionsWithSource: "+ e.getMessage());
        }
        return result;
    }

    public int getExceptionsTotalCount(ExceptionBasePaginationDtoRequest dto) {
        int totalCount = 0;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ExceptionBaseMapper mapper = session.getMapper(ExceptionBaseMapper.class);
            totalCount = mapper.getExceptionsTotalCountWithFilters(
                    dto.getRequestingProject(),
                    dto.getApprovalResponsible(),
                    dto.getRegistrationDate(),
                    dto.getQuarterYearSprint()
            );
        } catch (Exception e) {
            log.info("Error en getExceptionsTotalCount: "+ e.getMessage());
        }
        return totalCount;
    }

    public ExceptionBaseDataDtoResponse getExceptionById(String exceptionId) {
        ExceptionBaseDataDtoResponse result = null;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            ExceptionBaseMapper mapper = session.getMapper(ExceptionBaseMapper.class);
            result = mapper.getExceptionById(exceptionId);
        } catch (Exception e) {
            log.info("Error en getExceptionById: "+ e.getMessage());
        }
        return result;
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