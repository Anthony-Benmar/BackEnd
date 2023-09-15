package com.bbva.dto.bui.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationDataBuiFuenteCampoDtoResponse {
    private Integer buiFieldId;
    private String fuenteId;
    private String fuenteNombre;
    private String buiCampoNombre;
}
