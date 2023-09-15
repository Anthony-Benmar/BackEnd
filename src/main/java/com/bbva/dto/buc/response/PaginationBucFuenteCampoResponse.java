package com.bbva.dto.buc.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationBucFuenteCampoResponse {
    private Integer bucFieldId;
    private String fuenteId;
    private String fuenteNombre;
    private String bucCampoNombre;
}
