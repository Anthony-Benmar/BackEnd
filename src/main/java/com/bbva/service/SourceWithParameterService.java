package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.SourceWithParameterDao;
import com.bbva.dto.source_with_parameter.response.SourceWithParameterDTO;

import java.util.List;

public class SourceWithParameterService {
    private  final SourceWithParameterDao sourceWithParameterDao = new SourceWithParameterDao();
    public IDataResult<List<SourceWithParameterDTO>> getSourceWithParameter(){
        List<SourceWithParameterDTO> result;
        result = sourceWithParameterDao.getSourceWithParameter();
        return new SuccessDataResult<>(result);
    }
}
