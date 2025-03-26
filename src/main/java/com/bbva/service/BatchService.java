package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.BatchDao;
import com.bbva.dto.batch.request.*;
import com.bbva.dto.batch.response.*;
import com.bbva.entities.InsertEntity;

import java.util.List;

public class BatchService {

    private final BatchDao batchDao = new BatchDao();

    public IDataResult<String> getLastJobExecutionStatusDate(){
        String result = batchDao.getLastJobExecutionStatusDate();
        return new SuccessDataResult<>(result);
    }

    public IDataResult<JobExecutionFilterResponseDTO> filter(JobExecutionFilterRequestDTO dto) {
        var result = batchDao.filter(dto);
        return new SuccessDataResult<>(result);
    }

    public IDataResult<InsertEntity> insertReliabilityIncidence(InsertReliabilityIncidenceDTO dto) {
        var result = batchDao.insertReliabilityIncidence(dto);
        return new SuccessDataResult<>(result, "Job guardado correctamente");
    }

    public IDataResult<Void> saveJobExecutionStatus(List<InsertJobExecutionStatusRequest> request){
        try {
            batchDao.saveJobExecutionStatus(request);
            return new SuccessDataResult<>(null);
        } catch (Exception e) {
            return new ErrorDataResult<>(null,"500","No se pudo realizar el registro: " + e.getMessage());
        }
    }

    public IDataResult<Void> saveJobExecutionActive(List<InsertJobExecutionActiveRequest> request){
        try {
            batchDao.saveJobExecutionActive(request);
            return new SuccessDataResult<>(null);
        } catch (Exception e) {
            return new ErrorDataResult<>(null,"500","No se pudo realizar el registro: " + e.getMessage());
        }
    }

    public IDataResult<InsertAJIFJobExecutionResponseDTO> insertAJIFJobExecution(List dto){
        var result = batchDao.insertAJIFJobExecutionRequest(dto);
        return new SuccessDataResult(result);
    }

    public IDataResult<List<StatusJobExecutionDTO>> getStatusJobExecution(
            String jobName, Integer quantity
    ){
        var result = batchDao.getStatusJobExecution(jobName, quantity);
        return new SuccessDataResult<>(result);
    }

    public IDataResult<JobExecutionByIdDTO> getJobExecutionById(String folder, String orderId, String jobName,Integer runCounter) {
        if  (runCounter == null) {
            return new ErrorDataResult<>("El campo runCounter es requerido");
        }
        var result = batchDao.getJobExecutionById(folder, orderId, jobName, runCounter);
        return new SuccessDataResult<>(result);
    }

    public IDataResult<BatchIssuesActionFilterDtoResponse> filterIssueAction(BatchIssuesActionFilterDtoRequest dto) {
        var result = batchDao.filterIssueAction(dto);
        return new SuccessDataResult<>(result);
    }
}
