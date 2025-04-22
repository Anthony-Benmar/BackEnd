package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.ReliabilityDao;
import com.bbva.dto.reliability.request.InventoryInputsFilterDtoRequest;
import com.bbva.dto.reliability.request.InventoryJobUpdateDtoRequest;
import com.bbva.dto.reliability.response.InventoryInputsFilterDtoResponse;

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
}
