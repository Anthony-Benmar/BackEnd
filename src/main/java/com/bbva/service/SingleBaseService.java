package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.SingleBaseDao;
import com.bbva.dto.single_base.request.SingleBasePaginationDtoRequest;
import com.bbva.dto.single_base.request.SingleBaseReadOnlyDtoRequest;
import com.bbva.dto.single_base.response.SingleBaseDataDtoResponse;
import com.bbva.dto.single_base.response.SingleBasePaginatedResponseDTO;
import com.bbva.dto.single_base.response.SingleBaseReadOnlyDtoResponse;

import java.util.List;

public class SingleBaseService {
    private final SingleBaseDao singleBaseDao = new SingleBaseDao();

    public IDataResult<SingleBasePaginatedResponseDTO> getBaseUnicaWithSource(SingleBasePaginationDtoRequest dto) {
        // El DTO ya contiene el campo registeredFolioDate, así que no necesitas cambiar nada aquí
        List<SingleBaseDataDtoResponse> data = singleBaseDao.getBaseUnicaWithSource(dto);
        int totalCount = singleBaseDao.getBaseUnicaTotalCount(dto);

        SingleBasePaginatedResponseDTO response = new SingleBasePaginatedResponseDTO();
        response.setData(data);
        response.setTotalCount(totalCount);

        return new SuccessDataResult<>(response);
    }

    public IDataResult<SingleBaseReadOnlyDtoResponse> readOnly(SingleBaseReadOnlyDtoRequest request) {
        // Implementa la lógica de detalle si la necesitas
        SingleBaseReadOnlyDtoResponse response = new SingleBaseReadOnlyDtoResponse();
        return new SuccessDataResult<>(response);
    }

    // Métodos para combos
    public List<String> getDistinctFolios() {
        return singleBaseDao.getDistinctFolios();
    }

    public List<String> getDistinctProjectNames() {
        return singleBaseDao.getDistinctProjectNames();
    }

    public List<java.sql.Date> getDistinctRegisteredFolioDates() {
        return singleBaseDao.getDistinctRegisteredFolioDates();
    }

    public List<String> getDistinctStatusFolioTypes() {
        return singleBaseDao.getDistinctStatusFolioTypes();
    }

    public List<String> getDistinctFolioTypes() {
        return singleBaseDao.getDistinctFolioTypes();
    }
}