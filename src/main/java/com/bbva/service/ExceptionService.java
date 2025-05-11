package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.ExceptionDao;
import com.bbva.dto.exception.response.ExceptionEntityResponseDTO;

import java.util.List;

public class ExceptionService {
    private final ExceptionDao exceptionDao = new ExceptionDao();
    public IDataResult<List<ExceptionEntityResponseDTO>> getExceptionsWithSource(){
        List<ExceptionEntityResponseDTO> result;
         result = exceptionDao.getExceptionsWithSource();
        return new SuccessDataResult<>(result);
    }
}
