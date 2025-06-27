package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.SingleBaseDao;
import com.bbva.dto.single_base.request.SingleBasePaginationDtoRequest;
import com.bbva.dto.single_base.request.SingleBaseReadOnlyDtoRequest;
import com.bbva.dto.single_base.response.SingleBaseDataDtoResponse;
import com.bbva.dto.single_base.response.SingleBasePaginatedResponseDTO;
import com.bbva.dto.single_base.response.SingleBaseReadOnlyDtoResponse;

import java.util.List;

import static com.bbva.database.MyBatisConnectionFactory.getSqlSessionFactory;

public class SingleBaseService {
    private final SingleBaseDao singleBaseDao;
    public SingleBaseService(SingleBaseDao singleBaseDao) {
        this.singleBaseDao = singleBaseDao;
    }
    public SingleBaseService() {
        this.singleBaseDao = new SingleBaseDao(getSqlSessionFactory());
    }

    public IDataResult<SingleBasePaginatedResponseDTO> getBaseUnicaWithSource(SingleBasePaginationDtoRequest dto) {
        // El DTO ya contiene el campo registeredFolioDate, así que no necesitas cambiar nada aquí
        List<SingleBaseDataDtoResponse> data = singleBaseDao.getBaseUnicaWithSource(dto);
        int totalCount = singleBaseDao.getBaseUnicaTotalCount(dto);

        SingleBasePaginatedResponseDTO response = new SingleBasePaginatedResponseDTO();
        response.setData(data);
        response.setTotalCount(totalCount);

        return new SuccessDataResult<>(response);
    }

    public IDataResult<SingleBaseReadOnlyDtoResponse> readOnly(SingleBaseReadOnlyDtoRequest request) {
        // Implementación para devolver el detalle por ID
        SingleBaseDataDtoResponse data = singleBaseDao.getSingleBaseById(request.getSingleBaseId());
        SingleBaseReadOnlyDtoResponse response = new SingleBaseReadOnlyDtoResponse();
        if (data != null) {
            response.setId(data.getId());
            response.setFolio(data.getFolio());
            response.setProjectName(data.getProjectName());
            response.setUcSourceName(data.getUcSourceName());
            response.setUcSourceDesc(data.getUcSourceDesc());
            response.setRegisteredFolioDate(data.getRegisteredFolioDate());
            response.setStatusFolioType(data.getStatusFolioType());
            response.setAnalystProjectId(data.getAnalystProjectId());
            response.setAnalystCaId(data.getAnalystCaId());
            response.setResolutionSourceType(data.getResolutionSourceType());
            response.setResolutionSourceDate(data.getResolutionSourceDate());
            response.setReusedFolioCode(data.getReusedFolioCode());
            response.setResolutionCommentDesc(data.getResolutionCommentDesc());
            response.setFolioType(data.getFolioType());
            response.setOldSourceId(data.getOldSourceId());
            response.setUcFinalistDesc(data.getUcFinalistDesc());
            response.setCatalogId(data.getCatalogId());
        }
        return new SuccessDataResult<>(response);
    }

    // Métodos para combos
    public List<String> getDistinctFolios() {
        return singleBaseDao.getDistinctFolios();
    }

    public List<String> getDistinctProjectNames() {
        return singleBaseDao.getDistinctProjectNames();
    }

    public List<java.sql.Date> getDistinctRegisteredFolioDates() {
        return singleBaseDao.getDistinctRegisteredFolioDates();
    }

    public List<String> getDistinctStatusFolioTypes() {
        return singleBaseDao.getDistinctStatusFolioTypes();
    }

    public List<String> getDistinctFolioTypes() {
        return singleBaseDao.getDistinctFolioTypes();
    }
}