package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.CatalogDao;
import com.bbva.dao.SppDao;
import com.bbva.dto.catalog.request.ListByCatalogIdDtoRequest;
import com.bbva.dto.catalog.response.ListByCatalogIdDtoResponse;
import com.bbva.entities.common.PeriodEntity;
import com.bbva.entities.spp.Period;
import java.io.IOException;

public class CatalogService {
    private final CatalogDao catalogDao = new CatalogDao();
    private final SppDao sppDao = new SppDao();
    public IDataResult<ListByCatalogIdDtoResponse> catalogosByCatalogoId(ListByCatalogIdDtoRequest dto) {
        var result = catalogDao.getCatalogoByCatalogoId(dto);
        return new SuccessDataResult(result);
    }

    public IDataResult<Period> listPeriods() throws IOException, InterruptedException {
        var result = sppDao.listPeriodsForSelect();
        return new SuccessDataResult(result);
    }

    public IDataResult<PeriodEntity> listAllPeriods() {
        var result = catalogDao.listAllPeriods();
        return new SuccessDataResult(result);
    }
}
