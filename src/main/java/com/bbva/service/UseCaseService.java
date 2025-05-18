package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.UseCaseReliabilityDao;
import com.bbva.dto.use_case.request.UpdateOrInsertUseCaseDtoRequest;
import com.bbva.dto.use_case.request.UseCaseInputsFilterDtoRequest;
import com.bbva.dto.use_case.response.UseCaseInputsFilterDtoResponse;
import com.bbva.entities.use_case.UseCaseEntity;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UseCaseService {
    private final UseCaseReliabilityDao useCaseReliabilityDao = new UseCaseReliabilityDao();
    private static final Logger log= Logger.getLogger(JobService.class.getName());

    public IDataResult<UseCaseEntity> listUseCases() {
        var result = useCaseReliabilityDao.listAllUseCases();
        return new SuccessDataResult(result);
    }

    public IDataResult<UpdateOrInsertUseCaseDtoRequest> updateOrInsertUseCase(UpdateOrInsertUseCaseDtoRequest dto) {
        try {
            var result = useCaseReliabilityDao.updateOrInsertUseCase(dto);

            boolean isInsert = (dto.getUseCaseId() == null || dto.getUseCaseId().equals(0));
            boolean isUpdateOperation = !isInsert;
            if (dto.getUseCaseId() == null || dto.getUseCaseId().equals(0)) {
                if (isUpdateOperation) {
                    return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "UseCaseId is required for update.");
                }
            }
            if (dto.getUseCaseName() == null || dto.getUseCaseName().isBlank())
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "UseCaseName must not be null or empty");
            if (dto.getUseCaseDescription() == null || dto.getUseCaseDescription().isBlank())
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "UseCaseDescription must not be null or empty");
            if (dto.getDomainId() == null || dto.getDomainId().equals(0))
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "DomainId must not be null or 0");
            return new SuccessDataResult(result);
        }
        catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<UseCaseInputsFilterDtoResponse> getFilteredUseCases(UseCaseInputsFilterDtoRequest dto) {
        try {
            var result = useCaseReliabilityDao.getFilteredUseCases(dto);
            return new SuccessDataResult(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
