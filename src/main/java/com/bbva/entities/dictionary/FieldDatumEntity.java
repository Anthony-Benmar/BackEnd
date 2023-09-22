package com.bbva.entities.dictionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class FieldDatumEntity {
    
    private Integer fieldDatumId;
    private String physicalFieldName;
    private String logicalFieldName;
    private String descriptionFieldDesc;

}
