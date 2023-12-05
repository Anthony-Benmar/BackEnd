package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.BatchDao;
import com.bbva.dto.batch.request.InsertCSATJobExecutionRequest;
import com.bbva.dto.batch.request.InsertReliabilityIncidenceDTO;
import com.bbva.dto.batch.request.JobExecutionFilterRequestDTO;
import com.bbva.dto.batch.response.InsertAJIFJobExecutionResponseDTO;
import com.bbva.dto.batch.response.InsertCSATJobExecutionResponseDTO;
import com.bbva.dto.batch.response.JobExecutionFilterResponseDTO;

import java.util.List;

public class BatchService {

    private final BatchDao batchDao = new BatchDao();

    public IDataResult<JobExecutionFilterResponseDTO> filter(JobExecutionFilterRequestDTO dto) {
        var result = batchDao.filter(dto);
        return new SuccessDataResult(result);
    }

    public IDataResult<InsertReliabilityIncidenceDTO> insertReliabilityIncidence(InsertReliabilityIncidenceDTO dto) {
        var result = batchDao.insertReliabilityIncidence(dto);
        return new SuccessDataResult(result);
    }
    public IDataResult<InsertCSATJobExecutionResponseDTO> insertCSATJobExecution(List dto){
        var result = batchDao.insertCSATJobExecutionRequest(dto);
        return new SuccessDataResult(result);
    }
    public IDataResult<InsertAJIFJobExecutionResponseDTO> insertAJIFJobExecution(List dto){
        var result = batchDao.insertAJIFJobExecutionRequest(dto);
        return new SuccessDataResult(result);
    }
}
