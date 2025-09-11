package com.bbva.service;

import static com.bbva.database.MyBatisConnectionFactory.getSqlSessionFactory;

import java.util.List;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.VisaSourcesDao;
import com.bbva.dto.visa_sources.request.ApproveVisaSourceDtoRequest;
import com.bbva.dto.visa_sources.request.RegisterVisaSourceDtoRequest;
import com.bbva.dto.visa_sources.request.UpdateStatusVisaSourceDtoRequest;
import com.bbva.dto.visa_sources.request.VisaSourcePaginationDtoRequest;
import com.bbva.dto.visa_sources.response.VisaSourceApproveDtoResponse;
import com.bbva.dto.visa_sources.response.VisaSourceValidateExistDtoResponse;
import com.bbva.dto.visa_sources.response.VisaSourcesDataDtoResponse;
import com.bbva.dto.visa_sources.response.VisaSourcesPaginationDtoResponse;
import com.bbva.entities.InsertEntity;
import com.bbva.entities.UpdateEntity;

public class VisaSourcesService {
    private final VisaSourcesDao visaSourcesDao;

    public VisaSourcesService(VisaSourcesDao visaSourcesDao) {
        this.visaSourcesDao = visaSourcesDao;
    }

    public VisaSourcesService() {
        this.visaSourcesDao = new VisaSourcesDao(getSqlSessionFactory());
    }

    public IDataResult<VisaSourcesPaginationDtoResponse> getVisaSources(VisaSourcePaginationDtoRequest dto) {
        List<VisaSourcesDataDtoResponse> result = visaSourcesDao.getVisaSources(dto);
        int totalCount = visaSourcesDao.getVisaSourcesTotalCount(dto);
        VisaSourcesPaginationDtoResponse response = new VisaSourcesPaginationDtoResponse();
        response.setData(result);
        response.setTotalCount(totalCount);
        return new SuccessDataResult<>(response);
    }

    public SuccessDataResult<Boolean> registerVisaSource(RegisterVisaSourceDtoRequest dto) {
        InsertEntity response = visaSourcesDao.registerVisaSource(dto);
        boolean result = response.getNew_register() > 0;
        String message = result ? "Estado de la Solicitud de visado registrada correctamente."
                : "Falló el registro de solicitud de visado.";
        return new SuccessDataResult<>(result, message);
    }

    public SuccessDataResult<Boolean> updateVisaSource(RegisterVisaSourceDtoRequest dto) {
        UpdateEntity response = visaSourcesDao.updateVisaSource(dto);
        boolean success = response.getUpdated_register() == 1;
        String message = success ? "Estado de la Solicitud de visado actualizada correctamente."
                : "Falló la actualizacion de solicitud de visado.";
        return new SuccessDataResult<>(success, message);
    }

    public IDataResult<VisaSourceApproveDtoResponse> approveVisaSource(ApproveVisaSourceDtoRequest dto) {
        VisaSourceApproveDtoResponse response = visaSourcesDao.approveVisaSource(dto);
        return new SuccessDataResult<>(response);
    }

    public IDataResult<VisaSourceValidateExistDtoResponse> validateSourceIds(String id) {
        VisaSourceValidateExistDtoResponse result = visaSourcesDao.validateSourceIds(id);
        String message = result.getValidated() ? "La Fuente ID es valida." : "Fuente/s invalida/s.";
        return new SuccessDataResult<>(result, message);
    }
}
