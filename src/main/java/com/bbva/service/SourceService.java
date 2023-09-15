package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.SourceDao;
import com.bbva.dto.source.request.PaginationDtoRequest;
import com.bbva.dto.source.request.ReadOnlyDtoRequest;
import com.bbva.dto.source.response.PaginationResponse;
import com.bbva.dto.source.response.ReadOnlyDtoResponse;

public class SourceService {

    private final SourceDao sourceDao = new SourceDao();

    public IDataResult<PaginationResponse> pagination(PaginationDtoRequest dto) {
        var result = sourceDao.pagination(dto);
        return new SuccessDataResult(result);
    }

    public IDataResult<ReadOnlyDtoResponse> readOnly(ReadOnlyDtoRequest dto) {
        var result = sourceDao.readOnly(dto);
        return new SuccessDataResult(result);
    }

}