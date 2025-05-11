package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.BaseunicaDao;
import com.bbva.dto.baseunica.response.BaseunicaResponseDTO;

import java.util.List;

public class BaseunicaService {
    private final BaseunicaDao baseunicaDao = new BaseunicaDao();

    public IDataResult<List<BaseunicaResponseDTO>> getBaseUnicaWithSource(String tableName) {
        List<BaseunicaResponseDTO> result;
        result = baseunicaDao.getBaseUnicaWithSource(tableName);
        return new SuccessDataResult<>(result);
    }
}
