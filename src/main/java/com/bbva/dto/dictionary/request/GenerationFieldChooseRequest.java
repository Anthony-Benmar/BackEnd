package com.bbva.dto.dictionary.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenerationFieldChooseRequest {
    
    private Integer generationFieldId;
    private Integer fieldDatumId;

}
