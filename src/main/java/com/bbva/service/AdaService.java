package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.AdaDao;
import com.bbva.dto.ada.request.AdaJobExecutionFilterRequestDTO;
import com.bbva.dto.ada.response.AdaJobExecutionFilterResponseDTO;

public class AdaService {
    private final AdaDao adaDao = new AdaDao();

    public IDataResult<AdaJobExecutionFilterResponseDTO> filter(AdaJobExecutionFilterRequestDTO dto) {
        var result = adaDao.filter(dto);
        return new SuccessDataResult<>(result);
    }
}
