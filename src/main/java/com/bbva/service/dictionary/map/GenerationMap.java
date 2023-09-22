package com.bbva.service.dictionary.map;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;

import com.bbva.dto.dictionary.response.GenerationMasterResponse;
import com.bbva.entities.dictionary.GenerationEntity;
import com.bbva.enums.dictionary.StatusGenerationType;
import com.bbva.util.exception.ApplicationException;
import com.bbva.util.types.FechaUtil;

public class GenerationMap {
    
    private GenerationMap(){}

    public static GenerationMasterResponse entityToDTO(GenerationEntity generationEntity) {
        GenerationMasterResponse entityDTO = new GenerationMasterResponse(); 
		try {
            BeanUtils.copyProperties(entityDTO, generationEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ApplicationException("Error copiando campos entre Bean y DTO", e);
        }
        entityDTO.setGenerationDateFormatted(entityDTO.getGenerationDate()!= null ? FechaUtil.convertDateToString(entityDTO.getGenerationDate(), "dd/MM/yyyy HH:mm:ss") : null);
        entityDTO.setStatusName(StatusGenerationType.get(entityDTO.getStatus()).getDescripcion());
        entityDTO.setDictionaryFile(null);
		return entityDTO;		
	}

    public static List<GenerationMasterResponse> listEntityToDTO(List<GenerationEntity> listGenerationEntity) {
		return (listGenerationEntity != null ? listGenerationEntity.stream().map(GenerationMap::entityToDTO).collect(Collectors.toList()) : null);
	}

}
