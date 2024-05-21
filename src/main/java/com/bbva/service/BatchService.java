package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.BatchDao;
import com.bbva.dto.batch.request.BatchIssuesActionFilterDtoRequest;
import com.bbva.dto.batch.request.InsertReliabilityIncidenceDTO;
import com.bbva.dto.batch.request.JobExecutionFilterRequestDTO;
import com.bbva.dto.batch.response.*;

import java.util.List;

public class BatchService {

    private final BatchDao batchDao = new BatchDao();

    public IDataResult<JobExecutionFilterResponseDTO> filter(JobExecutionFilterRequestDTO dto) {
        var result = batchDao.filter(dto);
        return new SuccessDataResult(result);
    }

    public IDataResult<InsertReliabilityIncidenceDTO> insertReliabilityIncidence(InsertReliabilityIncidenceDTO dto) {
        var result = batchDao.insertReliabilityIncidence(dto);
        return new SuccessDataResult(result, "Job guardado correctamente");
    }
    public IDataResult<InsertCSATJobExecutionResponseDTO> insertCSATJobExecution(List dto){
        var result = batchDao.insertCSATJobExecutionRequest(dto);
        return new SuccessDataResult(result);
    }
    public IDataResult<InsertAJIFJobExecutionResponseDTO> insertAJIFJobExecution(List dto){
        var result = batchDao.insertAJIFJobExecutionRequest(dto);
        return new SuccessDataResult(result);
    }

    public IDataResult<List<StatusJobExecutionDTO>> getStatusJobExecution(
            String jobName, Integer quantity
    ){
        var result = batchDao.getStatusJobExecution(jobName, quantity);
        return new SuccessDataResult(result);
    }

    public IDataResult<JobExecutionByIdDTO> getJobExecutionById(String folder, String orderId, String jobName,Integer runCounter) {
        if  (runCounter == null) {
            return new ErrorDataResult("El campo runCounter es requerido");
        }
        var result = batchDao.getJobExecutionById(folder, orderId, jobName, runCounter);
        return new SuccessDataResult(result);
    }

    public IDataResult<BatchIssuesActionFilterDtoResponse> filterIssueAction(BatchIssuesActionFilterDtoRequest dto) {
        var result = batchDao.filterIssueAction(dto);
        return new SuccessDataResult(result);
    }
}
