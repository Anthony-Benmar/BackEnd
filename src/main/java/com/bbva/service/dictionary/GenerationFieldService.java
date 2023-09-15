package com.bbva.service.dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.dictionary.FieldDatumDao;
import com.bbva.dao.dictionary.GenerationFieldDao;
import com.bbva.dto.dictionary.request.GenerationFieldChooseRequest;
import com.bbva.dto.dictionary.response.GenerationDetailResponse;
import com.bbva.entities.dictionary.FieldDatumEntity;
import com.bbva.entities.dictionary.GenerationFieldEntity;
import com.bbva.enums.dictionary.StatusGenerationFieldType;

public final class GenerationFieldService {
    
    private static final Logger LOGGER = Logger.getLogger(GenerationFieldService.class.getName());

    private static GenerationFieldService instance = null;
    
    public static synchronized GenerationFieldService getInstance() {
        if (Objects.isNull(instance)) {
            instance = new GenerationFieldService();
        }
        return instance;
    } 

    public IDataResult<List<GenerationDetailResponse>> buscar(Integer generationId){
        try{
            List<GenerationFieldEntity> listaGenerationFieldEntity = GenerationFieldDao.getInstance().buscar(generationId);
            List<GenerationDetailResponse> listaGenerationDetailResponse = new ArrayList<>();
            GenerationDetailResponse generationDetailResponse = null;
            for(GenerationFieldEntity generationFieldEntity : listaGenerationFieldEntity){
                generationDetailResponse = new GenerationDetailResponse();
                generationDetailResponse.setGenerationFieldEntity(generationFieldEntity);
                generationDetailResponse.setAplicaSeleccion(StatusGenerationFieldType.RESUELTO == StatusGenerationFieldType.get(generationFieldEntity.getStatus()) ||
                                                            StatusGenerationFieldType.OBSERVADO == StatusGenerationFieldType.get(generationFieldEntity.getStatus()));
                generationDetailResponse.setStatusName(StatusGenerationFieldType.get(generationFieldEntity.getStatus()).getDescripcion());
                listaGenerationDetailResponse.add(generationDetailResponse);
            }
            return new SuccessDataResult<>(listaGenerationDetailResponse);
        }catch(Exception ex){
            LOGGER.log(Level.SEVERE, StringUtils.EMPTY, ex);
            return new ErrorDataResult<>("Error de Sistema.");
        } 
    }

    public IDataResult<GenerationFieldChooseRequest> escogerDatum(GenerationFieldChooseRequest request){
        try{
            FieldDatumEntity fieldDatumEntity = FieldDatumDao.getInstance().buscarPorId(request.getFieldDatumId());
            GenerationFieldEntity generationFieldEntity = GenerationFieldDao.getInstance().buscarPorId(request.getGenerationFieldId());
            this.copyFieldDatum(generationFieldEntity, fieldDatumEntity);
            generationFieldEntity.setStatus(StatusGenerationFieldType.RESUELTO.getCodigo());

            GenerationFieldDao.getInstance().actualizarFieldDatum(generationFieldEntity);     

            return new SuccessDataResult<>(request);
        }catch(Exception ex){
            LOGGER.log(Level.SEVERE, StringUtils.EMPTY, ex);
            return new ErrorDataResult<>("Error de Sistema.");
        } 
    }

    public void copyFieldDatum(GenerationFieldEntity entityDestiny, FieldDatumEntity entityOrigin){
        entityDestiny.setPhysicalFieldName(entityOrigin.getPhysicalFieldName());
        entityDestiny.setLogicalFieldName(entityOrigin.getLogicalFieldName());
        entityDestiny.setDescriptionFieldDesc(entityOrigin.getDescriptionFieldDesc());
    }

}
