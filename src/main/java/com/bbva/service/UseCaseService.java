package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.UseCaseReliabilityDao;
import com.bbva.dto.use_case.request.UpdateOrInsertUseCaseDtoRequest;
import com.bbva.dto.use_case.request.UseCaseInputsFilterDtoRequest;
import com.bbva.dto.use_case.response.UpdateOrInsertDtoResponse;
import com.bbva.dto.use_case.response.UseCaseInputsFilterDtoResponse;
import com.bbva.entities.use_case.UseCaseEntity;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UseCaseService {
    private final UseCaseReliabilityDao useCaseReliabilityDao = new UseCaseReliabilityDao();
    private static final Logger log= Logger.getLogger(UseCaseService.class.getName());

    public IDataResult<List<UseCaseEntity>> listUseCases() {
        try {
            var result = useCaseReliabilityDao.listAllUseCases();
            return new SuccessDataResult<>(result);
        }catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<UpdateOrInsertDtoResponse> updateOrInsertUseCase(UpdateOrInsertUseCaseDtoRequest dto) {
        try {
            boolean isInsert = (dto.getUseCaseId() == null || dto.getUseCaseId().equals(0));
            boolean isUpdateOperation = !isInsert;
            if ((dto.getUseCaseId() == null || dto.getUseCaseId().equals(0)) && isUpdateOperation) {
                return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "UseCaseId is required for update.");
            }
            if (dto.getUseCaseName() == null || dto.getUseCaseName().isBlank())
                return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "UseCaseName must not be null or empty");
            if (dto.getUseCaseDescription() == null || dto.getUseCaseDescription().isBlank())
                return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "UseCaseDescription must not be null or empty");
            if (dto.getDomainId() == null || dto.getDomainId().equals(0))
                return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "DomainId must not be null or 0");

            var result = useCaseReliabilityDao.updateOrInsertUseCase(dto);
            return new SuccessDataResult<>(result, HttpStatusCodes.HTTP_OK, isInsert ? "Use case inserted successfully." : "Use case updated successfully.");
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<UseCaseInputsFilterDtoResponse> getFilteredUseCases(UseCaseInputsFilterDtoRequest dto) {
        try {
            var result = useCaseReliabilityDao.getFilteredUseCases(dto);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
