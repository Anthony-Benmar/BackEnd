package com.bbva.dto.template.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationDataTemplateDtoResponse {
    private Integer templateFieldId;
    private String fuenteId;
    private String fuenteNombre;
    private String templateCampoNombre;
}
