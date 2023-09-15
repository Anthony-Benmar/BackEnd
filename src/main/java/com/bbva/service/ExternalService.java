package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.ExternalDao;
import com.bbva.dto.external.request.GobiernoDtoRequest;
import com.bbva.dto.external.response.GobiernoDtoResponse;

public class ExternalService {

    private final ExternalDao gobiernoDao = new ExternalDao();

    public IDataResult<GobiernoDtoResponse> gobierno(GobiernoDtoRequest dto) {
        var result = gobiernoDao.gobierno(dto);
        return new SuccessDataResult(result);
    }
}