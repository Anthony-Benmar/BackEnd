package com.bbva.dto.dictionary.response;

import com.bbva.entities.dictionary.GenerationFieldEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class GenerationDetailResponse {
    
    private GenerationFieldEntity generationFieldEntity;
    private boolean aplicaSeleccion;
    private String statusName;

}
