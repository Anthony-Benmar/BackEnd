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

public class ExceptionBaseService {
    private final ExceptionBaseDao exceptionBaseDao = new ExceptionBaseDao();

    public IDataResult<ExceptionBasePaginatedResponseDTO> getExceptionsWithSource(ExceptionBasePaginationDtoRequest dto) {
        List<ExceptionBaseDataDtoResponse> data = exceptionBaseDao.getExceptionsWithSource(dto);
        int totalCount = exceptionBaseDao.getExceptionsTotalCount(dto);

        ExceptionBasePaginatedResponseDTO response = new ExceptionBasePaginatedResponseDTO();
        response.setData(data);
        response.setTotalCount(totalCount);

        return new SuccessDataResult<>(response);
    }

    public IDataResult<ExceptionBaseReadOnlyDtoResponse> readOnly(ExceptionBaseReadOnlyDtoRequest request) {
        ExceptionBaseReadOnlyDtoResponse data = exceptionBaseDao.getExceptionById(request.getId());
        return new SuccessDataResult<>(data);
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