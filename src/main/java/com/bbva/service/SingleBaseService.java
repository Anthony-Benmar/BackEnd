package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.SingleBaseDao;
import com.bbva.dto.singleBase.response.SingleBaseResponseDTO;

import java.util.List;

public class SingleBaseService {
    private final SingleBaseDao singleBaseDao = new SingleBaseDao();

    public IDataResult<List<SingleBaseResponseDTO>> getBaseUnicaWithSource() {
        List<SingleBaseResponseDTO> result;
        result = singleBaseDao.getBaseUnicaWithSource();
        return new SuccessDataResult<>(result);
    }
}
