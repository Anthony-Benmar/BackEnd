package com.bbva.service.dictionary;

import java.util.List;
import java.util.Objects;

import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import java.util.logging.Level;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.dictionary.FieldDatumDao;
import com.bbva.entities.dictionary.FieldDatumEntity;

public final class FieldDatumService {
    
    private static final Logger LOGGER = Logger.getLogger(FieldDatumService.class.getName());

    private static FieldDatumService instance = null;
    
    public static synchronized FieldDatumService getInstance() {
        if (Objects.isNull(instance)) {
            instance = new FieldDatumService();
        }
        return instance;
    } 

    public IDataResult<List<FieldDatumEntity>> filtrar(String physicalFieldName){
        try{
            return new SuccessDataResult<>(FieldDatumDao.getInstance().filtrar(physicalFieldName));
        }catch(Exception ex){
            LOGGER.log(Level.SEVERE, StringUtils.EMPTY, ex);
            return new ErrorDataResult<>("Error de Sistema.");
        } 
    }

}
