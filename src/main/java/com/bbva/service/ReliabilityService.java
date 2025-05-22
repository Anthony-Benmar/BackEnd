package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.ReliabilityDao;
import com.bbva.dto.reliability.request.InventoryInputsFilterDtoRequest;
import com.bbva.dto.reliability.request.InventoryJobUpdateDtoRequest;
import com.bbva.dto.reliability.response.ExecutionValidationDtoResponse;
import com.bbva.dto.reliability.response.InventoryInputsFilterDtoResponse;
import com.bbva.dto.reliability.response.PendingCustodyJobsDtoResponse;
import com.bbva.dto.reliability.response.ProjectCustodyInfoDtoResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReliabilityService {
    private final ReliabilityDao reliabilityDao = new ReliabilityDao();
    private static final Logger log= Logger.getLogger(ReliabilityService.class.getName());
    public IDataResult<InventoryInputsFilterDtoResponse> inventoryInputsFilter(InventoryInputsFilterDtoRequest dto) {
        var result = reliabilityDao.inventoryInputsFilter(dto);
        return new SuccessDataResult<>(result);
    }

    public IDataResult<Void> updateInventoryJobStock(InventoryJobUpdateDtoRequest dto) {
        try {
            reliabilityDao.updateInventoryJobStock(dto);
            return new SuccessDataResult<>(null, "Job stock updated successfully");
        } catch (Exception e) {
            log.severe("Error updating job stock: " + e.getMessage());
            return new ErrorDataResult<>(null, "500", e.getMessage());
        }
    }
    public IDataResult<List<PendingCustodyJobsDtoResponse>> getPendingCustodyJobs(String sdatoolId) {
        try {
            var result = reliabilityDao.getPendingCustodyJobs(sdatoolId);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<List<ProjectCustodyInfoDtoResponse>> getProjectCustodyInfo(String sdatoolId) {
        try {
            var result = reliabilityDao.getProjectCustodyInfo(sdatoolId);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<ExecutionValidationDtoResponse> getExecutionValidation(String jobName) {
        try {
            var result = reliabilityDao.getExecutionValidation(jobName);
            return new SuccessDataResult<>(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult<>(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
