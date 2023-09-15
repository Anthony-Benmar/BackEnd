package com.bbva.dto.source.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationDataSourceFuenteCampoDtoResponse {
    private Integer sourceFieldId;
    private String fuenteId;
    private String fuenteNombre;
    private String sourceCampoNombre;
}
