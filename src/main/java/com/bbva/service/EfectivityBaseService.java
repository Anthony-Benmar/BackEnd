package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.EfectivityBaseDao;
import com.bbva.dto.efectivity_base.request.EfectivityBasePaginationDtoRequest;
import com.bbva.dto.efectivity_base.request.EfectivityBaseReadOnlyDtoRequest;
import com.bbva.dto.efectivity_base.response.EfectivityBaseDataDtoResponse;
import com.bbva.dto.efectivity_base.response.EfectivityBaseDataReadOnlyDtoResponse;
import com.bbva.dto.efectivity_base.response.EfectivityBasePaginatedResponseDTO;

import java.util.List;

import static com.bbva.database.MyBatisConnectionFactory.sqlSessionFactory;

public class EfectivityBaseService {
    private final EfectivityBaseDao efectivityBaseDao = new EfectivityBaseDao(sqlSessionFactory);

    public IDataResult<EfectivityBasePaginatedResponseDTO> getBaseEfectivityWithSource(EfectivityBasePaginationDtoRequest dto) {
        List<EfectivityBaseDataDtoResponse> data = efectivityBaseDao.getBaseEfectivityWithSource(dto);
        int totalCount = efectivityBaseDao.getBaseEfectivityTotalCount(dto);

        EfectivityBasePaginatedResponseDTO response = new EfectivityBasePaginatedResponseDTO();
        response.setData(data);
        response.setTotalCount(totalCount);

        return new SuccessDataResult<>(response);
    }
    public IDataResult<EfectivityBaseDataReadOnlyDtoResponse> readOnly(EfectivityBaseReadOnlyDtoRequest request) {
        // Implementa la lógica de detalle si la necesitas
        EfectivityBaseDataDtoResponse data = efectivityBaseDao.getBaseEfectivityById(request.getEfectivityBaseId());
        EfectivityBaseDataReadOnlyDtoResponse response = new EfectivityBaseDataReadOnlyDtoResponse();

        // Map fields if data is not null
        if (data != null) {
            response.setId(data.getId());
            response.setTicketCode(data.getTicketCode());
            response.setSprintDate(data.getSprintDate());
            response.setSdatoolProject(data.getSdatoolProject());
            response.setSdatoolFinalProject(data.getSdatoolFinalProject());
            response.setFolio(data.getFolio());
            response.setTdsDescription(data.getTdsDescription());
            response.setRegisterDate(data.getRegisterDate());
            response.setAnalystAmbassador(data.getAnalystAmbassador());
            response.setRegistrationResponsible(data.getRegistrationResponsible());
            response.setBuildObservations(data.getBuildObservations());
            response.setRegistrationObservations(data.getRegistrationObservations());
            response.setSourceTable(data.getSourceTable());
        }

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