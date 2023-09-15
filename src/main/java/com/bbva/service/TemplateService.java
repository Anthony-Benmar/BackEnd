package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.TemplateDao;
import com.bbva.dto.template.request.PaginationDtoRequest;
import com.bbva.dto.template.response.TemplatePaginationDtoResponse;

public class TemplateService {
    private final TemplateDao templateDao = new TemplateDao();

    public IDataResult<TemplatePaginationDtoResponse> pagination(PaginationDtoRequest dto) {
        var result = templateDao.pagination(dto);
        return new SuccessDataResult(result);
    }

}
