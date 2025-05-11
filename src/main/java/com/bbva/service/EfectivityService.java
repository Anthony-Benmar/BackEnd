package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.EfectivityDao;
import com.bbva.dto.efectivity.response.EfectivityEntityResponseDTO;

import java.util.List;

public class EfectivityService {
    private final EfectivityDao efectivityDao = new EfectivityDao();
    public IDataResult<List<EfectivityEntityResponseDTO>> getEfectivityWithSource( String tableName){
        List<EfectivityEntityResponseDTO> result;
        try {
            result = efectivityDao.getEfectivityWithSource(tableName);
        } catch (Exception e) {
            return new ErrorDataResult(e.getCause(),"500","No se pudo realizar el listado");
        }
        return new SuccessDataResult(result);
    }
}
