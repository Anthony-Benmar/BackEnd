package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.UseCaseReliabilityDao;
import java.io.IOException;
import com.bbva.entities.usecase.UseCaseEntity;

public class UseCaseService {
    private final UseCaseReliabilityDao useCaseReliabilityDao = new UseCaseReliabilityDao();

    public IDataResult<UseCaseEntity> listUseCases() {
        var result = useCaseReliabilityDao.listAllUseCases();
        return new SuccessDataResult(result);
    }
}
