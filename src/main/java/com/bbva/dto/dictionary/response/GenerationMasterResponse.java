package com.bbva.dto.dictionary.response;

import com.bbva.entities.dictionary.GenerationEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenerationMasterResponse extends GenerationEntity{
    
    private String generationDateFormatted;
    private String statusName;

}
