package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.BuiDao;
import com.bbva.dto.bui.request.PaginationDtoRequest;
import com.bbva.dto.bui.request.ReadOnlyDtoRequest;
import com.bbva.dto.bui.response.PaginationResponse;
import com.bbva.dto.bui.response.ReadOnlyDtoResponse;

public class BuiService {

    private final BuiDao buiDao = new BuiDao();

    public IDataResult<PaginationResponse> pagination(PaginationDtoRequest dto) {
        var result = buiDao.pagination(dto);
        return new SuccessDataResult(result);
    }

    public IDataResult<ReadOnlyDtoResponse> readOnly(ReadOnlyDtoRequest dto) {
        var result = buiDao.readOnly(dto);
        return new SuccessDataResult(result);
    }

}