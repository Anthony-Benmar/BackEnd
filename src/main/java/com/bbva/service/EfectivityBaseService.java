package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.EfectivityBaseDao;
import com.bbva.dto.efectivity_base.request.EfectivityBasePaginationDtoRequest;
import com.bbva.dto.efectivity_base.response.EfectivityBaseDataDtoResponse;
import com.bbva.dto.efectivity_base.response.EfectivityBasePaginatedResponseDTO;

import java.util.List;

public class EfectivityBaseService {
    private final EfectivityBaseDao efectivityBaseDao = new EfectivityBaseDao();

    public IDataResult<EfectivityBasePaginatedResponseDTO> getBaseEfectivityWithSource(EfectivityBasePaginationDtoRequest dto) {
        List<EfectivityBaseDataDtoResponse> data = efectivityBaseDao.getBaseEfectivityWithSource(dto);
        int totalCount = efectivityBaseDao.getBaseEfectivityTotalCount(dto);

        EfectivityBasePaginatedResponseDTO response = new EfectivityBasePaginatedResponseDTO();
        response.setData(data);
        response.setTotalCount(totalCount);

        return new SuccessDataResult<>(response);
    }

    // Métodos para combos
    public List<String> getDistinctSdatoolProjects() {
        return efectivityBaseDao.getDistinctSdatoolProjects();
    }

    public List<String> getDistinctSprintDates() {
        return efectivityBaseDao.getDistinctSprintDates();
    }

    public List<java.sql.Date> getDistinctRegisterDates() {
        return efectivityBaseDao.getDistinctRegisterDates();
    }

    public List<String> getDistinctEfficiencies() {
        return efectivityBaseDao.getDistinctEfficiencies();
    }
}