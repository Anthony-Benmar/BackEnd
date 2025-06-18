package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.ExceptionBaseDao;
import com.bbva.dto.exception_base.request.ExceptionBasePaginationDtoRequest;
import com.bbva.dto.exception_base.request.ExceptionBaseReadOnlyDtoRequest;
import com.bbva.dto.exception_base.response.ExceptionBaseDataDtoResponse;
import com.bbva.dto.exception_base.response.ExceptionBasePaginatedResponseDTO;
import com.bbva.dto.exception_base.response.ExceptionBaseReadOnlyDtoResponse;

import java.util.List;

import static com.bbva.database.MyBatisConnectionFactory.sqlSessionFactory;

public class ExceptionBaseService {
    private final ExceptionBaseDao exceptionBaseDao;
    public ExceptionBaseService(ExceptionBaseDao exceptionBaseDao) {
        this.exceptionBaseDao = exceptionBaseDao;
    }

    public ExceptionBaseService() {
        this.exceptionBaseDao = new ExceptionBaseDao(sqlSessionFactory);
    }
    public IDataResult<ExceptionBasePaginatedResponseDTO> getExceptionsWithSource(ExceptionBasePaginationDtoRequest dto) {
        List<ExceptionBaseDataDtoResponse> data = exceptionBaseDao.getExceptionsWithSource(dto);
        int totalCount = exceptionBaseDao.getExceptionsTotalCount(dto);

        ExceptionBasePaginatedResponseDTO response = new ExceptionBasePaginatedResponseDTO();
        response.setData(data);
        response.setTotalCount(totalCount);

        return new SuccessDataResult<>(response);
    }

    public IDataResult<ExceptionBaseReadOnlyDtoResponse> readOnly(ExceptionBaseReadOnlyDtoRequest request) {
        ExceptionBaseDataDtoResponse data = exceptionBaseDao.getExceptionById(request.getId());
        ExceptionBaseReadOnlyDtoResponse response = new ExceptionBaseReadOnlyDtoResponse();
        if (data != null){
            response.setId(data.getId());
            response.setSourceId(data.getSourceId());
            response.setTdsDescription(data.getTdsDescription());
            response.setTdsSource(data.getTdsSource());
            response.setRequestingProject(data.getRequestingProject());
            response.setApprovalResponsible(data.getApprovalResponsible());
            response.setRegistrationDate(data.getRegistrationDate());
            response.setQuarterYearSprint(data.getQuarterYearSprint());
            response.setShutdownCommitmentStatus(data.getShutdownCommitmentStatus());
            response.setShutdownCommitmentDate(data.getShutdownCommitmentDate());
            response.setShutdownProject(data.getShutdownProject());
        }
        return new SuccessDataResult<>(response);
    }

    // Métodos para combos
    public List<String> getDistinctRequestingProjects() {
        return exceptionBaseDao.getDistinctRequestingProjects();
    }

    public List<String> getDistinctApprovalResponsibles() {
        return exceptionBaseDao.getDistinctApprovalResponsibles();
    }

    public List<String> getDistinctRegistrationDates() {
        return exceptionBaseDao.getDistinctRegistrationDates();
    }

    public List<String> getDistinctQuarterYearSprints() {
        return exceptionBaseDao.getDistinctQuarterYearSprints();
    }
}