package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.BucDao;
import com.bbva.dto.buc.request.ReadOnlyDtoRequest;
import com.bbva.dto.buc.response.ReadOnlyDtoResponse;
import com.bbva.dto.buc.request.PaginationDtoRequest;
import com.bbva.dto.buc.response.PaginationResponse;

public class BucService {
    private final BucDao bucDao = new BucDao();

    public IDataResult<PaginationResponse> pagination(PaginationDtoRequest dto) {
        var result = bucDao.pagination(dto);
        return new SuccessDataResult(result);
    }

    public IDataResult<ReadOnlyDtoResponse> readOnly(ReadOnlyDtoRequest dto) {
        var result = bucDao.readOnly(dto);
        return new SuccessDataResult(result);
    }

}
