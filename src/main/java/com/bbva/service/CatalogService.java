package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.CatalogDao;
import com.bbva.dao.SppDao;
import com.bbva.dto.catalog.request.ListByCatalogIdDtoRequest;
import com.bbva.dto.catalog.response.CatalogResponseDto;
import com.bbva.dto.catalog.response.ElementsDto;
import com.bbva.dto.catalog.response.ListByCatalogIdDtoResponse;
import com.bbva.entities.batch.GetCatalogEntity;
import com.bbva.entities.common.PeriodEntity;
import com.bbva.entities.spp.Period;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CatalogService {
    private final CatalogDao catalogDao = new CatalogDao();
    private final SppDao sppDao = new SppDao();

    public IDataResult<CatalogResponseDto> getCatalog (
            Integer catalogId,
            Integer parentCatalogId,
            Integer parentElementId) {
        ArrayList<GetCatalogEntity> catalogs = catalogDao.getCatalog(catalogId, parentCatalogId, parentElementId);

        if (catalogs.size() <= 1)  return new ErrorDataResult(null, "404", "Catalog not found.");

        CatalogResponseDto response = new CatalogResponseDto();
        List<ElementsDto> elements = new ArrayList<>();

        GetCatalogEntity catalogInformation = catalogs
                .stream()
                .filter(catalog -> catalog.getCatalogId() == catalog.getElementId())
                .findFirst().get();

        response.setId(catalogInformation.getCatalogId());
        response.setDescription(catalogInformation.getElementDescription());
        response.setDescription(catalogInformation.getElementName());

        catalogs
                .stream()
                .filter(catalog -> catalog.getCatalogId() != catalog.getElementId())
                .forEach(
                        catalog -> {
                           ElementsDto element = new ElementsDto();
                           element.setId(catalog.getElementId());
                           element.setName(catalog.getElementName());
                           element.setDescription(catalog.getElementDescription());
                           elements.add(element);
                        }
        );

        response.setElements(elements);
        return new SuccessDataResult(response);
    }

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

    public IDataResult<List<PeriodEntity>> getActivePeriod() {
        List<PeriodEntity> result = catalogDao.getActivePeriod();
        return new SuccessDataResult<>(result);
    }
}
